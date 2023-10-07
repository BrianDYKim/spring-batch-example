package me.example.chapter1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Chapter1Application

fun main(args: Array<String>) {
    runApplication<Chapter1Application>(*args)
}
