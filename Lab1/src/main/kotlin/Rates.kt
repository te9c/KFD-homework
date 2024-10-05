package org.example

import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random

class Rates(
    // Uppercase or lowercase?
    // Let it be uppercase for now...
    val pairs : MutableMap<Pair<String, String>, Int> = mutableMapOf(
        Pair("USD", "RUB") to 9561,
        Pair("EUR", "RUB") to 10500,
        Pair("USD", "EUR") to 91,
        Pair("USD", "USDT") to 100,
        Pair("USD", "BTC") to 1600,
    )
) {

    fun updateRates() {
        for ((k, v) in pairs) {
            val minValue = max(ceil(v.toDouble() * (1.toDouble() + MIN_CHANGE_PERCENT)).toInt(), 1)
            val maxValue = (v.toDouble() * (1.toDouble() + MAX_CHANGE_PERCENT)).toInt()

            pairs[k] = Random.Default.nextInt(minValue, maxValue + 1)
        }
    }

    companion object {
        const val MAX_CHANGE_PERCENT = 0.05
        const val MIN_CHANGE_PERCENT = -0.05
    }
}