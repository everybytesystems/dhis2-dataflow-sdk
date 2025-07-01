# 🚀 EBSCore SDK Demo Applications

This directory contains full demo applications showcasing the EBSCore SDK on different platforms. Each demo is a complete, runnable application demonstrating real-world usage.

## 📱 Platform Demos

### 🤖 [Android Demo](android/)
**Native Android application** demonstrating:
- DHIS2 authentication and connection
- Data synchronization and offline capabilities
- Material Design UI with Jetpack Compose
- Real-time data visualization
- Tracker data management

**Run:** `./gradlew :demos:android:installDebug`

### 🍎 [iOS Demo](ios/)
**Native iOS application** demonstrating:
- DHIS2 integration with SwiftUI
- Offline-first data management
- iOS-native authentication flows
- Health data visualization
- Push notifications for data sync

**Run:** Open in Xcode and run on simulator/device

### 🖥️ [Desktop Demo](desktop/)
**Compose Multiplatform desktop application** demonstrating:
- Cross-platform DHIS2 integration
- Advanced analytics and reporting
- Data export capabilities
- Multi-window interface
- Bulk data operations

**Run:** `./gradlew :demos:desktop:run`

### 🌐 [Web Demo](web/)
**Kotlin/JS web application** demonstrating:
- Browser-based DHIS2 integration
- Progressive Web App (PWA) capabilities
- Real-time dashboards
- Responsive design
- Web-based data entry

**Run:** `./gradlew :demos:web:jsBrowserDevelopmentRun`

## 🎯 Demo Features

Each demo application includes:

### Core Features
- ✅ **Authentication** - Multiple auth methods (Basic, OAuth2, PAT)
- ✅ **Data Sync** - Bidirectional synchronization with DHIS2
- ✅ **Offline Support** - Work without internet connection
- ✅ **Error Handling** - Robust error handling and recovery
- ✅ **Performance** - Optimized for production use

### Platform-Specific Features
- ✅ **Native UI** - Platform-appropriate user interface
- ✅ **Platform APIs** - Integration with platform-specific features
- ✅ **Performance** - Optimized for each platform
- ✅ **Best Practices** - Following platform conventions

## 🚀 Quick Start

### Prerequisites
- JDK 17+
- Android Studio (for Android demo)
- Xcode (for iOS demo, macOS only)
- DHIS2 instance credentials

### Running Demos

1. **Clone and setup:**
   ```bash
   git clone https://github.com/everybytesystems/ebscore-sdk.git
   cd ebscore-sdk
   ```

2. **Configure DHIS2 connection:**
   Edit the configuration files in each demo to point to your DHIS2 instance.

3. **Run specific demo:**
   ```bash
   # Android
   ./gradlew :demos:android:installDebug
   
   # Desktop
   ./gradlew :demos:desktop:run
   
   # Web
   ./gradlew :demos:web:jsBrowserDevelopmentRun
   ```

## 🔧 Configuration

Each demo includes configuration files for:
- **DHIS2 Connection** - Server URL and authentication
- **Feature Flags** - Enable/disable specific features
- **UI Themes** - Customize appearance
- **Performance Settings** - Optimize for your use case

## 📚 Learning Path

1. **Start with [Android Demo](android/)** - Most comprehensive example
2. **Try [Desktop Demo](desktop/)** - Advanced analytics features
3. **Explore [Web Demo](web/)** - Browser-based integration
4. **Build [iOS Demo](ios/)** - Native iOS development

## 🤝 Contributing

Want to improve the demos or add new features?
- See [CONTRIBUTING.md](../CONTRIBUTING.md) for guidelines
- Each demo has its own README with specific setup instructions
- Follow platform-specific best practices

## 🆘 Support

- **Issues:** [GitHub Issues](https://github.com/everybytesystems/ebscore-sdk/issues)
- **Discussions:** [GitHub Discussions](https://github.com/everybytesystems/ebscore-sdk/discussions)
- **Documentation:** [Getting Started Guide](../docs/GETTING_STARTED.md)

---

**Ready to build your own DHIS2 application? Start with these demos! 🚀**