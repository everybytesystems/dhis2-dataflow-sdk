package com.everybytesystems.dataflow.ui.dhis2

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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

/**
 * Enhanced DHIS2 Components
 * Specialized components for health information systems
 */

// ============================================================================
// 📊 DHIS2 DATA MODELS
// ============================================================================

data class DHIS2Program(
    val id: String,
    val name: String,
    val description: String? = null,
    val programType: ProgramType,
    val trackedEntityType: String? = null,
    val organisationUnits: List<String> = emptyList(),
    val programStages: List<DHIS2ProgramStage> = emptyList(),
    val attributes: List<DHIS2Attribute> = emptyList(),
    val isActive: Boolean = true,
    val metadata: Map<String, Any> = emptyMap()
)

data class DHIS2ProgramStage(
    val id: String,
    val name: String,
    val description: String? = null,
    val sortOrder: Int = 0,
    val repeatable: Boolean = false,
    val dataElements: List<DHIS2DataElement> = emptyList(),
    val programStageSections: List<DHIS2ProgramStageSection> = emptyList()
)

data class DHIS2ProgramStageSection(
    val id: String,
    val name: String,
    val description: String? = null,
    val sortOrder: Int = 0,
    val dataElements: List<DHIS2DataElement> = emptyList()
)

data class DHIS2DataElement(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val description: String? = null,
    val valueType: ValueType,
    val optionSet: DHIS2OptionSet? = null,
    val mandatory: Boolean = false,
    val categoryCombo: String? = null
)

data class DHIS2OptionSet(
    val id: String,
    val name: String,
    val options: List<DHIS2Option>
)

data class DHIS2Option(
    val id: String,
    val name: String,
    val code: String? = null,
    val sortOrder: Int = 0
)

data class DHIS2Attribute(
    val id: String,
    val name: String,
    val shortName: String? = null,
    val description: String? = null,
    val valueType: ValueType,
    val optionSet: DHIS2OptionSet? = null,
    val mandatory: Boolean = false,
    val unique: Boolean = false
)

data class DHIS2TrackedEntity(
    val id: String,
    val trackedEntityType: String,
    val organisationUnit: String,
    val attributes: Map<String, String> = emptyMap(),
    val enrollments: List<DHIS2Enrollment> = emptyList(),
    val createdDate: String,
    val lastUpdatedDate: String
)

data class DHIS2Enrollment(
    val id: String,
    val program: String,
    val trackedEntity: String,
    val organisationUnit: String,
    val enrollmentDate: String,
    val incidentDate: String? = null,
    val status: EnrollmentStatus,
    val events: List<DHIS2Event> = emptyList()
)

data class DHIS2Event(
    val id: String,
    val program: String,
    val programStage: String,
    val enrollment: String? = null,
    val organisationUnit: String,
    val eventDate: String,
    val status: EventStatus,
    val dataValues: Map<String, String> = emptyMap()
)

data class DHIS2DataSet(
    val id: String,
    val name: String,
    val description: String? = null,
    val periodType: PeriodType,
    val dataElements: List<DHIS2DataElement> = emptyList(),
    val sections: List<DHIS2DataSetSection> = emptyList(),
    val organisationUnits: List<String> = emptyList()
)

data class DHIS2DataSetSection(
    val id: String,
    val name: String,
    val description: String? = null,
    val sortOrder: Int = 0,
    val dataElements: List<DHIS2DataElement> = emptyList()
)

enum class ProgramType {
    WITH_REGISTRATION,
    WITHOUT_REGISTRATION
}

enum class ValueType {
    TEXT,
    LONG_TEXT,
    NUMBER,
    INTEGER,
    INTEGER_POSITIVE,
    INTEGER_NEGATIVE,
    INTEGER_ZERO_OR_POSITIVE,
    BOOLEAN,
    TRUE_ONLY,
    DATE,
    DATETIME,
    TIME,
    EMAIL,
    PHONE_NUMBER,
    URL,
    FILE_RESOURCE,
    IMAGE,
    COORDINATE,
    ORGANISATION_UNIT,
    USERNAME,
    TRACKER_ASSOCIATE
}

enum class EnrollmentStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class EventStatus {
    ACTIVE,
    COMPLETED,
    VISITED,
    SCHEDULE,
    OVERDUE,
    SKIPPED
}

enum class PeriodType {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY
}

// ============================================================================
// 📋 DHIS2 PROGRAM MANAGER
// ============================================================================

@Composable
fun DHIS2ProgramManager(
    programs: List<DHIS2Program>,
    onProgramSelect: (DHIS2Program) -> Unit,
    onCreateProgram: () -> Unit,
    modifier: Modifier = Modifier,
    showCreateButton: Boolean = true,
    searchQuery: String = "",
    onSearchQueryChange: ((String) -> Unit)? = null
) {
    var filteredPrograms by remember(programs, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) {
                programs
            } else {
                programs.filter { program ->
                    program.name.contains(searchQuery, ignoreCase = true) ||
                    program.description?.contains(searchQuery, ignoreCase = true) == true
                }
            }
        )
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Header with search and create button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Programs",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (showCreateButton) {
                Button(
                    onClick = onCreateProgram,
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Program")
                }
            }
        }
        
        // Search bar
        if (onSearchQueryChange != null) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search programs...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )
        }
        
        // Programs list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPrograms) { program ->
                DHIS2ProgramCard(
                    program = program,
                    onClick = { onProgramSelect(program) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            if (filteredPrograms.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
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
                                text = if (searchQuery.isBlank()) "No programs available" else "No programs found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DHIS2ProgramCard(
    program: DHIS2Program,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with program name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = program.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Badge(
                    containerColor = if (program.isActive) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                ) {
                    Text(
                        text = if (program.isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            // Description
            program.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Program details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Program type
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when (program.programType) {
                            ProgramType.WITH_REGISTRATION -> Icons.Default.PersonAdd
                            ProgramType.WITHOUT_REGISTRATION -> Icons.Default.Event
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = program.programType.name.replace("_", " "),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Stages count
                if (program.programStages.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${program.programStages.size} stages",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 👤 DHIS2 TRACKED ENTITY MANAGER
// ============================================================================

@Composable
fun DHIS2TrackedEntityManager(
    trackedEntities: List<DHIS2TrackedEntity>,
    onEntitySelect: (DHIS2TrackedEntity) -> Unit,
    onCreateEntity: () -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    showCreateButton: Boolean = true
) {
    var filteredEntities by remember(trackedEntities, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) {
                trackedEntities
            } else {
                trackedEntities.filter { entity ->
                    entity.attributes.values.any { value ->
                        value.contains(searchQuery, ignoreCase = true)
                    }
                }
            }
        )
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tracked Entities",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (showCreateButton) {
                Button(
                    onClick = onCreateEntity,
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Register")
                }
            }
        }
        
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Search entities...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )
        
        // Entities list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredEntities) { entity ->
                DHIS2TrackedEntityCard(
                    entity = entity,
                    onClick = { onEntitySelect(entity) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            if (filteredEntities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (searchQuery.isBlank()) "No entities registered" else "No entities found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DHIS2TrackedEntityCard(
    entity: DHIS2TrackedEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Entity identifier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = entity.id,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Badge {
                    Text(
                        text = "${entity.enrollments.size} enrollments",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            // Attributes
            if (entity.attributes.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entity.attributes.entries.take(3).toList()) { (key, value) ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = "$key: $value",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                    
                    if (entity.attributes.size > 3) {
                        item {
                            AssistChip(
                                onClick = { },
                                label = {
                                    Text(
                                        text = "+${entity.attributes.size - 3} more",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                        }
                    }
                }
            }
            
            // Dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Created: ${entity.createdDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Updated: ${entity.lastUpdatedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 📝 DHIS2 DATA ENTRY FORM
// ============================================================================

@Composable
fun DHIS2DataEntryForm(
    dataSet: DHIS2DataSet,
    dataValues: Map<String, String>,
    onDataValueChange: (String, String) -> Unit,
    onSave: () -> Unit,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    validationErrors: Map<String, String> = emptyMap()
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Form header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = dataSet.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                dataSet.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Badge {
                        Text(
                            text = dataSet.periodType.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    Badge {
                        Text(
                            text = "${dataSet.dataElements.size} elements",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
        
        // Form sections
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (dataSet.sections.isNotEmpty()) {
                items(dataSet.sections) { section ->
                    DHIS2DataSetSectionCard(
                        section = section,
                        dataValues = dataValues,
                        onDataValueChange = onDataValueChange,
                        isReadOnly = isReadOnly,
                        validationErrors = validationErrors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                item {
                    DHIS2DataElementsCard(
                        title = "Data Elements",
                        dataElements = dataSet.dataElements,
                        dataValues = dataValues,
                        onDataValueChange = onDataValueChange,
                        isReadOnly = isReadOnly,
                        validationErrors = validationErrors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        // Action buttons
        if (!isReadOnly) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onValidate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Validate")
                    }
                    
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun DHIS2DataSetSectionCard(
    section: DHIS2DataSetSection,
    dataValues: Map<String, String>,
    onDataValueChange: (String, String) -> Unit,
    isReadOnly: Boolean,
    validationErrors: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section header
            Text(
                text = section.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            section.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            // Data elements
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                section.dataElements.forEach { dataElement ->
                    DHIS2DataElementField(
                        dataElement = dataElement,
                        value = dataValues[dataElement.id] ?: "",
                        onValueChange = { value ->
                            onDataValueChange(dataElement.id, value)
                        },
                        isReadOnly = isReadOnly,
                        error = validationErrors[dataElement.id],
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DHIS2DataElementsCard(
    title: String,
    dataElements: List<DHIS2DataElement>,
    dataValues: Map<String, String>,
    onDataValueChange: (String, String) -> Unit,
    isReadOnly: Boolean,
    validationErrors: Map<String, String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                dataElements.forEach { dataElement ->
                    DHIS2DataElementField(
                        dataElement = dataElement,
                        value = dataValues[dataElement.id] ?: "",
                        onValueChange = { value ->
                            onDataValueChange(dataElement.id, value)
                        },
                        isReadOnly = isReadOnly,
                        error = validationErrors[dataElement.id],
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DHIS2DataElementField(
    dataElement: DHIS2DataElement,
    value: String,
    onValueChange: (String) -> Unit,
    isReadOnly: Boolean,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (dataElement.valueType) {
            ValueType.TEXT, ValueType.EMAIL, ValueType.PHONE_NUMBER, ValueType.URL -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(dataElement.name)
                            if (dataElement.mandatory) {
                                Text(
                                    text = " *",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isReadOnly,
                    isError = error != null,
                    keyboardOptions = when (dataElement.valueType) {
                        ValueType.EMAIL -> KeyboardOptions(keyboardType = KeyboardType.Email)
                        ValueType.PHONE_NUMBER -> KeyboardOptions(keyboardType = KeyboardType.Phone)
                        ValueType.URL -> KeyboardOptions(keyboardType = KeyboardType.Uri)
                        else -> KeyboardOptions.Default
                    }
                )
            }
            
            ValueType.LONG_TEXT -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(dataElement.name)
                            if (dataElement.mandatory) {
                                Text(
                                    text = " *",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    readOnly = isReadOnly,
                    isError = error != null,
                    maxLines = 5
                )
            }
            
            ValueType.NUMBER, ValueType.INTEGER, ValueType.INTEGER_POSITIVE, 
            ValueType.INTEGER_NEGATIVE, ValueType.INTEGER_ZERO_OR_POSITIVE -> {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(dataElement.name)
                            if (dataElement.mandatory) {
                                Text(
                                    text = " *",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isReadOnly,
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            ValueType.BOOLEAN, ValueType.TRUE_ONLY -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(dataElement.name)
                        if (dataElement.mandatory) {
                            Text(
                                text = " *",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    if (dataElement.valueType == ValueType.TRUE_ONLY) {
                        Checkbox(
                            checked = value.toBoolean(),
                            onCheckedChange = { onValueChange(it.toString()) },
                            enabled = !isReadOnly
                        )
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = value == "true",
                                onClick = { onValueChange("true") },
                                label = { Text("Yes") },
                                enabled = !isReadOnly
                            )
                            FilterChip(
                                selected = value == "false",
                                onClick = { onValueChange("false") },
                                label = { Text("No") },
                                enabled = !isReadOnly
                            )
                        }
                    }
                }
            }
            
            ValueType.DATE -> {
                // Date picker would be implemented here
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(dataElement.name)
                            if (dataElement.mandatory) {
                                Text(
                                    text = " *",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = isReadOnly,
                    isError = error != null,
                    placeholder = { Text("YYYY-MM-DD") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                )
            }
            
            else -> {
                // Handle option sets and other types
                if (dataElement.optionSet != null) {
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !isReadOnly && it }
                    ) {
                        OutlinedTextField(
                            value = dataElement.optionSet.options.find { it.code == value }?.name ?: value,
                            onValueChange = { },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(dataElement.name)
                                    if (dataElement.mandatory) {
                                        Text(
                                            text = " *",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            isError = error != null,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            dataElement.optionSet.options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.name) },
                                    onClick = {
                                        onValueChange(option.code ?: option.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(dataElement.name)
                                if (dataElement.mandatory) {
                                    Text(
                                        text = " *",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = isReadOnly,
                        isError = error != null
                    )
                }
            }
        }
        
        // Error message
        error?.let { errorMessage ->
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        
        // Description
        dataElement.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

fun generateSampleDHIS2Programs(): List<DHIS2Program> {
    return listOf(
        DHIS2Program(
            id = "program1",
            name = "Maternal Health Program",
            description = "Comprehensive maternal health tracking and care program",
            programType = ProgramType.WITH_REGISTRATION,
            trackedEntityType = "person",
            programStages = listOf(
                DHIS2ProgramStage(
                    id = "stage1",
                    name = "Registration",
                    description = "Initial registration and baseline data collection"
                ),
                DHIS2ProgramStage(
                    id = "stage2",
                    name = "Antenatal Care",
                    description = "Regular antenatal checkups and monitoring",
                    repeatable = true
                ),
                DHIS2ProgramStage(
                    id = "stage3",
                    name = "Delivery",
                    description = "Delivery and immediate postpartum care"
                )
            ),
            isActive = true
        ),
        DHIS2Program(
            id = "program2",
            name = "Immunization Program",
            description = "Child immunization tracking and scheduling",
            programType = ProgramType.WITH_REGISTRATION,
            trackedEntityType = "person",
            programStages = listOf(
                DHIS2ProgramStage(
                    id = "stage4",
                    name = "Birth Registration",
                    description = "Register newborn for immunization program"
                ),
                DHIS2ProgramStage(
                    id = "stage5",
                    name = "Vaccination",
                    description = "Administer vaccines according to schedule",
                    repeatable = true
                )
            ),
            isActive = true
        ),
        DHIS2Program(
            id = "program3",
            name = "Malaria Case Management",
            description = "Malaria case detection, treatment, and follow-up",
            programType = ProgramType.WITHOUT_REGISTRATION,
            isActive = true
        )
    )
}

fun generateSampleTrackedEntities(): List<DHIS2TrackedEntity> {
    return listOf(
        DHIS2TrackedEntity(
            id = "entity1",
            trackedEntityType = "person",
            organisationUnit = "ou1",
            attributes = mapOf(
                "firstName" to "Jane",
                "lastName" to "Doe",
                "dateOfBirth" to "1990-05-15",
                "phoneNumber" to "+1234567890"
            ),
            enrollments = listOf(
                DHIS2Enrollment(
                    id = "enrollment1",
                    program = "program1",
                    trackedEntity = "entity1",
                    organisationUnit = "ou1",
                    enrollmentDate = "2023-01-15",
                    status = EnrollmentStatus.ACTIVE
                )
            ),
            createdDate = "2023-01-15",
            lastUpdatedDate = "2023-12-01"
        ),
        DHIS2TrackedEntity(
            id = "entity2",
            trackedEntityType = "person",
            organisationUnit = "ou1",
            attributes = mapOf(
                "firstName" to "John",
                "lastName" to "Smith",
                "dateOfBirth" to "2023-03-20",
                "motherName" to "Mary Smith"
            ),
            enrollments = listOf(
                DHIS2Enrollment(
                    id = "enrollment2",
                    program = "program2",
                    trackedEntity = "entity2",
                    organisationUnit = "ou1",
                    enrollmentDate = "2023-03-20",
                    status = EnrollmentStatus.ACTIVE
                )
            ),
            createdDate = "2023-03-20",
            lastUpdatedDate = "2023-11-15"
        )
    )
}

fun generateSampleDataSet(): DHIS2DataSet {
    return DHIS2DataSet(
        id = "dataset1",
        name = "Monthly Health Facility Report",
        description = "Monthly reporting of key health indicators at facility level",
        periodType = PeriodType.MONTHLY,
        sections = listOf(
            DHIS2DataSetSection(
                id = "section1",
                name = "Maternal Health",
                description = "Maternal health indicators",
                dataElements = listOf(
                    DHIS2DataElement(
                        id = "de1",
                        name = "Antenatal care 1st visit",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    ),
                    DHIS2DataElement(
                        id = "de2",
                        name = "Antenatal care 4th visit",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    ),
                    DHIS2DataElement(
                        id = "de3",
                        name = "Skilled birth attendance",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    )
                )
            ),
            DHIS2DataSetSection(
                id = "section2",
                name = "Child Health",
                description = "Child health indicators",
                dataElements = listOf(
                    DHIS2DataElement(
                        id = "de4",
                        name = "BCG doses given",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    ),
                    DHIS2DataElement(
                        id = "de5",
                        name = "DPT 1st dose",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    ),
                    DHIS2DataElement(
                        id = "de6",
                        name = "Measles doses given",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    )
                )
            ),
            DHIS2DataSetSection(
                id = "section3",
                name = "Disease Surveillance",
                description = "Disease surveillance and outbreak reporting",
                dataElements = listOf(
                    DHIS2DataElement(
                        id = "de7",
                        name = "Malaria cases",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = true
                    ),
                    DHIS2DataElement(
                        id = "de8",
                        name = "Suspected cholera cases",
                        valueType = ValueType.INTEGER_ZERO_OR_POSITIVE,
                        mandatory = false
                    ),
                    DHIS2DataElement(
                        id = "de9",
                        name = "Outbreak reported",
                        valueType = ValueType.BOOLEAN,
                        mandatory = false
                    )
                )
            )
        )
    )
}