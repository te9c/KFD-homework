package org.example.microservicetransactions.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class ExchangerBalanceDto(
    @field:NotBlank
    val currencyCode: String,
    @field:NotNull
    @field:PositiveOrZero
    val amount: Int
)