plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.compose.multiplatform) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}