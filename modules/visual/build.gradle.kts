plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

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
                implementation(project(":dhis2-dataflow-sdk-data"))
                implementation(project(":dhis2-dataflow-sdk-core"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
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
    namespace = "com.everybytesystems.dataflow.visual"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString()
    )
    
    pom {
        name.set("DHIS2 DataFlow SDK Visual")
        description.set("Visualization and AI Insight services")
        url.set("https://github.com/everybytesystems/dhis2-dataflow-sdk")
        
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        
        developers {
            developer {
                id.set("everybytesystems")
                name.set("EVERYBYTE SYSTEMS")
                email.set("socaya@everybytesystems.com")
            }
        }
        
        scm {
            url.set("https://github.com/everybytesystems/dhis2-dataflow-sdk")
            connection.set("scm:git:git@github.com:everybytesystems/dhis2-dataflow-sdk.git")
            developerConnection.set("scm:git:git@github.com:everybytesystems/dhis2-dataflow-sdk.git")
        }
    }
    
    publishToMavenCentral()
    signAllPublications()
}
