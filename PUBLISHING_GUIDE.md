# DHIS2 EBSCore SDK - Publishing Guide

## 📦 **Package Distribution Setup**

This guide covers publishing the DHIS2 EBSCore SDK to various package repositories for easy distribution and consumption.

---

## 🎯 **Publishing Options Overview**

| Platform | Difficulty | Time to Setup | Global Reach | Cost |
|----------|------------|---------------|--------------|------|
| **JitPack** | ⭐ Easy | 5 minutes | High | Free |
| **GitHub Packages** | ⭐⭐ Medium | 15 minutes | Medium | Free |
| **Maven Central** | ⭐⭐⭐ Advanced | 1-2 hours | Highest | Free |

---

## 🚀 **Option 1: JitPack (Quickest - Recommended for Testing)**

### **Setup (5 minutes)**

JitPack automatically builds and publishes your library from GitHub releases.

#### **1. Create a Release**
```bash
# Tag and push a release
git tag v1.0.0
git push origin v1.0.0
```

#### **2. Verify on JitPack**
- Visit: https://jitpack.io/#everybytesystems/dhis2-ebscore-sdk
- Click "Get it" to trigger the first build
- Wait for green checkmark (build success)

#### **3. Usage by Consumers**
```kotlin
// In build.gradle.kts
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.everybytesystems.dhis2-ebscore-sdk:dhis2-ebscore-sdk-core:1.0.0")
    implementation("com.github.everybytesystems.dhis2-ebscore-sdk:dhis2-ebscore-sdk-auth:1.0.0")
}
```

### **Advantages**
- ✅ **Zero configuration** - Works immediately
- ✅ **Automatic builds** - Builds from GitHub releases
- ✅ **All platforms** - Supports all Kotlin Multiplatform targets
- ✅ **Version control** - Each git tag becomes a version

---

## 🏢 **Option 2: GitHub Packages (Integrated)**

### **Setup (15 minutes)**

#### **1. Add Publishing Configuration**

Create `gradle/publishing.gradle.kts`:

```kotlin
// gradle/publishing.gradle.kts
apply(plugin = "maven-publish")

configure<PublishingExtension> {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/everybytesystems/dhis2-ebscore-sdk")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    
    publications {
        register<MavenPublication>("maven") {
            groupId = "com.everybytesystems"
            artifactId = project.name
            version = project.version.toString()
            
            from(components["kotlin"])
            
            pom {
                name.set("DHIS2 EBSCore SDK")
                description.set("A comprehensive, type-safe, and production-ready Kotlin Multiplatform SDK for DHIS2 integration")
                url.set("https://github.com/everybytesystems/dhis2-ebscore-sdk")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("everybytesystems")
                        name.set("EveryByte Systems")
                        email.set("support@everybytesystems.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/everybytesystems/dhis2-ebscore-sdk.git")
                    developerConnection.set("scm:git:ssh://github.com:everybytesystems/dhis2-ebscore-sdk.git")
                    url.set("https://github.com/everybytesystems/dhis2-ebscore-sdk/tree/main")
                }
            }
        }
    }
}
```

#### **2. Apply to Modules**

Add to each module's `build.gradle.kts`:
```kotlin
apply(from = "$rootDir/gradle/publishing.gradle.kts")
```

#### **3. Configure GitHub Secrets**
- Go to: https://github.com/everybytesystems/dhis2-ebscore-sdk/settings/secrets/actions
- Add secrets:
  - `USERNAME`: Your GitHub username
  - `TOKEN`: GitHub token with `write:packages` scope

#### **4. Publish**
```bash
./gradlew publish
```

#### **5. Usage by Consumers**
```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/everybytesystems/dhis2-ebscore-sdk")
        credentials {
            username = "your-github-username"
            password = "your-github-token"
        }
    }
}

dependencies {
    implementation("com.everybytesystems:dhis2-ebscore-sdk-core:1.0.0")
}
```

---

## 🌍 **Option 3: Maven Central (Production - Industry Standard)**

### **Prerequisites (One-time Setup)**

#### **1. Create Sonatype Account**
- Visit: https://issues.sonatype.org/secure/Signup!default.jspa
- Create JIRA account
- Request namespace: `com.everybytesystems`

#### **2. Generate GPG Key**
```bash
# Generate GPG key
gpg --gen-key
# Use: EveryByte Systems <support@everybytesystems.com>

# List keys to get key ID
gpg --list-secret-keys --keyid-format LONG

# Export public key to key servers
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID

# Export private key for CI/CD
gpg --export-secret-keys YOUR_KEY_ID | base64 > private-key.txt
```

### **Setup (1-2 hours)**

#### **1. Add Publishing Plugin**

Update root `build.gradle.kts`:
```kotlin
plugins {
    // ... existing plugins
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")
}
```

#### **2. Configure Publishing**

Add to `gradle.properties`:
```properties
# Publishing
GROUP=com.everybytesystems
VERSION_NAME=1.0.0

# POM
POM_NAME=DHIS2 EBSCore SDK
POM_DESCRIPTION=A comprehensive, type-safe, and production-ready Kotlin Multiplatform SDK for DHIS2 integration
POM_INCEPTION_YEAR=2024
POM_URL=https://github.com/everybytesystems/dhis2-ebscore-sdk

# License
POM_LICENSE_NAME=MIT License
POM_LICENSE_URL=https://opensource.org/licenses/MIT
POM_LICENSE_DIST=repo

# Developer
POM_DEVELOPER_ID=everybytesystems
POM_DEVELOPER_NAME=EveryByte Systems
POM_DEVELOPER_EMAIL=support@everybytesystems.com

# SCM
POM_SCM_URL=https://github.com/everybytesystems/dhis2-ebscore-sdk
POM_SCM_CONNECTION=scm:git:git://github.com/everybytesystems/dhis2-ebscore-sdk.git
POM_SCM_DEV_CONNECTION=scm:git:ssh://git@github.com:everybytesystems/dhis2-ebscore-sdk.git

# Sonatype
SONATYPE_HOST=S01
RELEASE_SIGNING_ENABLED=true
```

#### **3. Configure GitHub Secrets**
Add these secrets to your repository:
- `MAVEN_CENTRAL_USERNAME`: Sonatype username
- `MAVEN_CENTRAL_PASSWORD`: Sonatype password
- `SIGNING_KEY_ID`: GPG key ID
- `SIGNING_PASSWORD`: GPG key passphrase
- `SIGNING_SECRET_KEY`: Base64 encoded private key

#### **4. Update GitHub Actions**

The existing `.github/workflows/ci.yml` already includes publishing steps. Just ensure the secrets are configured.

#### **5. Publish**
```bash
# Publish to staging
./gradlew publishToSonatype

# Close and release (after verification)
./gradlew closeAndReleaseSonatypeStagingRepository
```

#### **6. Usage by Consumers (After Release)**
```kotlin
dependencies {
    implementation("com.everybytesystems:dhis2-ebscore-sdk-core:1.0.0")
    implementation("com.everybytesystems:dhis2-ebscore-sdk-auth:1.0.0")
    implementation("com.everybytesystems:dhis2-ebscore-sdk-metadata:1.0.0")
    implementation("com.everybytesystems:dhis2-ebscore-sdk-data:1.0.0")
    implementation("com.everybytesystems:dhis2-ebscore-sdk-visual:1.0.0")
}
```

---

## 🔄 **Automated Publishing Workflow**

### **GitHub Actions Integration**

The project already includes automated publishing in `.github/workflows/ci.yml`. When you push a tag:

```bash
git tag v1.0.0
git push origin v1.0.0
```

This triggers:
1. ✅ **Build and test** all modules
2. ✅ **Generate documentation**
3. ✅ **Publish to Maven Central** (if configured)
4. ✅ **Create GitHub Release** with changelog
5. ✅ **Deploy documentation** to GitHub Pages

---

## 📊 **Publishing Strategy Recommendation**

### **Phase 1: Immediate (JitPack)**
```bash
# Quick setup for immediate availability
git tag v1.0.0
git push origin v1.0.0
# Available at: https://jitpack.io/#everybytesystems/dhis2-ebscore-sdk
```

### **Phase 2: Professional (Maven Central)**
- Set up Sonatype account
- Configure GPG signing
- Publish to Maven Central for professional distribution

### **Phase 3: Enterprise (All Platforms)**
- GitHub Packages for private/enterprise use
- Maven Central for public distribution
- JitPack for development/testing versions

---

## 🎯 **Quick Start: JitPack Publishing (5 minutes)**

Since the repository is ready, let's start with JitPack:

### **1. Create First Release**
```bash
cd /Users/stephocay/projects/ebscoresdk
git tag v1.0.0
git push origin v1.0.0
```

### **2. Verify Build**
- Visit: https://jitpack.io/#everybytesystems/dhis2-ebscore-sdk
- Click "Get it" next to v1.0.0
- Wait for green checkmark

### **3. Test Usage**
Create a test project and add:
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.everybytesystems.dhis2-ebscore-sdk:dhis2-ebscore-sdk-core:1.0.0")
}
```

### **4. Update Documentation**
Add installation instructions to README.md with JitPack coordinates.

---

## 📚 **Documentation Updates Needed**

After publishing, update these files:

### **README.md**
```markdown
### Installation

#### JitPack (Recommended)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.everybytesystems.dhis2-ebscore-sdk:dhis2-ebscore-sdk-core:1.0.0")
}
```

#### Maven Central (Coming Soon)
```kotlin
dependencies {
    implementation("com.everybytesystems:dhis2-ebscore-sdk-core:1.0.0")
}
```
```

### **CHANGELOG.md**
```markdown
## [1.0.0] - 2024-01-XX

### Added
- Complete DHIS2 API implementation (14/14 APIs)
- Kotlin Multiplatform support (JVM, Android, iOS, JS)
- Version-aware architecture (DHIS2 2.36-2.42)
- Enterprise-grade features and performance
- Comprehensive documentation and examples

### Published
- Available on JitPack: https://jitpack.io/#everybytesystems/dhis2-ebscore-sdk
- Maven Central publishing in progress
```

---

## 🎉 **Ready to Publish!**

The DHIS2 EBSCore SDK is **production-ready** and can be published immediately to:

1. ✅ **JitPack** - 5 minutes setup, immediate availability
2. ✅ **GitHub Packages** - 15 minutes setup, integrated with GitHub
3. ✅ **Maven Central** - 1-2 hours setup, industry standard

**Recommendation**: Start with JitPack for immediate availability, then set up Maven Central for professional distribution.

**Which publishing option would you like to implement first?**