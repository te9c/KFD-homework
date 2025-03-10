package com.example.lab4.db

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "username")
data class ExchangerUser (
    @Column(unique = true)
    val username: String,

    @Column
    @JsonIgnore
    val password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    val authorities: List<String>,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = [CascadeType.ALL])
    var balances: MutableList<Balance> = mutableListOf(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "username", cascade = [CascadeType.ALL])
    @JsonIgnore
    var transactions: MutableList<Transaction> = mutableListOf(),
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
}