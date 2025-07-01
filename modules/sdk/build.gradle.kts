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
    
    js(IR) {
        browser()
        nodejs()
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":ebscore-core"))
                api(project(":ebscore-auth"))
                api(project(":ebscore-dhis2"))
                api(project(":ebscore-dhis2-metadata"))
                api(project(":ebscore-dhis2-data"))
                api(project(":ebscore-storage"))
                api(project(":ebscore-network"))
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
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
                // Android-specific dependencies if needed
            }
        }
        
        val jvmMain by getting {
            dependencies {
                // JVM-specific dependencies if needed
            }
        }
        
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                // iOS-specific dependencies if needed
            }
        }
    }
}

android {
    namespace = "com.everybytesystems.ebscore.sdk"
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
    pom {
        name.set("DHIS2 DataFlow SDK")
        description.set("All-in-one umbrella SDK including auth, data sync, visualization, and AI Insights")
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
}
