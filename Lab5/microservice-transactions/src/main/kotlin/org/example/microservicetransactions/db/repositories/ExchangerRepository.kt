package org.example.microservicetransactions.db.repositories

import org.example.microservicetransactions.db.ExchangerBalance
import org.springframework.data.jpa.repository.JpaRepository

interface ExchangerRepository : JpaRepository<ExchangerBalance, Long> {
    fun findByCurrencyCode(code: String): ExchangerBalance?
}