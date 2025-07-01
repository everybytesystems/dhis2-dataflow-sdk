pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ebscore-sdk"

include(":ebscore-core")

project(":ebscore-core").projectDir = file("modules/core")