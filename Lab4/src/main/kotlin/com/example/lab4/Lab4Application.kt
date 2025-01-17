package com.example.lab4

import com.example.lab4.db.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class Lab4Application

fun main(args: Array<String>) {
    runApplication<Lab4Application>(*args)
}
