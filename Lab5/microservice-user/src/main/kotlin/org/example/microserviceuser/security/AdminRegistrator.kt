package org.example.microserviceuser.security

import org.example.microserviceuser.db.ExchangerUser
import org.example.microserviceuser.db.repositories.UserRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


// This service is signing up admin user on startup with
// admin admin for login and password respectively
@Service
class AdminRegistrator(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @EventListener(ApplicationReadyEvent::class)
    fun registerAdmin() {
        var user = userRepository.findByUsername("admin")
        if (user != null) {
            return
        }

        user = ExchangerUser(
            "admin",
            passwordEncoder.encode("admin"),
            listOf( "ROLE_USER", "ROLE_ADMIN"))
        userRepository.save(user)
    }
}