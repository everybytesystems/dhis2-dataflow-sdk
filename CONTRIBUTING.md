# Contributing to DHIS2 EBSCore SDK

Thank you for your interest in contributing to the DHIS2 EBSCore SDK! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Reporting Issues

1. **Search existing issues** first to avoid duplicates
2. **Use the issue templates** when creating new issues
3. **Provide detailed information** including:
   - DHIS2 version you're working with
   - SDK version
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

- **JDK 17 or higher**
- **Git**
- **IDE with Kotlin support** (IntelliJ IDEA recommended)

### Getting Started

1. **Clone the repository**:
```bash
git clone https://github.com/everybytesystems/dhis2-ebscore-sdk.git
cd dhis2-ebscore-sdk
```

2. **Build the project**:
```bash
./gradlew build
```

3. **Run tests**:
```bash
./gradlew test
```

4. **Run integration tests**:
```bash
./gradlew integrationTest
```

### Project Structure

```
dhis2-ebscore-sdk/
├── modules/
│   ├── core/           # Core SDK with all APIs
│   ├── auth/           # Authentication module
│   ├── metadata/       # Enhanced metadata operations
│   ├── data/           # Data processing utilities
│   └── visual/         # Visualization helpers
├── examples/           # Usage examples
├── docs/              # Documentation
├── .github/           # GitHub workflows and templates
└── gradle/            # Gradle wrapper and configuration
```

## 📝 Coding Standards

### Kotlin Style Guide

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) with these additions:

#### Naming Conventions
- **Classes**: PascalCase (`DataValuesApi`)
- **Functions**: camelCase (`getTrackedEntities`)
- **Properties**: camelCase (`baseUrl`)
- **Constants**: SCREAMING_SNAKE_CASE (`DEFAULT_TIMEOUT`)

#### Code Organization
- **Group related functionality** in the same file
- **Use meaningful names** for classes, functions, and variables
- **Keep functions small** and focused on a single responsibility
- **Add KDoc comments** for public APIs

#### Example:
```kotlin
/**
 * Retrieves tracked entities based on the specified criteria.
 * 
 * @param program The program identifier to filter by
 * @param orgUnit The organization unit identifier
 * @param pageSize Maximum number of entities to return (default: 50)
 * @return ApiResponse containing the list of tracked entities
 */
suspend fun getTrackedEntities(
    program: String? = null,
    orgUnit: String? = null,
    pageSize: Int = 50
): ApiResponse<TrackedEntitiesResponse> {
    val params = buildMap {
        program?.let { put("program", it) }
        orgUnit?.let { put("ou", it) }
        put("pageSize", pageSize.toString())
    }
    
    return get("trackedEntities", params)
}
```

### API Design Principles

1. **Consistency**: Follow established patterns across all APIs
2. **Type Safety**: Use strong typing and avoid `Any` types
3. **Version Awareness**: Check version compatibility for features
4. **Error Handling**: Provide meaningful error messages
5. **Documentation**: Include comprehensive KDoc comments

### Testing Guidelines

#### Unit Tests
- **Test all public methods**
- **Use descriptive test names**
- **Follow AAA pattern** (Arrange, Act, Assert)
- **Mock external dependencies**

Example:
```kotlin
@Test
fun `getTrackedEntities should return success response with valid parameters`() {
    // Arrange
    val expectedResponse = TrackedEntitiesResponse(/* ... */)
    coEvery { httpClient.get<TrackedEntitiesResponse>(any()) } returns expectedResponse
    
    // Act
    val result = trackerApi.getTrackedEntities(program = "testProgram")
    
    // Assert
    assertTrue(result is ApiResponse.Success)
    assertEquals(expectedResponse, result.data)
}
```

#### Integration Tests
- **Test real API interactions** (with test DHIS2 instance)
- **Test version compatibility**
- **Test error scenarios**

## 🔄 Development Workflow

### Branch Strategy

- **`main`**: Production-ready code
- **`develop`**: Integration branch for features
- **`feature/*`**: Individual feature branches
- **`hotfix/*`**: Critical bug fixes
- **`release/*`**: Release preparation

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

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
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

Examples:
```
feat(tracker): add support for working lists API

fix(analytics): resolve geospatial coordinate serialization issue

docs(readme): update installation instructions

test(data-values): add integration tests for bulk operations
```

### Pull Request Process

1. **Create a feature branch** from `develop`
2. **Make your changes** following the coding standards
3. **Add/update tests** as needed
4. **Update documentation** if applicable
5. **Ensure all checks pass**:
   ```bash
   ./gradlew test detekt build
   ```
6. **Create a pull request** with:
   - Clear title and description
   - Reference to related issues
   - Screenshots/examples if applicable
   - Checklist of completed items

### Code Review Guidelines

#### For Authors
- **Keep PRs focused** and reasonably sized
- **Provide context** in the description
- **Respond promptly** to feedback
- **Test thoroughly** before requesting review

#### For Reviewers
- **Be constructive** and respectful
- **Focus on code quality** and maintainability
- **Check for test coverage**
- **Verify documentation** is updated
- **Test the changes** if possible

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :dhis2-ebscore-sdk-core:test

# Run integration tests
./gradlew integrationTest

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Test Categories

1. **Unit Tests**: Fast, isolated tests for individual components
2. **Integration Tests**: Tests that interact with real DHIS2 instances
3. **Performance Tests**: Tests for performance and scalability
4. **Compatibility Tests**: Tests across different DHIS2 versions

### Test Configuration

Create a `test.properties` file for integration tests:
```properties
dhis2.baseUrl=https://your-test-instance.dhis2.org
dhis2.username=test-user
dhis2.password=test-password
```

## 📚 Documentation

### API Documentation

- **Use KDoc** for all public APIs
- **Include examples** in documentation
- **Document parameters** and return values
- **Note version requirements** for features

### README Updates

- **Keep examples current** with latest API
- **Update feature lists** when adding new functionality
- **Maintain compatibility matrix**

### Implementation Status

Update `IMPLEMENTATION_STATUS.md` when:
- Adding new APIs or features
- Changing implementation status
- Adding version support

## 🚀 Release Process

### Version Numbering

We follow [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features (backward compatible)
- **PATCH**: Bug fixes (backward compatible)

### Release Checklist

1. **Update version** in `gradle.properties`
2. **Update CHANGELOG.md** with release notes
3. **Ensure all tests pass**
4. **Create release tag**: `git tag v1.0.0`
5. **Push tag**: `git push origin v1.0.0`
6. **GitHub Actions** will handle the rest

## 🆘 Getting Help

### Communication Channels

- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and discussions
- **Email**: support@everybytesystems.com

### Resources

- [DHIS2 Developer Documentation](https://developers.dhis2.org/)
- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Ktor Documentation](https://ktor.io/docs/)

## 📄 License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

## 🙏 Recognition

Contributors will be recognized in:
- **CONTRIBUTORS.md** file
- **Release notes**
- **GitHub contributors** section

Thank you for contributing to the DHIS2 EBSCore SDK! 🎉