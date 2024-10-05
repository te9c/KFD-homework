package org.example

class Exchanger {
    val balance : MutableMap<String, Int> = mutableMapOf(
        "RUB" to 10_000_000,
        "USD" to 1_000_000,
        "EUR" to 1_000_000,
        "USDT" to 1_000_000,
        "BTC" to 150_000_000,
    )
    val currencyFactors : Map<String, Int> = mapOf(
        "RUB" to 100,
        "USD" to 100,
        "EUR" to 100,
        "USDT" to 100,
        "BTC" to 100_000_000,
    )
    val rates = Rates()

    // TODO: refactor: make one internal function for buying/selling
    fun buy(user : User, pair : Pair<String, String>, amount : Int) : ExchangeStatus {
        val rate = rates.pairs[pair] ?: return ExchangeStatus.INVALID_PAIR

        var userCurrency = user.balance[pair.second] ?: 0
        if (userCurrency < amount * rate) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_USER
        }

        var exchangerCurrency = balance[pair.first] ?: 0
        if (exchangerCurrency < amount * currencyFactors[pair.second]!!) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_EXCHANGER
        }

        balance[pair.first] = (balance[pair.first] ?: 0) - amount * currencyFactors[pair.first]!!
        user.balance[pair.second] = (user.balance[pair.second] ?: 0) - amount * rate

        balance[pair.second] = (balance[pair.second] ?: 0) + amount * rate
        user.balance[pair.first] = (user.balance[pair.first] ?: 0) +  amount * currencyFactors[pair.first]!!

        rates.updateRates()
        return ExchangeStatus.OK
    }


    fun sell(user : User, pair : Pair<String, String>, amount : Int) : ExchangeStatus {
        val rate = rates.pairs[pair] ?: return ExchangeStatus.INVALID_PAIR

        var userCurrency = user.balance[pair.first] ?: 0
        if (userCurrency < amount * currencyFactors[pair.first]!!) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_USER
        }

        var exchangerCurrency = balance[pair.second] ?: 0
        if  (exchangerCurrency < amount * rate) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_EXCHANGER
        }

        balance[pair.second] = (balance[pair.second] ?: 0) - amount * rate
        user.balance[pair.first] = (user.balance[pair.first] ?: 0) - amount * currencyFactors[pair.first]!!

        balance[pair.first] = (balance[pair.first] ?: 0) + amount * currencyFactors[pair.first]!!
        user.balance[pair.second] = (user.balance[pair.second] ?: 0) +  amount * rate

        rates.updateRates()

        return ExchangeStatus.OK
    }
}