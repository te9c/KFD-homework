package com.example.lab4.controllers

import com.example.lab4.RateUpdater
import com.example.lab4.db.CurrencyPair
import com.example.lab4.db.repositories.RateRepository
import com.example.lab4.db.repositories.findByCurrencyPair
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.ErrorResponse
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
    fun addRate(@Valid @RequestBody rateRequest: RateRequest) : ResponseEntity<Any> {
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

class RateRequest(
    @field:NotBlank(message = "Base currency cannot be blank")
    val baseCurrency: String,
    @field:NotBlank(message = "Quote currency cannot be blank")
    val quoteCurrency: String,
    @field:Positive
    @field:NotNull(message = "Rate cannot be null")
    val rate: Int,

    @field:Positive
    val factor: Int = 100
)