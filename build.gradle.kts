plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "net.research.kt.coroutine"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // DropWizard
    implementation("io.dropwizard:dropwizard-core:4.0.6")
    implementation("io.dropwizard:dropwizard-json-logging:4.0.6")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.8.0")

    // Jackson Kotlin Module
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

    // Database - MySQL
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("io.dropwizard:dropwizard-jdbi3:4.0.6")
    implementation("org.jdbi:jdbi3-kotlin:3.45.1")
    implementation("org.jdbi:jdbi3-kotlin-sqlobject:3.45.1")

    // Database Migration - Flyway
    implementation("io.dropwizard:dropwizard-migrations:4.0.6")
    implementation("org.flywaydb:flyway-core:10.8.1")
    implementation("org.flywaydb:flyway-mysql:10.8.1")

    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // Testing
    testImplementation("io.dropwizard:dropwizard-testing:4.0.6")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.assertj:assertj-core:3.25.1")
    testImplementation("io.mockk:mockk:1.13.9")
}

application {
    mainClass.set("net.research.kt.coroutine.CoroutineSampleApplicationKt")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    test {
        useJUnitPlatform()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
