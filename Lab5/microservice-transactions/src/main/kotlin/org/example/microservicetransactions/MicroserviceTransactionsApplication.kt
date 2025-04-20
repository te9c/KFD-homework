package org.example.microservicetransactions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MicroserviceTransactionsApplication

fun main(args: Array<String>) {
    runApplication<MicroserviceTransactionsApplication>(*args)
}
