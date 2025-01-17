package com.example.lab4.db

import jakarta.persistence.*

@Entity
data class ExchangerBalance(
    @Column(unique = true)
    val currencyCode: String,
    var amount: Int
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}