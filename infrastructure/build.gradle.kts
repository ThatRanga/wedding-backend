/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("wedding.backend.kotlin-application-conventions")
}

dependencies {
    implementation("software.amazon.awscdk:aws-cdk-lib:2.88.0")
}

application {
    // Define the main class for the application.
    mainClass.set("wedding.backend.infrastructure.AppKt")
}
