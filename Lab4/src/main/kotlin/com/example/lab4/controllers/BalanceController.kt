package com.example.lab4.controllers

import com.example.lab4.db.Balance
import com.example.lab4.db.repositories.BalanceRepository
import com.example.lab4.db.repositories.UserRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/balance")
class BalanceController(
    private val userRepository: UserRepository,
    private val balanceRepository: BalanceRepository
) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getBalances() = balanceRepository.findAll()

    @PreAuthorize("#u == authentication.name || hasRole('ADMIN')")
    @GetMapping("/{username}")
    fun getBalances(@PathVariable @P("u") username: String) : ResponseEntity<List<Balance>> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user.balances)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{id}")
    fun getBalance(@PathVariable id: Long): ResponseEntity<Balance?> {
        val balance = balanceRepository.findById(id).getOrNull() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(balance)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteBalance(@PathVariable id: Long): ResponseEntity<Void> {
        val balance = balanceRepository.findById(id).getOrNull() ?: return ResponseEntity.notFound().build()
        balanceRepository.delete(balance)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateBalance(@PathVariable @P("username") username: String, @RequestBody balances: List<@Valid BalanceRequest>): ResponseEntity<List<Balance>> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()

        for (balance in balances) {
            val userBalance = user.balances.firstOrNull { it.currencyCode == balance.currencyCode }
            if (userBalance == null) {
                user.balances.add(Balance(balance.currencyCode, balance.amount, user))
            } else {
                userBalance.amount += balance.amount
            }
        }
        userRepository.save(user)
        return ResponseEntity.ok(user.balances)
    }
}

class BalanceRequest(
    @field:NotBlank
    val currencyCode: String,
    @field:PositiveOrZero
    val amount: Int,
)