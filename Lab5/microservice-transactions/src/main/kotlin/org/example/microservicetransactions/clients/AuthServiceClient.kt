package org.example.microservicetransactions.clients

import org.example.microservicetransactions.dto.UserDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "microservice-user")
interface AuthServiceClient {
    @PostMapping("/api/auth/getuser", produces= arrayOf("application/json"))
    fun getUserDetails(@RequestBody token: String): UserDto?

    @PostMapping("/api/auth/{username}", produces = arrayOf("application/json"))
    fun getUser(@PathVariable username: String): UserDto?
}