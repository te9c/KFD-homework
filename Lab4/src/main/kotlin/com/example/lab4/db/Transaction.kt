package com.example.lab4.db

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
data class Transaction(
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "username")
    @JsonIdentityReference(alwaysAsId = true)
    val username: ExchangerUser,

    // let the plus value be user gainings
    // and minus value user expenses
    // This will help avoid flag for buying/selling
    val currencyPair: String,
    val rate: Int,
    val baseCurrencyDelta: Int,
    val quoteCurrencyDelta: Int,

    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}