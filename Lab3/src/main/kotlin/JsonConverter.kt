package org.example

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class JsonConverter(val json : Json) {
    constructor() : this(Json.Default)

    suspend inline fun <reified T> encodeToStringList(objs : List<T>, dispatcher : CoroutineDispatcher) : List<String> = coroutineScope {
        objs.map { obj -> async(dispatcher) { json.encodeToString(obj)} }.awaitAll()
    }

    suspend inline fun <reified T> decodeFromStringList(strings : List<String>, dispatcher : CoroutineDispatcher) : List<T> = coroutineScope {
        strings.map { str -> async(dispatcher) {json.decodeFromString<T>(str) } }.awaitAll()
    }
}