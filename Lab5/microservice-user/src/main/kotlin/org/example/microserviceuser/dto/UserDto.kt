package org.example.microserviceuser.dto

import jakarta.validation.constraints.NotBlank

// This is not specifically ExchangeUser dto but
// rather just login/register dto.
// So I would rename it in case I would need
// to create "real" user dto.
data class UserDto(
    @field:NotBlank
    val username: String,
    @field:NotBlank
    val password: String,
    val authorities: List<String>?
)