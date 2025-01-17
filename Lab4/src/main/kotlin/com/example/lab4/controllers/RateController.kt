package com.example.lab4.controllers

import com.example.lab4.db.CurrencyPair
import com.example.lab4.db.repositories.RateRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api/rates")
class RateController(
    private val rateRepository: RateRepository
) {
    @GetMapping
    fun getRates() = rateRepository.findAll()

    @GetMapping("/{id}")
    fun getRate(@PathVariable id: Long) = rateRepository.findById(id)

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun addRate(@RequestBody rateRequest: RateRequest) : ResponseEntity<Any> {
        val rate = rateRepository.findByCurrencyPair(rateRequest.baseCurrency, rateRequest.quoteCurrency)
        if (rate != null) {
            return ResponseEntity.badRequest().build()
        }
        val ret = rateRepository.save(CurrencyPair(rateRequest.baseCurrency, rateRequest.quoteCurrency, rateRequest.rate))
        return ResponseEntity.created(URI.create("/api/rates/${ret.id}")).body(ret)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRate(@PathVariable id: Long) = rateRepository.deleteById(id)
}

data class RateRequest(
    val baseCurrency: String,
    val quoteCurrency: String,
    val rate: Int,
)