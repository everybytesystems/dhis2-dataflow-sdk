plugins {
    kotlin("jvm") version "2.0.21"
    application
}

dependencies {
    implementation(project(":dhis2-dataflow-sdk-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(17)
}