package org.example.microservicetransactions.dto

data class UserDto(
    val username: String,
    val password: String,
    val authorities: List<String>?
)