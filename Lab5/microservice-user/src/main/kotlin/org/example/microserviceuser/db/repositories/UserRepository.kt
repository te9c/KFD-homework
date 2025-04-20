package org.example.microserviceuser.db.repositories

import org.example.microserviceuser.db.ExchangerUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<ExchangerUser, Long> {
    fun findByUsername(username: String?): ExchangerUser?
}