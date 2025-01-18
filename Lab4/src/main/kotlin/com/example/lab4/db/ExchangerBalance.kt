package com.example.lab4.db

import jakarta.persistence.*

@Entity
class ExchangerBalance(
    currencyCode: String,
    var amount: Int
) {
    @Column(unique = true)
    val currencyCode: String = currencyCode.uppercase()
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}