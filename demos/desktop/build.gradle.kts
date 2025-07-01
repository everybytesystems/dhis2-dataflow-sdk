plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    application
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    
    sourceSets {
        val jvmMain by getting {
            dependencies {
                // EBSCore SDK
                implementation(project(":modules:core"))
                implementation(project(":modules:auth"))
                implementation(project(":modules:metadata"))
                implementation(project(":modules:data"))
                implementation(project(":modules:analytics"))
                
                // Compose Desktop
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
            }
        }
    }
}

application {
    mainClass.set("com.everybytesystems.ebscore.demo.desktop.MainKt")
}

compose.desktop {
    application {
        mainClass = "com.everybytesystems.ebscore.demo.desktop.MainKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "EBSCore SDK Desktop Demo"
            packageVersion = "1.0.0"
            description = "Desktop demo application for EBSCore SDK"
            copyright = "© 2024 EveryByte Systems. All rights reserved."
            vendor = "EveryByte Systems"
            
            windows {
                menuGroup = "EBSCore SDK"
                upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
            }
            
            macOS {
                bundleID = "com.everybytesystems.ebscore.demo.desktop"
            }
            
            linux {
                packageName = "ebscore-sdk-desktop-demo"
            }
        }
    }
}