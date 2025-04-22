package org.example.microservicetransactions.controllers

import jakarta.validation.Valid
import org.example.microservicetransactions.db.repositories.ExchangerRepository
import org.example.microservicetransactions.dto.ExchangerBalanceDto
import org.example.microservicetransactions.db.ExchangerBalance
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
    fun addBalances(@RequestBody requests: List<@Valid ExchangerBalanceDto>): ResponseEntity<Any> {
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