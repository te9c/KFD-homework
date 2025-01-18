package com.example.lab4.db

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "currencyCode"])])
class Balance(
    currencyCode: String,
    @Column
    var amount: Int,
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(nullable = false, name = "user_id")
    val user: ExchangerUser? = null
) {
    val currencyCode = currencyCode.uppercase()
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}