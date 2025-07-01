plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":ebscore-core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("ch.qos.logback:logback-classic:1.4.11")
}

application {
    mainClass.set("com.everybytesystems.dataflow.examples.enhanced.EnhancedApisExampleKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}