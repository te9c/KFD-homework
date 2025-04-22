package org.example.microservicetransactions.controllers

import jakarta.validation.Valid
import org.example.microservicetransactions.db.CurrencyPair
import org.example.microservicetransactions.db.repositories.RateRepository
import org.example.microservicetransactions.db.repositories.findByCurrencyPair
import org.example.microservicetransactions.dto.RateDto
import org.example.microservicetransactions.services.RateUpdater
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/rates")
class RateController(
    private val rateRepository: RateRepository,
    private val rateUpdater: RateUpdater
) {
    @GetMapping
    fun getRates() = rateRepository.findAll()

    @GetMapping("/{id}")
    fun getRate(@PathVariable id: Long) = rateRepository.findById(id)

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun addRate(@Valid @RequestBody rateRequest: RateDto) : ResponseEntity<Any> {
        val rate = rateRepository.findByCurrencyPair(rateRequest.baseCurrency, rateRequest.quoteCurrency)
        if (rate != null) {
            return ResponseEntity.badRequest().build()
        }
        val ret = rateRepository.save(CurrencyPair(rateRequest.baseCurrency, rateRequest.quoteCurrency, rateRequest.rate, rateRequest.factor))
        return ResponseEntity.created(URI.create("/api/rates/${ret.id}")).body(ret)
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRates() : ResponseEntity<List<CurrencyPair>> {
        return ResponseEntity.ok(rateUpdater.updateRates())
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRate(@PathVariable id: Long) = rateRepository.deleteById(id)
}
