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
}

