package org.example

import java.text.NumberFormat
import java.util.Locale
import kotlin.system.exitProcess

val exchanger = Exchanger()
val user = User()

fun main() {
    val commands = arrayOf(
        Command("balance", ::printBalance),
        Command("help", ::printHelp),
        Command("exit", { _ -> exitProcess(0) }),
        Command("rates", ::printRates),
        Command("sell", ::sell),
        Command("buy", ::buy)
    )

    while (true) {
        print("-> ")
        val input = readln().split(' ').filter { it.isNotBlank() }
        if (input.isNotEmpty()) {
            val command = commands.firstOrNull({it.name == input[0]})
            if (command == null) {
                println("Invalid command: ${input[0]}")
            } else {
                command.callback(input)
            }
        }
    }
}

fun formatCurrency(num : Int, factor : Int) : String {
    val format = NumberFormat.getNumberInstance(Locale.US)
    format.minimumFractionDigits = 2
    format.maximumFractionDigits = 12

    return format.format(num.toDouble() / factor)
}

fun printHelp(args : Collection<String>) {
    println("""Currency exchange (KFD test assignment)
Available commands:

balance - show your and exchanger's current balance
rates - show exchange rates and exchanger balance
buy <Pair> <Amount> - buy specified pair
sell <Pair> <Amount> - sell specified pair
help - show this message
exit - exit this shell""")
}

fun printBalance(args : Collection<String>) {
    println("User balance:")
    for (cur in user.balance) {
        println("${cur.key} : ${formatCurrency(cur.value, exchanger.currencyFactors[cur.key]!!)}")
    }

    println()
    println("Exchanger balance:")
    for (cur in exchanger.balance) {
        println("${cur.key} : ${formatCurrency(cur.value, exchanger.currencyFactors[cur.key]!!)}")
    }
}

fun printRates(args : Collection<String>) {
    println("Current rates:")
    for (rate in exchanger.rates.pairs) {
        println("${rate.key.first}/${rate.key.second} : ${formatCurrency(rate.value, exchanger.currencyFactors[rate.key.second]!!)}")
    }
}

fun processArgsForBuyAndSell(args : Collection<String>) : Pair<Pair<String, String>, Int>? {
    val pair = args.elementAt(1).split('/')
    if (pair.count() != 2) {
        println("Invalid pair format: ${args.elementAt(1)}")
        return null
    }

    val value = args.elementAt(2).toIntOrNull()
    if (value == null) {
        println("Invalid number format: ${args.elementAt(2)}")
        return null
    }

    return Pair(Pair(pair[0].uppercase(), pair[1].uppercase()), value)
}

fun processExchangeStatus(result : ExchangeStatus) {
    when (result) {
        ExchangeStatus.INSUFFICIENT_FUNDS_EXCHANGER -> println("Exchanger has insufficient funds.")
        ExchangeStatus.INVALID_PAIR -> println("Invalid pair.")
        ExchangeStatus.INSUFFICIENT_FUNDS_USER -> println("User has insufficient funds.")
        ExchangeStatus.OK -> println("Done.")
        else -> Unit
    }
}

fun buy(args : Collection<String>) {
    if (args.count() != 3) {
        println("Buy takes two arguments!")
        return
    }

    val argsPair = processArgsForBuyAndSell(args)
    if (argsPair == null) {
        return
    }

    val result = exchanger.buy(user, argsPair.first, argsPair.second)
    processExchangeStatus(result)
}
fun sell(args : Collection<String>) {
    if (args.count() != 3) {
        println("Sell takes two arguments!")
        return
    }

    val argsPair = processArgsForBuyAndSell(args)
    if (argsPair == null) {
        return
    }

    val result = exchanger.sell(user, argsPair.first, argsPair.second)
    processExchangeStatus(result)
}

class Command(
    val name: String,
    val callback: (Collection<String>) -> Unit
)