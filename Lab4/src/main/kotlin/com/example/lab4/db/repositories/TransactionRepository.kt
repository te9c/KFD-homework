package com.example.lab4.db.repositories

import com.example.lab4.db.ExchangerUser
import com.example.lab4.db.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByUser(user: ExchangerUser) : List<Transaction>
}