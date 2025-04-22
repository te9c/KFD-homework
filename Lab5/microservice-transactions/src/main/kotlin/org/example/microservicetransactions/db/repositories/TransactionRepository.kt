package org.example.microservicetransactions.db.repositories

import org.example.microservicetransactions.db.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByUsername(username: String) : List<Transaction>
}