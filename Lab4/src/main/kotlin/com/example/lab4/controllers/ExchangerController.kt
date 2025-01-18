package com.example.lab4.controllers

import com.example.lab4.db.ExchangerBalance
import com.example.lab4.db.repositories.ExchangerRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/exchangerbalance")
class ExchangerController(
    private val exchangerRepository: ExchangerRepository
) {
    @GetMapping
    fun getExchangerBalance() = exchangerRepository.findAll()

    @GetMapping("/{id}")
    fun ExchangerBalance(@PathVariable id: Long) = exchangerRepository.findById(id)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun deleteExchangerBalance(@PathVariable id: Long) = exchangerRepository.deleteById(id)

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun addBalance(@Valid @RequestBody request: ExchangerBalanceRequest): ResponseEntity<Any> {
        val balance = exchangerRepository.findByCurrencyCode(request.currencyCode)
        if (balance != null) {
            balance.amount = request.amount
            val ret = exchangerRepository.save(balance)
            return ResponseEntity.ok(ret)
        } else {
            val ret = exchangerRepository.save(ExchangerBalance(request.currencyCode, request.amount))
            return ResponseEntity.ok(ret)
        }
    }
}

data class ExchangerBalanceRequest(
    @field:NotBlank
    val currencyCode: String,
    @field:NotNull
    @field:PositiveOrZero
    val amount: Int
)