import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

group = "ru.undframe.mission.notes.manager"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    //webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    //data
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    implementation("io.r2dbc:r2dbc-postgresql")

    //crypto
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    //JWT
    implementation("com.auth0:java-jwt:4.0.0")

    //Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")

    //for test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}