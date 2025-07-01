package com.everybytesystems.dataflow.ui.accessibility

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlin.math.*

/**
 * Accessibility Components
 * Comprehensive accessibility support for inclusive design
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

enum class FontScale {
    SMALL(0.85f),
    NORMAL(1.0f),
    LARGE(1.15f),
    EXTRA_LARGE(1.3f),
    HUGE(1.5f);
    
    constructor(scale: Float) {
        this.scale = scale
    }
    
    val scale: Float
}

enum class ContrastLevel {
    NORMAL,
    HIGH,
    MAXIMUM
}

enum class MotionPreference {
    FULL,
    REDUCED,
    NONE
}

enum class ColorBlindnessType {
    NONE,
    PROTANOPIA,    // Red-blind
    DEUTERANOPIA,  // Green-blind
    TRITANOPIA,    // Blue-blind
    ACHROMATOPSIA  // Complete color blindness
}

data class AccessibilitySettings(
    val fontScale: FontScale = FontScale.NORMAL,
    val contrastLevel: ContrastLevel = ContrastLevel.NORMAL,
    val motionPreference: MotionPreference = MotionPreference.FULL,
    val colorBlindnessType: ColorBlindnessType = ColorBlindnessType.NONE,
    val enableScreenReader: Boolean = false,
    val enableKeyboardNavigation: Boolean = true,
    val enableHapticFeedback: Boolean = true,
    val enableSoundFeedback: Boolean = false,
    val showFocusIndicators: Boolean = true,
    val enableRTL: Boolean = false,
    val reduceTransparency: Boolean = false,
    val enableButtonShapes: Boolean = false,
    val enableLargeText: Boolean = false
)

// ============================================================================
// 🎨 ACCESSIBILITY THEME PROVIDER
// ============================================================================

@Composable
fun AccessibilityThemeProvider(
    settings: AccessibilitySettings,
    content: @Composable () -> Unit
) {
    val accessibilityColorScheme = createAccessibilityColorScheme(
        baseColorScheme = MaterialTheme.colorScheme,
        contrastLevel = settings.contrastLevel,
        colorBlindnessType = settings.colorBlindnessType,
        reduceTransparency = settings.reduceTransparency
    )
    
    val accessibilityTypography = createAccessibilityTypography(
        baseTypography = MaterialTheme.typography,
        fontScale = settings.fontScale,
        enableLargeText = settings.enableLargeText
    )
    
    CompositionLocalProvider(
        LocalAccessibilitySettings provides settings
    ) {
        MaterialTheme(
            colorScheme = accessibilityColorScheme,
            typography = accessibilityTypography,
            content = content
        )
    }
}

val LocalAccessibilitySettings = compositionLocalOf { AccessibilitySettings() }

private fun createAccessibilityColorScheme(
    baseColorScheme: ColorScheme,
    contrastLevel: ContrastLevel,
    colorBlindnessType: ColorBlindnessType,
    reduceTransparency: Boolean
): ColorScheme {
    var colorScheme = baseColorScheme
    
    // Apply contrast adjustments
    colorScheme = when (contrastLevel) {
        ContrastLevel.HIGH -> colorScheme.copy(
            primary = adjustContrast(colorScheme.primary, 1.2f),
            onPrimary = adjustContrast(colorScheme.onPrimary, 1.2f),
            secondary = adjustContrast(colorScheme.secondary, 1.2f),
            onSecondary = adjustContrast(colorScheme.onSecondary, 1.2f),
            surface = adjustContrast(colorScheme.surface, 1.1f),
            onSurface = adjustContrast(colorScheme.onSurface, 1.2f)
        )
        ContrastLevel.MAXIMUM -> colorScheme.copy(
            primary = Color.Black,
            onPrimary = Color.White,
            secondary = Color.Black,
            onSecondary = Color.White,
            surface = Color.White,
            onSurface = Color.Black,
            background = Color.White,
            onBackground = Color.Black
        )
        else -> colorScheme
    }
    
    // Apply color blindness adjustments
    colorScheme = when (colorBlindnessType) {
        ColorBlindnessType.PROTANOPIA -> adjustForProtanopia(colorScheme)
        ColorBlindnessType.DEUTERANOPIA -> adjustForDeuteranopia(colorScheme)
        ColorBlindnessType.TRITANOPIA -> adjustForTritanopia(colorScheme)
        ColorBlindnessType.ACHROMATOPSIA -> convertToGrayscale(colorScheme)
        else -> colorScheme
    }
    
    // Reduce transparency if needed
    if (reduceTransparency) {
        colorScheme = colorScheme.copy(
            surfaceVariant = colorScheme.surfaceVariant.copy(alpha = 1f),
            outline = colorScheme.outline.copy(alpha = 1f)
        )
    }
    
    return colorScheme
}

private fun createAccessibilityTypography(
    baseTypography: Typography,
    fontScale: FontScale,
    enableLargeText: Boolean
): Typography {
    val scale = if (enableLargeText) maxOf(fontScale.scale, 1.3f) else fontScale.scale
    
    return baseTypography.copy(
        displayLarge = baseTypography.displayLarge.copy(fontSize = baseTypography.displayLarge.fontSize * scale),
        displayMedium = baseTypography.displayMedium.copy(fontSize = baseTypography.displayMedium.fontSize * scale),
        displaySmall = baseTypography.displaySmall.copy(fontSize = baseTypography.displaySmall.fontSize * scale),
        headlineLarge = baseTypography.headlineLarge.copy(fontSize = baseTypography.headlineLarge.fontSize * scale),
        headlineMedium = baseTypography.headlineMedium.copy(fontSize = baseTypography.headlineMedium.fontSize * scale),
        headlineSmall = baseTypography.headlineSmall.copy(fontSize = baseTypography.headlineSmall.fontSize * scale),
        titleLarge = baseTypography.titleLarge.copy(fontSize = baseTypography.titleLarge.fontSize * scale),
        titleMedium = baseTypography.titleMedium.copy(fontSize = baseTypography.titleMedium.fontSize * scale),
        titleSmall = baseTypography.titleSmall.copy(fontSize = baseTypography.titleSmall.fontSize * scale),
        bodyLarge = baseTypography.bodyLarge.copy(fontSize = baseTypography.bodyLarge.fontSize * scale),
        bodyMedium = baseTypography.bodyMedium.copy(fontSize = baseTypography.bodyMedium.fontSize * scale),
        bodySmall = baseTypography.bodySmall.copy(fontSize = baseTypography.bodySmall.fontSize * scale),
        labelLarge = baseTypography.labelLarge.copy(fontSize = baseTypography.labelLarge.fontSize * scale),
        labelMedium = baseTypography.labelMedium.copy(fontSize = baseTypography.labelMedium.fontSize * scale),
        labelSmall = baseTypography.labelSmall.copy(fontSize = baseTypography.labelSmall.fontSize * scale)
    )
}

// ============================================================================
// 🎯 ACCESSIBLE COMPONENTS
// ============================================================================

@Composable
fun AccessibleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentDescription: String? = null,
    hapticFeedback: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val settings = LocalAccessibilitySettings.current
    val haptic = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }
    
    Button(
        onClick = {
            if (hapticFeedback && settings.enableHapticFeedback) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onClick()
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter || keyEvent.key == Key.Spacebar) {
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        onClick()
                        true
                    } else false
                } else false
            }
            .semantics {
                contentDescription?.let { this.contentDescription = it }
                role = Role.Button
            }
            .then(
                if (settings.showFocusIndicators) {
                    Modifier.accessibilityFocusIndicator()
                } else Modifier
            )
            .then(
                if (settings.enableButtonShapes) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(8.dp)
                    )
                } else Modifier
            ),
        enabled = enabled,
        colors = colors,
        content = content
    )
}

@Composable
fun AccessibleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    contentDescription: String? = null,
    errorMessage: String? = null
) {
    val settings = LocalAccessibilitySettings.current
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .semantics {
                contentDescription?.let { this.contentDescription = it }
                if (isError && errorMessage != null) {
                    error(errorMessage)
                }
            }
            .then(
                if (settings.showFocusIndicators) {
                    Modifier.accessibilityFocusIndicator()
                } else Modifier
            ),
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (settings.contrastLevel == ContrastLevel.MAXIMUM) {
                Color.Black
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    )
}

@Composable
fun AccessibleCard(
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    contentDescription: String? = null,
    role: Role = Role.Button,
    content: @Composable ColumnScope.() -> Unit
) {
    val settings = LocalAccessibilitySettings.current
    val haptic = LocalHapticFeedback.current
    
    Card(
        onClick = if (onClick != null) {
            {
                if (settings.enableHapticFeedback) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onClick()
            }
        } else null,
        modifier = modifier
            .semantics {
                contentDescription?.let { this.contentDescription = it }
                this.role = role
            }
            .then(
                if (settings.showFocusIndicators && onClick != null) {
                    Modifier.accessibilityFocusIndicator()
                } else Modifier
            ),
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        content = content
    )
}

// ============================================================================
// 🎯 FOCUS MANAGEMENT
// ============================================================================

@Composable
fun FocusIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    width: Dp = 3.dp
) {
    val settings = LocalAccessibilitySettings.current
    
    if (settings.showFocusIndicators) {
        Box(
            modifier = modifier
                .border(width, color, RoundedCornerShape(4.dp))
                .padding(2.dp)
        )
    }
}

fun Modifier.accessibilityFocusIndicator(
    color: Color = Color.Unspecified,
    width: Dp = 3.dp
): Modifier = composed {
    val settings = LocalAccessibilitySettings.current
    val focusColor = if (color == Color.Unspecified) {
        MaterialTheme.colorScheme.primary
    } else color
    
    if (settings.showFocusIndicators) {
        this.onFocusChanged { focusState ->
            // Focus indication is handled by the border modifier
        }.border(
            width = if (settings.showFocusIndicators) width else 0.dp,
            color = focusColor.copy(alpha = 0.8f),
            shape = RoundedCornerShape(4.dp)
        )
    } else this
}

// ============================================================================
// 🎨 HIGH CONTRAST COMPONENTS
// ============================================================================

@Composable
fun HighContrastDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = Color.Unspecified
) {
    val settings = LocalAccessibilitySettings.current
    val dividerColor = when {
        color != Color.Unspecified -> color
        settings.contrastLevel == ContrastLevel.MAXIMUM -> Color.Black
        settings.contrastLevel == ContrastLevel.HIGH -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    }
    
    Divider(
        modifier = modifier,
        thickness = if (settings.contrastLevel == ContrastLevel.MAXIMUM) thickness * 2 else thickness,
        color = dividerColor
    )
}

@Composable
fun HighContrastIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    val settings = LocalAccessibilitySettings.current
    val iconTint = when {
        tint != Color.Unspecified -> tint
        settings.contrastLevel == ContrastLevel.MAXIMUM -> Color.Black
        else -> LocalContentColor.current
    }
    
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.semantics {
            contentDescription?.let { this.contentDescription = it }
        },
        tint = iconTint
    )
}

// ============================================================================
// 🔊 SCREEN READER SUPPORT
// ============================================================================

@Composable
fun ScreenReaderText(
    text: String,
    modifier: Modifier = Modifier
) {
    // Invisible text that's only read by screen readers
    Text(
        text = text,
        modifier = modifier
            .size(0.dp)
            .semantics {
                contentDescription = text
                invisibleToUser()
            }
    )
}

@Composable
fun AnnouncementText(
    text: String,
    priority: LiveRegionMode = LiveRegionMode.Polite
) {
    val settings = LocalAccessibilitySettings.current
    
    if (settings.enableScreenReader) {
        Text(
            text = text,
            modifier = Modifier
                .size(0.dp)
                .semantics {
                    liveRegion = priority
                    contentDescription = text
                }
        )
    }
}

// ============================================================================
// ⌨️ KEYBOARD NAVIGATION
// ============================================================================

@Composable
fun KeyboardNavigationContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val settings = LocalAccessibilitySettings.current
    
    Box(
        modifier = modifier
            .then(
                if (settings.enableKeyboardNavigation) {
                    Modifier.onKeyEvent { keyEvent ->
                        when (keyEvent.key) {
                            Key.Tab -> {
                                // Handle tab navigation
                                true
                            }
                            Key.DirectionUp, Key.DirectionDown,
                            Key.DirectionLeft, Key.DirectionRight -> {
                                // Handle arrow key navigation
                                true
                            }
                            else -> false
                        }
                    }
                } else Modifier
            )
    ) {
        content()
    }
}

// ============================================================================
// 🎛️ ACCESSIBILITY SETTINGS PANEL
// ============================================================================

@Composable
fun AccessibilitySettingsPanel(
    settings: AccessibilitySettings,
    onSettingsChange: (AccessibilitySettings) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Accessibility Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Font Scale
        item {
            AccessibilitySettingSection(
                title = "Text Size",
                description = "Adjust text size for better readability"
            ) {
                FontScaleSelector(
                    currentScale = settings.fontScale,
                    onScaleChange = { 
                        onSettingsChange(settings.copy(fontScale = it))
                    }
                )
            }
        }
        
        // Contrast Level
        item {
            AccessibilitySettingSection(
                title = "Contrast",
                description = "Increase contrast for better visibility"
            ) {
                ContrastLevelSelector(
                    currentLevel = settings.contrastLevel,
                    onLevelChange = {
                        onSettingsChange(settings.copy(contrastLevel = it))
                    }
                )
            }
        }
        
        // Motion Preferences
        item {
            AccessibilitySettingSection(
                title = "Motion",
                description = "Control animation and motion effects"
            ) {
                MotionPreferenceSelector(
                    currentPreference = settings.motionPreference,
                    onPreferenceChange = {
                        onSettingsChange(settings.copy(motionPreference = it))
                    }
                )
            }
        }
        
        // Color Blindness Support
        item {
            AccessibilitySettingSection(
                title = "Color Vision",
                description = "Adjust colors for color vision differences"
            ) {
                ColorBlindnessSelector(
                    currentType = settings.colorBlindnessType,
                    onTypeChange = {
                        onSettingsChange(settings.copy(colorBlindnessType = it))
                    }
                )
            }
        }
        
        // Toggle Settings
        item {
            AccessibilityToggleSettings(
                settings = settings,
                onSettingsChange = onSettingsChange
            )
        }
    }
}

@Composable
private fun AccessibilitySettingSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            content()
        }
    }
}

@Composable
private fun FontScaleSelector(
    currentScale: FontScale,
    onScaleChange: (FontScale) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FontScale.values().forEach { scale ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentScale == scale,
                        onClick = { onScaleChange(scale) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = currentScale == scale,
                    onClick = { onScaleChange(scale) }
                )
                
                Column {
                    Text(
                        text = scale.name.replace('_', ' '),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize * scale.scale
                        )
                    )
                    Text(
                        text = "${(scale.scale * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ContrastLevelSelector(
    currentLevel: ContrastLevel,
    onLevelChange: (ContrastLevel) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ContrastLevel.values().forEach { level ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentLevel == level,
                        onClick = { onLevelChange(level) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = currentLevel == level,
                    onClick = { onLevelChange(level) }
                )
                
                Text(
                    text = level.name.replace('_', ' '),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun MotionPreferenceSelector(
    currentPreference: MotionPreference,
    onPreferenceChange: (MotionPreference) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MotionPreference.values().forEach { preference ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentPreference == preference,
                        onClick = { onPreferenceChange(preference) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = currentPreference == preference,
                    onClick = { onPreferenceChange(preference) }
                )
                
                Text(
                    text = preference.name.replace('_', ' '),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ColorBlindnessSelector(
    currentType: ColorBlindnessType,
    onTypeChange: (ColorBlindnessType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ColorBlindnessType.values().forEach { type ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentType == type,
                        onClick = { onTypeChange(type) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = currentType == type,
                    onClick = { onTypeChange(type) }
                )
                
                Column {
                    Text(
                        text = when (type) {
                            ColorBlindnessType.NONE -> "None"
                            ColorBlindnessType.PROTANOPIA -> "Protanopia (Red-blind)"
                            ColorBlindnessType.DEUTERANOPIA -> "Deuteranopia (Green-blind)"
                            ColorBlindnessType.TRITANOPIA -> "Tritanopia (Blue-blind)"
                            ColorBlindnessType.ACHROMATOPSIA -> "Achromatopsia (Complete)"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (type != ColorBlindnessType.NONE) {
                        Text(
                            text = "Colors adjusted for this type of color vision",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccessibilityToggleSettings(
    settings: AccessibilitySettings,
    onSettingsChange: (AccessibilitySettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AccessibilityToggle(
            label = "Screen Reader Support",
            description = "Optimize for screen readers",
            checked = settings.enableScreenReader,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableScreenReader = it))
            }
        )
        
        AccessibilityToggle(
            label = "Keyboard Navigation",
            description = "Enable keyboard shortcuts and navigation",
            checked = settings.enableKeyboardNavigation,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableKeyboardNavigation = it))
            }
        )
        
        AccessibilityToggle(
            label = "Haptic Feedback",
            description = "Vibration feedback for interactions",
            checked = settings.enableHapticFeedback,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableHapticFeedback = it))
            }
        )
        
        AccessibilityToggle(
            label = "Sound Feedback",
            description = "Audio feedback for interactions",
            checked = settings.enableSoundFeedback,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableSoundFeedback = it))
            }
        )
        
        AccessibilityToggle(
            label = "Focus Indicators",
            description = "Show visual focus indicators",
            checked = settings.showFocusIndicators,
            onCheckedChange = {
                onSettingsChange(settings.copy(showFocusIndicators = it))
            }
        )
        
        AccessibilityToggle(
            label = "Right-to-Left Layout",
            description = "Enable RTL text direction",
            checked = settings.enableRTL,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableRTL = it))
            }
        )
        
        AccessibilityToggle(
            label = "Reduce Transparency",
            description = "Make backgrounds more opaque",
            checked = settings.reduceTransparency,
            onCheckedChange = {
                onSettingsChange(settings.copy(reduceTransparency = it))
            }
        )
        
        AccessibilityToggle(
            label = "Button Shapes",
            description = "Add borders to buttons for clarity",
            checked = settings.enableButtonShapes,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableButtonShapes = it))
            }
        )
        
        AccessibilityToggle(
            label = "Large Text",
            description = "Force large text size",
            checked = settings.enableLargeText,
            onCheckedChange = {
                onSettingsChange(settings.copy(enableLargeText = it))
            }
        )
    }
}

@Composable
private fun AccessibilityToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange
            )
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// ============================================================================
// 🛠️ COLOR ADJUSTMENT UTILITIES
// ============================================================================

private fun adjustContrast(color: Color, factor: Float): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsv)
    
    // Adjust saturation and value for better contrast
    hsv[1] = (hsv[1] * factor).coerceIn(0f, 1f)
    hsv[2] = if (hsv[2] > 0.5f) {
        (hsv[2] * factor).coerceIn(0f, 1f)
    } else {
        (hsv[2] / factor).coerceIn(0f, 1f)
    }
    
    return Color(android.graphics.Color.HSVToColor(hsv))
}

private fun adjustForProtanopia(colorScheme: ColorScheme): ColorScheme {
    // Adjust red colors for protanopia (red-blind)
    return colorScheme.copy(
        primary = adjustRedForProtanopia(colorScheme.primary),
        error = Color(0xFF0066CC) // Use blue instead of red for errors
    )
}

private fun adjustForDeuteranopia(colorScheme: ColorScheme): ColorScheme {
    // Adjust green colors for deuteranopia (green-blind)
    return colorScheme.copy(
        primary = adjustGreenForDeuteranopia(colorScheme.primary),
        secondary = adjustGreenForDeuteranopia(colorScheme.secondary)
    )
}

private fun adjustForTritanopia(colorScheme: ColorScheme): ColorScheme {
    // Adjust blue colors for tritanopia (blue-blind)
    return colorScheme.copy(
        primary = adjustBlueForTritanopia(colorScheme.primary),
        secondary = adjustBlueForTritanopia(colorScheme.secondary)
    )
}

private fun convertToGrayscale(colorScheme: ColorScheme): ColorScheme {
    return colorScheme.copy(
        primary = colorScheme.primary.toGrayscale(),
        onPrimary = colorScheme.onPrimary.toGrayscale(),
        secondary = colorScheme.secondary.toGrayscale(),
        onSecondary = colorScheme.onSecondary.toGrayscale(),
        tertiary = colorScheme.tertiary.toGrayscale(),
        onTertiary = colorScheme.onTertiary.toGrayscale(),
        error = colorScheme.error.toGrayscale(),
        onError = colorScheme.onError.toGrayscale()
    )
}

private fun Color.toGrayscale(): Color {
    val gray = (red * 0.299f + green * 0.587f + blue * 0.114f)
    return Color(gray, gray, gray, alpha)
}

private fun adjustRedForProtanopia(color: Color): Color {
    // Shift red towards yellow/orange for better visibility
    return Color(
        red = (color.red * 0.7f + color.green * 0.3f).coerceIn(0f, 1f),
        green = color.green,
        blue = color.blue,
        alpha = color.alpha
    )
}

private fun adjustGreenForDeuteranopia(color: Color): Color {
    // Shift green towards blue/yellow for better visibility
    return Color(
        red = color.red,
        green = (color.green * 0.7f + color.blue * 0.3f).coerceIn(0f, 1f),
        blue = color.blue,
        alpha = color.alpha
    )
}

private fun adjustBlueForTritanopia(color: Color): Color {
    // Shift blue towards green/red for better visibility
    return Color(
        red = color.red,
        green = color.green,
        blue = (color.blue * 0.7f + color.red * 0.3f).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}