package org.example.microservicetransactions.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class BalanceDto(
    @field:NotBlank
    val currencyCode: String,
    @field:PositiveOrZero
    val amount: Int,
)