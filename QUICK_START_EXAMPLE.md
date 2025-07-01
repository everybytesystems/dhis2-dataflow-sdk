# 🚀 Quick Start: Login to DHIS2 in 5 Minutes

## What You'll Do
Connect to a real DHIS2 server and login with your credentials.

## 📱 Android Example

### Step 1: Add the SDK to your app
```kotlin
// In your app's build.gradle.kts
dependencies {
    implementation("com.everybytesystems.ebscore-sdk:sdk:1.0.0")
}
```

### Step 2: Initialize the SDK
```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var sdk: EBSCoreSdk
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the SDK
        sdk = EBSCoreSdkBuilder()
            .baseUrl("https://play.dhis2.org/dev")  // DHIS2 demo server
            .apiVersion("41")
            .enableLogging(true)
            .databaseDriverFactory(DatabaseDriverFactory(this))
            .secureStorageFactory(SecureStorageFactory(this))
            .build()
        
        lifecycleScope.launch {
            sdk.initialize()
        }
    }
}
```

### Step 3: Login to DHIS2
```kotlin
// In your login screen or activity
class LoginActivity : ComponentActivity() {
    
    private fun loginToDHIS2() {
        lifecycleScope.launch {
            try {
                // Login with username and password
                val result = sdk.authenticate(
                    AuthConfig.Basic(
                        username = "admin",      // DHIS2 username
                        password = "district"    // DHIS2 password
                    )
                )
                
                when (result) {
                    is ApiResponse.Success -> {
                        val user = result.data
                        println("✅ Login successful!")
                        println("Welcome ${user.displayName}!")
                        println("Your roles: ${user.authorities}")
                        
                        // Now you can use DHIS2 data
                        loadDHIS2Data()
                    }
                    
                    is ApiResponse.Error -> {
                        println("❌ Login failed: ${result.message}")
                        // Show error to user
                    }
                    
                    is ApiResponse.Loading -> {
                        println("🔄 Logging in...")
                        // Show loading spinner
                    }
                }
                
            } catch (e: Exception) {
                println("💥 Error: ${e.message}")
            }
        }
    }
    
    private suspend fun loadDHIS2Data() {
        // Sync metadata from DHIS2
        sdk.metadataService.syncAll()
        
        // Get data elements
        val dataElements = sdk.metadataService.getDataElements()
        println("📊 Found ${dataElements.size} data elements")
        
        // Get organisation units
        val orgUnits = sdk.metadataService.getOrganisationUnits()
        println("🏥 Found ${orgUnits.size} organisation units")
        
        // Search for specific data
        val ancData = sdk.metadataService.searchDataElements("ANC")
        println("🔍 Found ${ancData.size} ANC-related data elements")
    }
}
```

## 🍎 iOS Example

### Step 1: Add SDK to your iOS project
```swift
// In your Package.swift or Xcode
dependencies: [
    .package(url: "https://github.com/everybytesystems/dhis2-ebscore-sdk", from: "1.0.0")
]
```

### Step 2: Initialize and Login
```swift
import DHIS2EBSCoreSDK

class ContentView: View {
    @State private var sdk: EBSCoreSdk?
    @State private var loginStatus = "Not logged in"
    @State private var userData = ""
    
    var body: some View {
        VStack {
            Text(loginStatus)
            Text(userData)
            
            Button("Login to DHIS2") {
                Task {
                    await loginToDHIS2()
                }
            }
        }
        .onAppear {
            initializeSDK()
        }
    }
    
    private func initializeSDK() {
        sdk = EBSCoreSdkBuilder()
            .baseUrl("https://play.dhis2.org/dev")
            .apiVersion("41")
            .enableLogging(true)
            .databaseDriverFactory(DatabaseDriverFactory())
            .secureStorageFactory(SecureStorageFactory())
            .build()
        
        Task {
            await sdk?.initialize()
        }
    }
    
    private func loginToDHIS2() async {
        guard let sdk = sdk else { return }
        
        loginStatus = "🔄 Logging in..."
        
        do {
            let result = await sdk.authenticate(
                AuthConfig.Basic(
                    username: "admin",
                    password: "district"
                )
            )
            
            switch result {
            case .success(let user):
                loginStatus = "✅ Login successful!"
                userData = "Welcome \(user.displayName)!\nRoles: \(user.authorities.joined(separator: ", "))"
                
                // Load DHIS2 data
                await loadDHIS2Data()
                
            case .error(let message):
                loginStatus = "❌ Login failed: \(message)"
                
            case .loading:
                loginStatus = "🔄 Logging in..."
            }
            
        } catch {
            loginStatus = "💥 Error: \(error.localizedDescription)"
        }
    }
    
    private func loadDHIS2Data() async {
        guard let sdk = sdk else { return }
        
        // Sync metadata
        await sdk.metadataService.syncAll()
        
        // Get data
        let dataElements = await sdk.metadataService.getDataElements()
        let orgUnits = await sdk.metadataService.getOrganisationUnits()
        
        userData += "\n📊 Found \(dataElements.count) data elements"
        userData += "\n🏥 Found \(orgUnits.count) organisation units"
    }
}
```

## 🌐 **Real DHIS2 Servers You Can Try**

### 1. **DHIS2 Demo Server** (Free to use)
```kotlin
.baseUrl("https://play.dhis2.org/dev")
// Username: admin
// Password: district
```

### 2. **Your Organization's DHIS2**
```kotlin
.baseUrl("https://your-organization.dhis2.org")
// Use your actual credentials
```

### 3. **Local DHIS2 Instance**
```kotlin
.baseUrl("http://localhost:8080")
// If you're running DHIS2 locally
```

## 🔐 **Different Login Methods**

### Username & Password
```kotlin
sdk.authenticate(AuthConfig.Basic("username", "password"))
```

### Personal Access Token (More Secure)
```kotlin
sdk.authenticate(AuthConfig.PersonalAccessToken("d2pat_your_token_here"))
```

### OAuth2 (Enterprise)
```kotlin
sdk.authenticate(AuthConfig.OAuth2(
    clientId = "your-app-id",
    redirectUri = "yourapp://callback",
    authorizationUrl = "https://your-idp.org/auth",
    tokenUrl = "https://your-idp.org/token"
))
```

## 📊 **What You Can Do After Login**

```kotlin
// Get all data elements
val dataElements = sdk.metadataService.getDataElements()

// Get organisation units by level
val districts = sdk.metadataService.getOrganisationUnitsByLevel(3)

// Search for specific data
val malaria = sdk.metadataService.searchDataElements("Malaria")

// Get programs
val programs = sdk.metadataService.getPrograms()

// Get data sets
val dataSets = sdk.metadataService.getDataSets()

// Check what's available
println("Available data:")
println("- ${dataElements.size} data elements")
println("- ${districts.size} districts") 
println("- ${programs.size} programs")
println("- ${dataSets.size} data sets")
```

## 🔄 **Offline Support**

The SDK automatically:
- ✅ Saves data locally when you're online
- ✅ Works offline with cached data
- ✅ Syncs changes when you're back online
- ✅ Handles conflicts intelligently

```kotlin
// This works even offline!
val cachedData = sdk.metadataService.getDataElements()
println("Using ${cachedData.size} cached data elements")
```

## 🚨 **Common Issues & Solutions**

### "Connection failed"
- ✅ Check your internet connection
- ✅ Verify the DHIS2 server URL is correct
- ✅ Make sure the server is running

### "Authentication failed"
- ✅ Double-check username/password
- ✅ Verify you have permission to access the server
- ✅ Try the demo server first: `play.dhis2.org/dev`

### "No data found"
- ✅ Make sure you're logged in first
- ✅ Call `sdk.metadataService.syncAll()` to download data
- ✅ Check if your user has the right permissions

## 🎯 **Try It Now!**

1. **Use the demo server**: `https://play.dhis2.org/dev` with `admin/district`
2. **Copy the Android/iOS code above**
3. **Run it and see the magic happen!**

You'll be connected to a real DHIS2 instance with real health data! 🏥📊

---

**Ready to build amazing DHIS2 apps? Let's go! 🚀**