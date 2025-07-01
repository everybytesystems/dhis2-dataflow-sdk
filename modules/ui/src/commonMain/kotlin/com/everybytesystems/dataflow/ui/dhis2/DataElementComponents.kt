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
 * DHIS2 Data Element Components
 * Comprehensive data element selection and management
 */

// ============================================================================
// 📊 DATA ELEMENT MODELS
// ============================================================================

data class DataElement(
    val id: String,
    val name: String,
    val displayName: String = name,
    val shortName: String = name,
    val code: String? = null,
    val description: String? = null,
    val formName: String? = null,
    val valueType: ValueType = ValueType.TEXT,
    val aggregationType: AggregationType = AggregationType.SUM,
    val domainType: DomainType = DomainType.AGGREGATE,
    val categoryCombo: String? = null,
    val optionSet: String? = null,
    val commentOptionSet: String? = null,
    val zeroIsSignificant: Boolean = false,
    val url: String? = null,
    val fieldMask: String? = null,
    val dataElementGroups: List<String> = emptyList(),
    val dataSets: List<String> = emptyList(),
    val legendSets: List<String> = emptyList(),
    val aggregationLevels: List<Int> = emptyList()
)

enum class ValueType(val displayName: String) {
    TEXT("Text"),
    LONG_TEXT("Long text"),
    LETTER("Letter"),
    PHONE_NUMBER("Phone number"),
    EMAIL("Email"),
    BOOLEAN("Yes/No"),
    TRUE_ONLY("Yes only"),
    DATE("Date"),
    DATETIME("Date & time"),
    TIME("Time"),
    NUMBER("Number"),
    UNIT_INTERVAL("Unit interval"),
    PERCENTAGE("Percentage"),
    INTEGER("Integer"),
    INTEGER_POSITIVE("Positive integer"),
    INTEGER_NEGATIVE("Negative integer"),
    INTEGER_ZERO_OR_POSITIVE("Zero or positive integer"),
    TRACKER_ASSOCIATE("Tracker associate"),
    USERNAME("Username"),
    COORDINATE("Coordinate"),
    ORGANISATION_UNIT("Organisation unit"),
    REFERENCE("Reference"),
    AGE("Age"),
    URL("URL"),
    FILE_RESOURCE("File"),
    IMAGE("Image")
}

enum class AggregationType(val displayName: String) {
    SUM("Sum"),
    AVERAGE("Average"),
    AVERAGE_SUM_ORG_UNIT("Average (sum in org unit hierarchy)"),
    COUNT("Count"),
    STDDEV("Standard deviation"),
    VARIANCE("Variance"),
    MIN("Min"),
    MAX("Max"),
    NONE("None"),
    CUSTOM("Custom"),
    DEFAULT("Default")
}

enum class DomainType(val displayName: String) {
    AGGREGATE("Aggregate"),
    TRACKER("Tracker")
}

data class DataElementGroup(
    val id: String,
    val name: String,
    val displayName: String = name,
    val shortName: String = name,
    val code: String? = null,
    val description: String? = null,
    val dataElements: List<String> = emptyList()
)

data class DataElementGroupSet(
    val id: String,
    val name: String,
    val displayName: String = name,
    val shortName: String = name,
    val code: String? = null,
    val description: String? = null,
    val compulsory: Boolean = false,
    val dataDimension: Boolean = false,
    val dataElementGroups: List<String> = emptyList()
)

enum class DataElementSelectionMode {
    SINGLE,
    MULTIPLE
}

// ============================================================================
// 📊 DATA ELEMENT SELECTOR
// ============================================================================

@Composable
fun DataElementSelector(
    selectedElements: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    dataElements: List<DataElement>,
    modifier: Modifier = Modifier,
    selectionMode: DataElementSelectionMode = DataElementSelectionMode.MULTIPLE,
    placeholder: String = "Select data elements...",
    enabled: Boolean = true,
    isError: Boolean = false,
    maxSelections: Int? = null,
    dataElementGroups: List<DataElementGroup> = emptyList(),
    showGroups: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    
    val selectedElementNames = remember(selectedElements, dataElements) {
        dataElements.filter { it.id in selectedElements }.map { it.displayName }
    }
    
    val displayText = when {
        selectedElementNames.isEmpty() -> placeholder
        selectedElementNames.size == 1 -> selectedElementNames.first()
        else -> "${selectedElementNames.size} elements selected"
    }
    
    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = true,
        isError = isError,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DataUsage,
                contentDescription = "Data elements"
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { if (enabled) showDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select data elements"
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
    
    if (showDialog) {
        DataElementSelectionDialog(
            selectedElements = selectedElements,
            onSelectionChange = onSelectionChange,
            dataElements = dataElements,
            onDismiss = { showDialog = false },
            selectionMode = selectionMode,
            maxSelections = maxSelections,
            dataElementGroups = dataElementGroups,
            showGroups = showGroups
        )
    }
}

// ============================================================================
// 📱 DATA ELEMENT SELECTION DIALOG
// ============================================================================

@Composable
fun DataElementSelectionDialog(
    selectedElements: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    dataElements: List<DataElement>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    selectionMode: DataElementSelectionMode = DataElementSelectionMode.MULTIPLE,
    maxSelections: Int? = null,
    dataElementGroups: List<DataElementGroup> = emptyList(),
    showGroups: Boolean = true,
    title: String = "Select Data Elements"
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var selectedValueType by remember { mutableStateOf<ValueType?>(null) }
    var selectedAggregationType by remember { mutableStateOf<AggregationType?>(null) }
    
    val tabs = if (showGroups && dataElementGroups.isNotEmpty()) {
        listOf("All Elements", "By Group", "Filters")
    } else {
        listOf("All Elements", "Filters")
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.width(600.dp),
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // Selection summary
                if (selectedElements.isNotEmpty()) {
                    Text(
                        text = "${selectedElements.size} element(s) selected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Tab row
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(tab) }
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.height(500.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // All elements tab
                        DataElementListContent(
                            dataElements = dataElements,
                            selectedElements = selectedElements,
                            onSelectionChange = onSelectionChange,
                            selectionMode = selectionMode,
                            maxSelections = maxSelections,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            valueTypeFilter = selectedValueType,
                            aggregationTypeFilter = selectedAggregationType
                        )
                    }
                    1 -> {
                        if (showGroups && dataElementGroups.isNotEmpty()) {
                            // By group tab
                            DataElementGroupContent(
                                dataElementGroups = dataElementGroups,
                                dataElements = dataElements,
                                selectedElements = selectedElements,
                                onSelectionChange = onSelectionChange,
                                selectionMode = selectionMode,
                                maxSelections = maxSelections,
                                selectedGroup = selectedGroup,
                                onGroupChange = { selectedGroup = it }
                            )
                        } else {
                            // Filters tab
                            DataElementFiltersContent(
                                selectedValueType = selectedValueType,
                                onValueTypeChange = { selectedValueType = it },
                                selectedAggregationType = selectedAggregationType,
                                onAggregationTypeChange = { selectedAggregationType = it }
                            )
                        }
                    }
                    2 -> {
                        // Filters tab (when groups are shown)
                        DataElementFiltersContent(
                            selectedValueType = selectedValueType,
                            onValueTypeChange = { selectedValueType = it },
                            selectedAggregationType = selectedAggregationType,
                            onAggregationTypeChange = { selectedAggregationType = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedElements.isNotEmpty()) {
                    TextButton(
                        onClick = { onSelectionChange(emptySet()) }
                    ) {
                        Text("Clear All")
                    }
                }
                
                Button(onClick = onDismiss) {
                    Text("Done")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DataElementListContent(
    dataElements: List<DataElement>,
    selectedElements: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    selectionMode: DataElementSelectionMode,
    maxSelections: Int?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    valueTypeFilter: ValueType?,
    aggregationTypeFilter: AggregationType?
) {
    val filteredElements = remember(dataElements, searchQuery, valueTypeFilter, aggregationTypeFilter) {
        dataElements.filter { element ->
            val matchesSearch = searchQuery.isEmpty() || 
                element.displayName.contains(searchQuery, ignoreCase = true) ||
                element.code?.contains(searchQuery, ignoreCase = true) == true ||
                element.description?.contains(searchQuery, ignoreCase = true) == true
            
            val matchesValueType = valueTypeFilter == null || element.valueType == valueTypeFilter
            val matchesAggregationType = aggregationTypeFilter == null || element.aggregationType == aggregationTypeFilter
            
            matchesSearch && matchesValueType && matchesAggregationType
        }
    }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search data elements...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )
                    }
                }
            } else null,
            singleLine = true
        )
        
        // Results summary
        Text(
            text = "${filteredElements.size} elements found",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Elements list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredElements) { element ->
                DataElementListItem(
                    element = element,
                    isSelected = element.id in selectedElements,
                    onSelectionChange = { isSelected ->
                        handleElementSelection(
                            elementId = element.id,
                            isSelected = isSelected,
                            currentSelection = selectedElements,
                            selectionMode = selectionMode,
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

@Composable
private fun DataElementGroupContent(
    dataElementGroups: List<DataElementGroup>,
    dataElements: List<DataElement>,
    selectedElements: Set<String>,
    onSelectionChange: (Set<String>) -> Unit,
    selectionMode: DataElementSelectionMode,
    maxSelections: Int?,
    selectedGroup: String?,
    onGroupChange: (String?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group selector
        var groupExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = groupExpanded,
            onExpandedChange = { groupExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedGroup?.let { groupId ->
                    dataElementGroups.find { it.id == groupId }?.displayName
                } ?: "All Groups",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Data Element Group") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded)
                }
            )
            
            ExposedDropdownMenu(
                expanded = groupExpanded,
                onDismissRequest = { groupExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Groups") },
                    onClick = {
                        onGroupChange(null)
                        groupExpanded = false
                    }
                )
                
                dataElementGroups.forEach { group ->
                    DropdownMenuItem(
                        text = { Text(group.displayName) },
                        onClick = {
                            onGroupChange(group.id)
                            groupExpanded = false
                        }
                    )
                }
            }
        }
        
        // Group elements
        val groupElements = remember(dataElements, selectedGroup) {
            if (selectedGroup == null) {
                dataElements
            } else {
                val group = dataElementGroups.find { it.id == selectedGroup }
                dataElements.filter { it.id in (group?.dataElements ?: emptyList()) }
            }
        }
        
        Text(
            text = "${groupElements.size} elements in group",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(groupElements) { element ->
                DataElementListItem(
                    element = element,
                    isSelected = element.id in selectedElements,
                    onSelectionChange = { isSelected ->
                        handleElementSelection(
                            elementId = element.id,
                            isSelected = isSelected,
                            currentSelection = selectedElements,
                            selectionMode = selectionMode,
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

@Composable
private fun DataElementFiltersContent(
    selectedValueType: ValueType?,
    onValueTypeChange: (ValueType?) -> Unit,
    selectedAggregationType: AggregationType?,
    onAggregationTypeChange: (AggregationType?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Value type filter
        var valueTypeExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = valueTypeExpanded,
            onExpandedChange = { valueTypeExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedValueType?.displayName ?: "All Value Types",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Value Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = valueTypeExpanded)
                }
            )
            
            ExposedDropdownMenu(
                expanded = valueTypeExpanded,
                onDismissRequest = { valueTypeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Value Types") },
                    onClick = {
                        onValueTypeChange(null)
                        valueTypeExpanded = false
                    }
                )
                
                ValueType.values().forEach { valueType ->
                    DropdownMenuItem(
                        text = { Text(valueType.displayName) },
                        onClick = {
                            onValueTypeChange(valueType)
                            valueTypeExpanded = false
                        }
                    )
                }
            }
        }
        
        // Aggregation type filter
        var aggregationTypeExpanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = aggregationTypeExpanded,
            onExpandedChange = { aggregationTypeExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedAggregationType?.displayName ?: "All Aggregation Types",
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                label = { Text("Aggregation Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = aggregationTypeExpanded)
                }
            )
            
            ExposedDropdownMenu(
                expanded = aggregationTypeExpanded,
                onDismissRequest = { aggregationTypeExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All Aggregation Types") },
                    onClick = {
                        onAggregationTypeChange(null)
                        aggregationTypeExpanded = false
                    }
                )
                
                AggregationType.values().forEach { aggregationType ->
                    DropdownMenuItem(
                        text = { Text(aggregationType.displayName) },
                        onClick = {
                            onAggregationTypeChange(aggregationType)
                            aggregationTypeExpanded = false
                        }
                    )
                }
            }
        }
        
        // Clear filters button
        if (selectedValueType != null || selectedAggregationType != null) {
            OutlinedButton(
                onClick = {
                    onValueTypeChange(null)
                    onAggregationTypeChange(null)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Clear Filters")
            }
        }
    }
}

@Composable
private fun DataElementListItem(
    element: DataElement,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    selectionMode: DataElementSelectionMode
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
                DataElementSelectionMode.SINGLE -> {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectionChange(true) }
                    )
                }
                DataElementSelectionMode.MULTIPLE -> {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelectionChange
                    )
                }
            }
            
            // Element info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = element.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (element.description != null) {
                    Text(
                        text = element.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (element.code != null) {
                        AssistChip(
                            onClick = { },
                            label = { 
                                Text(
                                    text = element.code,
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                    
                    AssistChip(
                        onClick = { },
                        label = { 
                            Text(
                                text = element.valueType.displayName,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getValueTypeColor(element.valueType).copy(alpha = 0.2f),
                            labelColor = getValueTypeColor(element.valueType)
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    
                    AssistChip(
                        onClick = { },
                        label = { 
                            Text(
                                text = element.aggregationType.displayName,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
            
            // Value type icon
            Icon(
                imageVector = getValueTypeIcon(element.valueType),
                contentDescription = element.valueType.displayName,
                tint = getValueTypeColor(element.valueType)
            )
        }
    }
}

// ============================================================================
// 📊 DATA ELEMENT DETAILS
// ============================================================================

@Composable
fun DataElementDetails(
    element: DataElement,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = element.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (element.description != null) {
                                Text(
                                    text = element.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        if (onEditClick != null) {
                            IconButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit data element"
                                )
                            }
                        }
                    }
                    
                    // Key properties
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssistChip(
                            onClick = { },
                            label = { Text(element.valueType.displayName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = getValueTypeIcon(element.valueType),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = getValueTypeColor(element.valueType).copy(alpha = 0.2f),
                                labelColor = getValueTypeColor(element.valueType)
                            )
                        )
                        
                        AssistChip(
                            onClick = { },
                            label = { Text(element.aggregationType.displayName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Functions,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        
                        AssistChip(
                            onClick = { },
                            label = { Text(element.domainType.displayName) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }
        
        // Basic Information
        item {
            DataElementInfoSection(
                title = "Basic Information",
                icon = Icons.Default.Info
            ) {
                DataElementInfoItem("ID", element.id)
                DataElementInfoItem("Name", element.name)
                DataElementInfoItem("Display Name", element.displayName)
                DataElementInfoItem("Short Name", element.shortName)
                element.code?.let { DataElementInfoItem("Code", it) }
                element.formName?.let { DataElementInfoItem("Form Name", it) }
                element.description?.let { DataElementInfoItem("Description", it) }
            }
        }
        
        // Technical Properties
        item {
            DataElementInfoSection(
                title = "Technical Properties",
                icon = Icons.Default.Settings
            ) {
                DataElementInfoItem("Value Type", element.valueType.displayName)
                DataElementInfoItem("Aggregation Type", element.aggregationType.displayName)
                DataElementInfoItem("Domain Type", element.domainType.displayName)
                DataElementInfoItem("Zero is Significant", if (element.zeroIsSignificant) "Yes" else "No")
                element.categoryCombo?.let { DataElementInfoItem("Category Combination", it) }
                element.optionSet?.let { DataElementInfoItem("Option Set", it) }
                element.commentOptionSet?.let { DataElementInfoItem("Comment Option Set", it) }
                element.fieldMask?.let { DataElementInfoItem("Field Mask", it) }
                element.url?.let { DataElementInfoItem("URL", it) }
            }
        }
        
        // Associations
        if (element.dataElementGroups.isNotEmpty() || element.dataSets.isNotEmpty() || element.legendSets.isNotEmpty()) {
            item {
                DataElementInfoSection(
                    title = "Associations",
                    icon = Icons.Default.Link
                ) {
                    if (element.dataElementGroups.isNotEmpty()) {
                        DataElementInfoItem("Data Element Groups", "${element.dataElementGroups.size} group(s)")
                    }
                    if (element.dataSets.isNotEmpty()) {
                        DataElementInfoItem("Data Sets", "${element.dataSets.size} set(s)")
                    }
                    if (element.legendSets.isNotEmpty()) {
                        DataElementInfoItem("Legend Sets", "${element.legendSets.size} set(s)")
                    }
                    if (element.aggregationLevels.isNotEmpty()) {
                        DataElementInfoItem("Aggregation Levels", element.aggregationLevels.joinToString(", "))
                    }
                }
            }
        }
    }
}

@Composable
private fun DataElementInfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun DataElementInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun handleElementSelection(
    elementId: String,
    isSelected: Boolean,
    currentSelection: Set<String>,
    selectionMode: DataElementSelectionMode,
    maxSelections: Int?,
    onSelectionChange: (Set<String>) -> Unit
) {
    val newSelection = when {
        !isSelected -> currentSelection - elementId
        selectionMode == DataElementSelectionMode.SINGLE -> setOf(elementId)
        maxSelections != null && currentSelection.size >= maxSelections -> currentSelection
        else -> currentSelection + elementId
    }
    
    onSelectionChange(newSelection)
}

private fun getValueTypeIcon(valueType: ValueType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (valueType) {
        ValueType.TEXT, ValueType.LONG_TEXT, ValueType.LETTER -> Icons.Default.TextFields
        ValueType.NUMBER, ValueType.INTEGER, ValueType.INTEGER_POSITIVE,
        ValueType.INTEGER_NEGATIVE, ValueType.INTEGER_ZERO_OR_POSITIVE,
        ValueType.PERCENTAGE, ValueType.UNIT_INTERVAL -> Icons.Default.Numbers
        ValueType.BOOLEAN, ValueType.TRUE_ONLY -> Icons.Default.CheckBox
        ValueType.DATE, ValueType.DATETIME, ValueType.TIME -> Icons.Default.DateRange
        ValueType.PHONE_NUMBER -> Icons.Default.Phone
        ValueType.EMAIL -> Icons.Default.Email
        ValueType.URL -> Icons.Default.Link
        ValueType.FILE_RESOURCE -> Icons.Default.AttachFile
        ValueType.IMAGE -> Icons.Default.Image
        ValueType.COORDINATE -> Icons.Default.LocationOn
        ValueType.ORGANISATION_UNIT -> Icons.Default.Business
        else -> Icons.Default.DataUsage
    }
}

private fun getValueTypeColor(valueType: ValueType): Color {
    return when (valueType) {
        ValueType.TEXT, ValueType.LONG_TEXT, ValueType.LETTER -> Color(0xFF2196F3)
        ValueType.NUMBER, ValueType.INTEGER, ValueType.INTEGER_POSITIVE,
        ValueType.INTEGER_NEGATIVE, ValueType.INTEGER_ZERO_OR_POSITIVE,
        ValueType.PERCENTAGE, ValueType.UNIT_INTERVAL -> Color(0xFF4CAF50)
        ValueType.BOOLEAN, ValueType.TRUE_ONLY -> Color(0xFFFF9800)
        ValueType.DATE, ValueType.DATETIME, ValueType.TIME -> Color(0xFF9C27B0)
        ValueType.PHONE_NUMBER, ValueType.EMAIL -> Color(0xFF607D8B)
        ValueType.URL, ValueType.FILE_RESOURCE, ValueType.IMAGE -> Color(0xFF795548)
        ValueType.COORDINATE, ValueType.ORGANISATION_UNIT -> Color(0xFF009688)
        else -> Color(0xFF757575)
    }
}