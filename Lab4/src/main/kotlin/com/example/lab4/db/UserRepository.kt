package com.example.lab4.db

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<ExchangerUser, Long> {
    fun findByUsername(username: String?): ExchangerUser?
}