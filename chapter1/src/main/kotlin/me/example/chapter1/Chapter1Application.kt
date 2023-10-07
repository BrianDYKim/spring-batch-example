package me.example.chapter1

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class Chapter1Application

fun main(args: Array<String>) {
    runApplication<Chapter1Application>(*args)
}
