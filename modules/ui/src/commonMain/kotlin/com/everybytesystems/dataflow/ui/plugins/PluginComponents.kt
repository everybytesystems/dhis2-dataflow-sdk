package com.everybytesystems.dataflow.ui.plugins

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*

/**
 * Extensibility & Plugin Model Components
 * Plugin management, custom components, and third-party integrations
 */

// ============================================================================
// 🔌 DATA MODELS
// ============================================================================

enum class PluginStatus {
    AVAILABLE,
    INSTALLED,
    ENABLED,
    DISABLED,
    UPDATING,
    ERROR
}

enum class PluginCategory {
    ANALYTICS,
    AUTHENTICATION,
    CHARTS,
    COMMUNICATION,
    DATA_SOURCES,
    FORMS,
    INTEGRATIONS,
    NOTIFICATIONS,
    SECURITY,
    STORAGE,
    UI_COMPONENTS,
    UTILITIES
}

enum class ParameterType {
    STRING,
    INT,
    FLOAT,
    BOOLEAN,
    COLOR,
    LIST,
    OBJECT
}

enum class IntegrationStatus {
    CONNECTED,
    DISCONNECTED,
    SYNCING,
    ERROR,
    PENDING
}

data class Plugin(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val author: String,
    val category: PluginCategory,
    val status: PluginStatus,
    val icon: ImageVector,
    val rating: Float = 0f,
    val downloads: Int = 0,
    val tags: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val changelog: String = "",
    val documentation: String = ""
)

data class PluginStore(
    val featured: List<Plugin>,
    val popular: List<Plugin>,
    val recent: List<Plugin>,
    val categories: Map<PluginCategory, List<Plugin>>,
    val installed: List<Plugin>,
    val updates: List<Plugin>
)

data class ComponentParameter(
    val name: String,
    val type: ParameterType,
    val defaultValue: Any? = null,
    val required: Boolean = false,
    val description: String = "",
    val options: List<Any> = emptyList()
)

data class CustomComponent(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val parameters: List<ComponentParameter>,
    val preview: @Composable () -> Unit,
    val component: @Composable (Map<String, Any>) -> Unit
)

data class Integration(
    val id: String,
    val name: String,
    val description: String,
    val provider: String,
    val icon: ImageVector,
    val isConnected: Boolean,
    val status: IntegrationStatus,
    val lastSync: Instant? = null,
    val configuration: Map<String, Any> = emptyMap(),
    val capabilities: List<String> = emptyList()
)

// ============================================================================
// 🔌 PLUGIN MANAGER
// ============================================================================

@Composable
fun PluginManager(
    store: PluginStore,
    onInstallPlugin: (Plugin) -> Unit,
    onUninstallPlugin: (Plugin) -> Unit,
    onEnablePlugin: (Plugin) -> Unit,
    onDisablePlugin: (Plugin) -> Unit,
    onConfigurePlugin: (Plugin) -> Unit,
    onUpdatePlugin: (Plugin) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Featured", "Categories", "Installed", "Updates")
    
    Column(modifier = modifier.fillMaxSize()) {
        // Tab navigation
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        
        // Tab content
        when (selectedTab) {
            0 -> FeaturedPluginsTab(
                plugins = store.featured,
                onInstall = onInstallPlugin,
                onConfigure = onConfigurePlugin
            )
            1 -> CategoriesTab(
                categories = store.categories,
                onInstall = onInstallPlugin,
                onConfigure = onConfigurePlugin
            )
            2 -> InstalledPluginsTab(
                plugins = store.installed,
                onUninstall = onUninstallPlugin,
                onEnable = onEnablePlugin,
                onDisable = onDisablePlugin,
                onConfigure = onConfigurePlugin
            )
            3 -> UpdatesTab(
                plugins = store.updates,
                onUpdate = onUpdatePlugin
            )
        }
    }
}

@Composable
fun FeaturedPluginsTab(
    plugins: List<Plugin>,
    onInstall: (Plugin) -> Unit,
    onConfigure: (Plugin) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(plugins) { plugin ->
            PluginCard(
                plugin = plugin,
                onPrimaryAction = { 
                    when (plugin.status) {
                        PluginStatus.AVAILABLE -> onInstall(plugin)
                        PluginStatus.INSTALLED, PluginStatus.ENABLED -> onConfigure(plugin)
                        else -> {}
                    }
                },
                showFullDetails = true
            )
        }
    }
}

@Composable
fun CategoriesTab(
    categories: Map<PluginCategory, List<Plugin>>,
    onInstall: (Plugin) -> Unit,
    onConfigure: (Plugin) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        categories.forEach { (category, plugins) ->
            item {
                Text(
                    text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(plugins.take(3)) { plugin ->
                PluginCard(
                    plugin = plugin,
                    onPrimaryAction = { 
                        when (plugin.status) {
                            PluginStatus.AVAILABLE -> onInstall(plugin)
                            PluginStatus.INSTALLED, PluginStatus.ENABLED -> onConfigure(plugin)
                            else -> {}
                        }
                    }
                )
            }
            
            if (plugins.size > 3) {
                item {
                    TextButton(
                        onClick = { /* Navigate to full category */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View all ${plugins.size} plugins")
                    }
                }
            }
        }
    }
}

@Composable
fun InstalledPluginsTab(
    plugins: List<Plugin>,
    onUninstall: (Plugin) -> Unit,
    onEnable: (Plugin) -> Unit,
    onDisable: (Plugin) -> Unit,
    onConfigure: (Plugin) -> Unit
) {
    if (plugins.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "No plugins installed",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(plugins) { plugin ->
                InstalledPluginCard(
                    plugin = plugin,
                    onUninstall = { onUninstall(plugin) },
                    onEnable = { onEnable(plugin) },
                    onDisable = { onDisable(plugin) },
                    onConfigure = { onConfigure(plugin) }
                )
            }
        }
    }
}

@Composable
fun UpdatesTab(
    plugins: List<Plugin>,
    onUpdate: (Plugin) -> Unit
) {
    if (plugins.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color(0xFF4CAF50)
                )
                Text(
                    text = "All plugins are up to date",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${plugins.size} updates available",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    TextButton(
                        onClick = { plugins.forEach(onUpdate) }
                    ) {
                        Text("Update All")
                    }
                }
            }
            
            items(plugins) { plugin ->
                UpdatePluginCard(
                    plugin = plugin,
                    onUpdate = { onUpdate(plugin) }
                )
            }
        }
    }
}

// ============================================================================
// 🎴 PLUGIN CARDS
// ============================================================================

@Composable
fun PluginCard(
    plugin: Plugin,
    onPrimaryAction: () -> Unit,
    showFullDetails: Boolean = false
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = plugin.icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = plugin.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "by ${plugin.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                PluginStatusChip(status = plugin.status)
            }
            
            Text(
                text = plugin.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (showFullDetails) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (plugin.rating > 0) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFFFB300)
                                )
                                Text(
                                    text = "%.1f".format(plugin.rating),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        Text(
                            text = "${plugin.downloads} downloads",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "v${plugin.version}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (plugin.tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(plugin.tags.take(3)) { tag ->
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val buttonText = when (plugin.status) {
                    PluginStatus.AVAILABLE -> "Install"
                    PluginStatus.INSTALLED -> "Configure"
                    PluginStatus.ENABLED -> "Configure"
                    PluginStatus.DISABLED -> "Enable"
                    PluginStatus.UPDATING -> "Updating..."
                    PluginStatus.ERROR -> "Retry"
                }
                
                Button(
                    onClick = onPrimaryAction,
                    enabled = plugin.status != PluginStatus.UPDATING
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}

@Composable
fun InstalledPluginCard(
    plugin: Plugin,
    onUninstall: () -> Unit,
    onEnable: () -> Unit,
    onDisable: () -> Unit,
    onConfigure: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = plugin.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = plugin.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "v${plugin.version}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            PluginStatusChip(status = plugin.status)
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onConfigure,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configure",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = if (plugin.status == PluginStatus.ENABLED) onDisable else onEnable,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (plugin.status == PluginStatus.ENABLED) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (plugin.status == PluginStatus.ENABLED) "Disable" else "Enable",
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                IconButton(
                    onClick = onUninstall,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Uninstall",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun UpdatePluginCard(
    plugin: Plugin,
    onUpdate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = plugin.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = plugin.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Update available: v${plugin.version}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(onClick = onUpdate) {
                Text("Update")
            }
        }
    }
}

// ============================================================================
// 🎨 CUSTOM COMPONENT BUILDER
// ============================================================================

@Composable
fun CustomComponentBuilder(
    components: List<CustomComponent>,
    onCreateComponent: (CustomComponent) -> Unit,
    onEditComponent: (CustomComponent) -> Unit,
    onDeleteComponent: (CustomComponent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Custom Components (${components.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { /* Create new component */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create")
            }
        }
        
        if (components.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No custom components yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Create your first custom component",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(components) { component ->
                    CustomComponentCard(
                        component = component,
                        onEdit = { onEditComponent(component) },
                        onDelete = { onDeleteComponent(component) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomComponentCard(
    component: CustomComponent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                    text = component.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
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
                text = component.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = component.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Component preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    component.preview()
                }
            }
            
            Text(
                text = "${component.parameters.size} parameters",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 🔗 INTEGRATION MANAGER
// ============================================================================

@Composable
fun IntegrationManager(
    integrations: List<Integration>,
    onConnectIntegration: (Integration) -> Unit,
    onDisconnectIntegration: (Integration) -> Unit,
    onConfigureIntegration: (Integration) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Third-Party Integrations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(integrations) { integration ->
            IntegrationCard(
                integration = integration,
                onConnect = { onConnectIntegration(integration) },
                onDisconnect = { onDisconnectIntegration(integration) },
                onConfigure = { onConfigureIntegration(integration) }
            )
        }
    }
}

@Composable
fun IntegrationCard(
    integration: Integration,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onConfigure: () -> Unit
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = integration.icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = integration.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "by ${integration.provider}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IntegrationStatusChip(status = integration.status)
            }
            
            Text(
                text = integration.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (integration.capabilities.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(integration.capabilities.take(3)) { capability ->
                        AssistChip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = capability,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
            
            if (integration.isConnected && integration.lastSync != null) {
                Text(
                    text = "Last sync: ${formatRelativeTime(integration.lastSync)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (integration.isConnected) {
                    OutlinedButton(onClick = onConfigure) {
                        Text("Configure")
                    }
                    
                    OutlinedButton(onClick = onDisconnect) {
                        Text("Disconnect")
                    }
                } else {
                    Button(onClick = onConnect) {
                        Text("Connect")
                    }
                }
            }
        }
    }
}

// ============================================================================
// 🎨 HELPER COMPONENTS
// ============================================================================

@Composable
fun PluginStatusChip(status: PluginStatus) {
    val (color, text) = when (status) {
        PluginStatus.AVAILABLE -> Color(0xFF9E9E9E) to "Available"
        PluginStatus.INSTALLED -> Color(0xFF2196F3) to "Installed"
        PluginStatus.ENABLED -> Color(0xFF4CAF50) to "Enabled"
        PluginStatus.DISABLED -> Color(0xFFFF9800) to "Disabled"
        PluginStatus.UPDATING -> Color(0xFF9C27B0) to "Updating"
        PluginStatus.ERROR -> Color(0xFFF44336) to "Error"
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
fun IntegrationStatusChip(status: IntegrationStatus) {
    val (color, text) = when (status) {
        IntegrationStatus.CONNECTED -> Color(0xFF4CAF50) to "Connected"
        IntegrationStatus.DISCONNECTED -> Color(0xFF9E9E9E) to "Disconnected"
        IntegrationStatus.SYNCING -> Color(0xFF2196F3) to "Syncing"
        IntegrationStatus.ERROR -> Color(0xFFF44336) to "Error"
        IntegrationStatus.PENDING -> Color(0xFFFF9800) to "Pending"
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
fun PermissionChip(permission: String) {
    AssistChip(
        onClick = { },
        label = { 
            Text(
                text = permission,
                style = MaterialTheme.typography.labelSmall
            )
        },
        modifier = Modifier.height(20.dp)
    )
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

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