# Publishing Guide

Guide for publishing applications built with EBSCore SDK.

## 📦 JitPack Publishing

### Automatic Publishing

JitPack automatically builds releases when you create Git tags:

```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0

# JitPack will build automatically
```

### Using Published Versions

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.everybytesystems.ebscore-sdk:ebscore-sdk-core:1.0.0")
}
```

## 🏗️ Maven Central Publishing

### Setup

1. Configure `gradle.properties`:
```properties
# Maven Central Publishing
GROUP=com.everybytesystems
VERSION_NAME=1.0.0

# POM Configuration
POM_NAME=EBSCore SDK
POM_DESCRIPTION=A comprehensive, type-safe Kotlin Multiplatform SDK for DHIS2 integration
POM_INCEPTION_YEAR=2024
POM_URL=https://github.com/everybytesystems/ebscore-sdk

# License
POM_LICENSE_NAME=MIT License
POM_LICENSE_URL=https://opensource.org/licenses/MIT

# Developer
POM_DEVELOPER_ID=everybytesystems
POM_DEVELOPER_NAME=EveryByte Systems
POM_DEVELOPER_EMAIL=support@everybytesystems.com

# SCM
POM_SCM_URL=https://github.com/everybytesystems/ebscore-sdk
POM_SCM_CONNECTION=scm:git:git://github.com/everybytesystems/ebscore-sdk.git
POM_SCM_DEV_CONNECTION=scm:git:ssh://git@github.com:everybytesystems/ebscore-sdk.git

# Sonatype
SONATYPE_HOST=S01
RELEASE_SIGNING_ENABLED=true
```

2. Run the setup script:
```bash
./setup-maven-central.sh
```

### Publishing

```bash
# Publish to staging repository
./gradlew publishToSonatype

# Close and release
./gradlew closeAndReleaseSonatypeStagingRepository
```

## 📱 Application Publishing

### Android

1. **Build signed APK/AAB**:
```bash
./gradlew assembleRelease
./gradlew bundleRelease
```

2. **Upload to Google Play Console**

### iOS

1. **Build for App Store**:
```bash
./gradlew iosArm64MainBinaries
```

2. **Upload via Xcode or Application Loader**

### Desktop

1. **Create distribution packages**:
```bash
# Windows
./gradlew packageMsi

# macOS
./gradlew packageDmg

# Linux
./gradlew packageDeb
```

## 🔐 Security Considerations

### API Keys and Secrets

Never include sensitive information in your published code:

```kotlin
// ❌ Don't do this
val sdk = EBSCoreSdkBuilder()
    .baseUrl("https://production-server.org")
    .build()

sdk.authenticate(BasicAuthConfig("admin", "password123"))

// ✅ Do this instead
val baseUrl = BuildConfig.DHIS2_BASE_URL
val username = BuildConfig.DHIS2_USERNAME
val password = BuildConfig.DHIS2_PASSWORD

val sdk = EBSCoreSdkBuilder()
    .baseUrl(baseUrl)
    .build()

sdk.authenticate(BasicAuthConfig(username, password))
```

### ProGuard/R8 Rules

For Android releases, add ProGuard rules:

```proguard
# EBSCore SDK
-keep class com.everybytesystems.ebscore.** { *; }
-keepclassmembers class com.everybytesystems.ebscore.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
```

## 📊 Performance Optimization

### Build Optimization

```kotlin
// build.gradle.kts
kotlin {
    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-Xopt-in=kotlin.RequiresOptIn",
                    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
                )
            }
        }
    }
}
```

### Runtime Optimization

```kotlin
val sdk = EBSCoreSdkBuilder()
    .baseUrl("https://your-server.org")
    .enableCaching(true)
    .cacheSize(100 * 1024 * 1024) // 100MB
    .enableCompression(true)
    .connectTimeout(30_000)
    .readTimeout(60_000)
    .build()
```

## 🧪 Testing Before Publishing

### Automated Testing

```bash
# Run all tests
./gradlew test

# Run specific platform tests
./gradlew jvmTest
./gradlew androidTest
./gradlew iosTest
```

### Manual Testing Checklist

- [ ] Authentication works with target DHIS2 instance
- [ ] All required APIs function correctly
- [ ] Offline functionality works as expected
- [ ] Performance is acceptable on target devices
- [ ] Error handling works properly
- [ ] UI/UX is polished and responsive

## 📋 Release Checklist

### Pre-Release

- [ ] All tests pass
- [ ] Documentation is up to date
- [ ] CHANGELOG.md is updated
- [ ] Version numbers are bumped
- [ ] Security review completed
- [ ] Performance testing completed

### Release

- [ ] Create Git tag
- [ ] Build and test release artifacts
- [ ] Publish to distribution channels
- [ ] Update documentation
- [ ] Announce release

### Post-Release

- [ ] Monitor for issues
- [ ] Respond to user feedback
- [ ] Plan next release
- [ ] Update roadmap

---

For more information, see:
- [Getting Started](GETTING_STARTED.md)
- [API Reference](API_REFERENCE.md)
- [Examples](EXAMPLES.md)
