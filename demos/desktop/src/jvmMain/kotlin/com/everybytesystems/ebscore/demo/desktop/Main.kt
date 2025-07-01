package com.everybytesystems.ebscore.demo.desktop

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.launch

fun main() = application {
    val windowState = rememberWindowState(width = 1200.dp, height = 800.dp)
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "EBSCore SDK - Desktop Demo",
        state = windowState
    ) {
        EBSCoreDesktopDemo()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun EBSCoreDesktopDemo() {
    var selectedTab by remember { mutableStateOf(0) }
    var isConnected by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var systemInfo by remember { mutableStateOf<SystemInfoData?>(null) }
    var dataElements by remember { mutableStateOf<List<DataElementInfo>>(emptyList()) }
    var analyticsData by remember { mutableStateOf<List<AnalyticsRow>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "EBSCore SDK",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Desktop Demo Application",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                actions = {
                    // Connection Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            if (isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isConnected) "Connected" else "Disconnected",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null
                                    try {
                                        if (!isConnected) {
                                            // TODO: Implement actual connection
                                            kotlinx.coroutines.delay(2000) // Simulate connection
                                            isConnected = true
                                            systemInfo = SystemInfoData(
                                                version = "2.42.0",
                                                serverDate = "2024-01-15T10:30:00.000Z",
                                                database = "PostgreSQL 14.5"
                                            )
                                            dataElements = generateSampleDataElements()
                                            analyticsData = generateSampleAnalytics()
                                        } else {
                                            isConnected = false
                                            systemInfo = null
                                            dataElements = emptyList()
                                            analyticsData = emptyList()
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = e.message
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(if (isConnected) "Disconnect" else "Connect to DHIS2")
                        }
                    }
                }
            )
            
            // Tab Navigation
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("System Info") },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Metadata") },
                    icon = { Icon(Icons.Default.DataObject, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Analytics") },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> SystemInfoTab(systemInfo, errorMessage)
                    1 -> MetadataTab(dataElements, errorMessage)
                    2 -> AnalyticsTab(analyticsData, errorMessage)
                    3 -> SettingsTab()
                }
            }
        }
    }
}

@Composable
fun SystemInfoTab(systemInfo: SystemInfoData?, errorMessage: String?) {
    if (errorMessage != null) {
        ErrorCard(errorMessage)
    } else if (systemInfo != null) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "📊 DHIS2 System Information",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow("Version", systemInfo.version)
                InfoRow("Server Date", systemInfo.serverDate)
                InfoRow("Database", systemInfo.database)
            }
        }
    } else {
        EmptyStateCard("Connect to DHIS2 to view system information")
    }
}

@Composable
fun MetadataTab(dataElements: List<DataElementInfo>, errorMessage: String?) {
    if (errorMessage != null) {
        ErrorCard(errorMessage)
    } else if (dataElements.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "📋 Data Elements (${dataElements.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn {
                    items(dataElements) { element ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = element.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Type: ${element.valueType} | Domain: ${element.domainType}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        EmptyStateCard("Connect to DHIS2 to view data elements")
    }
}

@Composable
fun AnalyticsTab(analyticsData: List<AnalyticsRow>, errorMessage: String?) {
    if (errorMessage != null) {
        ErrorCard(errorMessage)
    } else if (analyticsData.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "📈 Analytics Data",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn {
                    items(analyticsData) { row ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = row.indicator,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${row.period} | ${row.orgUnit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = row.value.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        EmptyStateCard("Connect to DHIS2 to view analytics data")
    }
}

@Composable
fun SettingsTab() {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "⚙️ Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "DHIS2 Server Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = "https://play.dhis2.org/2.42.0",
                onValueChange = { },
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = "admin",
                onValueChange = { },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = "••••••••",
                onValueChange = { },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.CloudOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Data classes for demo
data class SystemInfoData(
    val version: String,
    val serverDate: String,
    val database: String
)

data class DataElementInfo(
    val id: String,
    val name: String,
    val valueType: String,
    val domainType: String
)

data class AnalyticsRow(
    val indicator: String,
    val period: String,
    val orgUnit: String,
    val value: Double
)

// Sample data generators
fun generateSampleDataElements(): List<DataElementInfo> {
    return listOf(
        DataElementInfo("FTRrcoaog83", "Accute Flaccid Paralysis (AFP) cases", "INTEGER", "AGGREGATE"),
        DataElementInfo("Jtf34kNZhzP", "Accute Flaccid Paralysis (AFP) death", "INTEGER", "AGGREGATE"),
        DataElementInfo("P3jJH5Tu5VC", "Acute Flaccid Paralysis (AFP) follow-up", "INTEGER", "AGGREGATE"),
        DataElementInfo("FQ2o8UBlcrS", "Acute Flaccid Paralysis (AFP) new", "INTEGER", "AGGREGATE"),
        DataElementInfo("M62VHgYT2n0", "Acute Flaccid Paralysis (AFP) referrals", "INTEGER", "AGGREGATE"),
        DataElementInfo("IpHINAT79UW", "Child Programme", "TEXT", "TRACKER"),
        DataElementInfo("eBAyeGv0exc", "Inpatient morbidity and mortality", "TEXT", "AGGREGATE"),
        DataElementInfo("Vf7vSPiYeF2", "Malaria case management", "INTEGER", "AGGREGATE"),
        DataElementInfo("ybzlGLjWwnK", "Malaria focus investigation", "INTEGER", "TRACKER"),
        DataElementInfo("M3xtLkYBlKI", "Population estimates", "INTEGER", "AGGREGATE")
    )
}

fun generateSampleAnalytics(): List<AnalyticsRow> {
    return listOf(
        AnalyticsRow("AFP Cases", "2024Q1", "Sierra Leone", 45.0),
        AnalyticsRow("AFP Cases", "2024Q2", "Sierra Leone", 52.0),
        AnalyticsRow("AFP Cases", "2024Q3", "Sierra Leone", 38.0),
        AnalyticsRow("AFP Deaths", "2024Q1", "Sierra Leone", 2.0),
        AnalyticsRow("AFP Deaths", "2024Q2", "Sierra Leone", 1.0),
        AnalyticsRow("AFP Deaths", "2024Q3", "Sierra Leone", 3.0),
        AnalyticsRow("Malaria Cases", "2024Q1", "Sierra Leone", 1250.0),
        AnalyticsRow("Malaria Cases", "2024Q2", "Sierra Leone", 1180.0),
        AnalyticsRow("Malaria Cases", "2024Q3", "Sierra Leone", 1420.0)
    )
}