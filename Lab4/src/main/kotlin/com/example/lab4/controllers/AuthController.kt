package com.example.lab4.controllers

import com.example.lab4.db.Balance
import com.example.lab4.db.ExchangerUser
import com.example.lab4.db.repositories.UserRepository
import com.example.lab4.security.CustomUserDetailsService
import com.example.lab4.security.JwtUtil
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
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

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest) : ResponseEntity<Any> {
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
        savedUser.balances = getDefaultBalances(savedUser)
        val ret = userRepository.save(savedUser)
        return ResponseEntity(ret, HttpStatus.CREATED)
    }

    @PostMapping("/signin")
    fun loginUser(@RequestBody @Valid request: AuthenticationRequest): ResponseEntity<String> {
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

    private fun getDefaultBalances(user: ExchangerUser): MutableList<Balance> {
        return mutableListOf(
            Balance("RUB", 1000000, user = user),
            Balance("USD", 1000000, user = user)
        )
    }
}

class AuthenticationRequest(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)