package org.example.microservicetransactions.db.repositories

import org.example.microservicetransactions.db.Balance
import org.springframework.data.jpa.repository.JpaRepository

interface BalanceRepository : JpaRepository<Balance, Long> {
    fun findBalancesByUsername(username: String): Collection<Balance>
    fun findByCurrencyCode(currencyCode: String): Balance?
}