package org.example.microserviceuser.controllers

import org.example.microserviceuser.db.ExchangerUser
import org.example.microserviceuser.db.repositories.UserRepository
import org.example.microserviceuser.dto.UserDto
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(val userRepository: UserRepository,
                     val encoder: PasswordEncoder) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    fun getUsers(): List<ExchangerUser> = userRepository.findAll()

    // @PreAuthorize("authentication.name == #u || hasRole('ADMIN')")
    @GetMapping("/{username}")
    fun getUser(@PathVariable @P("u") username: String): UserDto? {
        val user = userRepository.findByUsername(username) ?: return null
        return UserDto(user.username, user.password, user.authorities)
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("authentication.name == #u || hasRole('ADMIN')")
    fun deleteUser(@PathVariable @P("u") username: String): ResponseEntity<Void> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        userRepository.delete(user)
        return ResponseEntity.ok().build()
    }
}