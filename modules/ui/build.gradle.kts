plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.compose.multiplatform)
    `maven-publish`
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
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    // Temporarily disable JVM target due to Kotlin compiler bug
    // jvm("desktop") {
    //     compilations.all {
    //         kotlinOptions {
    //             jvmTarget = "11"
    //         }
    //     }
    // }
    
    js(IR) {
        browser()
        nodejs()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies { 
                // Core SDK dependencies
                implementation(project(":ebscore-core"))
                implementation(project(":ebscore-auth"))
                implementation(project(":ebscore-network"))
                implementation(project(":ebscore-storage"))
                
                // Generic modules
                implementation(project(":ebscore-sync"))
                
                // DHIS2 modules (if needed for UI components)
                // implementation(project(":ebscore-dhis2"))
                
                // Kotlin dependencies
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                
                // Compose Multiplatform UI
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // Additional UI libraries for enhanced functionality
                implementation("org.jetbrains.compose.material:material-icons-extended:${libs.versions.compose.multiplatform.get()}")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Android-specific UI dependencies
                implementation("androidx.activity:activity-compose:1.9.2")
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
                implementation("androidx.navigation:navigation-compose:2.8.2")
                implementation("androidx.compose.material3:material3-window-size-class:1.3.0")
                implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
                implementation("com.google.accompanist:accompanist-permissions:0.32.0")
                implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")
            }
        }
        
        // Temporarily disable JVM source set
        // val desktopMain by getting {
        //     dependencies {
        //         // JVM-specific UI dependencies
        //         implementation(compose.desktop.currentOs)
        //         implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${libs.versions.kotlinx.coroutines.get()}")
        //     }
        // }
        
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // iOS-specific UI dependencies if needed
            }
        }
        
        val iosX64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        
        val jsMain by getting {
            dependencies {
                // JS-specific UI dependencies if needed
            }
        }
    }
}

android {
    namespace = "com.everybytesystems.ebscore.ui"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.11"
    }
}

// Simple publishing configuration for JitPack
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            
            groupId = "com.everybytesystems"
            artifactId = "ebscore-ui"
            version = "1.0.0"
        }
    }
}
