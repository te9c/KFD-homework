package com.example.lab4.db.repositories

import com.example.lab4.db.ExchangerBalance
import org.springframework.data.jpa.repository.JpaRepository

interface ExchangerRepository : JpaRepository<ExchangerBalance, Long> {
    fun findByCurrencyCode(code: String): ExchangerBalance?
}