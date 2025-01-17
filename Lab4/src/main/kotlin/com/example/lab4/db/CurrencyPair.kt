package com.example.lab4.db

import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["baseCurrency", "quoteCurrency"])])
class CurrencyPair(
    baseCurrency: String,
    quoteCurrency: String,
    // Rate is showing how much
    // you need to spend quoteCurrency to buy baseCurrency
    val rate: Int
) {
    val baseCurrency: String = baseCurrency.uppercase()
    val quoteCurrency: String = quoteCurrency.uppercase()

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun toString(): String {
        return "$baseCurrency/$quoteCurrency"
    }
}