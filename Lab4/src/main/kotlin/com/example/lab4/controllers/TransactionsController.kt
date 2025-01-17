package com.example.lab4.controllers

import com.example.lab4.db.Transaction
import com.example.lab4.db.repositories.TransactionRepository
import com.example.lab4.db.repositories.UserRepository
import com.example.lab4.db.Balance
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/transactions")
class TransactionsController(
    val transactionRepository: TransactionRepository,
    val userRepository: UserRepository
) {
    @GetMapping()
    fun getTransactions() : List<Transaction> = transactionRepository.findAll()
    @GetMapping("/{username}")
    fun getTransactions(@PathVariable username: String): List<Transaction> {
        val user = userRepository.findByUsername(username) ?: return emptyList()
        return transactionRepository.findBySender(user) + transactionRepository.findByReceiver(user)
    }

    // I love kotlin coding...
    @PostMapping()
    fun makeTransaction(@RequestBody transactionRequest: TransactionRequest): ResponseEntity<Transaction> {
        val sender = userRepository.findByUsername(transactionRequest.sender) ?: return ResponseEntity.badRequest().build()
        val receiver = userRepository.findByUsername(transactionRequest.receiver) ?: return ResponseEntity.badRequest().build()
        var senderBalance = sender.balances.firstOrNull { it.currencyCode == transactionRequest.currencyCode }
        if (senderBalance == null || transactionRequest.amount > senderBalance.amount) {
            return ResponseEntity.badRequest().build()
        }

        var receiverBalance = receiver.balances.firstOrNull { it.currencyCode == transactionRequest.currencyCode }
        if (receiverBalance == null) {
            receiver.balances.add(Balance(transactionRequest.currencyCode, transactionRequest.amount, receiver))
        } else {
            receiverBalance.amount += transactionRequest.amount
        }

        senderBalance.amount -= transactionRequest.amount

        val transaction = Transaction(sender, receiver, transactionRequest.amount, transactionRequest.currencyCode, LocalDateTime.now())

        userRepository.save(sender)
        userRepository.save(receiver)
        val ret = transactionRepository.save(transaction)

        return ResponseEntity.ok(ret)
    }
}

class TransactionRequest(
    val sender: String,
    val receiver: String,
    val amount: Int,
    val currencyCode: String
)