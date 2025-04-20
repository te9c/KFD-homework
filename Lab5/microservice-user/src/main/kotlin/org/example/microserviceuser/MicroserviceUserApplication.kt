package org.example.microserviceuser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MicroserviceUserApplication

fun main(args: Array<String>) {
    runApplication<MicroserviceUserApplication>(*args)
}