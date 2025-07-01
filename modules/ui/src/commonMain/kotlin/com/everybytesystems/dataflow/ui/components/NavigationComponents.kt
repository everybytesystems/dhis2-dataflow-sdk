package com.everybytesystems.dataflow.ui.components

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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

/**
 * Navigation Components
 * Comprehensive navigation patterns and components
 */

// ============================================================================
// 📱 NAVIGATION MODELS
// ============================================================================

data class NavigationItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val badge: String? = null,
    val enabled: Boolean = true,
    val children: List<NavigationItem> = emptyList()
)

data class BreadcrumbItem(
    val label: String,
    val onClick: (() -> Unit)? = null
)

// ============================================================================
// 🗂️ NAVIGATION DRAWER
// ============================================================================

@Composable
fun DataFlowNavigationDrawer(
    items: List<NavigationItem>,
    selectedItemId: String?,
    onItemClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            header?.invoke()
            
            // Navigation items
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(items) { item ->
                    NavigationDrawerItem(
                        item = item,
                        isSelected = selectedItemId == item.id,
                        onItemClick = onItemClick,
                        level = 0
                    )
                }
            }
            
            // Footer
            footer?.invoke()
        }
    }
}

@Composable
private fun NavigationDrawerItem(
    item: NavigationItem,
    isSelected: Boolean,
    onItemClick: (NavigationItem) -> Unit,
    level: Int = 0,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasChildren = item.children.isNotEmpty()
    
    Column(modifier = modifier) {
        NavigationDrawerItem(
            label = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Badge
                    if (item.badge != null) {
                        Badge {
                            Text(
                                text = item.badge,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                    
                    // Expand/collapse icon for items with children
                    if (hasChildren) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.icon,
                    contentDescription = item.label
                )
            },
            selected = isSelected && !hasChildren,
            onClick = {
                if (hasChildren) {
                    isExpanded = !isExpanded
                } else {
                    onItemClick(item)
                }
            },
            modifier = Modifier.padding(start = (level * 16).dp)
        )
        
        // Children items
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                item.children.forEach { child ->
                    NavigationDrawerItem(
                        item = child,
                        isSelected = false, // Child selection logic can be added
                        onItemClick = onItemClick,
                        level = level + 1
                    )
                }
            }
        }
    }
}

// ============================================================================
// 📊 BOTTOM NAVIGATION
// ============================================================================

@Composable
fun DataFlowBottomNavigation(
    items: List<NavigationItem>,
    selectedItemId: String?,
    onItemClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badge != null) {
                                Badge {
                                    Text(
                                        text = item.badge,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (selectedItemId == item.id) item.selectedIcon else item.icon,
                            contentDescription = item.label
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = selectedItemId == item.id,
                onClick = { onItemClick(item) },
                enabled = item.enabled
            )
        }
    }
}

// ============================================================================
// 🗂️ TAB NAVIGATION
// ============================================================================

@Composable
fun DataFlowTabRow(
    tabs: List<NavigationItem>,
    selectedTabId: String?,
    onTabClick: (NavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false
) {
    val selectedIndex = tabs.indexOfFirst { it.id == selectedTabId }.takeIf { it >= 0 } ?: 0
    
    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabId == tab.id,
                    onClick = { onTabClick(tab) },
                    enabled = tab.enabled,
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedTabId == tab.id) tab.selectedIcon else tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(tab.label)
                            if (tab.badge != null) {
                                Badge {
                                    Text(
                                        text = tab.badge,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabId == tab.id,
                    onClick = { onTabClick(tab) },
                    enabled = tab.enabled,
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (tab.badge != null) {
                                        Badge {
                                            Text(
                                                text = tab.badge,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTabId == tab.id) tab.selectedIcon else tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                )
            }
        }
    }
}

// ============================================================================
// 🍞 BREADCRUMBS
// ============================================================================

@Composable
fun Breadcrumbs(
    items: List<BreadcrumbItem>,
    modifier: Modifier = Modifier,
    separator: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(items) { index, item ->
            if (index > 0) {
                item { separator() }
            }
            
            item {
                if (item.onClick != null && index < items.size - 1) {
                    TextButton(
                        onClick = item.onClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (index == items.size - 1) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (index == items.size - 1) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🔍 SEARCH BAR
// ============================================================================

@Composable
fun DataFlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search"
        )
    },
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon,
        trailingIcon = if (query.isNotEmpty()) {
            {
                Row {
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                    IconButton(
                        onClick = { onQueryChange("") }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            }
        } else trailingIcon,
        enabled = enabled,
        singleLine = true,
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onSearch = { onSearch(query) }
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Search
        )
    )
}

// ============================================================================
// 🎛️ FILTER PANEL
// ============================================================================

data class FilterOption(
    val id: String,
    val label: String,
    val count: Int? = null,
    val selected: Boolean = false
)

data class FilterGroup(
    val id: String,
    val title: String,
    val options: List<FilterOption>,
    val multiSelect: Boolean = true,
    val expanded: Boolean = true
)

@Composable
fun FilterPanel(
    filterGroups: List<FilterGroup>,
    onFilterChange: (String, String, Boolean) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onClearAll) {
                    Text("Clear All")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filter groups
            filterGroups.forEach { group ->
                FilterGroupItem(
                    group = group,
                    onFilterChange = { optionId, selected ->
                        onFilterChange(group.id, optionId, selected)
                    }
                )
                
                if (group != filterGroups.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun FilterGroupItem(
    group: FilterGroup,
    onFilterChange: (String, Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(group.expanded) }
    
    Column {
        // Group header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Group options
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                group.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFilterChange(option.id, !option.selected)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (group.multiSelect) {
                            Checkbox(
                                checked = option.selected,
                                onCheckedChange = { onFilterChange(option.id, it) }
                            )
                        } else {
                            RadioButton(
                                selected = option.selected,
                                onClick = { onFilterChange(option.id, true) }
                            )
                        }
                        
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (option.count != null) {
                            Text(
                                text = "(${option.count})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 🎯 QUICK ACTIONS
// ============================================================================

data class QuickAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val enabled: Boolean = true
)

@Composable
fun QuickActionsBar(
    actions: List<QuickAction>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(actions) { action ->
            OutlinedButton(
                onClick = action.onClick,
                enabled = action.enabled,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = action.label,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}