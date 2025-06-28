# DHIS2 DataFlow SDK — Technical Documentation

## 1. Introduction

The **DHIS2 DataFlow SDK** is a powerful Kotlin Multiplatform toolkit for Android and iOS, providing:

- Full integration with DHIS2 endpoints: metadata via `/api/metadata`, analytics via `/analytics` & Visualisation API :contentReference[oaicite:1]{index=1}
- Tracker data (`/tracker/enrollments`, `/tracker/events`)
- Live DataValueSets (`/api/dataValueSets`)
- A shared **Compose Multiplatform** UI layer, compatible with third-party charting libraries
- An **AI Insight Engine** that generates natural language commentary, trend detection, and recommendations

Combine real-time data, cross-platform UI, and insights—accelerating dev and boosting user engagement.

---

## 2. Architecture

A modular, layered structure designed for performance and offline capability:

- **UI Layer**: Compose Multiplatform + optional native (SwiftUI/UIKit) :contentReference[oaicite:2]{index=2}
- **Core Modules**:
    - `AuthManager`, `MetadataService`, `AnalyticsService`, `TrackerService`, `DataValueSetService`
    - `VisualisationService`, `AIInsightService`
    - `SyncManager` + SQLDelight (`CacheLayer`) :contentReference[oaicite:3]{index=3}
- **Ktor HTTP Client**: handles JSON, auth, timeouts, logging
- **Platform Bridge**: secure storage, permissions, background tasks
- **Offline Sync**: SQLDelight caching, delta-sync, retry, and conflict resolution

Highlights: cross-platform consistency, offline resilience, and extensibility.

---

## 3. Core Modules & API Functionality

- **AuthManager**: Basic, PAT, OAuth2/OIDC support with secure tokens
- **MetadataService**: sync and cache metadata
- **AnalyticsService**: dimensional queries to `/analytics`
- **TrackerService**: CRUD enrollment/event in offline-first flows
- **DataValueSetService**: live data operations with queuing
- **VisualisationService**: fetch chart/pivot/map config
- **AIInsightService**: generates narrative insights
- **SyncManager + SQLDelight**: sync coordination, offline persistence, and conflict handling

---

## 4. UI Layer Integration

Develop shared UI components:

- `ChartView`, `PivotTableView`, `MapView`, `DashboardScreen`
- Use Compose chart libraries like KoalaPlot, AAY-chart, CMPCharts :contentReference[oaicite:4]{index=4}
- Embed native UI for specialized features
- Include AI commentary UI elements via `AIInsightService`

---

## 5. Usage Examples

```kotlin
val sdk = DataFlowSdk.initialize(
  baseUrl = "https://dhis2.example.org",
  authConfig = AuthConfig.Basic("user","pass"),
  dbDriver = createSqlDriver(DbSchema, "dhis2.db")
)

sdk.metadataService.syncAll()
val elements = sdk.metadataService.getDataElements()

val analytics = sdk.analyticsService.query(
  dimensions = listOf("pe:LAST_YEAR","ou:USER_ORGUNIT"),
  filters = listOf("dx:dataElementId")
)

val dvs = sdk.dataValueSetService.fetch("DsUID","202507","OuUID")
sdk.dataValueSetService.post(DataValueSet(...))

val chart = sdk.visualisationService.getChart("ChartUID")
val insight = sdk.aiInsightService.explain(chart.data)
ChartView(chart)
InsightBanner(insight.summary)
```

## 6. Data Persistence & Offline Strategy

Ensuring robust offline functionality is vital for mobile use cases. The SDK uses SQLDelight for type-safe, cross-platform database management:

- Shared `.sq` files in `commonMain` define schema and queries; code generation ensures compile-time safety :contentReference[oaicite:1]{index=1}.
- Common `expect/actual` driver setup for Android (`AndroidSqliteDriver`) and iOS (`NativeSqliteDriver`).

A centralized **SyncManager** orchestrates:

- **Metadata refresh**—initial full load, followed by delta updates.
- **Caching analytics/visualisations**—indexed by query signatures.
- **Offline queuing** of tracker events and DataValueSets with background retry logic.
- **Conflict resolution** using timestamp or last-write strategies, plus exponential backoff and jitter.

Ktor HTTP client—configured with `HttpTimeout` and `HttpRequestRetry`—handles network communication, including retries on failures and consistent logging for debug and production environments :contentReference[oaicite:2]{index=2}.

**Benefits:**

- Fully offline-capable UX: dashboards, forms, and analytics remain functional without connectivity.
- Minimal bandwidth use via delta syncing.
- Reactive UI updates via SQLDelight flows.

---

## 7. Testing Strategy

Multi-layered testing ensures quality and cross-platform consistency:

### 📦 Unit Tests
- Use MockK/Mokkery and `kotlin.test` to isolate and test services like AuthManager, AnalyticsService, and SyncManager.

### 🔗 Integration Tests
- Employ in-memory SQLDelight (e.g., `AndroidSqliteDriver(name = null)`) and Ktor MockEngine or a DHIS2 sandbox to validate real workflows, including metadata sync and posting data.

### 📷 UI Snapshot Tests
- **Android**: Use Paparazzi to capture Compose UI snapshots against golden images.
- **Flutter wrapper**: Use golden tests to ensure visual consistency.

### 🤖 AI Prompt Tests
- Validate AIInsightService output includes expected narrative patterns (e.g., “trend”, “outlier”).
- Simulate AI failure to ensure graceful UI degradation.

### 🔄 CI Integration
- Automated execution across JVM and Native platforms.
- Ensure snapshot compliance, test coverage enforcement, and test stability in CI.

---

## 8. CI/CD & Distribution

A robust pipeline handles building, testing, and publishing SDK modules:

### 🔄 CI Pipeline
- Runs linting (Ktlint, Detekt), all tests (unit, integration, snapshot, AI prompts), and code coverage analysis on every PR.
- Ensures code quality and prevents regressions.

### 📦 Artifact Distribution
| Target         | Format                            | Distribution Channel              |
|----------------|-----------------------------------|-----------------------------------|
| Android/KMP    | AAR/JAR                           | Maven Central                     |
| iOS            | XCFramework + Podspec / SPM pkg   | CocoaPods & Swift Package Manager |
| Flutter        | Dart package                      | Pub.dev                           |

### 🚀 Release Workflow
1. A PR merge with a release tag triggers CI build and test execution.
2. Upon success, artifacts are automatically published.
3. End-to-end integration is validated via sample app builds across platforms.

---

## 9. Roadmap & Extensions

The DataFlow SDK is designed for extensibility and modular growth:

| Feature                                | Description |
|----------------------------------------|-------------|
| **OAuth2/OIDC & SSO**                 | Support for identity providers like Keycloak, Okta, Azure AD via AuthManager |
| **DHIS2 Program Rule Engine**         | Adds real-time validation & conditional logic support in tracker forms |
| **Push Notifications**                | FCM (Android) and APNS (iOS) integration for alerts (events, forms, enrollments) |
| **Interactive Geospatial Maps**       | Mapbox/OSM integration with AI-based geographic commentary (e.g., hotspots) |
| **Pluggable AI Backends / Offline LLM** | Support for on-device LLMs (e.g., LLaMA) and remote GPT services with easy backend swapping |

Each extension is optional and can be integrated as needed—ensuring the core SDK remains performant and lightweight.

---

*References:*
- JetBrains tutorial: combining Ktor & SQLDelight in KMP apps :contentReference[oaicite:3]{index=3}
- SQLDelight multi-platform caching guide :contentReference[oaicite:4]{index=4}
- Ktor timeout & retry plugin documentation :contentReference[oaicite:5]{index=5}

---
