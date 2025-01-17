package com.example.lab4.db.repositories

import com.example.lab4.db.ExchangerUser
import com.example.lab4.db.Transaction
import org.springframework.data.jpa.repository.JpaRepository

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findBySender(sender: ExchangerUser) : List<Transaction>
    fun findByReceiver(receiver: ExchangerUser) : List<Transaction>
}