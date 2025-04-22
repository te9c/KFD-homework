package org.example.microserviceeurekaserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class MicroserviceEurekaServerApplication

fun main(args: Array<String>) {
    runApplication<MicroserviceEurekaServerApplication>(*args)
}
