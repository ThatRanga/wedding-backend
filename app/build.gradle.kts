/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("wedding.backend.kotlin-application-conventions")
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.serialization") version "1.8.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("aws.sdk.kotlin:kms-jvm:0.21.3-beta")
    implementation("aws.sdk.kotlin:dynamodb:0.21.5-beta")
    implementation("aws.smithy.kotlin:http-client-engine-okhttp-jvm:0.16.6")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("aws.sdk.kotlin:sqs:0.29.1-beta")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
    // Define the main class for the application.
    mainClass.set("wedding.backend.app.ApplicationKt")
}