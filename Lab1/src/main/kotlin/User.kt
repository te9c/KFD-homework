package org.example

class User {
    var balance : MutableMap<String, Currency> = mutableMapOf(
        "USD" to Currency(100, 100)
    )
}