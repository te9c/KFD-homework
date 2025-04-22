package org.example.microservicetransactions.db

import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["baseCurrency", "quoteCurrency"])])
class CurrencyPair(
    baseCurrency: String = "",
    quoteCurrency: String = "",
    // Rate is showing how much you need to spend
    // quoteCurrency to buy one factor of baseCurrency
    var rate: Int = 0,
    // basically means that we are buying 100 amount of basecurrency
    // for $rate amount of quoteCurrency
    val factor: Int = 100
) {
    val baseCurrency: String = baseCurrency.uppercase()
    val quoteCurrency: String = quoteCurrency.uppercase()

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    override fun toString(): String {
        return "$baseCurrency/$quoteCurrency"
    }
}