package com.everybytesystems.dataflow.ui.storage

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*
import kotlin.time.Duration

/**
 * Data Storage & Offline Support Components
 * Comprehensive local storage management and offline-first architecture
 */

// ============================================================================
// 💾 DATA MODELS
// ============================================================================

enum class StorageType {
    CACHE,
    PERSISTENT,
    SECURE,
    TEMPORARY,
    OFFLINE_QUEUE
}

enum class SyncStatus {
    SYNCED,
    PENDING,
    SYNCING,
    FAILED,
    CONFLICT
}

data class StorageStats(
    val totalItems: Int,
    val totalSize: Long, // bytes
    val cacheSize: Long,
    val persistentSize: Long,
    val secureSize: Long,
    val temporarySize: Long,
    val offlineQueueSize: Long,
    val lastSyncTime: Instant?,
    val pendingSyncItems: Int,
    val failedSyncItems: Int
)

data class StorageItem(
    val id: String,
    val key: String,
    val value: Any,
    val type: StorageType,
    val size: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val tags: Set<String> = emptySet(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val metadata: Map<String, Any> = emptyMap()
)

data class OfflineQueueItem(
    val id: String,
    val operation: String,
    val data: Map<String, Any>,
    val endpoint: String,
    val method: String,
    val priority: Int = 0,
    val createdAt: Instant,
    val attempts: Int = 0,
    val lastError: String? = null,
    val nextRetry: Instant? = null
)

data class SyncOperation(
    val id: String,
    val type: String,
    val status: SyncStatus,
    val progress: Float = 0f,
    val itemsTotal: Int,
    val itemsCompleted: Int,
    val startTime: Instant,
    val estimatedCompletion: Instant? = null,
    val error: String? = null
)

// ============================================================================
// 💾 STORAGE DASHBOARD
// ============================================================================

@Composable
fun StorageDashboard(
    stats: StorageStats,
    onClearCache: () -> Unit,
    onClearTemporary: () -> Unit,
    onForceSync: () -> Unit,
    onExportData: () -> Unit,
    onImportData: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Storage Overview
        item {
            StorageOverviewCard(
                stats = stats,
                onForceSync = onForceSync,
                onExportData = onExportData,
                onImportData = onImportData
            )
        }
        
        // Storage Breakdown
        item {
            StorageBreakdownCard(stats = stats)
        }
        
        // Sync Status
        item {
            SyncStatusCard(
                stats = stats,
                onForceSync = onForceSync
            )
        }
        
        // Quick Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Storage Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StorageActionButton(
                            text = "Clear Cache",
                            icon = Icons.Default.DeleteSweep,
                            onClick = onClearCache,
                            modifier = Modifier.weight(1f)
                        )
                        
                        StorageActionButton(
                            text = "Clear Temp",
                            icon = Icons.Default.CleaningServices,
                            onClick = onClearTemporary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StorageOverviewCard(
    stats: StorageStats,
    onForceSync: () -> Unit,
    onExportData: () -> Unit,
    onImportData: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Storage Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onForceSync) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Force Sync"
                    )
                }
            }
            
            // Storage metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StorageMetric(
                    label = "Total Items",
                    value = stats.totalItems.toString(),
                    icon = Icons.Default.Storage
                )
                
                StorageMetric(
                    label = "Total Size",
                    value = formatBytes(stats.totalSize),
                    icon = Icons.Default.DataUsage
                )
                
                StorageMetric(
                    label = "Pending Sync",
                    value = stats.pendingSyncItems.toString(),
                    icon = Icons.Default.CloudSync,
                    color = if (stats.pendingSyncItems > 0) MaterialTheme.colorScheme.warning else MaterialTheme.colorScheme.primary
                )
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExportData,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export")
                }
                
                OutlinedButton(
                    onClick = onImportData,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import")
                }
            }
        }
    }
}

@Composable
fun StorageBreakdownCard(stats: StorageStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Storage Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            StorageBreakdownItem(
                label = "Cache",
                size = stats.cacheSize,
                total = stats.totalSize,
                color = Color(0xFF2196F3)
            )
            
            StorageBreakdownItem(
                label = "Persistent",
                size = stats.persistentSize,
                total = stats.totalSize,
                color = Color(0xFF4CAF50)
            )
            
            StorageBreakdownItem(
                label = "Secure",
                size = stats.secureSize,
                total = stats.totalSize,
                color = Color(0xFFFF9800)
            )
            
            StorageBreakdownItem(
                label = "Temporary",
                size = stats.temporarySize,
                total = stats.totalSize,
                color = Color(0xFF9C27B0)
            )
            
            StorageBreakdownItem(
                label = "Offline Queue",
                size = stats.offlineQueueSize,
                total = stats.totalSize,
                color = Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun SyncStatusCard(
    stats: StorageStats,
    onForceSync: () -> Unit
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
                    text = "Sync Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onForceSync) {
                    Text("Force Sync")
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Last Sync",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stats.lastSyncTime?.let { formatRelativeTime(it) } ?: "Never",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    val syncStatus = when {
                        stats.failedSyncItems > 0 -> "Failed" to Color(0xFFF44336)
                        stats.pendingSyncItems > 0 -> "Pending" to Color(0xFFFF9800)
                        else -> "Synced" to Color(0xFF4CAF50)
                    }
                    
                    Text(
                        text = syncStatus.first,
                        style = MaterialTheme.typography.bodyMedium,
                        color = syncStatus.second,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (stats.pendingSyncItems > 0 || stats.failedSyncItems > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (stats.pendingSyncItems > 0) {
                        Text(
                            text = "${stats.pendingSyncItems} pending",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFF9800)
                        )
                    }
                    
                    if (stats.failedSyncItems > 0) {
                        Text(
                            text = "${stats.failedSyncItems} failed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📋 OFFLINE QUEUE MANAGER
// ============================================================================

@Composable
fun OfflineQueueManager(
    queueItems: List<OfflineQueueItem>,
    onRetryItem: (OfflineQueueItem) -> Unit,
    onDeleteItem: (OfflineQueueItem) -> Unit,
    onRetryAll: () -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header with actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Offline Queue (${queueItems.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (queueItems.isNotEmpty()) {
                    TextButton(onClick = onRetryAll) {
                        Text("Retry All")
                    }
                    
                    TextButton(onClick = onClearAll) {
                        Text("Clear All")
                    }
                }
            }
        }
        
        if (queueItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "All operations synced",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(queueItems) { item ->
                    OfflineQueueItemCard(
                        item = item,
                        onRetry = { onRetryItem(item) },
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun OfflineQueueItemCard(
    item: OfflineQueueItem,
    onRetry: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.operation,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onRetry,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Text(
                text = "${item.method} ${item.endpoint}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Priority: ${item.priority}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "Attempts: ${item.attempts}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = formatRelativeTime(item.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (item.lastError != null) {
                Text(
                    text = "Error: ${item.lastError}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// ============================================================================
// 🔍 STORAGE BROWSER
// ============================================================================

@Composable
fun StorageBrowser(
    items: List<StorageItem>,
    onItemClick: (StorageItem) -> Unit,
    onItemDelete: (StorageItem) -> Unit,
    onFilterChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<StorageType?>(null) }
    
    val filteredItems = remember(items, searchQuery, selectedType) {
        items.filter { item ->
            val matchesSearch = searchQuery.isEmpty() || 
                item.key.contains(searchQuery, ignoreCase = true) ||
                item.tags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesType = selectedType == null || item.type == selectedType
            matchesSearch && matchesType
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search and filters
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    onFilterChange(it)
                },
                label = { Text("Search items...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Type filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("All") }
                    )
                }
                
                items(StorageType.values()) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = if (selectedType == type) null else type },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
        }
        
        // Items list
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (searchQuery.isEmpty()) "No items found" else "No items match your search",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(filteredItems) { item ->
                    StorageItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onDelete = { onItemDelete(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun StorageItemCard(
    item: StorageItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.key,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SyncStatusChip(status = item.syncStatus)
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StorageTypeChip(type = item.type)
                
                Text(
                    text = formatBytes(item.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (item.tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(item.tags.toList()) { tag ->
                        AssistChip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
            
            Text(
                text = "Updated ${formatRelativeTime(item.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 🎨 HELPER COMPONENTS
// ============================================================================

@Composable
fun StorageMetric(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StorageBreakdownItem(
    label: String,
    size: Long,
    total: Long,
    color: Color
) {
    val percentage = if (total > 0) (size.toFloat() / total.toFloat()) else 0f
    
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatBytes(size),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
        
        LinearProgressIndicator(
            progress = percentage,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun StorageActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun StorageTypeChip(type: StorageType) {
    val (color, text) = when (type) {
        StorageType.CACHE -> Color(0xFF2196F3) to "Cache"
        StorageType.PERSISTENT -> Color(0xFF4CAF50) to "Persistent"
        StorageType.SECURE -> Color(0xFFFF9800) to "Secure"
        StorageType.TEMPORARY -> Color(0xFF9C27B0) to "Temporary"
        StorageType.OFFLINE_QUEUE -> Color(0xFFF44336) to "Queue"
    }
    
    AssistChip(
        onClick = { },
        label = { 
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        modifier = Modifier.height(24.dp)
    )
}

@Composable
fun SyncStatusChip(status: SyncStatus) {
    val (color, text) = when (status) {
        SyncStatus.SYNCED -> Color(0xFF4CAF50) to "Synced"
        SyncStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        SyncStatus.SYNCING -> Color(0xFF2196F3) to "Syncing"
        SyncStatus.FAILED -> Color(0xFFF44336) to "Failed"
        SyncStatus.CONFLICT -> Color(0xFF9C27B0) to "Conflict"
    }
    
    AssistChip(
        onClick = { },
        label = { 
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        modifier = Modifier.height(24.dp)
    )
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

fun formatBytes(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "%.1f %s".format(size, units[unitIndex])
}

fun formatRelativeTime(instant: Instant): String {
    val now = Clock.System.now()
    val duration = now - instant
    
    return when {
        duration.inWholeMinutes < 1 -> "Just now"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m ago"
        duration.inWholeHours < 24 -> "${duration.inWholeHours}h ago"
        duration.inWholeDays < 7 -> "${duration.inWholeDays}d ago"
        else -> "${duration.inWholeDays / 7}w ago"
    }
}

// Extension property for warning color
val ColorScheme.warning: Color
    get() = Color(0xFFFF9800)