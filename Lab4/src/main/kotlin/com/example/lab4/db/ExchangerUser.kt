package com.example.lab4.db

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class ExchangerUser (
    @Column(unique = true)
    val username: String,

    @Column
    @JsonIgnore
    val password: String,

    @ElementCollection(fetch = FetchType.EAGER)
    val authorities: List<String>,

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = [CascadeType.ALL])
    var balances: MutableList<Balance> = mutableListOf(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sender", cascade = [CascadeType.ALL])
    var outcomingTransactions: MutableList<Transaction> = mutableListOf(),

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "receiver", cascade = [CascadeType.ALL])
    var incomingTransactions: MutableList<Transaction> = mutableListOf()
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null

    @JsonIgnore
    fun getTransactions() : List<Transaction> = outcomingTransactions + incomingTransactions
}