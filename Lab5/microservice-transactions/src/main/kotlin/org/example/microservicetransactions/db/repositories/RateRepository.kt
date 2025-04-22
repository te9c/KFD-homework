package org.example.microservicetransactions.db.repositories

import org.example.microservicetransactions.db.CurrencyPair
import org.springframework.data.jpa.repository.JpaRepository

interface RateRepository : JpaRepository<CurrencyPair, Long> {
    fun findByBaseCurrency(baseCurrency: String): List<CurrencyPair>
    fun findByQuoteCurrency(quoteCurrency: String): List<CurrencyPair>
}

fun RateRepository.findByCurrencyPair(currencyPair: String): CurrencyPair? {
    val s = currencyPair.split('/')
    val base = s.getOrNull(0) ?: return null
    val quote = s.getOrNull(1) ?: return null
    return findByCurrencyPair(base, quote)
}

fun RateRepository.findByCurrencyPair(baseCurrency: String, quoteCurrency: String): CurrencyPair? {
    return findByBaseCurrency(baseCurrency.uppercase()).firstOrNull { it.quoteCurrency == quoteCurrency.uppercase() }
}