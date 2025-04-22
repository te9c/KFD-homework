package org.example.microservicetransactions.security

import feign.Headers
import org.example.microservicetransactions.dto.UserDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "microservice-user")
interface AuthServiceClient {
    @PostMapping("/api/auth/getuser", produces= arrayOf("application/json"))
    fun getUserDetails(@RequestBody token: String): UserDto?
}