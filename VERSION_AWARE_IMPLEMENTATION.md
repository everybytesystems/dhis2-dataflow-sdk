# 🔄 DHIS2 Version-Aware SDK Implementation

## 📋 **Overview**

The DHIS2 DataFlow SDK now includes **comprehensive version detection and automatic API adaptation** that supports all DHIS2 versions from **2.36 to 2.42+**. The SDK automatically detects the DHIS2 version when connecting and adapts all API calls accordingly.

## 🎯 **Key Features**

### ✅ **Automatic Version Detection**
- Detects DHIS2 version on first connection using `/api/system/info`
- Caches version information for performance (configurable timeout)
- Supports version refresh on demand
- Handles version parsing for various formats (2.41.0, 2.40.3.1, 2.39-SNAPSHOT, etc.)

### ✅ **Comprehensive API Adaptation**
- **Endpoint Mapping**: Automatically uses correct API endpoints based on version
- **Parameter Transformation**: Converts deprecated parameters to new ones
- **Field Mapping**: Handles response field name changes
- **Feature Detection**: Checks if specific features are available
- **Fallback Mechanisms**: Provides alternatives for unsupported features

### ✅ **Version Coverage**
- **DHIS2 2.42**: Latest version with major tracker endpoint removals
- **DHIS2 2.41**: Java 17, sharing API changes, tracker naming conventions
- **DHIS2 2.40**: Various API improvements and deprecations
- **DHIS2 2.39**: Data exchange APIs, line listing features
- **DHIS2 2.38**: New tracker API, event visualizations
- **DHIS2 2.37**: Visualization API changes, outlier detection
- **DHIS2 2.36**: Base supported version

---

## 🏗️ **Architecture**

### **Core Components**

```kotlin
// 1. Version Detection
VersionDetector -> Detects DHIS2 version from /api/system/info

// 2. Version Information
DHIS2Version -> Represents version with comparison capabilities

// 3. Compatibility Management
VersionCompatibilityManager -> Handles version-specific adaptations

// 4. Version-Aware HTTP Client
VersionAwareHttpClient -> Automatically adapts API calls

// 5. Enhanced SDK
DataFlowSdk -> Uses version-aware client for all APIs
```

### **Integration Flow**

```
1. SDK Initialization
   ↓
2. Version Detection (if enabled)
   ↓
3. Compatibility Manager Setup
   ↓
4. Version-Aware HTTP Client
   ↓
5. Automatic API Adaptation
```

---

## 🔧 **Major API Changes Handled**

### **🎯 Tracker API Changes**

#### **DHIS2 2.42: Complete Removal**
```kotlin
// ❌ REMOVED in 2.42
/api/trackedEntityInstances
/api/enrollments  
/api/events
/api/relationships

// ✅ MUST USE in 2.42+
/api/tracker/trackedEntities
/api/tracker/enrollments
/api/tracker/events
/api/tracker/relationships
```

#### **DHIS2 2.38-2.41: Dual Support**
```kotlin
// Both available, SDK prefers new API
/api/trackedEntityInstances ← Legacy
/api/tracker/trackedEntities ← New (preferred)
```

#### **DHIS2 2.36-2.37: Legacy Only**
```kotlin
// Only legacy API available
/api/trackedEntityInstances
```

### **📊 Analytics API Changes**

#### **DHIS2 2.42: Table/Column Renames**
```kotlin
// Table renames
analytics_tei_* → analytics_te_*
analytics_tei_events_* → analytics_te_event_*
analytics_tei_enrollments_* → analytics_te_enrollment_*

// Column renames
trackedentityinstanceuid → trackedentity
programinstanceuid → enrollment
programstageinstanceuid → event
```

#### **DHIS2 2.37+: Outlier Detection**
```kotlin
// 2.37+: Dedicated endpoint
/api/outlierDetection

// 2.36: Fallback to basic analytics
/api/analytics
```

### **📱 Visualization API Changes**

#### **DHIS2 2.37+: Unified Visualizations**
```kotlin
// 2.37+: Unified API
/api/visualizations (replaces charts and reportTables)

// 2.36: Separate APIs
/api/charts
/api/reportTables
```

#### **DHIS2 2.38+: Event Visualizations**
```kotlin
// 2.38+: New event visualizations
/api/eventVisualizations

// 2.36-2.37: Legacy event charts/reports
/api/eventCharts
/api/eventReports
```

### **👥 User API Changes**

#### **DHIS2 2.42: User Object Restructure**
```kotlin
// ❌ BEFORE 2.42
{
  "firstName": "John",
  "userCredentials": {
    "username": "john",
    "userRoles": [...]
  }
}

// ✅ DHIS2 2.42+
{
  "firstName": "John",
  "username": "john",
  "userRoles": [...]
}
```

### **🔄 Parameter Changes**

#### **DHIS2 2.41+: Semicolon to Comma**
```kotlin
// ❌ DEPRECATED in 2.41
assignedUser=uid1;uid2;uid3
orgUnit=uid1;uid2;uid3

// ✅ NEW in 2.41+
assignedUsers=uid1,uid2,uid3
orgUnits=uid1,uid2,uid3
```

#### **DHIS2 2.42: Parameter Renames**
```kotlin
// Enrollments
programStatus → status

// Events  
programStatus → enrollmentStatus

// Ownership
ou → orgUnit
```

---

## 💻 **Usage Examples**

### **Basic Setup with Version Detection**

```kotlin
val sdk = DataFlowSdkBuilder()
    .baseUrl("https://play.dhis2.org/2.42.0")
    .autoDetectVersion(true) // Enable automatic detection
    .enableLogging(true)     // See version detection logs
    .build()

// Initialize with version detection
sdk.initialize()

// Check detected version
val version = sdk.getDetectedVersion()
println("Detected: ${version?.fullVersion}")
```

### **Feature Support Checking**

```kotlin
// Check if specific features are supported
if (sdk.isFeatureSupported(DHIS2Feature.NEW_TRACKER_API)) {
    // Use new tracker API
    println("✅ New Tracker API available")
} else if (sdk.isFeatureSupported(DHIS2Feature.TRACKER_API)) {
    // Use legacy tracker API
    println("⚠️ Using legacy Tracker API")
}

// Check data exchange support
if (sdk.isFeatureSupported(DHIS2Feature.DATA_EXCHANGE)) {
    // Use data exchange features (2.39+)
} else {
    // Use alternative sync methods
}
```

### **Automatic API Adaptation**

```kotlin
// The SDK automatically adapts this call based on version:

// DHIS2 2.42+: Uses /api/42/tracker/trackedEntities
// DHIS2 2.38-2.41: Uses /api/41/tracker/trackedEntities  
// DHIS2 2.36-2.37: Uses /api/36/trackedEntityInstances

val result = sdk.versionAwareHttpClient.getResource<Any>(
    resource = "trackedentityinstances",
    params = mapOf(
        "orgUnit" to "ImspTQPwCqd", // Auto-converted to "orgUnits" in 2.41+
        "pageSize" to 10
    )
)
```

### **Version-Specific Behavior**

```kotlin
val version = sdk.getDetectedVersion()

when {
    version?.isAtLeast(2, 42) == true -> {
        // DHIS2 2.42+ specific code
        println("Using DHIS2 2.42+ features")
    }
    
    version?.isAtLeast(2, 38) == true -> {
        // DHIS2 2.38+ specific code
        println("Using new tracker API")
    }
    
    else -> {
        // Legacy DHIS2 2.36-2.37 code
        println("Using legacy APIs")
    }
}
```

---

## ⚙️ **Configuration Options**

### **DHIS2Config Enhancements**

```kotlin
DHIS2Config(
    baseUrl = "https://dhis2.example.com",
    apiVersion = "42",                    // Default API version
    autoDetectVersion = true,             // Enable version detection
    versionCacheTimeoutMs = 5 * 60 * 1000 // Cache timeout (5 minutes)
)
```

### **Builder Configuration**

```kotlin
DataFlowSdkBuilder()
    .baseUrl("https://dhis2.example.com")
    .autoDetectVersion(true)              // Enable/disable detection
    .versionCacheTimeout(10 * 60 * 1000)  // Custom cache timeout
    .apiVersion("42")                     // Fallback version
    .build()
```

---

## 🔍 **Supported Features by Version**

| Feature | 2.36 | 2.37 | 2.38 | 2.39 | 2.40 | 2.41 | 2.42 |
|---------|------|------|------|------|------|------|------|
| **Metadata API** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Data API** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Legacy Tracker** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ |
| **New Tracker API** | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Analytics API** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Outlier Detection** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Visualizations API** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Event Visualizations** | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Data Exchange** | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| **Aggregate Data Exchange** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| **OAuth2 Support** | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Two-Factor Auth** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Webhooks** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |

---

## 🚨 **Breaking Changes Handled**

### **DHIS2 2.42 Breaking Changes**
- ✅ **Tracker endpoints removal**: Automatic fallback to new tracker API
- ✅ **User object restructure**: Automatic userCredentials flattening
- ✅ **Audit endpoints removal**: Clear error messages with alternatives
- ✅ **Analytics table renames**: Automatic column name mapping
- ✅ **Parameter deprecations**: Automatic parameter transformation

### **DHIS2 2.41 Breaking Changes**
- ✅ **Sharing API changes**: Legacy properties removed, new sharing object used
- ✅ **Parameter naming**: Semicolon to comma separation handled
- ✅ **Database renames**: Table/column name changes abstracted
- ✅ **Pagination changes**: skipPaging to paging conversion

### **DHIS2 2.38+ Changes**
- ✅ **New tracker API**: Automatic endpoint selection
- ✅ **Parameter changes**: orgUnit to orgUnits transformation
- ✅ **ACL changes**: Updated authorization handling

---

## 🎯 **Benefits**

### **For Developers**
- **Zero Configuration**: Works out of the box with any DHIS2 version
- **Backward Compatibility**: Existing code continues to work
- **Future Proof**: Automatically adapts to new DHIS2 versions
- **Clear Errors**: Helpful messages when features aren't supported

### **For Applications**
- **Universal Compatibility**: Single codebase works with all DHIS2 versions
- **Automatic Updates**: No code changes needed for DHIS2 upgrades
- **Performance**: Cached version detection minimizes overhead
- **Reliability**: Fallback mechanisms ensure continued operation

### **For Maintenance**
- **Reduced Complexity**: No need for version-specific code branches
- **Easy Testing**: Test against multiple DHIS2 versions easily
- **Documentation**: Clear feature support matrix
- **Monitoring**: Version detection logging for troubleshooting

---

## 🔮 **Future Enhancements**

### **Planned Features**
- **Version-specific optimizations**: Performance tuning per version
- **Advanced caching**: Smart cache invalidation based on version
- **Migration helpers**: Automatic data migration between versions
- **Version analytics**: Usage statistics and compatibility reports

### **Extensibility**
- **Custom adapters**: Plugin system for custom version handling
- **Configuration profiles**: Pre-defined settings for common scenarios
- **Testing utilities**: Mock version detection for testing
- **Documentation generation**: Automatic API documentation per version

---

## ✅ **Summary**

The DHIS2 DataFlow SDK now provides **comprehensive version-aware capabilities** that:

1. **🔍 Automatically detects** DHIS2 version on connection
2. **🔄 Adapts all API calls** based on detected version  
3. **📋 Handles parameter transformations** automatically
4. **🛡️ Provides fallback mechanisms** for unsupported features
5. **📊 Supports all versions** from DHIS2 2.36 to 2.42+
6. **🚀 Works out of the box** with zero configuration required

This implementation ensures that **your application will work seamlessly across all supported DHIS2 versions** without requiring version-specific code or manual API adaptations! 🎉