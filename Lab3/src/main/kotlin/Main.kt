package org.example

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime

@Serializable
data class Person(
    val name : String,
    val age : Int,
    val manyData : List<String>
)

fun getRandomString(length: Int): String {
    val chars = List(length, { 'a' + Random.nextInt(0, 'z' - 'a') })
    return chars.joinToString("")
}

suspend fun benchmarkEncode(amountOfTestData : Int, encode : suspend (List<Person>) -> List<String>) : Duration = coroutineScope {
    val testData = List(amountOfTestData, { Person(getRandomString(5), Random.nextInt(20, 100), List(100) { getRandomString(10) }) })
    val timeTaken = measureTime {
        encode(testData)
    }

    timeTaken
}
suspend fun benchmarkDecode(amountOfTestData: Int, decode : suspend (List<String>) -> List<Person>, generator : (Person) -> String) : Duration = coroutineScope {
    val testData = List(amountOfTestData, { Person(getRandomString(5), Random.nextInt(20, 100), List(100) { getRandomString(10)}) }).map {
        generator(it)
    }

    val timeTaken = measureTime {
        decode(testData)
    }

    timeTaken
}

fun main(args : Array<String>) = runBlocking {
    if (args.size != 3) {
        println("ERROR!")
        println("Enter amount of test data and amount of tests")
        return@runBlocking
    }

    val dataSet = args[1].toIntOrNull()
    val amountOfTests = args[2].toIntOrNull()
    if (dataSet == null) {
        println("ERROR!")
        println("Can't parse ${args[1]} to integer")
        return@runBlocking
    }
    if (amountOfTests == null) {
        println("ERROR!")
        println("Can't parse ${args[2]} to integer")
        return@runBlocking
    }

    println("Testing synchronous json serialization")
    var total : Duration = Duration.ZERO
    repeat (amountOfTests) {
        total += benchmarkEncode(dataSet) { persons -> persons.map { Json.encodeToString(it) } }
    }
    println("dataSet: $dataSet")
    println("amount of tests: $amountOfTests")
    println("AVG: ${total / amountOfTests}")

    println()

    println("Testing synchronous json deserialization")
    total = Duration.ZERO
    repeat (amountOfTests) {
        total += benchmarkDecode(dataSet,
            { persons -> persons.map { Json.decodeFromString(it) } },
            { Json.encodeToString(it) })
    }
    println("dataSet: $dataSet")
    println("amount of tests: $amountOfTests")
    println("AVG: ${total / amountOfTests}")


    println()
    println()

    val dispatcher = Executors.newFixedThreadPool(8).asCoroutineDispatcher()
    val jsonConverter = JsonConverter()

    println("Testing asynchronous json serialization")
    total = Duration.ZERO
    repeat (amountOfTests) {
        total += benchmarkEncode(dataSet) { persons -> jsonConverter.encodeToStringList(persons, dispatcher) }
    }
    println("dataSet: $dataSet")
    println("amount of tests: $amountOfTests")
    println("AVG: ${total / amountOfTests}")

    println()

    println("Testing asynchronous json deserialization")
    total = Duration.ZERO
    repeat (amountOfTests) {
        total += benchmarkDecode(dataSet,
            { persons -> jsonConverter.decodeFromStringList(persons, dispatcher) },
            { Json.encodeToString(it) })
    }
    println("dataSet: $dataSet")
    println("amount of tests: $amountOfTests")
    println("AVG: ${total / amountOfTests}")

    dispatcher.close()

    println()
    println()

    println("Testing cbor serialization")
    total = Duration.ZERO
    repeat (amountOfTests) {
        total += benchmarkEncode(dataSet) { persons -> persons.map { Cbor.encodeToHexString(it) } }
    }
    println("dataSet: $dataSet")
    println("amount of tests: $amountOfTests")
    println("AVG: ${total / amountOfTests}")

    println()

    println("Testing cbor deserialization")
    total = Duration.ZERO
    repeat (amountOfTests) {
        total += benchmarkDecode(dataSet, { persons -> persons.map { Cbor.decodeFromHexString(it) } }, { Cbor.encodeToHexString(it) })
    }
    println("dataSet: $dataSet")
    println("amount of tests: $amountOfTests")
    println("AVG: ${total / amountOfTests}")
}
