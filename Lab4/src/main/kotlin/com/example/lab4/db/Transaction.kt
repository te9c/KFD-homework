package com.example.lab4.db

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class Transaction(
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    val sender: ExchangerUser,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_id")
    val receiver: ExchangerUser,

    val amount: Int,

    val currencyCode: String,

    val timestamp: LocalDateTime
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}