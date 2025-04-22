package org.example.microserviceuser.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

// for naming see UserDto class.
data class BalanceDto(
    @field:NotBlank
    val currencyCode: String,
    @field:PositiveOrZero
    val amount: Int,
)