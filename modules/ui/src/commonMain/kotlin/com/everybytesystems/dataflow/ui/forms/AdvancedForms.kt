package com.everybytesystems.dataflow.ui.forms

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Advanced Forms Engine
 * Dynamic, schema-driven forms with comprehensive field types and validation
 */

// ============================================================================
// 📋 FORM SCHEMA & MODELS
// ============================================================================

@Serializable
data class FormSchema(
    val id: String,
    val title: String,
    val description: String = "",
    val version: String = "1.0",
    val sections: List<FormSection> = emptyList(),
    val settings: FormSettings = FormSettings()
)

@Serializable
data class FormSection(
    val id: String,
    val title: String,
    val description: String = "",
    val collapsible: Boolean = false,
    val collapsed: Boolean = false,
    val conditional: ConditionalLogic? = null,
    val fields: List<FormFieldSchema> = emptyList()
)

@Serializable
data class FormFieldSchema(
    val id: String,
    val type: AdvancedFieldType,
    val label: String,
    val placeholder: String = "",
    val helpText: String = "",
    val required: Boolean = false,
    val readonly: Boolean = false,
    val validation: FieldValidationRules = FieldValidationRules(),
    val conditional: ConditionalLogic? = null,
    val options: List<FieldOption> = emptyList(),
    val defaultValue: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class FormSettings(
    val allowDraft: Boolean = true,
    val autoSave: Boolean = true,
    val autoSaveInterval: Int = 30, // seconds
    val showProgress: Boolean = true,
    val submitButtonText: String = "Submit",
    val draftButtonText: String = "Save Draft",
    val resetButtonText: String = "Reset"
)

@Serializable
data class ConditionalLogic(
    val fieldId: String,
    val operator: ConditionalOperator,
    val value: String,
    val action: ConditionalAction
)

@Serializable
enum class ConditionalOperator {
    EQUALS, NOT_EQUALS, CONTAINS, NOT_CONTAINS, 
    GREATER_THAN, LESS_THAN, IS_EMPTY, IS_NOT_EMPTY
}

@Serializable
enum class ConditionalAction {
    SHOW, HIDE, ENABLE, DISABLE, REQUIRE, OPTIONAL
}

@Serializable
enum class AdvancedFieldType {
    // Text inputs
    TEXT, TEXTAREA, PASSWORD, EMAIL, PHONE, NUMBER, DECIMAL, URL,
    
    // Selection
    DROPDOWN, MULTISELECT, RADIO, CHECKBOX, SWITCH, TOGGLE,
    
    // Date/Time
    DATE, TIME, DATETIME, DATE_RANGE, TIME_RANGE,
    
    // Interactive
    SLIDER, STEPPER, RATING, COLOR_PICKER,
    
    // Media
    FILE_UPLOAD, IMAGE_UPLOAD, SIGNATURE, VOICE_RECORDING, CAMERA,
    
    // Advanced
    QR_SCANNER, BARCODE_SCANNER, OCR_FIELD, LOCATION_PICKER, MAP_SELECTOR,
    
    // Rich content
    WYSIWYG_EDITOR, AUTOCOMPLETE, TAGS, CHIPS,
    
    // Layout
    SECTION_HEADER, DIVIDER, SPACER, HTML_CONTENT,
    
    // Calculated
    CALCULATED_FIELD, FORMULA_FIELD
}

@Serializable
data class FieldValidationRules(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val pattern: String? = null,
    val customMessage: String? = null,
    val fileTypes: List<String> = emptyList(),
    val maxFileSize: Long? = null // bytes
)

@Serializable
data class FieldOption(
    val value: String,
    val label: String,
    val description: String = "",
    val icon: String? = null,
    val enabled: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

// ============================================================================
// 📝 DYNAMIC FORM RENDERER
// ============================================================================

/**
 * Dynamic Form Renderer - renders forms from schema
 */
@Composable
fun DynamicFormRenderer(
    schema: FormSchema,
    initialValues: Map<String, Any> = emptyMap(),
    onSubmit: (Map<String, Any>) -> Unit,
    onDraftSave: ((Map<String, Any>) -> Unit)? = null,
    onFieldChange: ((String, Any) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var formData by remember { mutableStateOf(initialValues) }
    var errors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var touched by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isSubmitting by remember { mutableStateOf(false) }
    var currentSection by remember { mutableStateOf(0) }
    
    // Auto-save functionality
    LaunchedEffect(formData) {
        if (schema.settings.autoSave && onDraftSave != null) {
            delay(schema.settings.autoSaveInterval * 1000L)
            onDraftSave(formData)
        }
    }
    
    fun updateField(fieldId: String, value: Any) {
        formData = formData + (fieldId to value)
        touched = touched + fieldId
        onFieldChange?.invoke(fieldId, value)
        
        // Validate field
        val field = findFieldInSchema(schema, fieldId)
        field?.let {
            val error = validateField(it, value)
            errors = if (error != null) {
                errors + (fieldId to error)
            } else {
                errors - fieldId
            }
        }
    }
    
    fun submitForm() {
        isSubmitting = true
        
        // Validate all fields
        val allErrors = mutableMapOf<String, String>()
        schema.sections.forEach { section ->
            section.fields.forEach { field ->
                val value = formData[field.id]
                val error = validateField(field, value)
                if (error != null) {
                    allErrors[field.id] = error
                }
            }
        }
        
        errors = allErrors
        touched = schema.sections.flatMap { it.fields.map { field -> field.id } }.toSet()
        
        if (allErrors.isEmpty()) {
            onSubmit(formData)
        }
        
        isSubmitting = false
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form header
        FormHeader(
            title = schema.title,
            description = schema.description,
            progress = if (schema.settings.showProgress) {
                currentSection.toFloat() / schema.sections.size.coerceAtLeast(1)
            } else null
        )
        
        // Form sections
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            itemsIndexed(schema.sections) { index, section ->
                if (shouldShowSection(section, formData)) {
                    FormSectionRenderer(
                        section = section,
                        formData = formData,
                        errors = errors,
                        touched = touched,
                        onFieldChange = ::updateField
                    )
                }
            }
        }
        
        // Form actions
        FormActions(
            settings = schema.settings,
            isSubmitting = isSubmitting,
            hasErrors = errors.isNotEmpty(),
            onSubmit = ::submitForm,
            onDraftSave = if (onDraftSave != null) {
                { onDraftSave(formData) }
            } else null,
            onReset = {
                formData = emptyMap()
                errors = emptyMap()
                touched = emptySet()
            }
        )
    }
}

@Composable
private fun FormHeader(
    title: String,
    description: String,
    progress: Float?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (description.isNotEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        progress?.let {
            LinearProgressIndicator(
                progress = { it },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun FormSectionRenderer(
    section: FormSection,
    formData: Map<String, Any>,
    errors: Map<String, String>,
    touched: Set<String>,
    onFieldChange: (String, Any) -> Unit
) {
    var isCollapsed by remember { mutableStateOf(section.collapsed) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section header
            if (section.title.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (section.collapsible) {
                                Modifier.clickable { isCollapsed = !isCollapsed }
                            } else Modifier
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = section.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        if (section.description.isNotEmpty()) {
                            Text(
                                text = section.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    if (section.collapsible) {
                        Icon(
                            imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = if (isCollapsed) "Expand" else "Collapse"
                        )
                    }
                }
            }
            
            // Section fields
            AnimatedVisibility(
                visible = !isCollapsed,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    section.fields.forEach { field ->
                        if (shouldShowField(field, formData)) {
                            AdvancedFieldRenderer(
                                field = field,
                                value = formData[field.id],
                                error = if (field.id in touched) errors[field.id] else null,
                                onValueChange = { value ->
                                    onFieldChange(field.id, value)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormActions(
    settings: FormSettings,
    isSubmitting: Boolean,
    hasErrors: Boolean,
    onSubmit: () -> Unit,
    onDraftSave: (() -> Unit)?,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Reset button
        OutlinedButton(
            onClick = onReset,
            enabled = !isSubmitting,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(settings.resetButtonText)
        }
        
        // Draft save button
        if (settings.allowDraft && onDraftSave != null) {
            OutlinedButton(
                onClick = onDraftSave,
                enabled = !isSubmitting,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(settings.draftButtonText)
            }
        }
        
        // Submit button
        Button(
            onClick = onSubmit,
            enabled = !isSubmitting && !hasErrors,
            modifier = Modifier.weight(1f)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(settings.submitButtonText)
        }
    }
}

// ============================================================================
// 🎯 ADVANCED FIELD RENDERER
// ============================================================================

@Composable
private fun AdvancedFieldRenderer(
    field: FormFieldSchema,
    value: Any?,
    error: String?,
    onValueChange: (Any) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Field label
        if (field.label.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = field.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                if (field.required) {
                    Text(
                        text = "*",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
        
        // Field input
        when (field.type) {
            AdvancedFieldType.TEXT -> {
                AdvancedTextField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.TEXTAREA -> {
                AdvancedTextArea(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.PASSWORD -> {
                AdvancedPasswordField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.EMAIL -> {
                AdvancedTextField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = Icons.Default.Email
                )
            }
            
            AdvancedFieldType.PHONE -> {
                AdvancedTextField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null,
                    keyboardType = KeyboardType.Phone,
                    leadingIcon = Icons.Default.Phone
                )
            }
            
            AdvancedFieldType.NUMBER -> {
                AdvancedNumberField(
                    value = value as? Double ?: 0.0,
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.DROPDOWN -> {
                AdvancedDropdown(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.MULTISELECT -> {
                AdvancedMultiSelect(
                    values = value as? List<String> ?: emptyList(),
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.RADIO -> {
                AdvancedRadioGroup(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field
                )
            }
            
            AdvancedFieldType.CHECKBOX -> {
                AdvancedCheckboxGroup(
                    values = value as? List<String> ?: emptyList(),
                    onValueChange = onValueChange,
                    field = field
                )
            }
            
            AdvancedFieldType.SWITCH -> {
                AdvancedSwitch(
                    checked = value as? Boolean ?: false,
                    onValueChange = onValueChange,
                    field = field
                )
            }
            
            AdvancedFieldType.DATE -> {
                AdvancedDatePicker(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.TIME -> {
                AdvancedTimePicker(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.SLIDER -> {
                AdvancedSlider(
                    value = value as? Float ?: 0f,
                    onValueChange = onValueChange,
                    field = field
                )
            }
            
            AdvancedFieldType.RATING -> {
                AdvancedRating(
                    value = value as? Int ?: 0,
                    onValueChange = onValueChange,
                    field = field
                )
            }
            
            AdvancedFieldType.COLOR_PICKER -> {
                AdvancedColorPicker(
                    value = value as? String ?: "#000000",
                    onValueChange = onValueChange,
                    field = field
                )
            }
            
            AdvancedFieldType.FILE_UPLOAD -> {
                AdvancedFileUpload(
                    value = value as? List<String> ?: emptyList(),
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.SIGNATURE -> {
                AdvancedSignaturePad(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.AUTOCOMPLETE -> {
                AdvancedAutocomplete(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.TAGS -> {
                AdvancedTagsInput(
                    values = value as? List<String> ?: emptyList(),
                    onValueChange = onValueChange,
                    field = field,
                    isError = error != null
                )
            }
            
            AdvancedFieldType.SECTION_HEADER -> {
                AdvancedSectionHeader(field = field)
            }
            
            AdvancedFieldType.DIVIDER -> {
                HorizontalDivider()
            }
            
            AdvancedFieldType.SPACER -> {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            else -> {
                // Placeholder for not yet implemented field types
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Construction,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Field type '${field.type}' coming soon",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Help text
        if (field.helpText.isNotEmpty()) {
            Text(
                text = field.helpText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Error message
        error?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun findFieldInSchema(schema: FormSchema, fieldId: String): FormFieldSchema? {
    schema.sections.forEach { section ->
        section.fields.forEach { field ->
            if (field.id == fieldId) return field
        }
    }
    return null
}

private fun shouldShowSection(section: FormSection, formData: Map<String, Any>): Boolean {
    return section.conditional?.let { conditional ->
        evaluateConditional(conditional, formData)
    } ?: true
}

private fun shouldShowField(field: FormFieldSchema, formData: Map<String, Any>): Boolean {
    return field.conditional?.let { conditional ->
        evaluateConditional(conditional, formData)
    } ?: true
}

private fun evaluateConditional(conditional: ConditionalLogic, formData: Map<String, Any>): Boolean {
    val fieldValue = formData[conditional.fieldId]?.toString() ?: ""
    
    val conditionMet = when (conditional.operator) {
        ConditionalOperator.EQUALS -> fieldValue == conditional.value
        ConditionalOperator.NOT_EQUALS -> fieldValue != conditional.value
        ConditionalOperator.CONTAINS -> fieldValue.contains(conditional.value, ignoreCase = true)
        ConditionalOperator.NOT_CONTAINS -> !fieldValue.contains(conditional.value, ignoreCase = true)
        ConditionalOperator.GREATER_THAN -> {
            val numValue = fieldValue.toDoubleOrNull()
            val condValue = conditional.value.toDoubleOrNull()
            numValue != null && condValue != null && numValue > condValue
        }
        ConditionalOperator.LESS_THAN -> {
            val numValue = fieldValue.toDoubleOrNull()
            val condValue = conditional.value.toDoubleOrNull()
            numValue != null && condValue != null && numValue < condValue
        }
        ConditionalOperator.IS_EMPTY -> fieldValue.isEmpty()
        ConditionalOperator.IS_NOT_EMPTY -> fieldValue.isNotEmpty()
    }
    
    return when (conditional.action) {
        ConditionalAction.SHOW -> conditionMet
        ConditionalAction.HIDE -> !conditionMet
        ConditionalAction.ENABLE -> conditionMet
        ConditionalAction.DISABLE -> !conditionMet
        ConditionalAction.REQUIRE -> conditionMet
        ConditionalAction.OPTIONAL -> !conditionMet
    }
}

private fun validateField(field: FormFieldSchema, value: Any?): String? {
    val stringValue = value?.toString() ?: ""
    
    // Required validation
    if (field.required && stringValue.isBlank()) {
        return "${field.label} is required"
    }
    
    // Skip other validations if field is empty and not required
    if (stringValue.isBlank()) return null
    
    val validation = field.validation
    
    // Length validation
    validation.minLength?.let { minLength ->
        if (stringValue.length < minLength) {
            return "${field.label} must be at least $minLength characters"
        }
    }
    
    validation.maxLength?.let { maxLength ->
        if (stringValue.length > maxLength) {
            return "${field.label} must be no more than $maxLength characters"
        }
    }
    
    // Numeric validation
    if (field.type == AdvancedFieldType.NUMBER || field.type == AdvancedFieldType.DECIMAL) {
        val numValue = stringValue.toDoubleOrNull()
        if (numValue == null) {
            return "${field.label} must be a valid number"
        }
        
        validation.minValue?.let { minValue ->
            if (numValue < minValue) {
                return "${field.label} must be at least $minValue"
            }
        }
        
        validation.maxValue?.let { maxValue ->
            if (numValue > maxValue) {
                return "${field.label} must be no more than $maxValue"
            }
        }
    }
    
    // Pattern validation
    validation.pattern?.let { pattern ->
        if (!Regex(pattern).matches(stringValue)) {
            return validation.customMessage ?: "${field.label} format is invalid"
        }
    }
    
    // Type-specific validation
    when (field.type) {
        AdvancedFieldType.EMAIL -> {
            val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            if (!emailPattern.matches(stringValue)) {
                return "Please enter a valid email address"
            }
        }
        AdvancedFieldType.URL -> {
            val urlPattern = Regex("^https?://[A-Za-z0-9.-]+\\.[A-Za-z]{2,}.*$")
            if (!urlPattern.matches(stringValue)) {
                return "Please enter a valid URL"
            }
        }
        AdvancedFieldType.PHONE -> {
            val phonePattern = Regex("^[+]?[0-9\\s\\-()]{10,}$")
            if (!phonePattern.matches(stringValue)) {
                return "Please enter a valid phone number"
            }
        }
        else -> { /* No additional validation */ }
    }
    
    return null
}

/**
 * Form Schema Builder - DSL for creating forms programmatically
 */
class FormSchemaBuilder {
    private var id: String = ""
    private var title: String = ""
    private var description: String = ""
    private var version: String = "1.0"
    private val sections = mutableListOf<FormSection>()
    private var settings = FormSettings()
    
    fun id(id: String) { this.id = id }
    fun title(title: String) { this.title = title }
    fun description(description: String) { this.description = description }
    fun version(version: String) { this.version = version }
    fun settings(block: FormSettingsBuilder.() -> Unit) {
        this.settings = FormSettingsBuilder().apply(block).build()
    }
    
    fun section(block: FormSectionBuilder.() -> Unit) {
        sections.add(FormSectionBuilder().apply(block).build())
    }
    
    fun build(): FormSchema {
        return FormSchema(
            id = id,
            title = title,
            description = description,
            version = version,
            sections = sections,
            settings = settings
        )
    }
}

class FormSectionBuilder {
    private var id: String = ""
    private var title: String = ""
    private var description: String = ""
    private var collapsible: Boolean = false
    private var collapsed: Boolean = false
    private var conditional: ConditionalLogic? = null
    private val fields = mutableListOf<FormFieldSchema>()
    
    fun id(id: String) { this.id = id }
    fun title(title: String) { this.title = title }
    fun description(description: String) { this.description = description }
    fun collapsible(collapsible: Boolean) { this.collapsible = collapsible }
    fun collapsed(collapsed: Boolean) { this.collapsed = collapsed }
    fun conditional(conditional: ConditionalLogic) { this.conditional = conditional }
    
    fun field(block: FormFieldBuilder.() -> Unit) {
        fields.add(FormFieldBuilder().apply(block).build())
    }
    
    fun build(): FormSection {
        return FormSection(
            id = id,
            title = title,
            description = description,
            collapsible = collapsible,
            collapsed = collapsed,
            conditional = conditional,
            fields = fields
        )
    }
}

class FormFieldBuilder {
    private var id: String = ""
    private var type: AdvancedFieldType = AdvancedFieldType.TEXT
    private var label: String = ""
    private var placeholder: String = ""
    private var helpText: String = ""
    private var required: Boolean = false
    private var readonly: Boolean = false
    private var validation = FieldValidationRules()
    private var conditional: ConditionalLogic? = null
    private val options = mutableListOf<FieldOption>()
    private var defaultValue: String? = null
    private val metadata = mutableMapOf<String, String>()
    
    fun id(id: String) { this.id = id }
    fun type(type: AdvancedFieldType) { this.type = type }
    fun label(label: String) { this.label = label }
    fun placeholder(placeholder: String) { this.placeholder = placeholder }
    fun helpText(helpText: String) { this.helpText = helpText }
    fun required(required: Boolean) { this.required = required }
    fun readonly(readonly: Boolean) { this.readonly = readonly }
    fun validation(block: FieldValidationBuilder.() -> Unit) {
        this.validation = FieldValidationBuilder().apply(block).build()
    }
    fun conditional(conditional: ConditionalLogic) { this.conditional = conditional }
    fun defaultValue(defaultValue: String) { this.defaultValue = defaultValue }
    
    fun option(value: String, label: String, description: String = "", enabled: Boolean = true) {
        options.add(FieldOption(value, label, description, enabled = enabled))
    }
    
    fun metadata(key: String, value: String) {
        metadata[key] = value
    }
    
    fun build(): FormFieldSchema {
        return FormFieldSchema(
            id = id,
            type = type,
            label = label,
            placeholder = placeholder,
            helpText = helpText,
            required = required,
            readonly = readonly,
            validation = validation,
            conditional = conditional,
            options = options,
            defaultValue = defaultValue,
            metadata = metadata
        )
    }
}

class FormSettingsBuilder {
    private var allowDraft: Boolean = true
    private var autoSave: Boolean = true
    private var autoSaveInterval: Int = 30
    private var showProgress: Boolean = true
    private var submitButtonText: String = "Submit"
    private var draftButtonText: String = "Save Draft"
    private var resetButtonText: String = "Reset"
    
    fun allowDraft(allowDraft: Boolean) { this.allowDraft = allowDraft }
    fun autoSave(autoSave: Boolean) { this.autoSave = autoSave }
    fun autoSaveInterval(interval: Int) { this.autoSaveInterval = interval }
    fun showProgress(showProgress: Boolean) { this.showProgress = showProgress }
    fun submitButtonText(text: String) { this.submitButtonText = text }
    fun draftButtonText(text: String) { this.draftButtonText = text }
    fun resetButtonText(text: String) { this.resetButtonText = text }
    
    fun build(): FormSettings {
        return FormSettings(
            allowDraft = allowDraft,
            autoSave = autoSave,
            autoSaveInterval = autoSaveInterval,
            showProgress = showProgress,
            submitButtonText = submitButtonText,
            draftButtonText = draftButtonText,
            resetButtonText = resetButtonText
        )
    }
}

class FieldValidationBuilder {
    private var minLength: Int? = null
    private var maxLength: Int? = null
    private var minValue: Double? = null
    private var maxValue: Double? = null
    private var pattern: String? = null
    private var customMessage: String? = null
    private val fileTypes = mutableListOf<String>()
    private var maxFileSize: Long? = null
    
    fun minLength(minLength: Int) { this.minLength = minLength }
    fun maxLength(maxLength: Int) { this.maxLength = maxLength }
    fun minValue(minValue: Double) { this.minValue = minValue }
    fun maxValue(maxValue: Double) { this.maxValue = maxValue }
    fun pattern(pattern: String) { this.pattern = pattern }
    fun customMessage(message: String) { this.customMessage = message }
    fun fileType(type: String) { fileTypes.add(type) }
    fun maxFileSize(size: Long) { this.maxFileSize = size }
    
    fun build(): FieldValidationRules {
        return FieldValidationRules(
            minLength = minLength,
            maxLength = maxLength,
            minValue = minValue,
            maxValue = maxValue,
            pattern = pattern,
            customMessage = customMessage,
            fileTypes = fileTypes,
            maxFileSize = maxFileSize
        )
    }
}

/**
 * DSL function for creating forms
 */
fun formSchema(block: FormSchemaBuilder.() -> Unit): FormSchema {
    return FormSchemaBuilder().apply(block).build()
}