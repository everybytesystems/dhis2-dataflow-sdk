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
import com.everybytesystems.dataflow.ui.components.*
import com.everybytesystems.dataflow.ui.theme.DataFlowColors

/**
 * DataFlow Form Components
 * Modern, accessible form components for data input and validation
 */

// ============================================================================
// 📝 FORM MODELS
// ============================================================================

/**
 * Form field definition
 */
data class FormField(
    val key: String,
    val label: String,
    val type: FieldType,
    val required: Boolean = false,
    val placeholder: String = "",
    val helpText: String = "",
    val validation: FieldValidation? = null,
    val options: List<FieldOption> = emptyList(),
    val defaultValue: Any? = null,
    val enabled: Boolean = true,
    val visible: Boolean = true
)

enum class FieldType {
    TEXT,
    EMAIL,
    PASSWORD,
    NUMBER,
    PHONE,
    URL,
    TEXTAREA,
    SELECT,
    MULTISELECT,
    CHECKBOX,
    RADIO,
    DATE,
    TIME,
    DATETIME,
    FILE,
    SLIDER,
    SWITCH,
    COLOR
}

/**
 * Field validation rules
 */
data class FieldValidation(
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: Regex? = null,
    val customValidator: ((String) -> String?)? = null
)

/**
 * Field option for select/radio fields
 */
data class FieldOption(
    val value: String,
    val label: String,
    val enabled: Boolean = true
)

/**
 * Form state
 */
data class FormState(
    val values: Map<String, Any> = emptyMap(),
    val errors: Map<String, String> = emptyMap(),
    val touched: Set<String> = emptySet(),
    val isSubmitting: Boolean = false,
    val isValid: Boolean = true
)

// ============================================================================
// 📋 FORM CONTAINER
// ============================================================================

/**
 * Modern form container with validation and submission
 */
@Composable
fun DataFlowForm(
    fields: List<FormField>,
    initialValues: Map<String, Any> = emptyMap(),
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    submitButtonText: String = "Submit",
    onSubmit: (Map<String, Any>) -> Unit,
    onFieldChange: ((String, Any) -> Unit)? = null
) {
    var formState by remember {
        mutableStateOf(
            FormState(values = initialValues)
        )
    }
    
    fun updateField(key: String, value: Any) {
        val newValues = formState.values.toMutableMap()
        newValues[key] = value
        
        val newTouched = formState.touched + key
        val errors = validateForm(fields, newValues)
        
        formState = formState.copy(
            values = newValues,
            touched = newTouched,
            errors = errors,
            isValid = errors.isEmpty()
        )
        
        onFieldChange?.invoke(key, value)
    }
    
    fun submitForm() {
        val allTouched = fields.map { it.key }.toSet()
        val errors = validateForm(fields, formState.values)
        
        formState = formState.copy(
            touched = allTouched,
            errors = errors,
            isValid = errors.isEmpty(),
            isSubmitting = true
        )
        
        if (errors.isEmpty()) {
            onSubmit(formState.values)
        }
        
        formState = formState.copy(isSubmitting = false)
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Form header
            if (title != null || subtitle != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    title?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Divider()
            }
            
            // Form fields
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(fields.filter { it.visible }) { field ->
                    FormFieldRenderer(
                        field = field,
                        value = formState.values[field.key],
                        error = if (field.key in formState.touched) {
                            formState.errors[field.key]
                        } else null,
                        onValueChange = { value ->
                            updateField(field.key, value)
                        }
                    )
                }
            }
            
            // Submit button
            Button(
                onClick = { submitForm() },
                enabled = !formState.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(submitButtonText)
            }
        }
    }
}

// ============================================================================
// 🎯 FIELD RENDERER
// ============================================================================

@Composable
private fun FormFieldRenderer(
    field: FormField,
    value: Any?,
    error: String?,
    onValueChange: (Any) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Field label
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
        
        // Field input
        when (field.type) {
            FieldType.TEXT -> {
                DataFlowTextField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    placeholder = field.placeholder,
                    enabled = field.enabled,
                    isError = error != null
                )
            }
            FieldType.EMAIL -> {
                DataFlowTextField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    placeholder = field.placeholder,
                    enabled = field.enabled,
                    isError = error != null,
                    keyboardType = KeyboardType.Email,
                    leadingIcon = Icons.Default.Email
                )
            }
            FieldType.PASSWORD -> {
                DataFlowPasswordField(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    placeholder = field.placeholder,
                    enabled = field.enabled,
                    isError = error != null
                )
            }
            FieldType.NUMBER -> {
                DataFlowTextField(
                    value = value?.toString() ?: "",
                    onValueChange = { onValueChange(it.toDoubleOrNull() ?: 0.0) },
                    placeholder = field.placeholder,
                    enabled = field.enabled,
                    isError = error != null,
                    keyboardType = KeyboardType.Number
                )
            }
            FieldType.TEXTAREA -> {
                DataFlowTextArea(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    placeholder = field.placeholder,
                    enabled = field.enabled,
                    isError = error != null
                )
            }
            FieldType.SELECT -> {
                DataFlowDropdown(
                    value = value as? String ?: "",
                    onValueChange = onValueChange,
                    options = field.options,
                    placeholder = field.placeholder,
                    enabled = field.enabled,
                    isError = error != null
                )
            }
            FieldType.CHECKBOX -> {
                DataFlowCheckbox(
                    checked = value as? Boolean ?: false,
                    onCheckedChange = onValueChange,
                    enabled = field.enabled
                )
            }
            FieldType.RADIO -> {
                DataFlowRadioGroup(
                    selectedValue = value as? String ?: "",
                    onValueChange = onValueChange,
                    options = field.options,
                    enabled = field.enabled
                )
            }
            FieldType.SWITCH -> {
                DataFlowSwitch(
                    checked = value as? Boolean ?: false,
                    onCheckedChange = onValueChange,
                    enabled = field.enabled
                )
            }
            FieldType.SLIDER -> {
                DataFlowSlider(
                    value = value as? Float ?: 0f,
                    onValueChange = onValueChange,
                    enabled = field.enabled
                )
            }
            else -> {
                // Placeholder for other field types
                DataFlowTextField(
                    value = value?.toString() ?: "",
                    onValueChange = onValueChange,
                    placeholder = "Not implemented: ${field.type}",
                    enabled = false,
                    isError = false
                )
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
// 📝 INPUT COMPONENTS
// ============================================================================

@Composable
fun DataFlowTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                IconButton(
                    onClick = { onTrailingIconClick?.invoke() }
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        enabled = enabled,
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}

@Composable
fun DataFlowPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Password",
    enabled: Boolean = true,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    DataFlowTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        isError = isError,
        keyboardType = KeyboardType.Password,
        leadingIcon = Icons.Default.Lock,
        trailingIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
        onTrailingIconClick = { passwordVisible = !passwordVisible }
    )
}

@Composable
fun DataFlowTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    isError: Boolean = false,
    minLines: Int = 3,
    maxLines: Int = 6
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        enabled = enabled,
        isError = isError,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error
        )
    )
}

@Composable
fun DataFlowDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    options: List<FieldOption>,
    modifier: Modifier = Modifier,
    placeholder: String = "Select option",
    enabled: Boolean = true,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded && enabled },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = options.find { it.value == value }?.label ?: "",
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            readOnly = true,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled,
            isError = isError,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error
            )
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    },
                    enabled = option.enabled
                )
            }
        }
    }
}

@Composable
fun DataFlowCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun DataFlowRadioGroup(
    selectedValue: String,
    onValueChange: (String) -> Unit,
    options: List<FieldOption>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled && option.enabled) {
                        onValueChange(option.value)
                    }
            ) {
                RadioButton(
                    selected = selectedValue == option.value,
                    onClick = { onValueChange(option.value) },
                    enabled = enabled && option.enabled,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled && option.enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            }
        }
    }
}

@Composable
fun DataFlowSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
fun DataFlowSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ============================================================================
// 🔧 VALIDATION UTILITIES
// ============================================================================

private fun validateForm(
    fields: List<FormField>,
    values: Map<String, Any>
): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    
    fields.forEach { field ->
        val value = values[field.key]
        val stringValue = value?.toString() ?: ""
        
        // Required validation
        if (field.required && stringValue.isBlank()) {
            errors[field.key] = "${field.label} is required"
            return@forEach
        }
        
        // Skip other validations if field is empty and not required
        if (stringValue.isBlank()) return@forEach
        
        // Field-specific validation
        field.validation?.let { validation ->
            // Min length
            validation.minLength?.let { minLength ->
                if (stringValue.length < minLength) {
                    errors[field.key] = "${field.label} must be at least $minLength characters"
                    return@forEach
                }
            }
            
            // Max length
            validation.maxLength?.let { maxLength ->
                if (stringValue.length > maxLength) {
                    errors[field.key] = "${field.label} must be no more than $maxLength characters"
                    return@forEach
                }
            }
            
            // Pattern validation
            validation.pattern?.let { pattern ->
                if (!pattern.matches(stringValue)) {
                    errors[field.key] = "${field.label} format is invalid"
                    return@forEach
                }
            }
            
            // Custom validation
            validation.customValidator?.let { validator ->
                validator(stringValue)?.let { error ->
                    errors[field.key] = error
                    return@forEach
                }
            }
        }
        
        // Type-specific validation
        when (field.type) {
            FieldType.EMAIL -> {
                val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                if (!emailPattern.matches(stringValue)) {
                    errors[field.key] = "Please enter a valid email address"
                }
            }
            FieldType.URL -> {
                val urlPattern = Regex("^https?://[A-Za-z0-9.-]+\\.[A-Za-z]{2,}.*$")
                if (!urlPattern.matches(stringValue)) {
                    errors[field.key] = "Please enter a valid URL"
                }
            }
            FieldType.PHONE -> {
                val phonePattern = Regex("^[+]?[0-9\\s\\-()]{10,}$")
                if (!phonePattern.matches(stringValue)) {
                    errors[field.key] = "Please enter a valid phone number"
                }
            }
            else -> { /* No additional validation */ }
        }
    }
    
    return errors
}