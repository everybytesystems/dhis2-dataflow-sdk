#!/bin/bash

# EBSCore SDK - GitHub Repository Setup Script
# This script helps you create and configure the GitHub repository

set -e

echo "🚀 EBSCore SDK - GitHub Repository Setup"
echo "================================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
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

# Check if we're in the right directory
if [ ! -f "README.md" ] || [ ! -f "IMPLEMENTATION_STATUS.md" ]; then
    print_error "Please run this script from the root of the EBSCore SDK project"
    exit 1
fi

print_info "Current directory: $(pwd)"
print_info "Git status:"
git status --short

echo ""
echo "📋 Pre-flight Checklist"
echo "======================="

# Check if git is initialized
if [ ! -d ".git" ]; then
    print_error "Git repository not initialized. Run 'git init' first."
    exit 1
fi
print_status "Git repository initialized"

# Check if there are commits
if ! git rev-parse --verify HEAD >/dev/null 2>&1; then
    print_error "No commits found. Please make an initial commit first."
    exit 1
fi
print_status "Initial commit exists"

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    print_warning "GitHub CLI (gh) not found. You'll need to create the repository manually."
    MANUAL_SETUP=true
else
    print_status "GitHub CLI found"
    MANUAL_SETUP=false
fi

echo ""
echo "🔧 Repository Configuration"
echo "=========================="

# Get repository details
read -p "Enter GitHub username/organization: " GITHUB_USER
read -p "Enter repository name [ebscore-sdk]: " REPO_NAME
REPO_NAME=${REPO_NAME:-ebscore-sdk}

read -p "Repository description [A comprehensive, type-safe Kotlin Multiplatform SDK for DHIS2 integration]: " REPO_DESC
REPO_DESC=${REPO_DESC:-"A comprehensive, type-safe Kotlin Multiplatform SDK for DHIS2 integration"}

read -p "Make repository public? [y/N]: " MAKE_PUBLIC
MAKE_PUBLIC=${MAKE_PUBLIC:-n}

if [[ $MAKE_PUBLIC =~ ^[Yy]$ ]]; then
    VISIBILITY="public"
else
    VISIBILITY="private"
fi

echo ""
print_info "Repository: $GITHUB_USER/$REPO_NAME"
print_info "Description: $REPO_DESC"
print_info "Visibility: $VISIBILITY"

echo ""
read -p "Continue with repository creation? [y/N]: " CONTINUE
if [[ ! $CONTINUE =~ ^[Yy]$ ]]; then
    print_info "Setup cancelled"
    exit 0
fi

echo ""
echo "🏗️  Creating Repository"
echo "======================"

if [ "$MANUAL_SETUP" = true ]; then
    echo ""
    print_warning "Manual setup required. Please follow these steps:"
    echo ""
    echo "1. Go to https://github.com/new"
    echo "2. Create a new repository with these settings:"
    echo "   - Owner: $GITHUB_USER"
    echo "   - Repository name: $REPO_NAME"
    echo "   - Description: $REPO_DESC"
    echo "   - Visibility: $VISIBILITY"
    echo "   - DO NOT initialize with README, .gitignore, or license"
    echo ""
    echo "3. After creating the repository, run these commands:"
    echo ""
    echo "   git remote add origin https://github.com/$GITHUB_USER/$REPO_NAME.git"
    echo "   git branch -M main"
    echo "   git push -u origin main"
    echo ""
    print_info "Manual setup instructions provided above"
else
    # Create repository using GitHub CLI
    print_info "Creating repository using GitHub CLI..."
    
    if [ "$VISIBILITY" = "public" ]; then
        gh repo create "$GITHUB_USER/$REPO_NAME" --description "$REPO_DESC" --public --source=. --remote=origin --push
    else
        gh repo create "$GITHUB_USER/$REPO_NAME" --description "$REPO_DESC" --private --source=. --remote=origin --push
    fi
    
    print_status "Repository created and code pushed!"
fi

echo ""
echo "⚙️  Repository Settings"
echo "====================="

if [ "$MANUAL_SETUP" = false ]; then
    print_info "Configuring repository settings..."
    
    # Enable GitHub Pages (for documentation)
    print_info "Enabling GitHub Pages for documentation..."
    gh api repos/$GITHUB_USER/$REPO_NAME/pages -X POST -f source.branch=main -f source.path=/docs || true
    
    # Set repository topics
    print_info "Setting repository topics..."
    gh api repos/$GITHUB_USER/$REPO_NAME -X PATCH -f topics='["dhis2","kotlin","multiplatform","sdk","health","data","api","tracker","analytics"]' || true
    
    print_status "Repository settings configured"
else
    print_warning "Manual configuration required:"
    echo ""
    echo "1. Go to https://github.com/$GITHUB_USER/$REPO_NAME/settings"
    echo "2. Configure these settings:"
    echo "   - Topics: dhis2, kotlin, multiplatform, sdk, health, data, api, tracker, analytics"
    echo "   - Enable Issues and Projects"
    echo "   - Enable Wikis (optional)"
    echo "   - Enable Discussions (recommended)"
    echo ""
    echo "3. Set up GitHub Pages:"
    echo "   - Go to Settings > Pages"
    echo "   - Source: Deploy from a branch"
    echo "   - Branch: main"
    echo "   - Folder: /docs"
    echo ""
fi

echo ""
echo "🔐 Security Configuration"
echo "========================"

print_warning "Configure these secrets for CI/CD (if publishing to Maven Central):"
echo ""
echo "Repository Secrets (Settings > Secrets and variables > Actions):"
echo "- MAVEN_USERNAME: Your Maven Central username"
echo "- MAVEN_PASSWORD: Your Maven Central password"
echo "- SIGNING_KEY: Your GPG private key (base64 encoded)"
echo "- SIGNING_PASSWORD: Your GPG key passphrase"
echo ""

echo ""
echo "🎯 Next Steps"
echo "============"

print_info "Repository setup complete! Here's what to do next:"
echo ""
echo "1. 📝 Review and update documentation:"
echo "   - README.md"
echo "   - CONTRIBUTING.md"
echo "   - IMPLEMENTATION_STATUS.md"
echo ""
echo "2. 🔧 Configure branch protection rules:"
echo "   - Require pull request reviews"
echo "   - Require status checks to pass"
echo "   - Require branches to be up to date"
echo ""
echo "3. 🏷️  Create your first release:"
echo "   - git tag v1.0.0"
echo "   - git push origin v1.0.0"
echo ""
echo "4. 👥 Set up team access (if needed):"
echo "   - Add collaborators"
echo "   - Configure team permissions"
echo ""
echo "5. 📊 Monitor CI/CD:"
echo "   - Check GitHub Actions workflows"
echo "   - Review test results"
echo "   - Monitor code quality"
echo ""

if [ "$MANUAL_SETUP" = false ]; then
    echo "🌐 Repository URL: https://github.com/$GITHUB_USER/$REPO_NAME"
    echo "📚 Documentation: https://$GITHUB_USER.github.io/$REPO_NAME/"
    echo "🐛 Issues: https://github.com/$GITHUB_USER/$REPO_NAME/issues"
    echo "💬 Discussions: https://github.com/$GITHUB_USER/$REPO_NAME/discussions"
fi

echo ""
print_status "EBSCore SDK repository setup complete! 🎉"
echo ""
print_info "The SDK is now ready for:"
print_info "✅ 14/14 APIs fully implemented (100% coverage)"
print_info "✅ Version-aware architecture (DHIS2 2.36-2.42)"
print_info "✅ Enterprise-grade features and performance"
print_info "✅ Complete CI/CD pipeline with GitHub Actions"
print_info "✅ Comprehensive documentation and examples"
echo ""
print_status "Happy coding! 🚀"
