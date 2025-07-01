package com.everybytesystems.dataflow.ui.dhis2

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

/**
 * DHIS2 System Administration Components
 * Comprehensive system administration and monitoring
 */

// ============================================================================
// ⚙️ SYSTEM MODELS
// ============================================================================

data class SystemInfo(
    val version: String,
    val revision: String,
    val buildTime: String,
    val serverDate: String,
    val serverTimeZoneId: String,
    val serverTimeZoneDisplayName: String,
    val lastAnalyticsTableSuccess: String? = null,
    val intervalSinceLastAnalyticsTableSuccess: String? = null,
    val lastAnalyticsTableRuntime: String? = null,
    val lastSystemMonitoringSuccess: String? = null,
    val systemId: String,
    val systemName: String? = null,
    val instanceBaseUrl: String? = null,
    val contextPath: String? = null,
    val userAgent: String? = null,
    val calendar: String = "iso8601",
    val dateFormat: String = "yyyy-MM-dd",
    val startModule: String? = null,
    val databaseInfo: DatabaseInfo? = null,
    val memoryInfo: MemoryInfo? = null,
    val cpuCores: Int? = null,
    val encryption: Boolean = false,
    val emailConfigured: Boolean = false,
    val redisEnabled: Boolean = false,
    val systemMonitoringUrl: String? = null,
    val systemNotificationEmail: String? = null,
    val isMetadataVersionEnabled: Boolean = false,
    val metadataVersion: String? = null
)

data class DatabaseInfo(
    val name: String,
    val user: String,
    val url: String,
    val databaseVersion: String,
    val spatialSupport: Boolean = false
)

data class MemoryInfo(
    val totalMemory: Long,
    val freeMemory: Long,
    val maxMemory: Long,
    val usedMemory: Long,
    val memoryUsagePercentage: Float
)

data class SystemSettings(
    val keyEmailHostName: String? = null,
    val keyEmailPort: Int? = null,
    val keyEmailUsername: String? = null,
    val keyEmailTls: Boolean = false,
    val keyEmailSender: String? = null,
    val keyInstanceBaseUrl: String? = null,
    val keySystemNotificationsEmail: String? = null,
    val keySystemTitle: String? = null,
    val keySystemSubTitle: String? = null,
    val keyFlag: String? = null,
    val keyFlagImage: String? = null,
    val keyStartModule: String? = null,
    val keyHelpPageLink: String? = null,
    val keyAccountRecovery: Boolean = true,
    val keySelfRegistrationNoRecaptcha: Boolean = false,
    val keyOpenIdProvider: String? = null,
    val keyOpenIdProviderLabel: String? = null,
    val keyCanGrantOwnUserAuthorityGroups: Boolean = false,
    val keyHideDailyPeriods: Boolean = false,
    val keyHideWeeklyPeriods: Boolean = false,
    val keyHideMonthlyPeriods: Boolean = false,
    val keyHideBiMonthlyPeriods: Boolean = false,
    val keyGatherAnalyticalObjectStatisticsInDashboardViews: Boolean = false,
    val keyCountPassiveDashboardViewsInUsageAnalytics: Boolean = false,
    val keyDashboardContextMenuItemSwitchViewType: Boolean = true,
    val keyDashboardContextMenuItemOpenInRelevantApp: Boolean = true,
    val keyDashboardContextMenuItemShowInterpretationsAndDetails: Boolean = true,
    val keyDashboardContextMenuItemViewFullscreen: Boolean = true,
    val keyDefaultAnalysisRelativePeriod: String? = null,
    val keyAnalysisDisplayProperty: String = "name",
    val keyRequireAddToView: Boolean = false,
    val keyAllowAssignedCategoryOptionGroupSets: Boolean = false,
    val keyUseCustomLogoFront: Boolean = false,
    val keyUseCustomLogoBanner: Boolean = false,
    val metadata: Map<String, Any> = emptyMap()
)

data class MaintenanceTask(
    val id: String,
    val name: String,
    val description: String,
    val category: MaintenanceCategory,
    val lastRun: String? = null,
    val nextRun: String? = null,
    val status: TaskStatus = TaskStatus.IDLE,
    val progress: Float = 0f,
    val duration: String? = null,
    val errorMessage: String? = null,
    val canRun: Boolean = true,
    val isScheduled: Boolean = false,
    val cronExpression: String? = null
)

enum class MaintenanceCategory {
    ANALYTICS,
    DATA_INTEGRITY,
    RESOURCE_TABLES,
    ORGANIZATION_UNIT_PATHS,
    CACHE_CLEARING,
    DATA_PRUNING,
    METADATA_VALIDATION,
    SYSTEM_MONITORING
}

enum class TaskStatus {
    IDLE,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class DataIntegrityCheck(
    val id: String,
    val name: String,
    val description: String,
    val severity: IntegritySeverity,
    val issueCount: Int = 0,
    val issues: List<String> = emptyList(),
    val lastChecked: String? = null,
    val canFix: Boolean = false
)

enum class IntegritySeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

// ============================================================================
// 📊 SYSTEM DASHBOARD
// ============================================================================

@Composable
fun SystemDashboard(
    systemInfo: SystemInfo,
    maintenanceTasks: List<MaintenanceTask>,
    dataIntegrityChecks: List<DataIntegrityCheck>,
    modifier: Modifier = Modifier,
    onRunTask: (MaintenanceTask) -> Unit = {},
    onRunIntegrityCheck: (DataIntegrityCheck) -> Unit = {},
    onViewSystemInfo: () -> Unit = {},
    onViewSettings: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // System Overview
        item {
            SystemOverviewCard(
                systemInfo = systemInfo,
                onViewDetails = onViewSystemInfo
            )
        }
        
        // Quick Actions
        item {
            QuickActionsCard(
                onViewSettings = onViewSettings,
                onClearCache = { /* Handle cache clearing */ },
                onGenerateAnalytics = { /* Handle analytics generation */ }
            )
        }
        
        // System Health
        item {
            SystemHealthCard(
                memoryInfo = systemInfo.memoryInfo,
                databaseInfo = systemInfo.databaseInfo,
                cpuCores = systemInfo.cpuCores
            )
        }
        
        // Recent Maintenance Tasks
        item {
            RecentMaintenanceCard(
                tasks = maintenanceTasks.take(5),
                onRunTask = onRunTask,
                onViewAll = { /* Navigate to maintenance page */ }
            )
        }
        
        // Data Integrity Summary
        item {
            DataIntegritySummaryCard(
                checks = dataIntegrityChecks,
                onRunCheck = onRunIntegrityCheck,
                onViewAll = { /* Navigate to data integrity page */ }
            )
        }
    }
}

@Composable
private fun SystemOverviewCard(
    systemInfo: SystemInfo,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = systemInfo.systemName ?: "DHIS2 System",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Version ${systemInfo.version}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Build: ${systemInfo.revision}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                OutlinedButton(
                    onClick = onViewDetails
                ) {
                    Text("View Details")
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SystemInfoItem(
                    label = "Server Date",
                    value = systemInfo.serverDate,
                    icon = Icons.Default.Schedule
                )
                
                SystemInfoItem(
                    label = "Time Zone",
                    value = systemInfo.serverTimeZoneDisplayName,
                    icon = Icons.Default.Public
                )
                
                SystemInfoItem(
                    label = "System ID",
                    value = systemInfo.systemId.take(8) + "...",
                    icon = Icons.Default.Fingerprint
                )
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onViewSettings: () -> Unit,
    onClearCache: () -> Unit,
    onGenerateAnalytics: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    QuickActionButton(
                        label = "Settings",
                        icon = Icons.Default.Settings,
                        onClick = onViewSettings
                    )
                }
                
                item {
                    QuickActionButton(
                        label = "Clear Cache",
                        icon = Icons.Default.ClearAll,
                        onClick = onClearCache
                    )
                }
                
                item {
                    QuickActionButton(
                        label = "Analytics",
                        icon = Icons.Default.Analytics,
                        onClick = onGenerateAnalytics
                    )
                }
                
                item {
                    QuickActionButton(
                        label = "Backup",
                        icon = Icons.Default.Backup,
                        onClick = { /* Handle backup */ }
                    )
                }
                
                item {
                    QuickActionButton(
                        label = "Logs",
                        icon = Icons.Default.Description,
                        onClick = { /* Handle logs */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemHealthCard(
    memoryInfo: MemoryInfo?,
    databaseInfo: DatabaseInfo?,
    cpuCores: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "System Health",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (memoryInfo != null) {
                HealthMetric(
                    label = "Memory Usage",
                    value = "${memoryInfo.memoryUsagePercentage.toInt()}%",
                    progress = memoryInfo.memoryUsagePercentage / 100f,
                    status = when {
                        memoryInfo.memoryUsagePercentage > 90f -> HealthStatus.CRITICAL
                        memoryInfo.memoryUsagePercentage > 75f -> HealthStatus.WARNING
                        else -> HealthStatus.GOOD
                    }
                )
            }
            
            if (databaseInfo != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Database",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${databaseInfo.name} ${databaseInfo.databaseVersion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    AssistChip(
                        onClick = { },
                        label = { Text("Connected") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            labelColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }
            
            if (cpuCores != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CPU Cores",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = cpuCores.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentMaintenanceCard(
    tasks: List<MaintenanceTask>,
    onRunTask: (MaintenanceTask) -> Unit,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Maintenance Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("View All")
                }
            }
            
            tasks.forEach { task ->
                MaintenanceTaskItem(
                    task = task,
                    onRun = { onRunTask(task) },
                    compact = true
                )
            }
        }
    }
}

@Composable
private fun DataIntegritySummaryCard(
    checks: List<DataIntegrityCheck>,
    onRunCheck: (DataIntegrityCheck) -> Unit,
    onViewAll: () -> Unit
) {
    val criticalIssues = checks.count { it.severity == IntegritySeverity.CRITICAL && it.issueCount > 0 }
    val errorIssues = checks.count { it.severity == IntegritySeverity.ERROR && it.issueCount > 0 }
    val warningIssues = checks.count { it.severity == IntegritySeverity.WARNING && it.issueCount > 0 }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data Integrity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onViewAll) {
                    Text("View All")
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IntegrityStatusItem(
                    label = "Critical",
                    count = criticalIssues,
                    color = Color(0xFFF44336)
                )
                
                IntegrityStatusItem(
                    label = "Errors",
                    count = errorIssues,
                    color = Color(0xFFFF9800)
                )
                
                IntegrityStatusItem(
                    label = "Warnings",
                    count = warningIssues,
                    color = Color(0xFFFFEB3B)
                )
                
                IntegrityStatusItem(
                    label = "Total Checks",
                    count = checks.size,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ============================================================================
// 🔧 MAINTENANCE TASKS
// ============================================================================

@Composable
fun MaintenanceTasksList(
    tasks: List<MaintenanceTask>,
    onRunTask: (MaintenanceTask) -> Unit,
    onScheduleTask: (MaintenanceTask) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group tasks by category
        MaintenanceCategory.values().forEach { category ->
            val categoryTasks = tasks.filter { it.category == category }
            if (categoryTasks.isNotEmpty()) {
                item {
                    Text(
                        text = category.name.replace("_", " "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(categoryTasks) { task ->
                    MaintenanceTaskItem(
                        task = task,
                        onRun = { onRunTask(task) },
                        onSchedule = { onScheduleTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MaintenanceTaskItem(
    task: MaintenanceTask,
    onRun: () -> Unit,
    onSchedule: (() -> Unit)? = null,
    compact: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (task.status) {
                TaskStatus.RUNNING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                TaskStatus.FAILED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                TaskStatus.COMPLETED -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (!compact) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status chip
                        AssistChip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = task.status.name,
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = getTaskStatusColor(task.status).copy(alpha = 0.2f),
                                labelColor = getTaskStatusColor(task.status)
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                        
                        if (task.lastRun != null) {
                            Text(
                                text = "Last run: ${task.lastRun}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (task.isScheduled && task.nextRun != null) {
                            Text(
                                text = "Next: ${task.nextRun}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
                // Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (task.canRun && task.status != TaskStatus.RUNNING) {
                        IconButton(
                            onClick = onRun
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Run task"
                            )
                        }
                    }
                    
                    if (onSchedule != null && !compact) {
                        IconButton(
                            onClick = onSchedule
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Schedule task"
                            )
                        }
                    }
                }
            }
            
            // Progress bar for running tasks
            if (task.status == TaskStatus.RUNNING) {
                LinearProgressIndicator(
                    progress = task.progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Error message
            if (task.status == TaskStatus.FAILED && task.errorMessage != null) {
                Text(
                    text = "Error: ${task.errorMessage}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ============================================================================
// 🔧 HELPER COMPONENTS
// ============================================================================

@Composable
private fun SystemInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

enum class HealthStatus {
    GOOD, WARNING, CRITICAL
}

@Composable
private fun HealthMetric(
    label: String,
    value: String,
    progress: Float,
    status: HealthStatus
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = when (status) {
                    HealthStatus.GOOD -> Color(0xFF4CAF50)
                    HealthStatus.WARNING -> Color(0xFFFF9800)
                    HealthStatus.CRITICAL -> Color(0xFFF44336)
                }
            )
        }
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = when (status) {
                HealthStatus.GOOD -> Color(0xFF4CAF50)
                HealthStatus.WARNING -> Color(0xFFFF9800)
                HealthStatus.CRITICAL -> Color(0xFFF44336)
            }
        )
    }
}

@Composable
private fun IntegrityStatusItem(
    label: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun getTaskStatusColor(status: TaskStatus): Color {
    return when (status) {
        TaskStatus.IDLE -> Color(0xFF607D8B)
        TaskStatus.RUNNING -> Color(0xFF2196F3)
        TaskStatus.COMPLETED -> Color(0xFF4CAF50)
        TaskStatus.FAILED -> Color(0xFFF44336)
        TaskStatus.CANCELLED -> Color(0xFFFF9800)
    }
}