# Contributing to EBSCore SDK

Thank you for your interest in contributing to the EBSCore SDK! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Reporting Issues

1. **Search existing issues** first to avoid duplicates
2. **Use the issue templates** when creating new issues
3. **Provide detailed information** including:
   - DHIS2 version you're working with
   - SDK version
   - Platform (Android, iOS, JVM, JS)
   - Steps to reproduce
   - Expected vs actual behavior
   - Code samples if applicable

### Submitting Pull Requests

1. **Fork the repository** and create a feature branch
2. **Follow the coding standards** outlined below
3. **Write tests** for new functionality
4. **Update documentation** as needed
5. **Ensure all tests pass** before submitting
6. **Create a detailed pull request** description

## 🏗️ Development Setup

### Prerequisites

- **JDK 17+** - Required for Kotlin Multiplatform
- **Android Studio** - For Android development
- **Xcode** - For iOS development (macOS only)
- **Git** - Version control

### Clone and Setup

```bash
git clone https://github.com/everybytesystems/ebscore-sdk.git
cd ebscore-sdk

# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Project Structure

```
ebscore-sdk/
├── modules/
│   ├── core/           # Core SDK functionality
│   ├── auth/           # Authentication
│   ├── metadata/       # Metadata management
│   ├── data/           # Data operations
│   ├── tracker/        # Tracker API
│   ├── analytics/      # Analytics API
│   ├── storage/        # Local storage
│   ├── sync/           # Synchronization
│   └── utils/          # Utilities
├── examples/           # Example applications
├── docs/              # Documentation
└── gradle/            # Gradle configuration
```

## 📝 Coding Standards

### Kotlin Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use **4 spaces** for indentation
- Maximum line length: **120 characters**
- Use meaningful variable and function names

### Code Organization

```kotlin
// Package structure
package com.everybytesystems.ebscore.{module}.{feature}

// Import order
import kotlin.*
import kotlinx.*
import platform-specific imports
import third-party libraries
import com.everybytesystems.ebscore.*

// Class structure
class ExampleClass {
    // Constants
    companion object {
        private const val DEFAULT_TIMEOUT = 30_000L
    }
    
    // Properties
    private val property: String
    
    // Constructor
    
    // Public methods
    
    // Private methods
}
```

### Documentation

- Use **KDoc** for public APIs
- Include code examples for complex functions
- Document all parameters and return values

```kotlin
/**
 * Retrieves data elements from DHIS2 server
 *
 * @param fields Comma-separated list of fields to include
 * @param filter Filter expression for data elements
 * @param paging Pagination parameters
 * @return ApiResponse containing list of data elements
 *
 * @sample
 * ```kotlin
 * val elements = api.getDataElements(
 *     fields = "id,name,valueType",
 *     filter = "domainType:eq:AGGREGATE"
 * )
 * ```
 */
suspend fun getDataElements(
    fields: String? = null,
    filter: String? = null,
    paging: PagingParams? = null
): ApiResponse<List<DataElement>>
```

## 🧪 Testing

### Test Structure

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test API interactions with mock servers
- **Platform Tests**: Test platform-specific functionality

### Writing Tests

```kotlin
class DataElementApiTest {
    
    @Test
    fun `getDataElements should return success with valid response`() = runTest {
        // Given
        val mockClient = MockHttpClient()
        val api = DataElementApi(mockClient)
        
        // When
        val result = api.getDataElements()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.data?.size)
    }
    
    @Test
    fun `getDataElements should handle network error`() = runTest {
        // Given
        val mockClient = MockHttpClient(simulateNetworkError = true)
        val api = DataElementApi(mockClient)
        
        // When
        val result = api.getDataElements()
        
        // Then
        assertTrue(result.isError)
        assertTrue(result.error is NetworkError)
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :modules:core:test

# Run tests for specific platform
./gradlew jvmTest
./gradlew androidTest
./gradlew iosTest
./gradlew jsTest
```

## 🔧 Building and Publishing

### Local Development

```bash
# Build all modules
./gradlew build

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Test with local version
./gradlew :examples:desktop:run
```

### Publishing to JitPack

```bash
# Create and push a tag
git tag v1.0.1
git push origin v1.0.1

# JitPack will automatically build the release
```

## 🐛 Debugging

### Common Issues

1. **Build Failures**
   - Clean and rebuild: `./gradlew clean build`
   - Check JDK version: `java -version`
   - Update Gradle wrapper: `./gradlew wrapper --gradle-version=8.5`

2. **Test Failures**
   - Run with verbose output: `./gradlew test --info`
   - Check platform-specific issues
   - Verify mock server responses

3. **Platform-Specific Issues**
   - **Android**: Check API level compatibility
   - **iOS**: Verify Xcode version and iOS deployment target
   - **JS**: Check Node.js version and browser compatibility

### Logging

```kotlin
// Use structured logging
private val logger = Logger("DataElementApi")

logger.debug { "Fetching data elements with filter: $filter" }
logger.info { "Successfully retrieved ${elements.size} data elements" }
logger.error(exception) { "Failed to fetch data elements" }
```

## 📋 Pull Request Checklist

Before submitting a pull request, ensure:

- [ ] Code follows the style guidelines
- [ ] All tests pass locally
- [ ] New functionality includes tests
- [ ] Documentation is updated
- [ ] CHANGELOG.md is updated (if applicable)
- [ ] Commit messages are descriptive
- [ ] No merge conflicts with main branch

### Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Test additions or modifications
- `chore`: Build process or auxiliary tool changes

Examples:
```
feat(auth): add OAuth2 authentication support

fix(tracker): resolve null pointer exception in event creation

docs(readme): update installation instructions
```

## 🎯 Development Workflow

1. **Create Issue** - Describe the feature or bug
2. **Fork Repository** - Create your own fork
3. **Create Branch** - Use descriptive branch names
4. **Implement Changes** - Follow coding standards
5. **Write Tests** - Ensure good test coverage
6. **Update Documentation** - Keep docs current
7. **Submit PR** - Use the PR template
8. **Code Review** - Address feedback
9. **Merge** - Squash and merge when approved

## 🆘 Getting Help

- **GitHub Discussions**: Ask questions and share ideas
- **GitHub Issues**: Report bugs and request features
- **Email**: development@everybytesystems.com

## 📄 License

By contributing to EBSCore SDK, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to EBSCore SDK! 🚀