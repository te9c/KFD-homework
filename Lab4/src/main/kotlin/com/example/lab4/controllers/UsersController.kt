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

    @PreAuthorize("authentication.name == #u || hasRole('ADMIN')")
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
}