package com.example.lab4.controllers

import com.example.lab4.db.ExchangerUser
import com.example.lab4.db.UserRepository
import org.jetbrains.annotations.NotNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(val userRepository: UserRepository, val encoder: PasswordEncoder) {
    @GetMapping()
    // How to secure this?
    fun getUsers(): List<ExchangerUser> = userRepository.findAll()
    @GetMapping("/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<ExchangerUser?> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }
    @PostMapping("/register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest) : ResponseEntity<Any> {
        val user = userRepository.findByUsername(registerRequest.username)
        if (user != null) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        val ret = userRepository.save(ExchangerUser(registerRequest.username, encoder.encode(registerRequest.rawPassword), listOf( "ROLE_USER" )))
        return ResponseEntity(ret, HttpStatus.CREATED)
    }
}

class RegisterRequest(
    val username: String,
    val rawPassword: String,
)