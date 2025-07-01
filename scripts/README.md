# 🔧 Setup and Publishing Scripts

This directory contains utility scripts for setting up and publishing the EBSCore SDK.

## 📁 Scripts

### 🚀 **Publishing Scripts**
- **publish-jitpack.sh** - Publish SDK to JitPack repository
- **setup-maven-central.sh** - Configure Maven Central publishing

### ⚙️ **Setup Scripts**
- **setup-github-repo.sh** - Generic GitHub repository setup
- **setup-github-repo-everybyte.sh** - EveryByte Systems specific setup

## 🚀 Usage

### Publishing to JitPack

```bash
# Publish current version to JitPack
./scripts/publish-jitpack.sh
```

### Setting up Maven Central

```bash
# Configure Maven Central publishing
./scripts/setup-maven-central.sh
```

### GitHub Repository Setup

```bash
# Generic GitHub setup
./scripts/setup-github-repo.sh

# EveryByte Systems specific setup
./scripts/setup-github-repo-everybyte.sh
```

## 📋 Prerequisites

### For Publishing Scripts
- **Git** - Version control
- **GitHub CLI** (optional) - For automated repository operations
- **GPG Key** - For signing artifacts (Maven Central)
- **Maven Central Account** - For publishing to Maven Central

### For Setup Scripts
- **GitHub Account** - Repository hosting
- **GitHub CLI** (recommended) - Automated setup
- **Git** - Version control

## 🔐 Security

### Secrets Required
- **MAVEN_USERNAME** - Maven Central username
- **MAVEN_PASSWORD** - Maven Central password
- **SIGNING_KEY** - GPG private key (base64 encoded)
- **SIGNING_PASSWORD** - GPG key passphrase

### Environment Variables
```bash
export GITHUB_TOKEN="your-github-token"
export MAVEN_USERNAME="your-maven-username"
export MAVEN_PASSWORD="your-maven-password"
```

## 📝 Notes

- Scripts are designed for Unix-like systems (macOS, Linux)
- Windows users should use Git Bash or WSL
- Always review scripts before execution
- Test in development environment first

---

For detailed publishing instructions, see [docs/PUBLISHING.md](../docs/PUBLISHING.md)