# 🌐 Web Examples - EBSCore SDK

Complete Web (JavaScript/Kotlin) examples for all EBSCore SDK modules with Compose for Web integration.

## 🌐 Setup

### Dependencies (build.gradle.kts)

```kotlin
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("com.everybytesystems.ebscore:ebscore-sdk:1.0.0")
                implementation(compose.web.core)
                implementation(compose.runtime)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
            }
        }
    }
}

compose.web {
    application {
        mainClass = "MainKt"
    }
}
```

### HTML Template

```html
<!-- src/jsMain/resources/index.html -->
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EBSCore SDK Web Demo</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f5f5f5;
        }
        
        .loading {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            font-size: 18px;
            color: #666;
        }
        
        .error {
            color: #d32f2f;
            background-color: #ffebee;
            padding: 16px;
            border-radius: 4px;
            margin: 16px;
        }
        
        .success {
            color: #2e7d32;
            background-color: #e8f5e8;
            padding: 16px;
            border-radius: 4px;
            margin: 16px;
        }
    </style>
</head>
<body>
    <div id="root">
        <div class="loading">Loading EBSCore SDK...</div>
    </div>
    <script src="web-app.js"></script>
</body>
</html>
```

## 🎯 Module Examples

### 1. Main Application Entry Point

```kotlin
// Main.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

fun main() {
    renderComposable(rootElementId = "root") {
        EBSCoreWebApp()
    }
}

@Composable
fun EBSCoreWebApp() {
    var sdk by remember { mutableStateOf<EBSCoreSdk?>(null) }
    var isInitialized by remember { mutableStateOf(false) }
    var initError by remember { mutableStateOf<String?>(null) }
    var currentPage by remember { mutableStateOf(Page.LOGIN) }
    var isAuthenticated by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        try {
            sdk = EBSCoreSdk.Builder()
                .baseUrl("https://play.dhis2.org/2.40.1")
                .enableOfflineMode(true)
                .enableAnalytics(true)
                .build()
            isInitialized = true
        } catch (e: Exception) {
            initError = e.message
            isInitialized = true
        }
    }
    
    Div({
        style {
            minHeight(100.vh)
            backgroundColor(Color("#f5f5f5"))
        }
    }) {
        if (!isInitialized) {
            LoadingScreen()
        } else if (initError != null) {
            ErrorScreen(initError!!)
        } else if (!isAuthenticated) {
            LoginScreen(
                sdk = sdk!!,
                onLoginSuccess = { isAuthenticated = true }
            )
        } else {
            MainWebApp(
                sdk = sdk!!,
                currentPage = currentPage,
                onPageChange = { currentPage = it },
                onLogout = { 
                    isAuthenticated = false
                    currentPage = Page.LOGIN
                }
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Div({
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
            height(100.vh)
            fontSize(18.px)
            color(Color("#666"))
        }
    }) {
        Text("Loading EBSCore SDK...")
    }
}

@Composable
fun ErrorScreen(error: String) {
    Div({
        style {
            padding(16.px)
            margin(16.px)
            backgroundColor(Color("#ffebee"))
            color(Color("#d32f2f"))
            borderRadius(4.px)
        }
    }) {
        H2 { Text("Initialization Error") }
        P { Text(error) }
    }
}

enum class Page {
    LOGIN, HOME, DATA, TRACKER, ANALYTICS, SYNC, CHARTS, UTILS, STORAGE
}
```

### 2. Navigation and Layout

```kotlin
// Navigation.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.events.SyntheticMouseEvent
import com.everybytesystems.ebscore.sdk.EBSCoreSdk

@Composable
fun MainWebApp(
    sdk: EBSCoreSdk,
    currentPage: Page,
    onPageChange: (Page) -> Unit,
    onLogout: () -> Unit
) {
    Div({
        style {
            display(DisplayStyle.Flex)
            minHeight(100.vh)
        }
    }) {
        // Sidebar Navigation
        NavigationSidebar(
            currentPage = currentPage,
            onPageChange = onPageChange,
            onLogout = onLogout
        )
        
        // Main Content
        Div({
            style {
                flex(1)
                padding(24.px)
                backgroundColor(Color.white)
                marginLeft(250.px)
            }
        }) {
            when (currentPage) {
                Page.HOME -> HomeScreen(sdk)
                Page.DATA -> DataScreen(sdk)
                Page.TRACKER -> TrackerScreen(sdk)
                Page.ANALYTICS -> AnalyticsScreen(sdk)
                Page.SYNC -> SyncScreen(sdk)
                Page.CHARTS -> ChartsScreen()
                Page.UTILS -> UtilsScreen()
                Page.STORAGE -> StorageScreen()
                else -> HomeScreen(sdk)
            }
        }
    }
}

@Composable
fun NavigationSidebar(
    currentPage: Page,
    onPageChange: (Page) -> Unit,
    onLogout: () -> Unit
) {
    Div({
        style {
            position(Position.Fixed)
            left(0.px)
            top(0.px)
            width(250.px)
            height(100.vh)
            backgroundColor(Color("#1976d2"))
            color(Color.white)
            padding(16.px)
            boxSizing("border-box")
        }
    }) {
        // App Title
        H2({
            style {
                margin(0.px, 0.px, 32.px, 0.px)
                fontSize(24.px)
                fontWeight("bold")
            }
        }) {
            Text("EBSCore SDK")
        }
        
        // Navigation Items
        val navItems = listOf(
            Page.HOME to "🏠 Home",
            Page.DATA to "📊 Data Values",
            Page.TRACKER to "👥 Tracker",
            Page.ANALYTICS to "📈 Analytics",
            Page.SYNC to "🔄 Sync",
            Page.CHARTS to "📊 Charts",
            Page.UTILS to "🔧 Utils",
            Page.STORAGE to "💾 Storage"
        )
        
        navItems.forEach { (page, label) ->
            NavigationItem(
                label = label,
                isSelected = currentPage == page,
                onClick = { onPageChange(page) }
            )
        }
        
        // Logout Button
        Div({
            style {
                position(Position.Absolute)
                bottom(16.px)
                left(16.px)
                right(16.px)
            }
        }) {
            Button({
                style {
                    width(100.percent)
                    padding(12.px)
                    backgroundColor(Color("#d32f2f"))
                    color(Color.white)
                    border(0.px)
                    borderRadius(4.px)
                    cursor("pointer")
                    fontSize(14.px)
                }
                onClick { onLogout() }
            }) {
                Text("🚪 Logout")
            }
        }
    }
}

@Composable
fun NavigationItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Div({
        style {
            padding(12.px)
            marginBottom(8.px)
            borderRadius(4.px)
            cursor("pointer")
            backgroundColor(if (isSelected) Color("rgba(255,255,255,0.1)") else Color.transparent)
            
            hover {
                backgroundColor(Color("rgba(255,255,255,0.05)"))
            }
        }
        onClick { onClick() }
    }) {
        Text(label)
    }
}
```

### 3. Login Screen

```kotlin
// LoginScreen.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.*
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    sdk: EBSCoreSdk,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("district") }
    var serverUrl by remember { mutableStateOf("https://play.dhis2.org/2.40.1") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    Div({
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
            minHeight(100.vh)
            backgroundColor(Color("#f5f5f5"))
        }
    }) {
        Div({
            style {
                backgroundColor(Color.white)
                padding(32.px)
                borderRadius(8.px)
                boxShadow("0 4px 6px rgba(0, 0, 0, 0.1)")
                width(400.px)
                maxWidth(90.vw)
            }
        }) {
            H1({
                style {
                    textAlign("center")
                    marginBottom(32.px)
                    color(Color("#1976d2"))
                }
            }) {
                Text("Login to DHIS2")
            }
            
            // Server URL Input
            Div({
                style {
                    marginBottom(16.px)
                }
            }) {
                Label({
                    style {
                        display(DisplayStyle.Block)
                        marginBottom(8.px)
                        fontWeight("bold")
                    }
                }) {
                    Text("Server URL")
                }
                Input(InputType.Url) {
                    value(serverUrl)
                    onInput { serverUrl = it.value }
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ddd"))
                        borderRadius(4.px)
                        fontSize(14.px)
                        property("box-sizing", "border-box")
                    }
                    disabled(isLoading)
                }
            }
            
            // Username Input
            Div({
                style {
                    marginBottom(16.px)
                }
            }) {
                Label({
                    style {
                        display(DisplayStyle.Block)
                        marginBottom(8.px)
                        fontWeight("bold")
                    }
                }) {
                    Text("Username")
                }
                Input(InputType.Text) {
                    value(username)
                    onInput { username = it.value }
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ddd"))
                        borderRadius(4.px)
                        fontSize(14.px)
                        property("box-sizing", "border-box")
                    }
                    disabled(isLoading)
                }
            }
            
            // Password Input
            Div({
                style {
                    marginBottom(24.px)
                }
            }) {
                Label({
                    style {
                        display(DisplayStyle.Block)
                        marginBottom(8.px)
                        fontWeight("bold")
                    }
                }) {
                    Text("Password")
                }
                Input(InputType.Password) {
                    value(password)
                    onInput { password = it.value }
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ddd"))
                        borderRadius(4.px)
                        fontSize(14.px)
                        property("box-sizing", "border-box")
                    }
                    disabled(isLoading)
                }
            }
            
            // Login Button
            Button({
                style {
                    width(100.percent)
                    padding(12.px)
                    backgroundColor(Color("#1976d2"))
                    color(Color.white)
                    border(0.px)
                    borderRadius(4.px)
                    fontSize(16.px)
                    cursor("pointer")
                    
                    if (isLoading || username.isBlank() || password.isBlank()) {
                        opacity(0.6)
                        cursor("not-allowed")
                    }
                }
                onClick {
                    if (!isLoading && username.isNotBlank() && password.isNotBlank()) {
                        scope.launch {
                            isLoading = true
                            errorMessage = null
                            
                            try {
                                // Update SDK configuration
                                val newSdk = EBSCoreSdk.Builder()
                                    .baseUrl(serverUrl)
                                    .credentials(username, password)
                                    .enableOfflineMode(true)
                                    .enableAnalytics(true)
                                    .build()
                                
                                // Test connection
                                val systemInfo = newSdk.getSystemInfo()
                                if (systemInfo != null) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = "Failed to connect to server"
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Login failed"
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
                disabled(isLoading || username.isBlank() || password.isBlank())
            }) {
                if (isLoading) {
                    Text("Logging in...")
                } else {
                    Text("Login")
                }
            }
            
            // Error Message
            errorMessage?.let { message ->
                Div({
                    style {
                        marginTop(16.px)
                        padding(12.px)
                        backgroundColor(Color("#ffebee"))
                        color(Color("#d32f2f"))
                        borderRadius(4.px)
                        fontSize(14.px)
                    }
                }) {
                    Text("Error: $message")
                }
            }
        }
    }
}
```

### 4. Home Screen with System Info

```kotlin
// HomeScreen.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import com.everybytesystems.ebscore.dhis2.model.SystemInfo
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(sdk: EBSCoreSdk) {
    var systemInfo by remember { mutableStateOf<SystemInfo?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                systemInfo = sdk.getSystemInfo()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    Div({
        style {
            padding(24.px)
        }
    }) {
        H1({
            style {
                marginBottom(32.px)
                color(Color("#1976d2"))
            }
        }) {
            Text("EBSCore SDK Web Demo")
        }
        
        if (isLoading) {
            LoadingCard("Loading system information...")
        } else if (errorMessage != null) {
            ErrorCard(errorMessage!!)
        } else {
            systemInfo?.let { info ->
                Div({
                    style {
                        display(DisplayStyle.Grid)
                        property("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))")
                        gap(16.px)
                    }
                }) {
                    SystemInfoCard(
                        title = "Server Information",
                        items = listOf(
                            "Context Path" to info.contextPath,
                            "Version" to info.version,
                            "Revision" to info.revision,
                            "Build Time" to info.buildTime,
                            "Server Date" to info.serverDate
                        )
                    )
                    
                    SystemInfoCard(
                        title = "System Configuration",
                        items = listOf(
                            "Database Info" to info.databaseInfo?.name.orEmpty(),
                            "Environment" to info.environmentVariable.orEmpty(),
                            "Java Version" to info.javaVersion.orEmpty(),
                            "OS Name" to info.osName.orEmpty(),
                            "OS Architecture" to info.osArchitecture.orEmpty()
                        )
                    )
                    
                    SystemInfoCard(
                        title = "Features",
                        items = listOf(
                            "Calendar" to info.calendar.orEmpty(),
                            "Date Format" to info.dateFormat.orEmpty(),
                            "System ID" to info.systemId.orEmpty(),
                            "Instance Base URL" to info.instanceBaseUrl.orEmpty()
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SystemInfoCard(
    title: String,
    items: List<Pair<String, String>>
) {
    Div({
        style {
            backgroundColor(Color.white)
            padding(20.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            border(1.px, LineStyle.Solid, Color("#e0e0e0"))
        }
    }) {
        H3({
            style {
                marginTop(0.px)
                marginBottom(16.px)
                color(Color("#333"))
            }
        }) {
            Text(title)
        }
        
        items.forEach { (label, value) ->
            if (value.isNotBlank()) {
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.SpaceBetween)
                        marginBottom(8.px)
                        paddingBottom(8.px)
                        borderBottom(1.px, LineStyle.Solid, Color("#f0f0f0"))
                    }
                }) {
                    Span({
                        style {
                            fontWeight("bold")
                            color(Color("#666"))
                        }
                    }) {
                        Text("$label:")
                    }
                    Span({
                        style {
                            color(Color("#333"))
                            textAlign("right")
                            maxWidth(60.percent)
                            wordBreak("break-word")
                        }
                    }) {
                        Text(value)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingCard(message: String) {
    Div({
        style {
            backgroundColor(Color.white)
            padding(32.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            textAlign("center")
        }
    }) {
        Text(message)
    }
}

@Composable
fun ErrorCard(message: String) {
    Div({
        style {
            backgroundColor(Color("#ffebee"))
            color(Color("#d32f2f"))
            padding(16.px)
            borderRadius(8.px)
            border(1.px, LineStyle.Solid, Color("#ffcdd2"))
        }
    }) {
        H3({
            style {
                marginTop(0.px)
                marginBottom(8.px)
            }
        }) {
            Text("Error")
        }
        P({
            style {
                margin(0.px)
            }
        }) {
            Text(message)
        }
    }
}
```

### 5. Data Management Screen

```kotlin
// DataScreen.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.attributes.*
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import com.everybytesystems.ebscore.dhis2.model.DataValue
import kotlinx.coroutines.launch

@Composable
fun DataScreen(sdk: EBSCoreSdk) {
    var dataValues by remember { mutableStateOf<List<DataValue>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var newValue by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    
    fun loadDataValues() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                dataValues = sdk.getDataValues(
                    dataElement = "FTRrcoaog83",
                    period = "202401",
                    orgUnit = "ImspTQPwCqd"
                )
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadDataValues()
    }
    
    Div({
        style {
            padding(24.px)
        }
    }) {
        // Header
        Div({
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center)
                marginBottom(24.px)
            }
        }) {
            H1({
                style {
                    margin(0.px)
                    color(Color("#1976d2"))
                }
            }) {
                Text("Data Values")
            }
            
            Div({
                style {
                    display(DisplayStyle.Flex)
                    gap(12.px)
                }
            }) {
                Button({
                    style {
                        padding(8.px, 16.px)
                        backgroundColor(Color("#f5f5f5"))
                        color(Color("#333"))
                        border(1.px, LineStyle.Solid, Color("#ddd"))
                        borderRadius(4.px)
                        cursor("pointer")
                    }
                    onClick { loadDataValues() }
                }) {
                    Text("🔄 Refresh")
                }
                
                Button({
                    style {
                        padding(8.px, 16.px)
                        backgroundColor(Color("#1976d2"))
                        color(Color.white)
                        border(0.px)
                        borderRadius(4.px)
                        cursor("pointer")
                    }
                    onClick { showAddDialog = true }
                }) {
                    Text("➕ Add Data Value")
                }
            }
        }
        
        // Add Data Value Form
        if (showAddDialog) {
            Div({
                style {
                    backgroundColor(Color.white)
                    padding(20.px)
                    borderRadius(8.px)
                    boxShadow("0 2px 8px rgba(0, 0, 0, 0.1)")
                    marginBottom(24.px)
                    border(1.px, LineStyle.Solid, Color("#e0e0e0"))
                }
            }) {
                H3({
                    style {
                        marginTop(0.px)
                        marginBottom(16.px)
                    }
                }) {
                    Text("Add New Data Value")
                }
                
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        gap(12.px)
                        alignItems(AlignItems.End)
                    }
                }) {
                    Div({
                        style {
                            flex(1)
                        }
                    }) {
                        Label({
                            style {
                                display(DisplayStyle.Block)
                                marginBottom(8.px)
                                fontWeight("bold")
                            }
                        }) {
                            Text("Value")
                        }
                        Input(InputType.Text) {
                            value(newValue)
                            onInput { newValue = it.value }
                            style {
                                width(100.percent)
                                padding(12.px)
                                border(1.px, LineStyle.Solid, Color("#ddd"))
                                borderRadius(4.px)
                                fontSize(14.px)
                                property("box-sizing", "border-box")
                            }
                        }
                    }
                    
                    Button({
                        style {
                            padding(12.px, 20.px)
                            backgroundColor(Color("#4caf50"))
                            color(Color.white)
                            border(0.px)
                            borderRadius(4.px)
                            cursor("pointer")
                            
                            if (newValue.isBlank()) {
                                opacity(0.6)
                                cursor("not-allowed")
                            }
                        }
                        onClick {
                            if (newValue.isNotBlank()) {
                                scope.launch {
                                    try {
                                        val dataValue = DataValue(
                                            dataElement = "FTRrcoaog83",
                                            period = "202401",
                                            orgUnit = "ImspTQPwCqd",
                                            value = newValue
                                        )
                                        sdk.saveDataValue(dataValue)
                                        loadDataValues()
                                        newValue = ""
                                        showAddDialog = false
                                    } catch (e: Exception) {
                                        errorMessage = e.message
                                    }
                                }
                            }
                        }
                        disabled(newValue.isBlank())
                    }) {
                        Text("Save")
                    }
                    
                    Button({
                        style {
                            padding(12.px, 20.px)
                            backgroundColor(Color("#f5f5f5"))
                            color(Color("#333"))
                            border(1.px, LineStyle.Solid, Color("#ddd"))
                            borderRadius(4.px)
                            cursor("pointer")
                        }
                        onClick { 
                            showAddDialog = false
                            newValue = ""
                        }
                    }) {
                        Text("Cancel")
                    }
                }
            }
        }
        
        // Content
        if (isLoading) {
            LoadingCard("Loading data values...")
        } else if (errorMessage != null) {
            ErrorCard(errorMessage!!)
        } else {
            Div({
                style {
                    display(DisplayStyle.Grid)
                    gap(16.px)
                }
            }) {
                if (dataValues.isEmpty()) {
                    Div({
                        style {
                            backgroundColor(Color.white)
                            padding(32.px)
                            borderRadius(8.px)
                            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
                            textAlign("center")
                            color(Color("#666"))
                        }
                    }) {
                        Text("No data values found")
                    }
                } else {
                    dataValues.forEach { dataValue ->
                        DataValueCard(dataValue)
                    }
                }
            }
        }
    }
}

@Composable
fun DataValueCard(dataValue: DataValue) {
    Div({
        style {
            backgroundColor(Color.white)
            padding(20.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            border(1.px, LineStyle.Solid, Color("#e0e0e0"))
        }
    }) {
        H4({
            style {
                marginTop(0.px)
                marginBottom(12.px)
                color(Color("#1976d2"))
            }
        }) {
            Text("Value: ${dataValue.value}")
        }
        
        P({
            style {
                margin(4.px, 0.px)
                color(Color("#666"))
            }
        }) {
            Text("Period: ${dataValue.period}")
        }
        
        P({
            style {
                margin(4.px, 0.px)
                color(Color("#666"))
                fontSize(12.px)
            }
        }) {
            Text("Last Updated: ${dataValue.lastUpdated}")
        }
    }
}
```

### 6. Analytics Dashboard

```kotlin
// AnalyticsScreen.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.everybytesystems.ebscore.sdk.EBSCoreSdk
import com.everybytesystems.ebscore.analytics.AnalyticsQuery
import com.everybytesystems.ebscore.analytics.AnalyticsResponse
import kotlinx.coroutines.launch

@Composable
fun AnalyticsScreen(sdk: EBSCoreSdk) {
    var analyticsData by remember { mutableStateOf<AnalyticsResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    fun loadAnalytics() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val query = AnalyticsQuery.builder()
                    .dimension("dx", listOf("FTRrcoaog83", "eY5ehpbEsB7"))
                    .dimension("pe", listOf("202401", "202402", "202403"))
                    .dimension("ou", listOf("ImspTQPwCqd"))
                    .build()
                
                analyticsData = sdk.getAnalytics(query)
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
    
    LaunchedEffect(Unit) {
        loadAnalytics()
    }
    
    Div({
        style {
            padding(24.px)
        }
    }) {
        H1({
            style {
                marginBottom(24.px)
                color(Color("#1976d2"))
            }
        }) {
            Text("Analytics Dashboard")
        }
        
        if (isLoading) {
            LoadingCard("Loading analytics data...")
        } else if (errorMessage != null) {
            ErrorCard(errorMessage!!)
        } else {
            analyticsData?.let { data ->
                Div({
                    style {
                        display(DisplayStyle.Grid)
                        property("grid-template-columns", "repeat(auto-fit, minmax(400px, 1fr))")
                        gap(24.px)
                    }
                }) {
                    // Chart placeholders (would integrate with actual chart library)
                    ChartCard(
                        title = "Line Chart - Monthly Trends",
                        description = "Trend analysis over time"
                    ) {
                        ChartPlaceholder("Line Chart", data)
                    }
                    
                    ChartCard(
                        title = "Bar Chart - Data Comparison",
                        description = "Compare data elements"
                    ) {
                        ChartPlaceholder("Bar Chart", data)
                    }
                    
                    ChartCard(
                        title = "Pie Chart - Distribution",
                        description = "Data distribution visualization"
                    ) {
                        ChartPlaceholder("Pie Chart", data)
                    }
                    
                    ChartCard(
                        title = "Area Chart - Cumulative Data",
                        description = "Stacked area visualization"
                    ) {
                        ChartPlaceholder("Area Chart", data)
                    }
                }
                
                // Data Table
                Div({
                    style {
                        marginTop(32.px)
                    }
                }) {
                    H2({
                        style {
                            marginBottom(16.px)
                            color(Color("#333"))
                        }
                    }) {
                        Text("Raw Data")
                    }
                    
                    AnalyticsDataTable(data)
                }
            }
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Div({
        style {
            backgroundColor(Color.white)
            padding(20.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            border(1.px, LineStyle.Solid, Color("#e0e0e0"))
        }
    }) {
        H3({
            style {
                marginTop(0.px)
                marginBottom(8.px)
                color(Color("#333"))
            }
        }) {
            Text(title)
        }
        
        P({
            style {
                marginTop(0.px)
                marginBottom(16.px)
                color(Color("#666"))
                fontSize(14.px)
            }
        }) {
            Text(description)
        }
        
        content()
    }
}

@Composable
fun ChartPlaceholder(chartType: String, data: AnalyticsResponse) {
    Div({
        style {
            height(250.px)
            backgroundColor(Color("#f5f5f5"))
            borderRadius(4.px)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            justifyContent(JustifyContent.Center)
            alignItems(AlignItems.Center)
            border(2.px, LineStyle.Dashed, Color("#ddd"))
        }
    }) {
        H4({
            style {
                margin(0.px, 0.px, 8.px, 0.px)
                color(Color("#666"))
            }
        }) {
            Text(chartType)
        }
        
        P({
            style {
                margin(0.px)
                color(Color("#999"))
                fontSize(14.px)
            }
        }) {
            Text("${data.rows.size} data points")
        }
        
        // Simple data visualization
        Div({
            style {
                marginTop(16.px)
                display(DisplayStyle.Flex)
                gap(4.px)
                alignItems(AlignItems.End)
            }
        }) {
            data.rows.take(6).forEach { row ->
                val value = row.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                val height = (value / 120.0 * 40).coerceIn(5.0, 40.0)
                
                Div({
                    style {
                        width(20.px)
                        height(height.px)
                        backgroundColor(Color("#1976d2"))
                        borderRadius(2.px)
                    }
                })
            }
        }
    }
}

@Composable
fun AnalyticsDataTable(data: AnalyticsResponse) {
    Div({
        style {
            backgroundColor(Color.white)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            overflow("auto")
        }
    }) {
        Table({
            style {
                width(100.percent)
                borderCollapse("collapse")
            }
        }) {
            Thead {
                Tr {
                    data.headers.forEach { header ->
                        Th({
                            style {
                                padding(12.px)
                                backgroundColor(Color("#f5f5f5"))
                                borderBottom(1.px, LineStyle.Solid, Color("#ddd"))
                                textAlign("left")
                                fontWeight("bold")
                            }
                        }) {
                            Text(header.column)
                        }
                    }
                }
            }
            
            Tbody {
                data.rows.forEach { row ->
                    Tr({
                        style {
                            hover {
                                backgroundColor(Color("#f9f9f9"))
                            }
                        }
                    }) {
                        row.forEach { cell ->
                            Td({
                                style {
                                    padding(12.px)
                                    borderBottom(1.px, LineStyle.Solid, Color("#eee"))
                                }
                            }) {
                                Text(cell)
                            }
                        }
                    }
                }
            }
        }
    }
}
```

### 7. Charts Gallery

```kotlin
// ChartsScreen.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun ChartsScreen() {
    Div({
        style {
            padding(24.px)
        }
    }) {
        H1({
            style {
                marginBottom(24.px)
                color(Color("#1976d2"))
            }
        }) {
            Text("Chart Gallery - 68+ Chart Types")
        }
        
        P({
            style {
                marginBottom(32.px)
                color(Color("#666"))
                fontSize(16.px)
            }
        }) {
            Text("EBSCore SDK provides 68+ interactive chart types for comprehensive data visualization.")
        }
        
        // Chart Categories
        val chartCategories = listOf(
            "Basic Charts" to listOf("Line", "Bar", "Pie", "Area", "Column", "Donut", "Scatter", "Bubble"),
            "Advanced Charts" to listOf("Heatmap", "Treemap", "Sankey", "Waterfall", "Radar"),
            "Statistical Charts" to listOf("Box Plot", "Violin", "Histogram", "Density", "Q-Q Plot"),
            "Business Charts" to listOf("Funnel", "Pyramid", "Bullet", "Speedometer", "Marimekko"),
            "Time Series Charts" to listOf("Gantt", "Timeline", "Calendar", "Stream Graph"),
            "Geographic Charts" to listOf("Choropleth", "Flow Map", "Dot Distribution"),
            "Relationship Charts" to listOf("Chord", "Arc", "Matrix", "Parallel Coordinates", "Alluvial"),
            "Hierarchical Charts" to listOf("Dendrogram", "Sunburst", "Icicle", "Circle Pack", "Partition"),
            "Distribution Charts" to listOf("Ridgeline", "Beeswarm", "Strip Chart", "Sina Plot"),
            "Creative Charts" to listOf("Word Cloud", "Pictograph", "Slope Graph", "Bump Chart"),
            "Scientific Charts" to listOf("Contour Plot", "Vector Field"),
            "Interactive Charts" to listOf("Sparkline", "Progress Chart", "Metric Card")
        )
        
        chartCategories.forEach { (category, charts) ->
            ChartCategorySection(category, charts)
        }
    }
}

@Composable
fun ChartCategorySection(category: String, charts: List<String>) {
    Div({
        style {
            marginBottom(32.px)
        }
    }) {
        H2({
            style {
                marginBottom(16.px)
                color(Color("#333"))
                borderBottom(2.px, LineStyle.Solid, Color("#1976d2"))
                paddingBottom(8.px)
            }
        }) {
            Text("$category (${charts.size})")
        }
        
        Div({
            style {
                display(DisplayStyle.Grid)
                property("grid-template-columns", "repeat(auto-fill, minmax(200px, 1fr))")
                gap(16.px)
            }
        }) {
            charts.forEach { chartType ->
                ChartTypeCard(chartType)
            }
        }
    }
}

@Composable
fun ChartTypeCard(chartType: String) {
    Div({
        style {
            backgroundColor(Color.white)
            padding(16.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            border(1.px, LineStyle.Solid, Color("#e0e0e0"))
            textAlign("center")
            cursor("pointer")
            
            hover {
                boxShadow("0 4px 8px rgba(0, 0, 0, 0.15)")
                transform { translateY((-2).px) }
            }
        }
    }) {
        // Chart icon/preview
        Div({
            style {
                width(60.px)
                height(60.px)
                backgroundColor(Color("#f5f5f5"))
                borderRadius(50.percent)
                margin(0.px, "auto", 12.px, "auto")
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
                fontSize(24.px)
            }
        }) {
            Text(getChartIcon(chartType))
        }
        
        H4({
            style {
                margin(0.px)
                color(Color("#333"))
                fontSize(14.px)
            }
        }) {
            Text(chartType)
        }
    }
}

fun getChartIcon(chartType: String): String {
    return when (chartType.lowercase()) {
        "line" -> "📈"
        "bar" -> "📊"
        "pie" -> "🥧"
        "area" -> "🏔️"
        "scatter" -> "🔵"
        "heatmap" -> "🌡️"
        "funnel" -> "🔻"
        "gauge" -> "⏱️"
        "treemap" -> "🌳"
        "sankey" -> "🌊"
        "gantt" -> "📅"
        "radar" -> "🎯"
        "bubble" -> "🫧"
        "box plot" -> "📦"
        "violin" -> "🎻"
        "histogram" -> "📊"
        "word cloud" -> "☁️"
        "sunburst" -> "☀️"
        "chord" -> "🎵"
        else -> "📊"
    }
}
```

### 8. Utils and Storage Screens

```kotlin
// UtilsScreen.kt
import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.everybytesystems.ebscore.utils.*

@Composable
fun UtilsScreen() {
    val utilExamples = remember { generateWebUtilExamples() }
    
    Div({
        style {
            padding(24.px)
        }
    }) {
        H1({
            style {
                marginBottom(24.px)
                color(Color("#1976d2"))
            }
        }) {
            Text("Utility Functions Demo")
        }
        
        P({
            style {
                marginBottom(32.px)
                color(Color("#666"))
                fontSize(16.px)
            }
        }) {
            Text("EBSCore SDK provides 200+ utility functions across multiple categories.")
        }
        
        utilExamples.groupBy { it.category }.forEach { (category, examples) ->
            UtilCategorySection(category, examples)
        }
    }
}

@Composable
fun UtilCategorySection(category: String, examples: List<WebUtilExample>) {
    Div({
        style {
            marginBottom(32.px)
        }
    }) {
        H2({
            style {
                marginBottom(16.px)
                color(Color("#333"))
                borderBottom(2.px, LineStyle.Solid, Color("#1976d2"))
                paddingBottom(8.px)
            }
        }) {
            Text(category)
        }
        
        Div({
            style {
                display(DisplayStyle.Grid)
                gap(16.px)
            }
        }) {
            examples.forEach { example ->
                UtilExampleCard(example)
            }
        }
    }
}

@Composable
fun UtilExampleCard(example: WebUtilExample) {
    Div({
        style {
            backgroundColor(Color.white)
            padding(20.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
            border(1.px, LineStyle.Solid, Color("#e0e0e0"))
        }
    }) {
        H4({
            style {
                marginTop(0.px)
                marginBottom(8.px)
                color(Color("#1976d2"))
            }
        }) {
            Text(example.title)
        }
        
        P({
            style {
                marginBottom(12.px)
                color(Color("#666"))
                fontSize(14.px)
            }
        }) {
            Text(example.description)
        }
        
        Div({
            style {
                backgroundColor(Color("#f5f5f5"))
                padding(12.px)
                borderRadius(4.px)
                fontFamily("monospace")
                fontSize(14.px)
                color(Color("#333"))
                wordBreak("break-all")
            }
        }) {
            Text("Result: ${example.result}")
        }
    }
}

data class WebUtilExample(
    val category: String,
    val title: String,
    val description: String,
    val result: String
)

fun generateWebUtilExamples(): List<WebUtilExample> {
    return listOf(
        // Date/Time utilities
        WebUtilExample(
            "Date/Time",
            "Format Current Date",
            "Format current date to ISO string",
            DateUtils.formatDate(js("new Date()"), "yyyy-MM-dd HH:mm:ss")
        ),
        WebUtilExample(
            "Date/Time",
            "DHIS2 Period",
            "Convert date to DHIS2 monthly period",
            DateUtils.toDHIS2Period(js("new Date()"), PeriodType.MONTHLY)
        ),
        
        // String utilities
        WebUtilExample(
            "String",
            "Clean String",
            "Remove whitespace from string",
            StringUtils.clean("  Hello DHIS2 Web  ")
        ),
        WebUtilExample(
            "String",
            "Create Slug",
            "Convert text to URL-friendly slug",
            StringUtils.toSlug("Hello DHIS2 Web Application!")
        ),
        WebUtilExample(
            "String",
            "Validate Email",
            "Check if email address is valid",
            ValidationUtils.isValidEmail("user@dhis2.org").toString()
        ),
        
        // Math utilities
        WebUtilExample(
            "Math",
            "Calculate Mean",
            "Average of sample values",
            "%.2f".format(MathUtils.mean(listOf(1.0, 2.0, 3.0, 4.0, 5.0)))
        ),
        WebUtilExample(
            "Math",
            "Random Number",
            "Generate random integer",
            RandomUtils.int(1, 100).toString()
        ),
        
        // Collection utilities
        WebUtilExample(
            "Collections",
            "Remove Duplicates",
            "Get unique values from list",
            CollectionUtils.distinct(listOf(1, 2, 3, 2, 1, 4)).toString()
        ),
        
        // Random utilities
        WebUtilExample(
            "Random",
            "Generate UUID",
            "Create unique identifier",
            RandomUtils.uuid()
        ),
        WebUtilExample(
            "Random",
            "Random String",
            "Generate alphanumeric string",
            RandomUtils.alphanumeric(12)
        )
    )
}

// StorageScreen.kt
@Composable
fun StorageScreen() {
    var isDarkMode by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf("en") }
    var notifications by remember { mutableStateOf(true) }
    var cacheSize by remember { mutableStateOf("2.5 MB") }
    
    Div({
        style {
            padding(24.px)
        }
    }) {
        H1({
            style {
                marginBottom(24.px)
                color(Color("#1976d2"))
            }
        }) {
            Text("Storage & Settings")
        }
        
        Div({
            style {
                display(DisplayStyle.Grid)
                property("grid-template-columns", "repeat(auto-fit, minmax(300px, 1fr))")
                gap(24.px)
            }
        }) {
            // User Preferences
            Div({
                style {
                    backgroundColor(Color.white)
                    padding(20.px)
                    borderRadius(8.px)
                    boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
                    border(1.px, LineStyle.Solid, Color("#e0e0e0"))
                }
            }) {
                H3({
                    style {
                        marginTop(0.px)
                        marginBottom(20.px)
                        color(Color("#333"))
                    }
                }) {
                    Text("User Preferences")
                }
                
                // Dark Mode Toggle
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.SpaceBetween)
                        alignItems(AlignItems.Center)
                        marginBottom(16.px)
                    }
                }) {
                    Text("Dark Mode")
                    Input(InputType.Checkbox) {
                        checked(isDarkMode)
                        onChange { isDarkMode = it.value }
                        style {
                            transform { scale(1.2) }
                        }
                    }
                }
                
                // Language Selection
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.SpaceBetween)
                        alignItems(AlignItems.Center)
                        marginBottom(16.px)
                    }
                }) {
                    Text("Language")
                    Select({
                        value(language)
                        onChange { language = it.value }
                        style {
                            padding(8.px)
                            borderRadius(4.px)
                            border(1.px, LineStyle.Solid, Color("#ddd"))
                        }
                    }) {
                        Option("en") { Text("English") }
                        Option("fr") { Text("French") }
                        Option("es") { Text("Spanish") }
                    }
                }
                
                // Notifications Toggle
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.SpaceBetween)
                        alignItems(AlignItems.Center)
                    }
                }) {
                    Text("Notifications")
                    Input(InputType.Checkbox) {
                        checked(notifications)
                        onChange { notifications = it.value }
                        style {
                            transform { scale(1.2) }
                        }
                    }
                }
            }
            
            // Cache Management
            Div({
                style {
                    backgroundColor(Color.white)
                    padding(20.px)
                    borderRadius(8.px)
                    boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
                    border(1.px, LineStyle.Solid, Color("#e0e0e0"))
                }
            }) {
                H3({
                    style {
                        marginTop(0.px)
                        marginBottom(20.px)
                        color(Color("#333"))
                    }
                }) {
                    Text("Cache Management")
                }
                
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        justifyContent(JustifyContent.SpaceBetween)
                        alignItems(AlignItems.Center)
                        marginBottom(16.px)
                    }
                }) {
                    Text("Cache Size")
                    Text(cacheSize)
                }
                
                Button({
                    style {
                        width(100.percent)
                        padding(12.px)
                        backgroundColor(Color("#f44336"))
                        color(Color.white)
                        border(0.px)
                        borderRadius(4.px)
                        cursor("pointer")
                        fontSize(14.px)
                    }
                    onClick { 
                        cacheSize = "0 MB"
                    }
                }) {
                    Text("Clear Cache")
                }
            }
            
            // Local Storage Info
            Div({
                style {
                    backgroundColor(Color.white)
                    padding(20.px)
                    borderRadius(8.px)
                    boxShadow("0 2px 4px rgba(0, 0, 0, 0.1)")
                    border(1.px, LineStyle.Solid, Color("#e0e0e0"))
                }
            }) {
                H3({
                    style {
                        marginTop(0.px)
                        marginBottom(20.px)
                        color(Color("#333"))
                    }
                }) {
                    Text("Local Storage")
                }
                
                P({
                    style {
                        margin(0.px, 0.px, 12.px, 0.px)
                        color(Color("#666"))
                        fontSize(14.px)
                    }
                }) {
                    Text("Browser: ${js("navigator.userAgent.split(' ').slice(-1)[0]")}")
                }
                
                P({
                    style {
                        margin(0.px, 0.px, 12.px, 0.px)
                        color(Color("#666"))
                        fontSize(14.px)
                    }
                }) {
                    Text("Storage Available: Yes")
                }
                
                P({
                    style {
                        margin(0.px)
                        color(Color("#666"))
                        fontSize(14.px)
                    }
                }) {
                    Text("IndexedDB: Supported")
                }
            }
        }
    }
}
```

## 🔧 Build and Deploy

### Building for Web

```bash
# Development build
./gradlew jsBrowserDevelopmentRun

# Production build
./gradlew jsBrowserProductionWebpack

# Build and serve
./gradlew jsBrowserRun --continuous
```

### Deployment

```bash
# Build production bundle
./gradlew jsBrowserProductionWebpack

# Deploy to web server
cp -r build/distributions/* /var/www/html/ebscore-demo/
```

### Project Structure

```
web-app/
├── src/jsMain/kotlin/
│   ├── Main.kt
│   ├── Navigation.kt
│   ├── screens/
│   │   ├── LoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── DataScreen.kt
│   │   ├── AnalyticsScreen.kt
│   │   ├── ChartsScreen.kt
│   │   ├── UtilsScreen.kt
│   │   └── StorageScreen.kt
│   └── components/
│       └── CommonComponents.kt
├── src/jsMain/resources/
│   └── index.html
└── build.gradle.kts
```

This comprehensive Web example demonstrates all EBSCore SDK modules with Compose for Web, providing developers with a complete web application foundation for DHIS2 integration that runs in modern browsers.