#!/bin/bash

# DHIS2 DataFlow SDK - Maven Central Setup Script
# This script helps set up Maven Central publishing for the SDK

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Helper functions
print_header() {
    echo -e "${BLUE}$1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

echo "🚀 DHIS2 DataFlow SDK - Maven Central Setup"
echo "==========================================="
echo ""

print_info "This script will help you set up Maven Central publishing for the DHIS2 DataFlow SDK."
echo ""

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    print_error "This script must be run from the project root directory"
    exit 1
fi

print_header "📋 Prerequisites Checklist"
echo "=========================="
echo ""

echo "Before proceeding, ensure you have:"
echo "1. 📧 Sonatype JIRA account (https://issues.sonatype.org/secure/Signup!default.jspa)"
echo "2. 🔑 GPG key pair generated"
echo "3. 🌐 Domain verification for com.everybytesystems"
echo "4. 📝 GitHub repository secrets configured"
echo ""

read -p "Have you completed all prerequisites? [y/N]: " PREREQS_DONE
if [[ ! $PREREQS_DONE =~ ^[Yy]$ ]]; then
    echo ""
    print_info "Please complete the prerequisites first. See PUBLISHING_GUIDE.md for detailed instructions."
    echo ""
    print_header "🔗 Quick Links:"
    echo "• Sonatype JIRA: https://issues.sonatype.org/secure/Signup!default.jspa"
    echo "• GPG Guide: https://central.sonatype.org/publish/requirements/gpg/"
    echo "• Publishing Guide: ./PUBLISHING_GUIDE.md"
    exit 0
fi

echo ""
print_header "🔧 Configuration Setup"
echo "======================"
echo ""

# Update gradle.properties for Maven Central
print_info "Updating gradle.properties for Maven Central..."

# Backup existing gradle.properties
cp gradle.properties gradle.properties.backup

# Add Maven Central configuration
cat >> gradle.properties << 'EOF'

# Maven Central Publishing Configuration
GROUP=com.everybytesystems
VERSION_NAME=1.0.0

# POM Configuration
POM_NAME=DHIS2 DataFlow SDK
POM_DESCRIPTION=A comprehensive, type-safe, and production-ready Kotlin Multiplatform SDK for DHIS2 integration
POM_INCEPTION_YEAR=2024
POM_URL=https://github.com/everybytesystems/dhis2-dataflow-sdk

# License
POM_LICENSE_NAME=MIT License
POM_LICENSE_URL=https://opensource.org/licenses/MIT
POM_LICENSE_DIST=repo

# Developer
POM_DEVELOPER_ID=everybytesystems
POM_DEVELOPER_NAME=EveryByte Systems
POM_DEVELOPER_EMAIL=support@everybytesystems.com

# SCM
POM_SCM_URL=https://github.com/everybytesystems/dhis2-dataflow-sdk
POM_SCM_CONNECTION=scm:git:git://github.com/everybytesystems/dhis2-dataflow-sdk.git
POM_SCM_DEV_CONNECTION=scm:git:ssh://git@github.com:everybytesystems/dhis2-dataflow-sdk.git

# Sonatype
SONATYPE_HOST=S01
RELEASE_SIGNING_ENABLED=true
EOF

print_success "gradle.properties updated with Maven Central configuration"

# Update root build.gradle.kts
print_info "Updating root build.gradle.kts..."

# Add vanniktech plugin to root build.gradle.kts
if ! grep -q "vanniktech.mavenPublish" build.gradle.kts; then
    sed -i.bak 's/alias(libs.plugins.compose.multiplatform) apply false/alias(libs.plugins.compose.multiplatform) apply false\n    alias(libs.plugins.vanniktech.mavenPublish) apply false/' build.gradle.kts
    rm build.gradle.kts.bak
    print_success "Added vanniktech.mavenPublish plugin to root build.gradle.kts"
fi

# Update module build files
print_info "Updating module build files for Maven Central..."

MODULES=("auth" "core" "data" "metadata" "visual")

for module in "${MODULES[@]}"; do
    MODULE_BUILD_FILE="modules/$module/build.gradle.kts"
    
    if [ -f "$MODULE_BUILD_FILE" ]; then
        # Replace simple maven-publish with vanniktech plugin
        if grep -q "maven-publish" "$MODULE_BUILD_FILE"; then
            # Replace the plugin
            sed -i.bak 's/`maven-publish`/alias(libs.plugins.vanniktech.mavenPublish)/' "$MODULE_BUILD_FILE"
            
            # Replace the publishing block with vanniktech configuration
            # This is a complex replacement, so we'll create a new publishing block
            
            # Remove old publishing block
            sed -i.bak '/\/\/ Simple publishing configuration for JitPack/,/^}$/d' "$MODULE_BUILD_FILE"
            
            # Add vanniktech configuration
            cat >> "$MODULE_BUILD_FILE" << EOF

// Maven Central publishing configuration
mavenPublishing {
    publishToMavenCentral(SonatypeHost.S01)
    signAllPublications()
    
    coordinates(
        groupId = "com.everybytesystems",
        artifactId = "dhis2-dataflow-sdk-$module",
        version = "1.0.0"
    )
    
    pom {
        name.set("DHIS2 DataFlow SDK - ${module^}")
        description.set("${module^} module of the DHIS2 DataFlow SDK")
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
            }
        }
        
        scm {
            url.set("https://github.com/everybytesystems/dhis2-dataflow-sdk")
            connection.set("scm:git:git://github.com/everybytesystems/dhis2-dataflow-sdk.git")
            developerConnection.set("scm:git:ssh://git@github.com:everybytesystems/dhis2-dataflow-sdk.git")
        }
    }
}
EOF
            
            rm "$MODULE_BUILD_FILE.bak"
            print_success "Updated $module module for Maven Central"
        fi
    fi
done

echo ""
print_header "🔐 GitHub Secrets Configuration"
echo "==============================="
echo ""

print_info "You need to configure the following GitHub secrets:"
echo ""
echo "Repository Settings → Secrets and variables → Actions"
echo "URL: https://github.com/everybytesystems/dhis2-dataflow-sdk/settings/secrets/actions"
echo ""
echo "Required secrets:"
echo "• MAVEN_CENTRAL_USERNAME: Your Sonatype JIRA username"
echo "• MAVEN_CENTRAL_PASSWORD: Your Sonatype JIRA password"
echo "• SIGNING_KEY_ID: Your GPG key ID (last 8 characters)"
echo "• SIGNING_PASSWORD: Your GPG key passphrase"
echo "• SIGNING_SECRET_KEY: Base64 encoded private GPG key"
echo ""

print_warning "To get your base64 encoded GPG key:"
echo "gpg --export-secret-keys YOUR_KEY_ID | base64 | tr -d '\\n'"
echo ""

read -p "Have you configured all GitHub secrets? [y/N]: " SECRETS_DONE
if [[ ! $SECRETS_DONE =~ ^[Yy]$ ]]; then
    print_warning "Please configure GitHub secrets before proceeding with publishing."
fi

echo ""
print_header "🧪 Test Build"
echo "============="
echo ""

print_info "Testing build with new Maven Central configuration..."

if ./gradlew clean build --no-daemon; then
    print_success "Build successful with Maven Central configuration!"
else
    print_error "Build failed. Please check the configuration."
    exit 1
fi

echo ""
print_header "📦 Publishing Commands"
echo "====================="
echo ""

print_info "Available publishing commands:"
echo ""
echo "1. Publish to Sonatype staging:"
echo "   ./gradlew publishToSonatype"
echo ""
echo "2. Close and release staging repository:"
echo "   ./gradlew closeAndReleaseSonatypeStagingRepository"
echo ""
echo "3. Publish and release in one step:"
echo "   ./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository"
echo ""

print_header "🎯 Next Steps"
echo "============="
echo ""
echo "1. 🔐 Configure GitHub secrets (if not done)"
echo "2. 🧪 Test staging publish: ./gradlew publishToSonatype"
echo "3. ✅ Verify artifacts in Sonatype staging"
echo "4. 🚀 Release: ./gradlew closeAndReleaseSonatypeStagingRepository"
echo "5. ⏰ Wait 10-30 minutes for Maven Central sync"
echo "6. 🎉 Verify on https://search.maven.org/"
echo ""

print_success "Maven Central setup complete!"
print_info "The SDK can now be published to Maven Central using the commands above."
echo ""
print_header "📚 Documentation"
echo "================"
echo "• Full guide: ./PUBLISHING_GUIDE.md"
echo "• Sonatype docs: https://central.sonatype.org/publish/"
echo "• Vanniktech plugin: https://github.com/vanniktech/gradle-maven-publish-plugin"
echo ""

print_success "🎉 Ready for Maven Central publishing!"