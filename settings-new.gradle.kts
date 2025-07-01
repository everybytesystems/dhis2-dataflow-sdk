pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "ebscore-sdk"

// Core modules
include(":core")
include(":core:api")
include(":core:auth")
include(":core:cache")
include(":core:config")
include(":core:error")
include(":core:utils")

// Data modules
include(":data")
include(":data:models")
include(":data:schema")
include(":data:mapper")
include(":data:validators")

// Integration modules
include(":integrations")
include(":integrations:dhis2")
include(":integrations:fhir")
include(":integrations:openmrs")
include(":integrations:redcap")

// Sync modules
include(":sync")
include(":sync:jobs")
include(":sync:engine")
include(":sync:tracker")

// Forms modules
include(":forms")
include(":forms:builder")
include(":forms:fields")
include(":forms:logic")
include(":forms:renderer")

// Analytics modules
include(":analytics")
include(":analytics:builder")
include(":analytics:visualization")
include(":analytics:exporter")

// Identity modules
include(":identity")
include(":identity:users")
include(":identity:orgs")
include(":identity:roles")
include(":identity:subscriptions")

// Billing modules
include(":billing")
include(":billing:plans")
include(":billing:payments")
include(":billing:invoices")
include(":billing:subscriptions")
include(":billing:discounts")

// Telemetry modules
include(":telemetry")
include(":telemetry:logger")
include(":telemetry:metrics")
include(":telemetry:events")

// CLI modules
include(":cli")
include(":cli:generate")
include(":cli:sync")
include(":cli:billing")

// Demo applications
include(":demos:android")
include(":demos:desktop")
include(":demos:web")

// Examples
include(":examples:minimal")
include(":examples:integration")

// Tests
include(":tests:unit")
include(":tests:integration")
include(":tests:e2e")

// Module directory mappings
project(":core").projectDir = file("core")
project(":core:api").projectDir = file("core/api")
project(":core:auth").projectDir = file("core/auth")
project(":core:cache").projectDir = file("core/cache")
project(":core:config").projectDir = file("core/config")
project(":core:error").projectDir = file("core/error")
project(":core:utils").projectDir = file("core/utils")

project(":data").projectDir = file("data")
project(":data:models").projectDir = file("data/models")
project(":data:schema").projectDir = file("data/schema")
project(":data:mapper").projectDir = file("data/mapper")
project(":data:validators").projectDir = file("data/validators")

project(":integrations").projectDir = file("integrations")
project(":integrations:dhis2").projectDir = file("integrations/dhis2")
project(":integrations:fhir").projectDir = file("integrations/fhir")
project(":integrations:openmrs").projectDir = file("integrations/openmrs")
project(":integrations:redcap").projectDir = file("integrations/redcap")

project(":sync").projectDir = file("sync")
project(":sync:jobs").projectDir = file("sync/jobs")
project(":sync:engine").projectDir = file("sync/engine")
project(":sync:tracker").projectDir = file("sync/tracker")

project(":forms").projectDir = file("forms")
project(":forms:builder").projectDir = file("forms/builder")
project(":forms:fields").projectDir = file("forms/fields")
project(":forms:logic").projectDir = file("forms/logic")
project(":forms:renderer").projectDir = file("forms/renderer")

project(":analytics").projectDir = file("analytics")
project(":analytics:builder").projectDir = file("analytics/builder")
project(":analytics:visualization").projectDir = file("analytics/visualization")
project(":analytics:exporter").projectDir = file("analytics/exporter")

project(":identity").projectDir = file("identity")
project(":identity:users").projectDir = file("identity/users")
project(":identity:orgs").projectDir = file("identity/orgs")
project(":identity:roles").projectDir = file("identity/roles")
project(":identity:subscriptions").projectDir = file("identity/subscriptions")

project(":billing").projectDir = file("billing")
project(":billing:plans").projectDir = file("billing/plans")
project(":billing:payments").projectDir = file("billing/payments")
project(":billing:invoices").projectDir = file("billing/invoices")
project(":billing:subscriptions").projectDir = file("billing/subscriptions")
project(":billing:discounts").projectDir = file("billing/discounts")

project(":telemetry").projectDir = file("telemetry")
project(":telemetry:logger").projectDir = file("telemetry/logger")
project(":telemetry:metrics").projectDir = file("telemetry/metrics")
project(":telemetry:events").projectDir = file("telemetry/events")

project(":cli").projectDir = file("cli")
project(":cli:generate").projectDir = file("cli/generate")
project(":cli:sync").projectDir = file("cli/sync")
project(":cli:billing").projectDir = file("cli/billing")

project(":demos:android").projectDir = file("demos/android")
project(":demos:desktop").projectDir = file("demos/desktop")
project(":demos:web").projectDir = file("demos/web")

project(":examples:minimal").projectDir = file("examples/minimal")
project(":examples:integration").projectDir = file("examples/integration")

project(":tests:unit").projectDir = file("tests/unit")
project(":tests:integration").projectDir = file("tests/integration")
project(":tests:e2e").projectDir = file("tests/e2e")