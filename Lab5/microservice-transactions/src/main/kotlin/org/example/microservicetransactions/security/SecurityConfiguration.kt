package org.example.microservicetransactions.security

import org.example.microservicetransactions.clients.AuthServiceClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    private val authServiceClient: AuthServiceClient
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder();

    @Bean
    fun filterSecurityChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            anonymous {
                authorities = listOf(SimpleGrantedAuthority("ROLE_ANON"))
            }
            addFilterAfter<UsernamePasswordAuthenticationFilter>(
                JwtAuthenticationFilter(authServiceClient)
            )
        }
        return http.build()
    }
}