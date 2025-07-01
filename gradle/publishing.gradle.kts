// Publishing configuration for DHIS2 DataFlow SDK
// This file configures Maven publishing for all modules

apply(plugin = "maven-publish")
apply(plugin = "signing")

configure<PublishingExtension> {
    repositories {
        // GitHub Packages
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/everybytesystems/dhis2-dataflow-sdk")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
        
        // Maven Central via Sonatype
        maven {
            name = "SonatypeStaging"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("MAVEN_CENTRAL_USERNAME")
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
            }
        }
        
        maven {
            name = "SonatypeSnapshots"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("MAVEN_CENTRAL_USERNAME")
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("MAVEN_CENTRAL_PASSWORD")
            }
        }
    }
    
    publications.withType<MavenPublication> {
        // Customize artifact ID based on module
        artifactId = when (project.name) {
            "core" -> "ebscore-core"
            "auth" -> "ebscore-auth"
            "metadata" -> "ebscore-metadata"
            "data" -> "ebscore-data"
            "ui" -> "ebscore-ui"
            else -> project.name
        }
        
        groupId = "com.everybytesystems"
        version = project.version.toString()
        
        pom {
            name.set("DHIS2 DataFlow SDK - ${project.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }}")
            description.set("A comprehensive, type-safe, and production-ready Kotlin Multiplatform SDK for DHIS2 integration")
            url.set("https://github.com/everybytesystems/dhis2-dataflow-sdk")
            
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                    distribution.set("repo")
                }
            }
            
            developers {
                developer {
                    id.set("everybytesystems")
                    name.set("EveryByte Systems")
                    email.set("support@everybytesystems.com")
                    organization.set("EveryByte Systems")
                    organizationUrl.set("https://everybytesystems.com")
                }
            }
            
            scm {
                connection.set("scm:git:git://github.com/everybytesystems/dhis2-dataflow-sdk.git")
                developerConnection.set("scm:git:ssh://github.com:everybytesystems/dhis2-dataflow-sdk.git")
                url.set("https://github.com/everybytesystems/dhis2-dataflow-sdk/tree/main")
            }
            
            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/everybytesystems/dhis2-dataflow-sdk/issues")
            }
        }
    }
}

// Signing configuration
configure<SigningExtension> {
    val signingKey = project.findProperty("signing.key") as String? ?: System.getenv("SIGNING_SECRET_KEY")
    val signingPassword = project.findProperty("signing.password") as String? ?: System.getenv("SIGNING_PASSWORD")
    
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(extensions.getByType<PublishingExtension>().publications)
    }
}

// Only sign if we're publishing to Maven Central
tasks.withType<Sign>().configureEach {
    onlyIf {
        gradle.taskGraph.allTasks.any { it.name.contains("ToSonatype") }
    }
}