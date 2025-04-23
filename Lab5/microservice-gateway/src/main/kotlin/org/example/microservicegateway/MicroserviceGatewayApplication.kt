package org.example.microservicegateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableDiscoveryClient
class MicroserviceGatewayApplication

//@Bean
//fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
//	return builder.routes()
//		.route("microservice-user", {r -> r.path("/api/auth/**", "/api/users", "/api/users/**").uri("lb://microservice-user")})
//		.route("microservice-transactions", {r ->
//			r.path(
//				"/api/balance/**",
//				"/api/balance",
//				"/api/exchangerbalance",
//				"/api/exchangerbalance/**",
//				"/api/rates",
//				"/api/rates/**",
//				"/api/transactions",
//				"/api/transactions/**").uri("lb://microservice-transactions")})
//		.build()
//}

fun main(args: Array<String>) {
	runApplication<MicroserviceGatewayApplication>(*args)
}
