package org.example

class Exchanger {
    val exchangerBalance : MutableMap<String, Currency> = mutableMapOf(
        "USD" to Currency(100, 100)
    )
    val rates = Rates()

    fun buy(user : User, pair : Pair<String, String>, amount : Int) : ExchangeStatus {
        val rate = rates.pairs[pair] ?: return ExchangeStatus.INVALID_PAIR

        var userCurrencyAmount = user.balance[pair.second]
        if (userCurrencyAmount == null || userCurrencyAmount.amount < amount * rate) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_USER
        }

        var exchangerCurrencyAmount = exchangerBalance[pair.first]
        if (exchangerCurrencyAmount == null || exchangerCurrencyAmount.amount < amount) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_EXCHANGER
        }

        exchangerBalance[pair.first]!!.amount -= amount
        user.balance[pair.second]!!.amount -= amount * rate

        exchangerBalance[pair.second]!!.amount += amount * rate
        user.balance[pair.first]!!.amount += amount

        rates.updateRates()

        return ExchangeStatus.OK
    }


    fun sell(user : User, pair : Pair<String, String>, amount : Int) : ExchangeStatus {
        val rate = rates.pairs[pair] ?: return ExchangeStatus.INVALID_PAIR

        var userCurrencyAmount = user.balance[pair.first]
        if (userCurrencyAmount == null || userCurrencyAmount.amount < amount) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_USER
        }

        var exchangerCurrencyAmount = exchangerBalance[pair.second]
        if (exchangerCurrencyAmount == null || exchangerCurrencyAmount.amount < amount * rate) {
            return ExchangeStatus.INSUFFICIENT_FUNDS_EXCHANGER
        }

        exchangerBalance[pair.second]!!.amount -= amount * rate
        user.balance[pair.first]!!.amount -= amount

        exchangerBalance[pair.first]!!.amount += amount
        user.balance[pair.second]!!.amount += amount * rate

        rates.updateRates()

        return ExchangeStatus.OK
    }
}