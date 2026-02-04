plugins {
    kotlin("jvm") version "1.9.22"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
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

    // Database (H2 for demo, can be replaced with PostgreSQL)
    implementation("com.h2database:h2:2.2.224")
    implementation("io.dropwizard:dropwizard-jdbi3:4.0.6")

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

    shadowJar {
        archiveBaseName.set("coroutine-sample")
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
        mergeServiceFiles()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
