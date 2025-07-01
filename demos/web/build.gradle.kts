plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "ebscore-demo.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                // EBSCore SDK
                implementation(project(":modules:core"))
                implementation(project(":modules:auth"))
                implementation(project(":modules:metadata"))
                implementation(project(":modules:data"))
                implementation(project(":modules:analytics"))
                
                // Compose Web
                implementation(compose.html.core)
                implementation(compose.runtime)
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}