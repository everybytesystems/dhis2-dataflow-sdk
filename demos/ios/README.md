# 🍎 EBSCore SDK - iOS Demo

Native iOS application demonstrating the EBSCore SDK integration with SwiftUI.

## 📱 Features

- **Native iOS UI** - Built with SwiftUI
- **DHIS2 Integration** - Complete SDK functionality
- **Offline Support** - Work without internet connection
- **iOS-Native Auth** - Biometric authentication support
- **Health Data** - Integration with HealthKit (optional)
- **Push Notifications** - Real-time sync notifications

## 🚀 Getting Started

### Prerequisites

- **Xcode 15+** - Latest version recommended
- **iOS 15+** - Minimum deployment target
- **macOS** - Required for iOS development
- **Apple Developer Account** - For device testing

### Setup

1. **Open in Xcode:**
   ```bash
   open demos/ios/EBSCoreDemo.xcodeproj
   ```

2. **Configure Team:**
   - Select your development team in project settings
   - Update bundle identifier if needed

3. **Install Dependencies:**
   - Dependencies are managed via Swift Package Manager
   - Xcode will automatically resolve dependencies

4. **Configure DHIS2:**
   - Edit `Config.swift` with your DHIS2 server details
   - Update authentication credentials

### Running

1. **Simulator:**
   - Select iOS Simulator
   - Press `Cmd+R` to build and run

2. **Device:**
   - Connect iOS device
   - Select device in Xcode
   - Press `Cmd+R` to build and run

## 🏗️ Architecture

### Project Structure

```
EBSCoreDemo/
├── App/
│   ├── EBSCoreDemoApp.swift      # App entry point
│   └── ContentView.swift         # Main view
├── Views/
│   ├── DashboardView.swift       # Dashboard tab
│   ├── MetadataView.swift        # Metadata tab
│   ├── AnalyticsView.swift       # Analytics tab
│   └── SettingsView.swift        # Settings tab
├── Models/
│   ├── AppState.swift            # App state management
│   └── DHIS2Models.swift         # DHIS2 data models
├── Services/
│   ├── DHIS2Service.swift        # SDK integration
│   └── StorageService.swift      # Local storage
└── Resources/
    ├── Config.swift              # Configuration
    └── Assets.xcassets           # App assets
```

### Key Components

- **DHIS2Service** - Wraps EBSCore SDK for iOS
- **AppState** - ObservableObject for state management
- **SwiftUI Views** - Native iOS user interface
- **Combine** - Reactive programming for data flow

## 🔧 Configuration

### DHIS2 Server

Edit `Config.swift`:

```swift
struct Config {
    static let dhis2BaseURL = "https://play.dhis2.org/2.42.0"
    static let defaultUsername = "admin"
    static let defaultPassword = "district"
    
    // Optional: OAuth2 configuration
    static let oauthClientId = "your-client-id"
    static let oauthClientSecret = "your-client-secret"
}
```

### App Configuration

- **Bundle Identifier:** `com.everybytesystems.ebscore.demo.ios`
- **Display Name:** EBSCore SDK Demo
- **Version:** 1.0.0
- **Minimum iOS:** 15.0

## 📱 Features Demo

### Dashboard
- System information display
- Connection status indicator
- Quick statistics overview
- Real-time sync status

### Metadata
- Data elements browser
- Organization units hierarchy
- Programs and datasets
- Search and filtering

### Analytics
- Data visualization
- Charts and graphs
- Export capabilities
- Drill-down analysis

### Settings
- Server configuration
- Authentication settings
- Sync preferences
- App preferences

## 🧪 Testing

### Unit Tests
```bash
# Run unit tests
xcodebuild test -scheme EBSCoreDemo -destination 'platform=iOS Simulator,name=iPhone 15'
```

### UI Tests
```bash
# Run UI tests
xcodebuild test -scheme EBSCoreDemoUITests -destination 'platform=iOS Simulator,name=iPhone 15'
```

## 📦 Distribution

### TestFlight
1. Archive the app in Xcode
2. Upload to App Store Connect
3. Add to TestFlight for beta testing

### App Store
1. Complete app review requirements
2. Submit for App Store review
3. Release to App Store

## 🔐 Security

### Keychain Storage
- Credentials stored in iOS Keychain
- Biometric authentication support
- Secure token management

### Network Security
- Certificate pinning
- TLS 1.3 encryption
- Network security policies

## 🆘 Troubleshooting

### Common Issues

1. **Build Errors:**
   - Clean build folder (`Cmd+Shift+K`)
   - Reset package caches
   - Update Xcode and dependencies

2. **Simulator Issues:**
   - Reset simulator
   - Clear app data
   - Restart Xcode

3. **Device Issues:**
   - Check provisioning profiles
   - Verify team settings
   - Update device iOS version

### Debug Logging

Enable debug logging in `Config.swift`:

```swift
struct Config {
    static let enableDebugLogging = true
    static let logLevel = LogLevel.debug
}
```

## 📚 Resources

- [iOS Development Guide](https://developer.apple.com/ios/)
- [SwiftUI Documentation](https://developer.apple.com/documentation/swiftui)
- [EBSCore SDK Documentation](../../docs/GETTING_STARTED.md)

---

**Note:** This is a demo application structure. The actual iOS demo implementation requires Xcode project files and Swift code, which are best created directly in Xcode with proper iOS project templates.