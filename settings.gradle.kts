rootProject.name = "batch"

pluginManagement {
    val kotlinVersion = "1.8.22"
    val springBootVersion = "3.1.4"
    val springDependencyManagementVersion = "1.1.3"

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion

        kotlin("kapt") version kotlinVersion
    }
}

include("common")

include("chapter1", "chapter3")