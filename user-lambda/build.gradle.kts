import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("wedding.backend.kotlin-application-conventions")
}



dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("aws.sdk.kotlin:sqs-jvm:0.16.0")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.1")
    implementation("aws.sdk.kotlin:s3:0.16.0")
}


tasks.withType<ShadowJar> {
    exclude("org.apache.tomcat.embed:*")
}

application {
    mainClass.set("wedding.backend.userlambda.UserLambdaHandler")
}