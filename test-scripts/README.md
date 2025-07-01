# 🧪 Test Scripts

This directory contains various test scripts and utilities for testing the EBSCore SDK.

## 📁 Contents

### 🔧 **Build Scripts**
- **build-simple.gradle.kts** - Simplified build configuration for testing
- **settings-simple.gradle.kts** - Minimal settings for quick testing

### 🧪 **Test Files**
- **SimpleTest.kt** - Basic SDK functionality test
- **test-comprehensive-api.kts** - Comprehensive API testing script
- **test-dhis2-login.kts** - DHIS2 authentication testing script

## 🚀 Usage

### Running Test Scripts

```bash
# Run comprehensive API test
kotlin test-scripts/test-comprehensive-api.kts

# Run DHIS2 login test
kotlin test-scripts/test-dhis2-login.kts

# Run simple test with custom build
./gradlew -c test-scripts/settings-simple.gradle.kts test
```

### Development Testing

These scripts are useful for:
- **Quick API Testing** - Test specific SDK functionality
- **Authentication Testing** - Verify DHIS2 connection
- **Development Debugging** - Isolate and test specific features
- **CI/CD Integration** - Automated testing in pipelines

## 📝 Notes

- These are development/testing utilities
- Not part of the main SDK distribution
- Use for debugging and validation
- Can be safely ignored for normal SDK usage

---

For production testing, see the main test suites in the modules directories.