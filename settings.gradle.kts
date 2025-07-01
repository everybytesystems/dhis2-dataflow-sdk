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

// Core Modules
include(":ebscore-core")
include(":ebscore-ui")
include(":ebscore-network")
include(":ebscore-storage")
include(":ebscore-auth")
include(":ebscore-utils")
include(":ebscore-sdk")

// Generic Modules
include(":ebscore-sync")

// DHIS2 Integration Module and Sub-modules
include(":ebscore-dhis2")
include(":ebscore-dhis2-metadata")
include(":ebscore-dhis2-data")
include(":ebscore-dhis2-analytics")
include(":ebscore-dhis2-sync")

// Demo Applications
include(":demos:android")
include(":demos:desktop")
include(":demos:web")

// Module Directory Mappings
project(":ebscore-core").projectDir = file("modules/core")
project(":ebscore-ui").projectDir = file("modules/ui")
project(":ebscore-network").projectDir = file("modules/network")
project(":ebscore-storage").projectDir = file("modules/storage")
project(":ebscore-auth").projectDir = file("modules/auth")
project(":ebscore-utils").projectDir = file("modules/utils")
project(":ebscore-sdk").projectDir = file("modules/sdk")

// Generic Module Directory Mappings
project(":ebscore-sync").projectDir = file("modules/sync")

// DHIS2 Module Directory Mappings
project(":ebscore-dhis2").projectDir = file("modules/dhis2")
project(":ebscore-dhis2-metadata").projectDir = file("modules/dhis2/metadata")
project(":ebscore-dhis2-data").projectDir = file("modules/dhis2/data")
project(":ebscore-dhis2-analytics").projectDir = file("modules/dhis2/analytics")
project(":ebscore-dhis2-sync").projectDir = file("modules/dhis2/sync")

// Demo Applications Directory Mappings
project(":demos:android").projectDir = file("demos/android")
project(":demos:desktop").projectDir = file("demos/desktop")
project(":demos:web").projectDir = file("demos/web")