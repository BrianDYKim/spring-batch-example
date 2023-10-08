package me.example.chapter3

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class Chapter3Application

fun main(args: Array<String>) {
    runApplication<Chapter3Application>(*args)
}
