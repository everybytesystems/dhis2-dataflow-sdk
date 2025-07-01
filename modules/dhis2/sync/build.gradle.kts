plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm("desktop")
    
    js(IR) {
        browser()
        nodejs()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(project(":ebscore-core"))
            implementation(project(":ebscore-network"))
            implementation(project(":ebscore-storage"))
            implementation(project(":ebscore-dhis2-metadata"))
            implementation(project(":ebscore-dhis2-data"))
            
            // Serialization
            implementation(libs.kotlinx.serialization.json)
            
            // Coroutines
            implementation(libs.kotlinx.coroutines.core)
            
            // DateTime
            implementation(libs.kotlinx.datetime)
        }
        
        androidMain.dependencies {
            // Android-specific dependencies if needed
        }
        
        iosMain.dependencies {
            // iOS-specific dependencies if needed
        }
        
        val desktopMain by getting {
            dependencies {
                // Desktop-specific dependencies if needed
            }
        }
        
        jsMain.dependencies {
            // JS-specific dependencies if needed
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.everybytesystems.ebscore.dhis2.sync"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {
    coordinates(
        groupId = "com.everybytesystems.ebscore",
        artifactId = "dhis2-sync",
        version = "1.0.0"
    )
    
    pom {
        name.set("EBSCore DHIS2 Sync")
        description.set("DHIS2 synchronization module for EBSCore SDK")
    }
}