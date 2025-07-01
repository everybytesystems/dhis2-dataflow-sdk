# 🔧 Core - Shared Utilities and Infrastructure

The core module provides foundational utilities and infrastructure components used across the entire EBSCore SDK.

## 📁 Structure

### **🌐 api/** - Base API client and HTTP handling
- **client/** - HTTP client implementation with multiplatform support
- **interceptors/** - Request/response interceptors for logging, auth, etc.
- **serialization/** - JSON/XML serialization utilities

### **🔐 auth/** - Authentication mechanisms
- **providers/** - Auth provider implementations (JWT, OAuth2, API keys)
- **tokens/** - Token management and refresh logic
- **storage/** - Secure credential storage (platform-specific)

### **💾 cache/** - Caching logic
- **strategies/** - Cache strategies (LRU, TTL, size-based)
- **storage/** - Cache storage backends (memory, disk, remote)
- **policies/** - Cache policies and invalidation rules

### **⚙️ config/** - Configuration management
- **loaders/** - Configuration loaders (file, environment, remote)
- **validators/** - Configuration validation and schema checking
- **environments/** - Environment-specific configurations

### **🚨 error/** - Error handling
- **types/** - Error type definitions and hierarchies
- **handlers/** - Global error handlers and recovery strategies
- **recovery/** - Error recovery and retry mechanisms

### **🛠️ utils/** - Shared utilities
- **time/** - Date/time utilities and formatters
- **files/** - File operations and I/O utilities
- **formatters/** - Data formatters (JSON, XML, CSV)
- **validators/** - Input validation and sanitization

## 🎯 Key Features

- **Multiplatform Support** - Works on Android, iOS, JVM, JS
- **Type Safety** - Comprehensive Kotlin type system
- **Performance Optimized** - Efficient caching and memory management
- **Extensible** - Plugin architecture for custom implementations
- **Well Tested** - Comprehensive unit and integration tests

## 📚 Usage Examples

### HTTP Client
```kotlin
val client = EBSCoreHttpClient {
    baseUrl = "https://api.example.com"
    timeout = 30.seconds
    retryPolicy = RetryPolicy.exponentialBackoff()
}

val response = client.get("/users")
```

### Authentication
```kotlin
val authProvider = OAuth2Provider {
    clientId = "your-client-id"
    clientSecret = "your-client-secret"
    redirectUri = "your-app://callback"
}

val token = authProvider.authenticate(credentials)
```

### Caching
```kotlin
val cache = LRUCache<String, User>(
    maxSize = 1000,
    ttl = 1.hours
)

cache.put("user-123", user)
val cachedUser = cache.get("user-123")
```