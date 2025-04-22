package org.example.microserviceuser.controllers

import jakarta.validation.Valid
import org.example.microserviceuser.dto.UserDto
import org.example.microserviceuser.db.ExchangerUser
import org.example.microserviceuser.db.repositories.UserRepository
import org.example.microserviceuser.security.CustomUserDetailsService
import org.example.microserviceuser.security.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userService: CustomUserDetailsService,
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    @GetMapping("/validate")
    fun validateToken(@RequestBody token: String): Boolean {
        try {
            return !jwtUtil.isTokenExpired(token);
        } catch(ex: Exception) {
            return false
        }
    }
    @PostMapping("/getuser")
    fun getUser(@RequestBody token: String): UserDto? {
        val username: String
        try {
            username = jwtUtil.extractUsername(token)
            if (jwtUtil.isTokenExpired(token))
                return null
        } catch (ex: Exception) {
            return null
        }
        val user = userRepository.findByUsername(username) ?: return null
        return UserDto(user.username, user.password, user.authorities)
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody registerRequest: UserDto): ResponseEntity<Any> {
        val user = userRepository.findByUsername(registerRequest.username)
        if (user != null) {
            return ResponseEntity.badRequest().build()
        }
        val authorities: MutableList<String> = mutableListOf("ROLE_USER")

        if (SecurityContextHolder.getContext().getAuthentication()?.authorities?.firstOrNull { it.authority == "ROLE_ADMIN" } != null &&
            registerRequest.authorities != null) {
            authorities.addAll(registerRequest.authorities)
        }

        val savedUser = ExchangerUser(registerRequest.username, encoder.encode(registerRequest.password), authorities)
        val ret = userRepository.save(savedUser)
        return ResponseEntity(ret, HttpStatus.CREATED)
    }

    @PostMapping("/signin")
    fun loginUser(@RequestBody @Valid request: UserDto): ResponseEntity<String> {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
        } catch (ex: BadCredentialsException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password")
        }

        val userDetails = userService.loadUserByUsername(request.username)
        val token = jwtUtil.generateToken(userDetails.username)
        return ResponseEntity.ok(token)
    }
}