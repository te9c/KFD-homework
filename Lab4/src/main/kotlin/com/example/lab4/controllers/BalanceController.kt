package com.example.lab4.controllers

import com.example.lab4.db.Balance
import com.example.lab4.db.repositories.BalanceRepository
import com.example.lab4.db.repositories.UserRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateBalance(@PathVariable @P("username") username: String, @RequestBody @Valid balance: Balance): ResponseEntity<List<Balance>> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        val userBalance = user.balances.firstOrNull {it.currencyCode == balance.currencyCode }
        if (userBalance == null) {
            user.balances.add(balance)
        } else {
            userBalance.amount += balance.amount
        }
        userRepository.save(user)
        return ResponseEntity.ok(user.balances)
    }
}