package org.example

open class Event()

class ErrorEvent(val message: String, val errorCode: Int, val exception : Exception? = null) : Event()
class UserLoggedEvent(val userName : String) : Event()
class ApiRequestEvent(val endpoint: String, val apiKey: String) : Event()