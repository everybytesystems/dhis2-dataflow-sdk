plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    // Temporarily disable SQLDelight to avoid JVM compilation issues
    // alias(libs.plugins.sqldelight)
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
    
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    // iOS targets commented out for now due to build issues
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                // Temporarily disable SQLDelight to avoid JVM compilation issues
                // implementation("app.cash.sqldelight:runtime:2.0.2")
                // implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
                implementation("io.ktor:ktor-client-core:3.0.1")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.1")
                implementation("io.ktor:ktor-client-auth:3.0.1")
                implementation("io.ktor:ktor-client-logging:3.0.1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.1")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Temporarily disable SQLDelight to avoid JVM compilation issues
                // implementation("app.cash.sqldelight:android-driver:2.0.2")
                implementation("io.ktor:ktor-client-okhttp:3.0.1")
            }
        }
        
        val jvmMain by getting {
            dependencies {
                // Temporarily exclude SQLDelight for JVM to avoid compiler issues
                // implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
                implementation("io.ktor:ktor-client-okhttp:3.0.1")
            }
        }
        
        // iOS source sets commented out for now
        /*
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Temporarily disable SQLDelight to avoid JVM compilation issues
                // implementation("app.cash.sqldelight:native-driver:2.0.2")
                implementation("io.ktor:ktor-client-darwin:3.0.1")
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
        */
    }
}

android {
    namespace = "com.everybytesystems.dataflow.core"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Temporarily disable SQLDelight configuration
// sqldelight {
//     databases {
//         create("DHIS2Database") {
//             packageName.set("com.everybytesystems.dataflow.core.database")
//         }
//     }
// }

// Simple publishing configuration for JitPack
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            
            groupId = "com.everybytesystems"
            artifactId = "dhis2-dataflow-sdk-core"
            version = "1.0.0"
        }
    }
}