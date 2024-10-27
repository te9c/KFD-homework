package org.example

class Logger {
    private fun getEventString(event : Event) : String {
        return when (event) {
            is ErrorEvent -> ErrorEventHandler.handle(event)
            is UserLoggedEvent -> UserLoggedHandler.handle(event)
            is ApiRequestEvent -> ApiRequestHandler.handle(event)
            else -> throw IllegalArgumentException("Unknown event: $event")
        }
    }

    fun log(event : Event) {
        println(getEventString(event))
    }
}