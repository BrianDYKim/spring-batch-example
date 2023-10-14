package me.example.chapter3.jpaReader.model

import jakarta.persistence.*

/**
 * @author Doyeop Kim
 * @since 2023/10/08
 */
@Entity
@Table(name = "people")
class Person(
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @get:Column(name = "name", nullable = false)
    var name: String = "",
    @get:Column(name = "age", nullable = false)
    var age: String = "",
    @get:Column(name = "address", nullable = false)
    var address: String = "",
) {
    // Secondary constructor
    constructor(name: String, age: String, address: String) : this(0L, name, age, address)
}