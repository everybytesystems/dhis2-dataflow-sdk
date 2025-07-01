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
 * DHIS2 Organisation Unit Components
 * Hierarchical organisation unit selection and management
 */

// ============================================================================
// 🏢 ORGANISATION UNIT MODELS
// ============================================================================

data class OrganisationUnit(
    val id: String,
    val name: String,
    val displayName: String = name,
    val shortName: String = name,
    val code: String? = null,
    val description: String? = null,
    val level: Int,
    val path: String,
    val parent: String? = null,
    val children: List<String> = emptyList(),
    val coordinates: String? = null,
    val featureType: String? = null,
    val geometry: String? = null,
    val openingDate: String? = null,
    val closedDate: String? = null,
    val comment: String? = null,
    val url: String? = null,
    val contactPerson: String? = null,
    val address: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val organisationUnitGroups: List<String> = emptyList(),
    val programs: List<String> = emptyList(),
    val dataSets: List<String> = emptyList()
)

data class OrganisationUnitLevel(
    val id: String,
    val name: String,
    val displayName: String = name,
    val level: Int,
    val offlineLevels: Int? = null
)

data class OrganisationUnitGroup(
    val id: String,
    val name: String,
    val displayName: String = name,
    val shortName: String = name,
    val code: String? = null,
    val description: String? = null,
    val symbol: String? = null,
    val color: String? = null,
    val organisationUnits: List<String> = emptyList()
)

enum class OrganisationUnitSelectionMode {
    SINGLE,
    MULTIPLE,
    DESCENDANTS,
    CHILDREN,
    SELECTED
}

// ============================================================================
// 🌳 ORGANISATION UNIT TREE
// ============================================================================

@Composable
fun OrganisationUnitTree(
    organisationUnits: List<OrganisationUnit>,
    selectedUnits: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: OrganisationUnitSelectionMode = OrganisationUnitSelectionMode.MULTIPLE,
    showSearch: Boolean = true,
    showLevels: Boolean = true,
    expandedByDefault: Boolean = false,
    maxSelections: Int? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedNodes by remember { mutableStateOf(setOf<String>()) }
    
    // Build tree structure
    val rootUnits = remember(organisationUnits) {
        organisationUnits.filter { it.parent == null }
    }
    
    val filteredUnits = remember(organisationUnits, searchQuery) {
        if (searchQuery.isEmpty()) {
            organisationUnits
        } else {
            organisationUnits.filter { 
                it.displayName.contains(searchQuery, ignoreCase = true) ||
                it.code?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }
    
    LaunchedEffect(expandedByDefault) {
        if (expandedByDefault) {
            expandedNodes = organisationUnits.map { it.id }.toSet()
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search organisation units...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                } else null,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Selection summary
        if (selectedUnits.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedUnits.size} unit(s) selected",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (selectedUnits.isNotEmpty()) {
                        TextButton(
                            onClick = { onSelectionChange(emptySet()) }
                        ) {
                            Text("Clear All")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Tree view
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (searchQuery.isEmpty()) {
                // Show hierarchical tree
                items(rootUnits) { unit ->
                    OrganisationUnitTreeNode(
                        unit = unit,
                        allUnits = organisationUnits,
                        selectedUnits = selectedUnits,
                        expandedNodes = expandedNodes,
                        onSelectionChange = { unitId, isSelected ->
                            handleUnitSelection(
                                unitId = unitId,
                                isSelected = isSelected,
                                currentSelection = selectedUnits,
                                selectionMode = selectionMode,
                                allUnits = organisationUnits,
                                maxSelections = maxSelections,
                                onSelectionChange = onSelectionChange
                            )
                        },
                        onExpandToggle = { unitId ->
                            expandedNodes = if (unitId in expandedNodes) {
                                expandedNodes - unitId
                            } else {
                                expandedNodes + unitId
                            }
                        },
                        level = 0,
                        selectionMode = selectionMode
                    )
                }
            } else {
                // Show filtered flat list
                items(filteredUnits) { unit ->
                    OrganisationUnitListItem(
                        unit = unit,
                        isSelected = unit.id in selectedUnits,
                        onSelectionChange = { isSelected ->
                            handleUnitSelection(
                                unitId = unit.id,
                                isSelected = isSelected,
                                currentSelection = selectedUnits,
                                selectionMode = selectionMode,
                                allUnits = organisationUnits,
                                maxSelections = maxSelections,
                                onSelectionChange = onSelectionChange
                            )
                        },
                        selectionMode = selectionMode
                    )
                }
            }
        }
    }
}

@Composable
private fun OrganisationUnitTreeNode(
    unit: OrganisationUnit,
    allUnits: List<OrganisationUnit>,
    selectedUnits: Set<String>,
    expandedNodes: Set<String>,
    onSelectionChange: (String, Boolean) -> Unit,
    onExpandToggle: (String) -> Unit,
    level: Int,
    selectionMode: OrganisationUnitSelectionMode
) {
    val hasChildren = unit.children.isNotEmpty()
    val isExpanded = unit.id in expandedNodes
    val isSelected = unit.id in selectedUnits
    val childUnits = allUnits.filter { it.parent == unit.id }
    
    Column {
        // Current unit
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (selectionMode != OrganisationUnitSelectionMode.SINGLE || !isSelected) {
                        onSelectionChange(unit.id, !isSelected)
                    }
                }
                .padding(
                    start = (level * 16).dp,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Expand/collapse button
            if (hasChildren) {
                IconButton(
                    onClick = { onExpandToggle(unit.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
            
            // Selection indicator
            when (selectionMode) {
                OrganisationUnitSelectionMode.SINGLE -> {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectionChange(unit.id, true) }
                    )
                }
                else -> {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onSelectionChange(unit.id, it) }
                    )
                }
            }
            
            // Unit info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                
                if (unit.code != null) {
                    Text(
                        text = "Code: ${unit.code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "Level ${unit.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Unit type icon
            Icon(
                imageVector = when (unit.level) {
                    1 -> Icons.Default.Public
                    2 -> Icons.Default.LocationCity
                    3 -> Icons.Default.Business
                    else -> Icons.Default.Place
                },
                contentDescription = "Level ${unit.level}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Children (if expanded)
        AnimatedVisibility(
            visible = isExpanded && hasChildren,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                childUnits.forEach { childUnit ->
                    OrganisationUnitTreeNode(
                        unit = childUnit,
                        allUnits = allUnits,
                        selectedUnits = selectedUnits,
                        expandedNodes = expandedNodes,
                        onSelectionChange = onSelectionChange,
                        onExpandToggle = onExpandToggle,
                        level = level + 1,
                        selectionMode = selectionMode
                    )
                }
            }
        }
    }
}

@Composable
private fun OrganisationUnitListItem(
    unit: OrganisationUnit,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    selectionMode: OrganisationUnitSelectionMode
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChange(!isSelected) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selection indicator
            when (selectionMode) {
                OrganisationUnitSelectionMode.SINGLE -> {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectionChange(true) }
                    )
                }
                else -> {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelectionChange
                    )
                }
            }
            
            // Unit info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (unit.code != null) {
                        Text(
                            text = unit.code,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Text(
                        text = "Level ${unit.level}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Text(
                    text = unit.path.replace("/", " > "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Unit type icon
            Icon(
                imageVector = when (unit.level) {
                    1 -> Icons.Default.Public
                    2 -> Icons.Default.LocationCity
                    3 -> Icons.Default.Business
                    else -> Icons.Default.Place
                },
                contentDescription = "Level ${unit.level}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 📋 ORGANISATION UNIT SELECTOR
// ============================================================================

@Composable
fun OrganisationUnitSelector(
    selectedUnits: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    organisationUnits: List<OrganisationUnit>,
    modifier: Modifier = Modifier,
    selectionMode: OrganisationUnitSelectionMode = OrganisationUnitSelectionMode.MULTIPLE,
    placeholder: String = "Select organisation units...",
    enabled: Boolean = true,
    isError: Boolean = false,
    maxSelections: Int? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    
    val selectedUnitNames = remember(selectedUnits, organisationUnits) {
        organisationUnits.filter { it.id in selectedUnits }.map { it.displayName }
    }
    
    val displayText = when {
        selectedUnitNames.isEmpty() -> placeholder
        selectedUnitNames.size == 1 -> selectedUnitNames.first()
        else -> "${selectedUnitNames.size} units selected"
    }
    
    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = true,
        isError = isError,
        trailingIcon = {
            IconButton(
                onClick = { if (enabled) showDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select organisation units"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
    
    if (showDialog) {
        OrganisationUnitSelectionDialog(
            selectedUnits = selectedUnits,
            onSelectionChange = onSelectionChange,
            organisationUnits = organisationUnits,
            onDismiss = { showDialog = false },
            selectionMode = selectionMode,
            maxSelections = maxSelections
        )
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun handleUnitSelection(
    unitId: String,
    isSelected: Boolean,
    currentSelection: Set<String>,
    selectionMode: OrganisationUnitSelectionMode,
    allUnits: List<OrganisationUnit>,
    maxSelections: Int?,
    onSelectionChange: (Set<String>) -> Unit
) {
    val newSelection = when {
        !isSelected -> currentSelection - unitId
        selectionMode == OrganisationUnitSelectionMode.SINGLE -> setOf(unitId)
        maxSelections != null && currentSelection.size >= maxSelections -> currentSelection
        else -> {
            when (selectionMode) {
                OrganisationUnitSelectionMode.DESCENDANTS -> {
                    val unit = allUnits.find { it.id == unitId }
                    val descendants = getDescendants(unit, allUnits)
                    currentSelection + unitId + descendants.map { it.id }
                }
                OrganisationUnitSelectionMode.CHILDREN -> {
                    val unit = allUnits.find { it.id == unitId }
                    val children = allUnits.filter { it.parent == unit?.id }
                    currentSelection + unitId + children.map { it.id }
                }
                else -> currentSelection + unitId
            }
        }
    }
    
    onSelectionChange(newSelection)
}

private fun getDescendants(unit: OrganisationUnit?, allUnits: List<OrganisationUnit>): List<OrganisationUnit> {
    if (unit == null) return emptyList()
    
    val children = allUnits.filter { it.parent == unit.id }
    return children + children.flatMap { getDescendants(it, allUnits) }
}

// ============================================================================
// 📱 ORGANISATION UNIT SELECTION DIALOG
// ============================================================================

@Composable
fun OrganisationUnitSelectionDialog(
    selectedUnits: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    organisationUnits: List<OrganisationUnit>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: OrganisationUnitSelectionMode = OrganisationUnitSelectionMode.MULTIPLE,
    maxSelections: Int? = null,
    title: String = "Select Organisation Units"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OrganisationUnitTree(
                organisationUnits = organisationUnits,
                selectedUnits = selectedUnits,
                onSelectionChange = onSelectionChange,
                selectionMode = selectionMode,
                maxSelections = maxSelections,
                modifier = Modifier.height(400.dp)
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}