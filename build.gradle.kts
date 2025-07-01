plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

// EBSCore SDK Configuration
group = "com.everybytesystems.ebscore"
version = "1.0.0"

allprojects {
    group = "com.everybytesystems.ebscore"
    version = "1.0.0"
    
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // For additional dependencies
    }
}