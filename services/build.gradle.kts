/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":domain-implementations:jpa"))
    implementation(project(":domain-implementations:jdbc"))
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")

    implementation("com.athaydes.rawhttp:rawhttp-core:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation(project(":cryptoutils"))
}

application {
    // Define the main class for the application.
    mainClass = "cat.uvic.teknos.coursemanagement.services.App"
}
