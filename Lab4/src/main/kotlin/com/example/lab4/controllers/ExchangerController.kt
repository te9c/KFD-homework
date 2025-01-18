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
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteExchangerBalance(@PathVariable id: Long) = exchangerRepository.deleteById(id)

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun addBalances(@RequestBody requests: List<@Valid ExchangerBalanceRequest>): ResponseEntity<Any> {
        for (request in requests) {
            val balance = exchangerRepository.findByCurrencyCode(request.currencyCode)
            if (balance != null) {
                balance.amount = request.amount
                val ret = exchangerRepository.save(balance)
            } else {
                val ret = exchangerRepository.save(ExchangerBalance(request.currencyCode, request.amount))
            }
        }
        return ResponseEntity.ok(getExchangerBalance())
    }
}

data class ExchangerBalanceRequest(
    @field:NotBlank
    val currencyCode: String,
    @field:NotNull
    @field:PositiveOrZero
    val amount: Int
)