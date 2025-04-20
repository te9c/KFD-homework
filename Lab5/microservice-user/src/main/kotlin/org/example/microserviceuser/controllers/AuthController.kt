package org.example.microserviceuser.controllers

import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// I need to create signup, signin and check method.

@RestController
@RequestMapping("/api/auth")
class AuthController() {
    @GetMapping("/validate")
    fun validateToken(@RequestBody token: String): Boolean {
        return true;
    }
}

//@RestController
//@RequestMapping("/api/auth")
//class AuthController(
//    private val authenticationManager: AuthenticationManager,
//    private val userService: CustomUserDetailsService,
//    private val userRepository: UserRepository,
//    private val encoder: PasswordEncoder,
//    private val jwtUtil: JwtUtil
//) {
//
//    @PostMapping("/signup")
//    fun registerUser(@Valid @RequestBody registerRequest: RegisterRequest) : ResponseEntity<Any> {
//        val user = userRepository.findByUsername(registerRequest.username)
//        if (user != null) {
//            return ResponseEntity.badRequest().build()
//        }
//        val authorities: MutableList<String> = mutableListOf("ROLE_USER")
//        if (SecurityContextHolder.getContext().getAuthentication()?.authorities?.firstOrNull { it.authority == "ROLE_ADMIN" } != null &&
//            registerRequest.authorities != null) {
//            authorities.addAll(registerRequest.authorities)
//        }
//        val savedUser = ExchangerUser(registerRequest.username, encoder.encode(registerRequest.password), authorities)
//        val ret = userRepository.save(savedUser)
//        return ResponseEntity(ret, HttpStatus.CREATED)
//    }
//
//    @PostMapping("/signin")
//    fun loginUser(@RequestBody @Valid request: AuthenticationRequest): ResponseEntity<String> {
//        try {
//            authenticationManager.authenticate(
//                UsernamePasswordAuthenticationToken(request.username, request.password)
//            )
//        } catch (ex: BadCredentialsException) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password")
//        }
//
//        val userDetails = userService.loadUserByUsername(request.username)
//        val token = jwtUtil.generateToken(userDetails.username)
//        return ResponseEntity.ok(token)
//    }
//}

class AuthenticationRequest(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String
)

data class RegisterRequest(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String,
    val authorities: List<String>?
)