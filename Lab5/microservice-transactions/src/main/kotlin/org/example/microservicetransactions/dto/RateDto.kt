package org.example.microservicetransactions.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class RateDto(
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