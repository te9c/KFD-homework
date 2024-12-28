package com.example.lab4.db

import jakarta.persistence.*

@Entity
class ExchangerUser (
    @Column(unique = true)
    val username: String,
    @Column
    val password: String,
    @ElementCollection(fetch = FetchType.EAGER)
    val authorities: List<String>
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
}