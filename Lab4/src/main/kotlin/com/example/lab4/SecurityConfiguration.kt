package com.example.lab4

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager

@Configuration
@EnableWebSecurity
class SecurityConfiguration(private val userDetailsService: CustomUserDetailsService) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterSecurityChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeHttpRequests {
                authorize(HttpMethod.POST, "/api/users/register", permitAll)
                authorize("/api/users/balance/{name}", WebExpressionAuthorizationManager("#name == authentication.name"))
                authorize("/api/users/{name}", WebExpressionAuthorizationManager("#name == authentication.name"))
                authorize(anyRequest, permitAll)
            }
            httpBasic { }
        }
        return http.build()
    }

    @Bean
    fun authinticationProvider(): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }
}