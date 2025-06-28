plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    // Temporarily disable SQLDelight to avoid compilation issues
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
                implementation(project(":dhis2-dataflow-sdk-core"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ktor.client.core)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Android-specific dependencies
            }
        }
        
        val jvmMain by getting {
            dependencies {
                // JVM-specific dependencies
            }
        }
        
        // iOS source sets commented out for now
        /*
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                // iOS-specific dependencies
            }
        }
        */
    }
}

android {
    namespace = "com.everybytesystems.dataflow.metadata"
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
//         create("MetadataDatabase") {
//             packageName.set("com.everybytesystems.dataflow.metadata.database")
//         }
//     }
// }

// Simple publishing configuration for JitPack
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            
            groupId = "com.everybytesystems"
            artifactId = "dhis2-dataflow-sdk-metadata"
            version = "1.0.0"
        }
    }
}
