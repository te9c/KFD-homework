package org.example.microservicetransactions.controllers

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/hello")
    @PreAuthorize("authentication.name == 'aboba'")
    fun sayHello(): String {
        return "Hello world"
    }
}