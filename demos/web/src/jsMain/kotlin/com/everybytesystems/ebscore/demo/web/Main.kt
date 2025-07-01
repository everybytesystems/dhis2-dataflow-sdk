package com.everybytesystems.ebscore.demo.web

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        EBSCoreWebDemo()
    }
}

@Composable
fun EBSCoreWebDemo() {
    var isConnected by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf("dashboard") }
    
    Style(AppStyleSheet)
    
    Div(attrs = { classes("app-container") }) {
        // Header
        Header(attrs = { classes("app-header") }) {
            Div(attrs = { classes("header-content") }) {
                H1(attrs = { classes("app-title") }) {
                    Text("🚀 EBSCore SDK")
                }
                P(attrs = { classes("app-subtitle") }) {
                    Text("Web Demo Application")
                }
                
                Div(attrs = { classes("connection-status") }) {
                    Span(attrs = { 
                        classes(if (isConnected) "status-connected" else "status-disconnected")
                    }) {
                        Text(if (isConnected) "🟢 Connected" else "🔴 Disconnected")
                    }
                    
                    Button(attrs = {
                        classes("connect-btn")
                        disabled(isLoading)
                        onClick {
                            isLoading = true
                            // Simulate connection
                            kotlinx.coroutines.MainScope().launch {
                                kotlinx.coroutines.delay(2000)
                                isConnected = !isConnected
                                isLoading = false
                            }
                        }
                    }) {
                        if (isLoading) {
                            Text("Connecting...")
                        } else {
                            Text(if (isConnected) "Disconnect" else "Connect to DHIS2")
                        }
                    }
                }
            }
        }
        
        // Navigation
        Nav(attrs = { classes("app-nav") }) {
            Button(attrs = {
                classes("nav-btn", if (activeTab == "dashboard") "active" else "")
                onClick { activeTab = "dashboard" }
            }) {
                Text("📊 Dashboard")
            }
            
            Button(attrs = {
                classes("nav-btn", if (activeTab == "metadata") "active" else "")
                onClick { activeTab = "metadata" }
            }) {
                Text("📋 Metadata")
            }
            
            Button(attrs = {
                classes("nav-btn", if (activeTab == "analytics") "active" else "")
                onClick { activeTab = "analytics" }
            }) {
                Text("📈 Analytics")
            }
            
            Button(attrs = {
                classes("nav-btn", if (activeTab == "settings") "active" else "")
                onClick { activeTab = "settings" }
            }) {
                Text("⚙️ Settings")
            }
        }
        
        // Main Content
        Main(attrs = { classes("app-main") }) {
            when (activeTab) {
                "dashboard" -> DashboardTab(isConnected)
                "metadata" -> MetadataTab(isConnected)
                "analytics" -> AnalyticsTab(isConnected)
                "settings" -> SettingsTab()
            }
        }
        
        // Footer
        Footer(attrs = { classes("app-footer") }) {
            P {
                Text("Built with EBSCore SDK • © 2024 EveryByte Systems")
            }
        }
    }
}

@Composable
fun DashboardTab(isConnected: Boolean) {
    Div(attrs = { classes("tab-content") }) {
        H2 { Text("📊 Dashboard") }
        
        if (isConnected) {
            Div(attrs = { classes("dashboard-grid") }) {
                // System Info Card
                Div(attrs = { classes("card") }) {
                    H3 { Text("System Information") }
                    P { Text("DHIS2 Version: 2.42.0") }
                    P { Text("Server Date: 2024-01-15T10:30:00.000Z") }
                    P { Text("Database: PostgreSQL 14.5") }
                }
                
                // Quick Stats Card
                Div(attrs = { classes("card") }) {
                    H3 { Text("Quick Statistics") }
                    P { Text("Data Elements: 1,247") }
                    P { Text("Organization Units: 89") }
                    P { Text("Programs: 12") }
                }
                
                // Recent Activity Card
                Div(attrs = { classes("card") }) {
                    H3 { Text("Recent Activity") }
                    P { Text("Last Sync: 2 minutes ago") }
                    P { Text("Data Updates: 15 today") }
                    P { Text("Active Users: 23") }
                }
            }
        } else {
            Div(attrs = { classes("empty-state") }) {
                P { Text("🔌 Connect to DHIS2 to view dashboard") }
            }
        }
    }
}

@Composable
fun MetadataTab(isConnected: Boolean) {
    Div(attrs = { classes("tab-content") }) {
        H2 { Text("📋 Metadata") }
        
        if (isConnected) {
            Div(attrs = { classes("metadata-list") }) {
                // Sample data elements
                listOf(
                    "Accute Flaccid Paralysis (AFP) cases",
                    "Accute Flaccid Paralysis (AFP) death",
                    "Acute Flaccid Paralysis (AFP) follow-up",
                    "Acute Flaccid Paralysis (AFP) new",
                    "Acute Flaccid Paralysis (AFP) referrals",
                    "Child Programme",
                    "Inpatient morbidity and mortality",
                    "Malaria case management",
                    "Malaria focus investigation",
                    "Population estimates"
                ).forEach { element ->
                    Div(attrs = { classes("metadata-item") }) {
                        H4 { Text(element) }
                        P { Text("Type: INTEGER • Domain: AGGREGATE") }
                    }
                }
            }
        } else {
            Div(attrs = { classes("empty-state") }) {
                P { Text("🔌 Connect to DHIS2 to view metadata") }
            }
        }
    }
}

@Composable
fun AnalyticsTab(isConnected: Boolean) {
    Div(attrs = { classes("tab-content") }) {
        H2 { Text("📈 Analytics") }
        
        if (isConnected) {
            Div(attrs = { classes("analytics-grid") }) {
                // Sample analytics data
                listOf(
                    Triple("AFP Cases", "2024Q1", "45"),
                    Triple("AFP Cases", "2024Q2", "52"),
                    Triple("AFP Cases", "2024Q3", "38"),
                    Triple("Malaria Cases", "2024Q1", "1,250"),
                    Triple("Malaria Cases", "2024Q2", "1,180"),
                    Triple("Malaria Cases", "2024Q3", "1,420")
                ).forEach { (indicator, period, value) ->
                    Div(attrs = { classes("analytics-item") }) {
                        H4 { Text(indicator) }
                        P { Text("Period: $period") }
                        P(attrs = { classes("analytics-value") }) { Text(value) }
                    }
                }
            }
        } else {
            Div(attrs = { classes("empty-state") }) {
                P { Text("🔌 Connect to DHIS2 to view analytics") }
            }
        }
    }
}

@Composable
fun SettingsTab() {
    Div(attrs = { classes("tab-content") }) {
        H2 { Text("⚙️ Settings") }
        
        Div(attrs = { classes("settings-form") }) {
            H3 { Text("DHIS2 Server Configuration") }
            
            Div(attrs = { classes("form-group") }) {
                Label { Text("Server URL") }
                Input(type = InputType.Url) {
                    value("https://play.dhis2.org/2.42.0")
                    classes("form-input")
                }
            }
            
            Div(attrs = { classes("form-group") }) {
                Label { Text("Username") }
                Input(type = InputType.Text) {
                    value("admin")
                    classes("form-input")
                }
            }
            
            Div(attrs = { classes("form-group") }) {
                Label { Text("Password") }
                Input(type = InputType.Password) {
                    value("district")
                    classes("form-input")
                }
            }
            
            Button(attrs = { classes("save-btn") }) {
                Text("Save Configuration")
            }
        }
    }
}

object AppStyleSheet : StyleSheet() {
    init {
        "body" style {
            margin(0.px)
            fontFamily("system-ui", "-apple-system", "sans-serif")
            backgroundColor(Color("#f5f5f5"))
        }
        
        ".app-container" style {
            minHeight(100.vh)
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
        }
        
        ".app-header" style {
            backgroundColor(Color("#1976d2"))
            color(Color.white)
            padding(16.px)
        }
        
        ".header-content" style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.SpaceBetween)
            alignItems(AlignItems.Center)
            maxWidth(1200.px)
            margin(0.px, auto)
        }
        
        ".app-title" style {
            margin(0.px)
            fontSize(24.px)
        }
        
        ".app-subtitle" style {
            margin(4.px, 0.px, 0.px, 0.px)
            opacity(0.8)
        }
        
        ".connection-status" style {
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            gap(16.px)
        }
        
        ".status-connected" style {
            color(Color("#4caf50"))
            fontWeight("bold")
        }
        
        ".status-disconnected" style {
            color(Color("#f44336"))
            fontWeight("bold")
        }
        
        ".connect-btn" style {
            backgroundColor(Color.white)
            color(Color("#1976d2"))
            border(0.px)
            padding(8.px, 16.px)
            borderRadius(4.px)
            cursor("pointer")
            fontWeight("bold")
        }
        
        ".app-nav" style {
            backgroundColor(Color.white)
            borderBottom(1.px, LineStyle.Solid, Color("#e0e0e0"))
            padding(0.px, 16.px)
            display(DisplayStyle.Flex)
            gap(8.px)
            maxWidth(1200.px)
            margin(0.px, auto)
            width(100.percent)
            boxSizing("border-box")
        }
        
        ".nav-btn" style {
            backgroundColor(Color.transparent)
            border(0.px)
            padding(12.px, 16.px)
            cursor("pointer")
            borderRadius(4.px, 4.px, 0.px, 0.px)
        }
        
        ".nav-btn.active" style {
            backgroundColor(Color("#e3f2fd"))
            color(Color("#1976d2"))
            fontWeight("bold")
        }
        
        ".app-main" style {
            flex(1)
            maxWidth(1200.px)
            margin(0.px, auto)
            width(100.percent)
            boxSizing("border-box")
        }
        
        ".tab-content" style {
            padding(24.px)
        }
        
        ".dashboard-grid" style {
            display(DisplayStyle.Grid)
            gridTemplateColumns("repeat(auto-fit, minmax(300px, 1fr))")
            gap(16.px)
            marginTop(16.px)
        }
        
        ".card" style {
            backgroundColor(Color.white)
            padding(20.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0,0,0,0.1)")
        }
        
        ".empty-state" style {
            textAlign("center")
            padding(40.px)
            color(Color("#666"))
            fontSize(18.px)
        }
        
        ".metadata-list" style {
            display(DisplayStyle.Grid)
            gap(12.px)
            marginTop(16.px)
        }
        
        ".metadata-item" style {
            backgroundColor(Color.white)
            padding(16.px)
            borderRadius(8.px)
            boxShadow("0 1px 3px rgba(0,0,0,0.1)")
        }
        
        ".analytics-grid" style {
            display(DisplayStyle.Grid)
            gridTemplateColumns("repeat(auto-fit, minmax(250px, 1fr))")
            gap(16.px)
            marginTop(16.px)
        }
        
        ".analytics-item" style {
            backgroundColor(Color.white)
            padding(16.px)
            borderRadius(8.px)
            boxShadow("0 1px 3px rgba(0,0,0,0.1)")
            textAlign("center")
        }
        
        ".analytics-value" style {
            fontSize(24.px)
            fontWeight("bold")
            color(Color("#1976d2"))
        }
        
        ".settings-form" style {
            backgroundColor(Color.white)
            padding(24.px)
            borderRadius(8.px)
            boxShadow("0 2px 4px rgba(0,0,0,0.1)")
            maxWidth(500.px)
        }
        
        ".form-group" style {
            marginBottom(16.px)
        }
        
        ".form-input" style {
            width(100.percent)
            padding(8.px, 12.px)
            border(1.px, LineStyle.Solid, Color("#ddd"))
            borderRadius(4.px)
            fontSize(14.px)
            boxSizing("border-box")
        }
        
        ".save-btn" style {
            backgroundColor(Color("#1976d2"))
            color(Color.white)
            border(0.px)
            padding(10.px, 20.px)
            borderRadius(4.px)
            cursor("pointer")
            fontWeight("bold")
        }
        
        ".app-footer" style {
            backgroundColor(Color("#333"))
            color(Color.white)
            textAlign("center")
            padding(16.px)
            marginTop("auto")
        }
    }
}