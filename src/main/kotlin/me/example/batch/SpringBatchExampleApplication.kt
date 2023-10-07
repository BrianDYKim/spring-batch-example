package me.example.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBatchExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringBatchExampleApplication>(*args)
}
