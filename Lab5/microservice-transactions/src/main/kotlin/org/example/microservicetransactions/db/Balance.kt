package org.example.microservicetransactions.db

import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "currencyCode"])])
class Balance(
    currencyCode: String = "",
    @Column
    var amount: Int = 0,
    @Column
    val username: String? = null
) {
    val currencyCode = currencyCode.uppercase()
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}