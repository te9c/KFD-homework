package org.example.microservicetransactions.controllers

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.example.microservicetransactions.clients.AuthServiceClient
import org.example.microservicetransactions.db.Balance
import org.example.microservicetransactions.db.repositories.BalanceRepository
import org.example.microservicetransactions.dto.BalanceDto
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/balance")
class BalanceController(
    private val balanceRepository: BalanceRepository,
    private val authServiceClient: AuthServiceClient
) {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    fun getBalances() = balanceRepository.findAll()

    @PreAuthorize("#u == authentication.name || hasRole('ADMIN')")
    @GetMapping("/{username}")
    fun getBalances(@PathVariable @P("u") username: String) : ResponseEntity<Collection<Balance>> {
        return ResponseEntity.ok(balanceRepository.findBalancesByUsername(username))
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
    fun updateBalance(@PathVariable @P("username") username: String, @RequestBody balances: List<@Valid BalanceDto>): ResponseEntity<List<Balance>> {
        //val user = authServiceClient.getUser(username) ?: return ResponseEntity.notFound().build()
        val userBalances = balanceRepository.findBalancesByUsername(username).toMutableList()

        for (balance in balances) {
            val userBalance = userBalances.firstOrNull { it.currencyCode == balance.currencyCode }
            if (userBalance == null) {
                userBalances.add(Balance(balance.currencyCode, balance.amount, username))
            } else {
                userBalance.amount += balance.amount
            }
        }
        balanceRepository.saveAll(userBalances)
        return ResponseEntity.ok(userBalances)
    }
}