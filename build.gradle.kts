import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
}

group = "com.gaiaspace"
version = "0.1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // Core Spring Boot Dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    
    // Kotlin Support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    
    // Discord API
    implementation("com.discord4j:discord4j-core:3.2.5")
    
    // Git Integration
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.7.0.202309050650-r")
    
    // GraphQL Support
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    
    // Dev Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}