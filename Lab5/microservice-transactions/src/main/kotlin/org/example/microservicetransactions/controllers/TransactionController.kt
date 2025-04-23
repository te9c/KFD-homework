package org.example.microservicetransactions.controllers

import jakarta.validation.Valid
import org.example.microservicetransactions.clients.AuthServiceClient
import org.example.microservicetransactions.db.Transaction
import org.example.microservicetransactions.db.repositories.*
import org.example.microservicetransactions.dto.TransactionDto
import org.example.microservicetransactions.dto.TransactionType
import org.example.microservicetransactions.services.RateUpdater
import org.example.microservicetransactions.db.CurrencyPair
import org.example.microservicetransactions.db.Balance
import org.example.microservicetransactions.db.ExchangerBalance
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
    private val rateUpdater: RateUpdater,
    private val authServiceClient: AuthServiceClient,
    private val balanceRepository: BalanceRepository
) {
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    fun getTransactions() : List<Transaction> = transactionRepository.findAll()

    @GetMapping("/user/{username}")
    @PreAuthorize("#u == authentication.name || hasRole('ADMIN')")
    fun getTransactions(@PathVariable @P("u") username: String): ResponseEntity<List<Transaction>> {
        //val user = userRepository.findByUsername(username) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(transactionRepository.findByUsername(username))
    }

    @GetMapping("/{id}")
    @PostAuthorize("!returnObject.hasBody() || returnObject.body.username == authentication.name || hasRole('ADMIN')")
    fun getTransactionById(@PathVariable @P("id") id: Long): ResponseEntity<Transaction?> {
        val transaction = transactionRepository.findById(id).getOrNull() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(transaction)
    }

    // I love kotlin coding...
    @PostMapping()
    @PreAuthorize("#tx.username == authentication.name || hasRole('ADMIN')")
    fun makeTransaction(@RequestBody @P("tx") @Valid transactionRequest: TransactionDto): ResponseEntity<Transaction?> {
        if (authServiceClient.getUser(transactionRequest.username) == null)
            return ResponseEntity.notFound().build()
        val currencyPair = rateRepository.findByCurrencyPair(transactionRequest.currencyPair) ?: return ResponseEntity.notFound().build()

        return when(transactionRequest.transactionType) {
            TransactionType.BUYING -> buy(transactionRequest.username, currencyPair, transactionRequest.amount)
            TransactionType.SELLING -> sell(transactionRequest.username, currencyPair, transactionRequest.amount)
            else -> ResponseEntity.badRequest().build()
        }.also {
            if (it.hasBody() && it.statusCode == HttpStatus.OK) {
                rateUpdater.updateRates()
            }
        }
    }

    private fun buy(username: String, currencyPair: CurrencyPair, amount : Int): ResponseEntity<Transaction?> {
        val srcUserBalance = balanceRepository.findByCurrencyCode(currencyPair.quoteCurrency) ?: return ResponseEntity.badRequest().build()
        val srcExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.baseCurrency) ?: return ResponseEntity.badRequest().build()

        val userExpense = amount * currencyPair.rate
        val exchangerExpense = amount * currencyPair.factor

        if (userExpense > srcUserBalance.amount || exchangerExpense > srcExchangerBalance.amount) {
            return ResponseEntity.badRequest().build()
        }

        var dstUserBalance = balanceRepository.findByCurrencyCode(currencyPair.baseCurrency)
        var dstExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.quoteCurrency)

        if (dstUserBalance == null) {
            dstUserBalance = Balance(currencyPair.baseCurrency, 0, username)
        }
        if (dstExchangerBalance == null) {
            dstExchangerBalance = exchangerRepository.save(ExchangerBalance(currencyPair.quoteCurrency, 0))
        }

        srcUserBalance.amount -= userExpense
        srcExchangerBalance.amount -= exchangerExpense
        dstUserBalance.amount += exchangerExpense
        dstExchangerBalance.amount += userExpense

        balanceRepository.save(srcUserBalance)
        balanceRepository.save(dstUserBalance)
        exchangerRepository.save(srcExchangerBalance)
        exchangerRepository.save(dstExchangerBalance)

        val ret = transactionRepository.save(Transaction(username, currencyPair.toString(), currencyPair.rate, exchangerExpense, -userExpense))
        return ResponseEntity.ok(ret)
    }

    private fun sell(username: String, currencyPair: CurrencyPair, amount: Int): ResponseEntity<Transaction?> {
        val srcUserBalance = balanceRepository.findByCurrencyCode(currencyPair.baseCurrency)  ?: return ResponseEntity.badRequest().build()
        val srcExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.quoteCurrency) ?: return ResponseEntity.badRequest().build()

        val userExpense = amount * currencyPair.factor
        val exchangerExpense = amount * currencyPair.rate

        if (userExpense > srcUserBalance.amount || exchangerExpense > srcExchangerBalance.amount) {
            return ResponseEntity.badRequest().build()
        }

        var dstUserBalance = balanceRepository.findByCurrencyCode(currencyPair.quoteCurrency)
        var dstExchangerBalance = exchangerRepository.findByCurrencyCode(currencyPair.baseCurrency)

        if (dstUserBalance == null) {
            dstUserBalance = Balance(currencyPair.quoteCurrency, 0, username)
        }
        if (dstExchangerBalance == null) {
            dstExchangerBalance = exchangerRepository.save(ExchangerBalance(currencyPair.baseCurrency, 0))
        }

        srcUserBalance.amount -= userExpense
        srcExchangerBalance.amount -= exchangerExpense
        dstUserBalance.amount += exchangerExpense
        dstExchangerBalance.amount += userExpense

        balanceRepository.save(srcUserBalance)
        balanceRepository.save(dstUserBalance)
        exchangerRepository.save(srcExchangerBalance)
        exchangerRepository.save(dstExchangerBalance)

        val ret = transactionRepository.save(Transaction(username, currencyPair.toString(), currencyPair.rate, -userExpense, exchangerExpense))
        return ResponseEntity.ok(ret)
    }
}