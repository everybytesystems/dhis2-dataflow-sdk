plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

// Publishing is configured via vanniktech.mavenPublish plugin below

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    // Temporarily disable iOS targets due to Xcode configuration issues
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    js(IR) {
        browser()
        nodejs()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies { 
                implementation(project(":ebscore-core"))
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.security.crypto)
                implementation(libs.androidx.biometric)
                implementation("androidx.lifecycle:lifecycle-process:2.7.0")
                implementation("androidx.fragment:fragment-ktx:1.6.2")
            }
        }
        
        // Temporarily disable JVM source set
        // val desktopMain by getting {
        //     dependencies {
        //         // JVM-specific dependencies if needed
        //     }
        // }
        
        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Native-specific dependencies if needed
            }
        }
        
        val iosX64Main by getting {
            dependsOn(nativeMain)
        }
        
        val iosArm64Main by getting {
            dependsOn(nativeMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(nativeMain)
        }
        
        val jsMain by getting {
            dependencies {
                // JS-specific dependencies if needed
            }
        }
    }
}

android {
    namespace = "com.everybytesystems.ebscore.auth"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

