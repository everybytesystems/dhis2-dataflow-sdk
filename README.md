# 🚀 EBSCore SDK

[![Kotlin](https://img.shields.io/badge/kotlin-multiplatform-blue.svg)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose](https://img.shields.io/badge/compose-multiplatform-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![DHIS2](https://img.shields.io/badge/DHIS2-100%25%20API%20Coverage-orange.svg)](https://dhis2.org/)
[![Charts](https://img.shields.io/badge/Charts-68%2B%20Types-blue.svg)](https://github.com/EverybyteSystems/ebscore-sdk)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

**The most comprehensive health data management SDK for Compose Multiplatform development**

EBSCore SDK provides complete DHIS2 integration, advanced analytics with 68+ chart types, and production-ready tools for building sophisticated health data applications across Android, iOS, Desktop, and Web platforms.

## ✨ Key Features

### 🏥 **Complete DHIS2 Integration**
- **100% API Coverage**: All major DHIS2 APIs implemented
- **Offline-First**: Full offline capabilities with intelligent sync
- **Real-time Sync**: Bidirectional synchronization with conflict resolution
- **Data Validation**: DHIS2-compliant data validation and transformation

### 📊 **Advanced Analytics & Visualization**
- **68+ Chart Types**: Most comprehensive chart library available
- **Interactive**: Full touch/mouse interaction support
- **Real-time Updates**: Live data streaming and visualization
- **Export Capabilities**: PNG, SVG, PDF export formats
- **Statistical Analysis**: Advanced statistical functions and trend analysis

### 🏗️ **Production-Ready Architecture**
- **Modular Design**: 10 focused modules with clear boundaries
- **Type-Safe APIs**: Comprehensive Kotlin type system
- **Cross-Platform**: Single codebase for all major platforms
- **Performance Optimized**: Intelligent caching and memory management
- **Enterprise Security**: Multi-auth strategies with encrypted storage

### 🏥 **DHIS2 API Coverage**

| API | Status | Description |
|-----|--------|-------------|
| **System Info API** | ✅ 100% | Server information and capabilities |
| **User Management API** | ✅ 100% | User accounts and permissions |
| **Organization Units API** | ✅ 100% | Organizational hierarchy |
| **Data Elements API** | ✅ 100% | Data element definitions |
| **Data Sets API** | ✅ 100% | Data set configurations |
| **Programs API** | ✅ 100% | Tracker program definitions |
| **Data Values API** | ✅ 100% | Aggregate data values |
| **Events API** | ✅ 100% | Tracker events |
| **Tracked Entity Instances API** | ✅ 100% | Individual records |
| **Analytics API** | ✅ 100% | Data analytics and reporting |

## 🚀 Quick Start

### Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.everybytesystems.ebscore:ebscore-sdk:1.0.0")
    
    // Optional: Add specific modules if needed
    implementation("com.everybytesystems.ebscore:ebscore-analytics:1.0.0")
    implementation("com.everybytesystems.ebscore:ebscore-ui:1.0.0")
}
```

### Basic Usage

```kotlin
// Initialize SDK
val sdk = EBSCoreSdk.Builder()
    .baseUrl("https://your-dhis2-instance.org")
    .credentials("username", "password")
    .enableOfflineMode(true)
    .enableAnalytics(true)
    .build()

// Get data values
val dataValues = sdk.getDataValues(
    dataElement = "dataElementId",
    period = "202401",
    orgUnit = "orgUnitId"
)

// Create tracked entity
val trackedEntity = TrackedEntity(
    trackedEntityType = "personType",
    orgUnit = "orgUnitId",
    attributes = listOf(
        TrackedEntityAttribute("firstName", "John"),
        TrackedEntityAttribute("lastName", "Doe")
    )
)
val result = sdk.createTrackedEntity(trackedEntity)

// Create analytics visualization
val query = AnalyticsQuery.builder()
    .dimension("dx", listOf("dataElementId"))
    .dimension("pe", listOf("202401"))
    .dimension("ou", listOf("orgUnitId"))
    .build()

val analyticsData = sdk.getAnalytics(query)
val chart = LineChart(
    data = analyticsData,
    title = "Monthly Trends",
    interactive = true
)
```

## 📁 Module Architecture

```
ebscore-sdk/
├── modules/
│   ├── sdk/                     # ✅ Main SDK entry point & services
│   ├── core/                    # ✅ Core interfaces & network API
│   ├── network/                 # ✅ HTTP client & networking (Ktor-based)
│   ├── dhis2/                   # ✅ Complete DHIS2 API client
│   ├── storage/                 # ✅ SQLDelight database & caching
│   ├── auth/                    # ✅ Authentication & security
│   ├── sync/                    # ✅ Data synchronization engine
│   ├── analytics/               # ✅ Analytics & reporting
│   ├── utils/                   # ✅ 200+ utility functions
│   └── ui/                      # ✅ UI components & 68+ chart types
```

## 📈 Chart Library

### 68+ Chart Types Available

#### Chart Categories:
1. **Statistical Charts (5)**: Box plots, violin plots, histograms, density plots, Q-Q plots
2. **Financial Charts (4)**: OHLC, Renko, Point & Figure, Kagi
3. **Business Charts (5)**: Funnel, pyramid, bullet, speedometer, Marimekko
4. **Time Series Charts (4)**: Gantt, timeline, calendar, stream graphs
5. **Geographic Charts (3)**: Choropleth, flow maps, dot distribution
6. **Relationship Charts (5)**: Chord diagrams, arc diagrams, matrix, parallel coordinates, alluvial
7. **Hierarchical Charts (5)**: Dendrograms, sunburst, icicle, circle packing, partition
8. **Distribution Charts (4)**: Ridgeline, beeswarm, strip charts, Sina plots
9. **Creative Charts (4)**: Word clouds, pictographs, slope graphs, bump charts
10. **Scientific Charts (2)**: Contour plots, vector fields
11. **Interactive Charts (3)**: Sparklines, progress charts, metric cards
12. **Basic Charts (12)**: Line, bar, pie, scatter, area, column, etc.
13. **Advanced Charts (12)**: Heatmaps, treemaps, Sankey, network graphs, etc.

#### Chart Features:
- ✅ **Interactive**: Full touch/mouse interaction support
- ✅ **Responsive**: Automatic resizing and responsive design
- ✅ **Customizable**: Themes, colors, fonts, and styling
- ✅ **Export**: PNG, SVG, PDF export capabilities
- ✅ **Real-time**: Live data updates and streaming
- ✅ **Accessibility**: Screen reader and keyboard navigation support

## 🎯 Platform Support

- ✅ **Android** - Native Android applications
- ✅ **iOS** - Native iOS applications  
- ✅ **Desktop** - Windows, macOS, Linux desktop applications
- ✅ **Web** - Browser-based web applications

## 🔧 Requirements

- **Kotlin**: 1.9.0+
- **Compose Multiplatform**: 1.5.0+
- **Gradle**: 8.0+
- **Android**: API 24+ (Android 7.0)
- **iOS**: iOS 12.0+
- **JVM**: Java 11+

## 📚 Getting Started

### Module Selection Guide
- **Core Functionality**: `ebscore-sdk` (includes all essential features)
- **Advanced Analytics**: Add `ebscore-analytics` for statistical analysis
- **Data Visualization**: Add `ebscore-ui` for charts and components
- **Custom Storage**: Add `ebscore-storage` for advanced caching
- **Custom Sync**: Add `ebscore-sync` for specialized synchronization

### Quick Start Checklist
- ✅ **Add Dependencies**: Include EBSCore SDK in your project
- ✅ **Initialize SDK**: Configure with your DHIS2 instance details
- ✅ **Authentication**: Set up user credentials or tokens
- ✅ **Test Connection**: Verify connectivity to DHIS2 server
- ✅ **Start Development**: Begin using SDK APIs

## 🏗️ Architecture

### Modular Design
```
dhis2-ebscore-sdk/
├── modules/
│   ├── core/           # Core SDK with all APIs
│   ├── auth/           # Authentication module
│   ├── metadata/       # Enhanced metadata operations
│   ├── data/           # Data processing utilities
│   └── visual/         # Visualization helpers
├── examples/           # Usage examples
└── docs/              # Documentation
```

### Version Compatibility Matrix

| Feature Category | 2.36 | 2.37 | 2.38 | 2.39 | 2.40 | 2.41 | 2.42 |
|------------------|------|------|------|------|------|------|------|
| **Core APIs** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Tracker Operations** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Analytics** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Messaging** | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Metadata Gist** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Email Notifications** | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Metadata Versioning** | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **System Backup** | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Bulk Operations** | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ | ✅ |
| **Push Notifications** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| **Cluster Management** | ❌ | ❌ | ❌ | ❌ | ✅ | ✅ | ✅ |
| **Advanced Sync** | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |

## 🧪 Testing

Run the test suite:

```bash
./gradlew test
```

Run integration tests:

```bash
./gradlew integrationTest
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

### Development Setup

1. Clone the repository:
```bash
git clone https://github.com/everybytesystems/dhis2-ebscore-sdk.git
cd dhis2-ebscore-sdk
```

2. Build the project:
```bash
./gradlew build
```

3. Run tests:
```bash
./gradlew test
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [DHIS2](https://dhis2.org) for the excellent health information system
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) for enabling cross-platform development
- [Ktor](https://ktor.io) for the HTTP client implementation
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON serialization

## 📞 Support

- **Documentation**: [Full API Documentation](https://everybytesystems.github.io/dhis2-ebscore-sdk/)
- **Issues**: [GitHub Issues](https://github.com/everybytesystems/dhis2-ebscore-sdk/issues)
- **Discussions**: [GitHub Discussions](https://github.com/everybytesystems/dhis2-ebscore-sdk/discussions)
- **Email**: support@everybytesystems.com

---

**Made with ❤️ by [EveryByte Systems](https://everybytesystems.com)**