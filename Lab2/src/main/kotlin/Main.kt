package org.example

fun errorEventInputHandler(logger : Logger) {
    println("Enter message:")
    val message = readln()
    println("Enter error code:")
    val errorCode = readln().toIntOrNull()
    if (errorCode != null) {
        logger.log(ErrorEvent(message, errorCode))
    }
}

fun userLoggedInputHandler(logger: Logger) {
    println("Enter user name:")
    val userName = readln()
    logger.log(UserLoggedEvent(userName))
}

fun apiRequestEventInputHandler(logger: Logger) {
    println("Enter endpoint:")
    val endpoint = readln()
    println("Enter API key")
    val apikey = readln()
    logger.log(ApiRequestEvent(endpoint, apikey))
}

fun main() {
    val logger = Logger()

    println("Welcome to event panel!")
    println("""Available options:
            |1. Invoke error event
            |2. Invoke user logging event
            |3. Invoke api request
            |4. Exit
        """.trimMargin())
    while (true) {
        print("-> ")

        when (readln()) {
            "1" -> errorEventInputHandler(logger)
            "2" -> userLoggedInputHandler(logger)
            "3" -> apiRequestEventInputHandler(logger)
            "4" -> break
        }
    }
}