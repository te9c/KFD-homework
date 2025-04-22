package org.example.microservicetransactions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
class MicroserviceTransactionsApplication

fun main(args: Array<String>) {
    runApplication<MicroserviceTransactionsApplication>(*args)
}
