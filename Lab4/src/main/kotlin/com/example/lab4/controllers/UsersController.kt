package com.example.lab4.controllers

import com.example.lab4.db.Balance
import com.example.lab4.db.ExchangerUser
import com.example.lab4.db.repositories.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(val userRepository: UserRepository,
                      val encoder: PasswordEncoder) {
    @GetMapping()
    fun getUsers(): List<ExchangerUser> = userRepository.findAll()
    @GetMapping("/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<ExchangerUser?> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    private fun getDefaultBalances(user: ExchangerUser): MutableList<Balance> {
        return mutableListOf(
            Balance("RUB", 1000, user = user),
            Balance("USD", 1000, user = user)
        )
    }
    @PostMapping("/register")
    fun registerUser(@RequestBody registerRequest: RegisterRequest) : ResponseEntity<Any> {
        val user = userRepository.findByUsername(registerRequest.username)
        if (user != null) {
            return ResponseEntity.badRequest().build()
        }
        val savedUser = ExchangerUser(registerRequest.username, encoder.encode(registerRequest.password), listOf( "ROLE_USER" ))
        savedUser.balances = getDefaultBalances(savedUser)
        val ret = userRepository.save(savedUser)
        return ResponseEntity(ret, HttpStatus.CREATED)
    }
}

class RegisterRequest(
    val username: String,
    val password: String
)