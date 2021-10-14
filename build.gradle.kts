import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") version "1.5.21"
}

val versionNumber = System.getenv("version")?.substringAfter("R-") ?: "DEV"

allprojects {
    group = "io.servock"
    version = versionNumber

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = "16"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            languageVersion = "1.5"
            apiVersion = "1.5"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        ignoreFailures = true
    }
}

dependencies {
    implementation(platform("org.http4k:http4k-bom:4.14.1.4"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")

    api("io.github.microutils:kotlin-logging:2.0.11")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    "testImplementation"("io.strikt:strikt-core:0.32.0")
}
