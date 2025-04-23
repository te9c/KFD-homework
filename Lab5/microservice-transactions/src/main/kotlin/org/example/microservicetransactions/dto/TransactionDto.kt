package org.example.microservicetransactions.dto

import com.fasterxml.jackson.annotation.JsonValue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive

class TransactionDto(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    @field:Pattern(regexp = "^\\w+/\\w+\$")
    val currencyPair: String,
    @field:NotNull
    val transactionType: TransactionType,
    @field:NotNull
    @field:Positive
    val amount: Int
)

enum class TransactionType(@get:JsonValue val type: String) {
    SELLING("selling"),
    BUYING("buying")
}