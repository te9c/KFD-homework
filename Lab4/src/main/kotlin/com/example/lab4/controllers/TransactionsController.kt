package com.example.lab4.controllers

import com.example.lab4.db.Transaction
import com.example.lab4.db.repositories.TransactionRepository
import com.example.lab4.db.repositories.UserRepository
import com.example.lab4.db.Balance
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/transactions")
class TransactionsController(
    val transactionRepository: TransactionRepository,
    val userRepository: UserRepository
) {
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    fun getTransactions() : List<Transaction> = transactionRepository.findAll()

    @GetMapping("/user/{username}")
    @PreAuthorize("#u == authentication.name")
    fun getTransactions(@PathVariable @P("u") username: String): ResponseEntity<List<Transaction>> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user.transactions)
    }

    @GetMapping("/{id}")
    @PostAuthorize("returnObject == null || returnObject.body.user == authentication.name")
    fun getTransactionById(@PathVariable @P("id") id: Long): ResponseEntity<Transaction?> {
        val transaction = transactionRepository.findById(id).getOrNull() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(transaction)
    }

    // I love kotlin coding...
    @PostMapping()
    @PreAuthorize("#t.sender == authentication.name")
    fun makeTransaction(@RequestBody @P("t") transactionRequest: TransactionRequest): ResponseEntity<Transaction> {
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
    @NotBlank
    val sender: String,
    @NotBlank
    val receiver: String,
    val amount: Int,
    @NotBlank
    val currencyCode: String
)