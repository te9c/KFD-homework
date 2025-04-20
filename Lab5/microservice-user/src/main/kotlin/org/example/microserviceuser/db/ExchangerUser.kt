package org.example.microserviceuser.db

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "username")
data class ExchangerUser (
    @Column(unique = true)
    val username: String = "",

    @Column
    @JsonIgnore
    val password: String = "",

    @ElementCollection(fetch = FetchType.EAGER)
    val authorities: List<String> = emptyList(),
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null
}