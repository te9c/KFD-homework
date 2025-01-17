package com.example.lab4.controllers

import com.example.lab4.db.Balance
import com.example.lab4.db.repositories.BalanceRepository
import com.example.lab4.db.repositories.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/balance")
class BalanceController(
    private val userRepository: UserRepository,
    private val balanceRepository: BalanceRepository
) {
    @GetMapping
    fun getBalances() = balanceRepository.findAll()
    @GetMapping("/{username}")
    fun getBalances(@PathVariable username: String) : ResponseEntity<List<Balance>> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user.balances)
    }
}