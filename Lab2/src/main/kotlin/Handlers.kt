package org.example


interface EventHandler<in T : Event> {
    fun handle(event: T) : String
}

object ErrorEventHandler : EventHandler<ErrorEvent> {
    override fun handle(event: ErrorEvent) : String {
        return """
            Error message: ${event.message}
            Error code: ${event.errorCode}
            """.trimIndent()
    }
}

object UserLoggedHandler : EventHandler<UserLoggedEvent> {
    override fun handle(event: UserLoggedEvent) : String {
        return """
            User logged!
            User name: ${event.userName}
        """.trimIndent()
    }
}

object ApiRequestHandler : EventHandler<ApiRequestEvent> {
    override fun handle(event : ApiRequestEvent) : String{
        return """
            API request received!
            API Endpoint: ${event.endpoint}
            API Key: ${event.apiKey}
        """.trimIndent()
    }
}