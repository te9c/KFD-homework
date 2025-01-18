package com.example.lab4.controllers

import com.example.lab4.services.RateUpdater
import com.example.lab4.db.*
import com.example.lab4.db.repositories.*
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.parameters.P
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/api/transactions")
class TransactionsController(
    private val transactionRepository: TransactionRepository,
    private val rateRepository: RateRepository,
    private val exchangerRepository: ExchangerRepository,
    private val userRepository: UserRepository,
    private val rateUpdater: RateUpdater
) {
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    fun getTransactions() : List<Transaction> = transactionRepository.findAll()

    @GetMapping("/user/{username}")
    @PreAuthorize("#u == authentication.name || hasRole('ADMIN')")
    fun getTransactions(@PathVariable @P("u") username: String): ResponseEntity<List<Transaction>> {
        val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user.transactions)
    }

    @GetMapping("/{id}")
    @PostAuthorize("!returnObject.hasBody() || returnObject.body.username == authentication.name || hasRole('ADMIN')")
    fun getTransactionById(@PathVariable @P("id") id: Long): ResponseEntity<Transaction?> {
        val transaction = transactionRepository.findById(id).getOrNull() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(transaction)
    }

    // I love kotlin coding...
    @PostMapping()
    @PreAuthorize("#tx.user == authentication.name || hasRole('ADMIN')")
    fun makeTransaction(@RequestBody @P("tx") @Valid transactionRequest: TransactionRequest): ResponseEntity<Transaction?> {
        val user = userRepository.findByUsername(transactionRequest.user) ?: return ResponseEntity.notFound().build()
        val currencyPair = rateRepository.findByCurrencyPair(transactionRequest.currencyPair) ?: return ResponseEntity.notFound().build()

        return when(transactionRequest.transactionType) {
            TransactionType.BUYING -> buy(user, currencyPair, transactionRequest.amount)
            TransactionType.SELLING -> sell(user, currencyPair, transactionRequest.amount)
            else -> ResponseEntity.badRequest().build()
        }.also {
            if (it.hasBody() && it.statusCode == HttpStatus.OK) {
                rateUpdater.updateRates()
            }
        }
    }

    private fun buy(user: ExchangerUser, currencyPair: CurrencyPair, amount : Int): ResponseEntity<Transaction?> {
        val srcUserBalance = user.balances.firstOrNull { it.currencyCode == currencyPair.quoteCurrency } ?: return ResponseEntity.badRequest().build()
        val srcExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.baseCurrency) ?: return ResponseEntity.badRequest().build()

        val userExpense = amount * currencyPair.rate
        val exchangerExpense = amount * currencyPair.factor

        if (userExpense > srcUserBalance.amount || exchangerExpense > srcExchangerBalance.amount) {
            return ResponseEntity.badRequest().build()
        }

        var dstUserBalance = user.balances.firstOrNull { it.currencyCode == currencyPair.baseCurrency }
        var dstExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.quoteCurrency)

        if (dstUserBalance == null) {
            dstUserBalance = Balance(currencyPair.baseCurrency, 0, user)
            user.balances.add(dstUserBalance)
        }
        if (dstExchangerBalance == null) {
            dstExchangerBalance = exchangerRepository.save(ExchangerBalance(currencyPair.quoteCurrency, 0))
        }

        srcUserBalance.amount -= userExpense
        srcExchangerBalance.amount -= exchangerExpense
        dstUserBalance.amount += exchangerExpense
        dstExchangerBalance.amount += userExpense

        userRepository.save(user)
        exchangerRepository.save(srcExchangerBalance)
        exchangerRepository.save(dstExchangerBalance)

        val ret = transactionRepository.save(Transaction(user, currencyPair.toString(), currencyPair.rate, exchangerExpense, -userExpense))
        return ResponseEntity.ok(ret)
    }

    private fun sell(user: ExchangerUser, currencyPair: CurrencyPair, amount: Int): ResponseEntity<Transaction?> {
        val srcUserBalance = user.balances.firstOrNull { it.currencyCode == currencyPair.baseCurrency } ?: return ResponseEntity.badRequest().build()
        val srcExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.quoteCurrency) ?: return ResponseEntity.badRequest().build()

        val userExpense = amount * currencyPair.factor
        val exchangerExpense = amount * currencyPair.rate

        if (userExpense > srcUserBalance.amount || exchangerExpense > srcExchangerBalance.amount) {
            return ResponseEntity.badRequest().build()
        }

        var dstUserBalance = user.balances.firstOrNull { it.currencyCode == currencyPair.quoteCurrency }
        var dstExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.baseCurrency)

        if (dstUserBalance == null) {
            dstUserBalance = Balance(currencyPair.quoteCurrency, 0, user)
            user.balances.add(dstUserBalance)
        }
        if (dstExchangerBalance == null) {
            dstExchangerBalance = exchangerRepository.save(ExchangerBalance(currencyPair.baseCurrency, 0))
        }

        srcUserBalance.amount -= userExpense
        srcExchangerBalance.amount -= exchangerExpense
        dstUserBalance.amount += exchangerExpense
        dstExchangerBalance.amount += userExpense

        userRepository.save(user)
        exchangerRepository.save(srcExchangerBalance)
        exchangerRepository.save(dstExchangerBalance)

        val ret = transactionRepository.save(Transaction(user, currencyPair.toString(), currencyPair.rate, -userExpense, exchangerExpense))
        return ResponseEntity.ok(ret)
    }
}

class TransactionRequest(
    @field:NotBlank
    val user: String,
    @field:NotBlank
    @field:Pattern(regexp = "^\\w+/\\w+\$")
    val currencyPair: String,
    @field:NotNull
    val transactionType: TransactionType,
    @field:NotNull
    @field:Positive
    val amount: Int
)

enum class TransactionType(@get:JsonValue val type: String) {
    SELLING("selling"),
    BUYING("buying")
}