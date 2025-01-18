package com.example.lab4

import com.example.lab4.db.CurrencyPair
import com.example.lab4.db.repositories.RateRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class RateUpdater(
    private val rateRepository: RateRepository
) {
    public val MinChangePercent: Double = 0.05
    public val MaxChangePercent: Double = 0.05
    public fun updateRates(minChangePercent: Double = MinChangePercent, maxChangePercent: Double = MaxChangePercent): List<CurrencyPair> {
        val rates = rateRepository.findAll().onEach {
            val minChange = Math.ceil(it.rate * (1.00 - MinChangePercent)).toInt()
            val maxChange = Math.ceil(it.rate * (1.00 + MaxChangePercent)).toInt()
            it.rate = Random.nextInt(minChange, maxChange)
        }
        return rateRepository.saveAll(rates)
    }
}