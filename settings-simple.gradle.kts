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

rootProject.name = "dhis2-dataflow-sdk"

include(":dhis2-dataflow-sdk-core")

project(":dhis2-dataflow-sdk-core").projectDir = file("modules/core")