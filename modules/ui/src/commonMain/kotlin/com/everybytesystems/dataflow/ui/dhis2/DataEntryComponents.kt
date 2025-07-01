package com.everybytesystems.dataflow.ui.dhis2

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*

/**
 * DHIS2 Data Entry Components
 * Comprehensive data entry forms and validation
 */

// ============================================================================
// 📝 DATA ENTRY MODELS
// ============================================================================

data class DataValue(
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val categoryOptionCombo: String? = null,
    val attributeOptionCombo: String? = null,
    val value: String,
    val storedBy: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val comment: String? = null,
    val followUp: Boolean = false,
    val deleted: Boolean = false
)

data class DataEntryField(
    val id: String,
    val dataElement: DataElement,
    val categoryOptionCombo: String? = null,
    val value: String = "",
    val comment: String = "",
    val isRequired: Boolean = false,
    val isReadOnly: Boolean = false,
    val isDisabled: Boolean = false,
    val validationRules: List<ValidationRule> = emptyList(),
    val validationErrors: List<String> = emptyList(),
    val hasUnsavedChanges: Boolean = false
)

data class ValidationRule(
    val id: String,
    val name: String,
    val description: String? = null,
    val instruction: String? = null,
    val importance: ValidationImportance = ValidationImportance.MEDIUM,
    val operator: ValidationOperator,
    val leftSide: ValidationExpression,
    val rightSide: ValidationExpression
)

enum class ValidationImportance {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class ValidationOperator {
    EQUAL_TO,
    NOT_EQUAL_TO,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL_TO,
    LESS_THAN,
    LESS_THAN_OR_EQUAL_TO,
    COMPULSORY_PAIR,
    EXCLUSIVE_PAIR
}

data class ValidationExpression(
    val expression: String,
    val description: String? = null,
    val missingValueStrategy: String = "SKIP_IF_ANY_VALUE_MISSING"
)

enum class DataEntryStatus {
    DRAFT,
    COMPLETE,
    APPROVED,
    ACCEPTED,
    REJECTED
}

data class DataSet(
    val id: String,
    val name: String,
    val displayName: String = name,
    val shortName: String = name,
    val code: String? = null,
    val description: String? = null,
    val periodType: PeriodType,
    val categoryCombo: String? = null,
    val mobile: Boolean = false,
    val version: Int = 1,
    val expiryDays: Int = 0,
    val timelyDays: Int = 0,
    val notifyCompletingUser: Boolean = false,
    val openFuturePeriods: Int = 0,
    val fieldCombinationRequired: Boolean = false,
    val validCompleteOnly: Boolean = false,
    val noValueRequiresComment: Boolean = false,
    val skipOffline: Boolean = false,
    val dataElementDecoration: Boolean = false,
    val renderAsTabs: Boolean = false,
    val renderHorizontally: Boolean = false,
    val compulsoryFieldsCompleteOnly: Boolean = false,
    val dataElements: List<String> = emptyList(),
    val sections: List<DataSetSection> = emptyList(),
    val organisationUnits: List<String> = emptyList(),
    val indicators: List<String> = emptyList(),
    val validationRules: List<String> = emptyList()
)

data class DataSetSection(
    val id: String,
    val name: String,
    val displayName: String = name,
    val description: String? = null,
    val sortOrder: Int = 0,
    val dataElements: List<String> = emptyList(),
    val greyedFields: List<String> = emptyList(),
    val showRowTotals: Boolean = false,
    val showColumnTotals: Boolean = false
)

// ============================================================================
// 📝 DATA ENTRY FORM
// ============================================================================

@Composable
fun DataEntryForm(
    dataSet: DataSet,
    period: Period,
    orgUnit: OrganisationUnit,
    dataElements: List<DataElement>,
    dataValues: Map<String, DataValue>,
    onValueChange: (String, String, String?) -> Unit,
    onCommentChange: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    showValidation: Boolean = true,
    showComments: Boolean = true,
    showProgress: Boolean = true,
    onSave: (() -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onValidate: (() -> Unit)? = null,
    validationResults: Map<String, List<String>> = emptyMap(),
    isSaving: Boolean = false,
    isValidating: Boolean = false
) {
    var selectedSection by remember { mutableStateOf(0) }
    var showValidationDialog by remember { mutableStateOf(false) }
    
    val dataEntryFields = remember(dataSet, dataElements, dataValues, validationResults) {
        createDataEntryFields(dataSet, dataElements, dataValues, validationResults)
    }
    
    val completionPercentage = remember(dataEntryFields) {
        val totalRequired = dataEntryFields.count { it.isRequired }
        val completedRequired = dataEntryFields.count { it.isRequired && it.value.isNotEmpty() }
        if (totalRequired > 0) (completedRequired.toFloat() / totalRequired) * 100 else 100f
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Form header
        DataEntryFormHeader(
            dataSet = dataSet,
            period = period,
            orgUnit = orgUnit,
            completionPercentage = completionPercentage,
            showProgress = showProgress,
            onSave = onSave,
            onComplete = onComplete,
            onValidate = onValidate,
            isSaving = isSaving,
            isValidating = isValidating,
            isReadOnly = isReadOnly,
            hasValidationErrors = validationResults.isNotEmpty()
        )
        
        // Section tabs (if multiple sections)
        if (dataSet.sections.size > 1) {
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                modifier = Modifier.fillMaxWidth()
            ) {
                dataSet.sections.forEachIndexed { index, section ->
                    val sectionFields = dataEntryFields.filter { it.dataElement.id in section.dataElements }
                    val hasErrors = sectionFields.any { it.validationErrors.isNotEmpty() }
                    
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(section.displayName)
                                if (hasErrors) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = "Has errors",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
        
        // Form content
        if (dataSet.sections.isNotEmpty()) {
            val currentSection = dataSet.sections[selectedSection]
            val sectionFields = dataEntryFields.filter { it.dataElement.id in currentSection.dataElements }
            
            DataEntrySection(
                section = currentSection,
                fields = sectionFields,
                onValueChange = onValueChange,
                onCommentChange = onCommentChange,
                isReadOnly = isReadOnly,
                showComments = showComments,
                modifier = Modifier.weight(1f)
            )
        } else {
            // Single section form
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dataEntryFields) { field ->
                    DataEntryFieldComponent(
                        field = field,
                        onValueChange = { value, comment ->
                            onValueChange(field.dataElement.id, value, field.categoryOptionCombo)
                            if (comment != null) {
                                onCommentChange(field.dataElement.id, comment)
                            }
                        },
                        isReadOnly = isReadOnly,
                        showComment = showComments
                    )
                }
            }
        }
        
        // Validation dialog
        if (showValidationDialog && validationResults.isNotEmpty()) {
            ValidationResultsDialog(
                validationResults = validationResults,
                onDismiss = { showValidationDialog = false }
            )
        }
    }
}

@Composable
private fun DataEntryFormHeader(
    dataSet: DataSet,
    period: Period,
    orgUnit: OrganisationUnit,
    completionPercentage: Float,
    showProgress: Boolean,
    onSave: (() -> Unit)?,
    onComplete: (() -> Unit)?,
    onValidate: (() -> Unit)?,
    isSaving: Boolean,
    isValidating: Boolean,
    isReadOnly: Boolean,
    hasValidationErrors: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title and info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dataSet.displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${period.displayName} • ${orgUnit.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (dataSet.description != null) {
                        Text(
                            text = dataSet.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Status indicators
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (hasValidationErrors) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Has Errors") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                labelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }
                    
                    if (isReadOnly) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Read Only") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
            
            // Progress bar
            if (showProgress) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completion",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "${completionPercentage.toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = completionPercentage / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = when {
                            completionPercentage >= 100f -> Color(0xFF4CAF50)
                            completionPercentage >= 75f -> Color(0xFF2196F3)
                            completionPercentage >= 50f -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        }
                    )
                }
            }
            
            // Action buttons
            if (!isReadOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onValidate != null) {
                        OutlinedButton(
                            onClick = onValidate,
                            enabled = !isValidating,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isValidating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Validate")
                        }
                    }
                    
                    if (onSave != null) {
                        Button(
                            onClick = onSave,
                            enabled = !isSaving,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save")
                        }
                    }
                    
                    if (onComplete != null) {
                        Button(
                            onClick = onComplete,
                            enabled = completionPercentage >= 100f && !hasValidationErrors,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Complete")
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📝 DATA ENTRY SECTION
// ============================================================================

@Composable
private fun DataEntrySection(
    section: DataSetSection,
    fields: List<DataEntryField>,
    onValueChange: (String, String, String?) -> Unit,
    onCommentChange: (String, String) -> Unit,
    isReadOnly: Boolean,
    showComments: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        if (section.description != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = section.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
        
        // Data entry fields
        items(fields) { field ->
            DataEntryFieldComponent(
                field = field,
                onValueChange = { value, comment ->
                    onValueChange(field.dataElement.id, value, field.categoryOptionCombo)
                    if (comment != null) {
                        onCommentChange(field.dataElement.id, comment)
                    }
                },
                isReadOnly = isReadOnly,
                showComment = showComments
            )
        }
    }
}

// ============================================================================
// 📝 DATA ENTRY FIELD COMPONENT
// ============================================================================

@Composable
fun DataEntryFieldComponent(
    field: DataEntryField,
    onValueChange: (String, String?) -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    showComment: Boolean = true
) {
    var value by remember(field.value) { mutableStateOf(field.value) }
    var comment by remember(field.comment) { mutableStateOf(field.comment) }
    var showCommentField by remember { mutableStateOf(comment.isNotEmpty()) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                field.validationErrors.isNotEmpty() -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                field.hasUnsavedChanges -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                field.isDisabled -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = if (field.validationErrors.isNotEmpty()) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Field label and indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = field.dataElement.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        if (field.isRequired) {
                            Text(
                                text = "*",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    if (field.dataElement.description != null) {
                        Text(
                            text = field.dataElement.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Field indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (field.hasUnsavedChanges) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Unsaved changes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    if (field.validationErrors.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Validation error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    if (showComment && comment.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Comment,
                            contentDescription = "Has comment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Input field based on value type
            when (field.dataElement.valueType) {
                ValueType.TEXT, ValueType.LETTER -> {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { 
                            value = it
                            onValueChange(it, if (showCommentField) comment else null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled,
                        isError = field.validationErrors.isNotEmpty(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                }
                
                ValueType.LONG_TEXT -> {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { 
                            value = it
                            onValueChange(it, if (showCommentField) comment else null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled,
                        isError = field.validationErrors.isNotEmpty(),
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        )
                    )
                }
                
                ValueType.NUMBER, ValueType.INTEGER, ValueType.INTEGER_POSITIVE,
                ValueType.INTEGER_NEGATIVE, ValueType.INTEGER_ZERO_OR_POSITIVE,
                ValueType.PERCENTAGE, ValueType.UNIT_INTERVAL -> {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { 
                            value = it
                            onValueChange(it, if (showCommentField) comment else null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled,
                        isError = field.validationErrors.isNotEmpty(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )
                }
                
                ValueType.BOOLEAN -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RadioButton(
                                selected = value == "true",
                                onClick = { 
                                    value = "true"
                                    onValueChange("true", if (showCommentField) comment else null)
                                },
                                enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled
                            )
                            Text("Yes")
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RadioButton(
                                selected = value == "false",
                                onClick = { 
                                    value = "false"
                                    onValueChange("false", if (showCommentField) comment else null)
                                },
                                enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled
                            )
                            Text("No")
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            RadioButton(
                                selected = value.isEmpty(),
                                onClick = { 
                                    value = ""
                                    onValueChange("", if (showCommentField) comment else null)
                                },
                                enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled
                            )
                            Text("Not specified")
                        }
                    }
                }
                
                ValueType.TRUE_ONLY -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = value == "true",
                            onCheckedChange = { checked ->
                                value = if (checked) "true" else ""
                                onValueChange(value, if (showCommentField) comment else null)
                            },
                            enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled
                        )
                        Text("Yes")
                    }
                }
                
                ValueType.DATE -> {
                    var showDatePicker by remember { mutableStateOf(false) }
                    
                    OutlinedTextField(
                        value = value,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                if (!isReadOnly && !field.isReadOnly && !field.isDisabled) {
                                    showDatePicker = true
                                }
                            },
                        enabled = false,
                        isError = field.validationErrors.isNotEmpty(),
                        placeholder = { Text("Select date") },
                        trailingIcon = {
                            IconButton(
                                onClick = { 
                                    if (!isReadOnly && !field.isReadOnly && !field.isDisabled) {
                                        showDatePicker = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    
                    if (showDatePicker) {
                        // Date picker would be implemented here
                        // For now, just close the picker
                        LaunchedEffect(Unit) {
                            showDatePicker = false
                        }
                    }
                }
                
                else -> {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { 
                            value = it
                            onValueChange(it, if (showCommentField) comment else null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled,
                        isError = field.validationErrors.isNotEmpty(),
                        singleLine = true
                    )
                }
            }
            
            // Validation errors
            if (field.validationErrors.isNotEmpty()) {
                field.validationErrors.forEach { error ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Comment field
            if (showComment) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { showCommentField = !showCommentField }
                    ) {
                        Icon(
                            imageVector = if (showCommentField) Icons.Default.ExpandLess else Icons.Default.Comment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (showCommentField) "Hide Comment" else "Add Comment")
                    }
                }
                
                AnimatedVisibility(
                    visible = showCommentField,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { 
                            comment = it
                            onValueChange(value, it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isReadOnly && !field.isReadOnly && !field.isDisabled,
                        placeholder = { Text("Add a comment...") },
                        minLines = 2,
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        )
                    )
                }
            }
        }
    }
}

// ============================================================================
// ✅ VALIDATION RESULTS DIALOG
// ============================================================================

@Composable
fun ValidationResultsDialog(
    validationResults: Map<String, List<String>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Validation Results",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                validationResults.forEach { (fieldId, errors) ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = fieldId, // Would be field name in real implementation
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                errors.forEach { error ->
                                    Text(
                                        text = "• $error",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun createDataEntryFields(
    dataSet: DataSet,
    dataElements: List<DataElement>,
    dataValues: Map<String, DataValue>,
    validationResults: Map<String, List<String>>
): List<DataEntryField> {
    return dataElements.filter { it.id in dataSet.dataElements }.map { element ->
        val dataValue = dataValues[element.id]
        DataEntryField(
            id = element.id,
            dataElement = element,
            value = dataValue?.value ?: "",
            comment = dataValue?.comment ?: "",
            isRequired = true, // Would be determined by business rules
            validationErrors = validationResults[element.id] ?: emptyList(),
            hasUnsavedChanges = false // Would be tracked by state management
        )
    }
}