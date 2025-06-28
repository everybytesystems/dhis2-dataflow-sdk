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
include(":dhis2-dataflow-sdk-auth")
include(":dhis2-dataflow-sdk-data")
include(":dhis2-dataflow-sdk-metadata")
include(":dhis2-dataflow-sdk-visual")
// include(":dhis2-dataflow-sdk")  // Disabled for now - has compilation issues

// Examples
// include(":examples:enhanced-apis")  // Disabled for now - has compilation issues

project(":dhis2-dataflow-sdk-core").projectDir = file("modules/core")
project(":dhis2-dataflow-sdk-auth").projectDir = file("modules/auth")
project(":dhis2-dataflow-sdk-data").projectDir = file("modules/data")
project(":dhis2-dataflow-sdk-metadata").projectDir = file("modules/metadata")
project(":dhis2-dataflow-sdk-visual").projectDir = file("modules/visual")
// project(":dhis2-dataflow-sdk").projectDir = file("modules/sdk")  // Disabled

// project(":examples:enhanced-apis").projectDir = file("examples/enhanced-apis")  // Disabled