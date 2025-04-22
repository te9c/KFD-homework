package org.example.microservicetransactions.db

import jakarta.persistence.*

@Entity
class ExchangerBalance(
    currencyCode: String = "",
    var amount: Int = 0
) {
    @Column(unique = true)
    val currencyCode: String = currencyCode.uppercase()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}