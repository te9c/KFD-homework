package com.example.lab4.controllers

import com.example.lab4.db.Balance
import com.example.lab4.db.ExchangerUser
import com.example.lab4.db.repositories.UserRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.parameters.P
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsersController(val userRepository: UserRepository,
                      val encoder: PasswordEncoder) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    fun getUsers(): List<ExchangerUser> = userRepository.findAll()

    @PreAuthorize("authentication.name == #u")
    @GetMapping("/{username}")
    fun getUser(@PathVariable @P("u") username: String): ResponseEntity<ExchangerUser?> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') || authentication.name == #u")
    fun deleteUser(@PathVariable @P("u") username: String): ResponseEntity<Void> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        userRepository.delete(user)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_ANON') || hasRole('ROLE_USER')")
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

    // TODO: Admin user creation

    private fun getDefaultBalances(user: ExchangerUser): MutableList<Balance> {
        return mutableListOf(
            Balance("RUB", 1000, user = user),
            Balance("USD", 1000, user = user)
        )
    }
}

data class RegisterRequest(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String,
    val authorities: List<String>?
)