plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {
    implementation(project(":domain"))
    implementation("org.hibernate:hibernate-core:6.5.0.Final")
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    testImplementation(project(":utilities"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // If you need these specifically for the database testing
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation("org.dbunit:dbunit:2.7.3")
}

tasks.test {
    useJUnitPlatform()
}