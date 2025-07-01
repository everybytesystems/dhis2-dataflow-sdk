package com.everybytesystems.dataflow.ui.theme

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
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

/**
 * Enhanced Theme & Design System
 * Advanced theming with design tokens, dynamic colors, and customization
 */

// ============================================================================
// 🎨 DESIGN TOKENS
// ============================================================================

data class DesignTokens(
    val colors: ColorTokens,
    val typography: TypographyTokens,
    val spacing: SpacingTokens,
    val elevation: ElevationTokens,
    val shapes: ShapeTokens,
    val motion: MotionTokens
)

data class ColorTokens(
    val primary: ColorScale,
    val secondary: ColorScale,
    val tertiary: ColorScale,
    val neutral: ColorScale,
    val error: ColorScale,
    val warning: ColorScale,
    val success: ColorScale,
    val info: ColorScale
)

data class ColorScale(
    val color50: Color,
    val color100: Color,
    val color200: Color,
    val color300: Color,
    val color400: Color,
    val color500: Color,
    val color600: Color,
    val color700: Color,
    val color800: Color,
    val color900: Color,
    val color950: Color
)

data class TypographyTokens(
    val fontFamilies: FontFamilies,
    val fontWeights: FontWeights,
    val fontSizes: FontSizes,
    val lineHeights: LineHeights,
    val letterSpacing: LetterSpacing
)

data class FontFamilies(
    val primary: FontFamily,
    val secondary: FontFamily,
    val monospace: FontFamily,
    val display: FontFamily
)

data class FontWeights(
    val thin: FontWeight,
    val light: FontWeight,
    val regular: FontWeight,
    val medium: FontWeight,
    val semiBold: FontWeight,
    val bold: FontWeight,
    val extraBold: FontWeight,
    val black: FontWeight
)

data class FontSizes(
    val xs: TextUnit,
    val sm: TextUnit,
    val base: TextUnit,
    val lg: TextUnit,
    val xl: TextUnit,
    val xl2: TextUnit,
    val xl3: TextUnit,
    val xl4: TextUnit,
    val xl5: TextUnit,
    val xl6: TextUnit
)

data class LineHeights(
    val tight: TextUnit,
    val normal: TextUnit,
    val relaxed: TextUnit,
    val loose: TextUnit
)

data class LetterSpacing(
    val tight: TextUnit,
    val normal: TextUnit,
    val wide: TextUnit,
    val wider: TextUnit,
    val widest: TextUnit
)

data class SpacingTokens(
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xl2: Dp,
    val xl3: Dp,
    val xl4: Dp,
    val xl5: Dp,
    val xl6: Dp
)

data class ElevationTokens(
    val none: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xl2: Dp,
    val xl3: Dp
)

data class ShapeTokens(
    val none: CornerBasedShape,
    val xs: CornerBasedShape,
    val sm: CornerBasedShape,
    val md: CornerBasedShape,
    val lg: CornerBasedShape,
    val xl: CornerBasedShape,
    val xl2: CornerBasedShape,
    val full: CornerBasedShape
)

data class MotionTokens(
    val durations: DurationTokens,
    val easings: EasingTokens
)

data class DurationTokens(
    val instant: Int,
    val fast: Int,
    val normal: Int,
    val slow: Int,
    val slower: Int
)

data class EasingTokens(
    val linear: Easing,
    val easeIn: Easing,
    val easeOut: Easing,
    val easeInOut: Easing,
    val spring: Easing
)

// ============================================================================
// 🎨 THEME CONFIGURATION
// ============================================================================

data class ThemeConfiguration(
    val colorScheme: ColorScheme,
    val typography: Typography,
    val designTokens: DesignTokens,
    val isDarkTheme: Boolean = false,
    val isDynamicColor: Boolean = false,
    val customizations: ThemeCustomizations = ThemeCustomizations()
)

data class ThemeCustomizations(
    val primaryColor: Color? = null,
    val fontFamily: FontFamily? = null,
    val cornerRadius: Dp? = null,
    val animations: Boolean = true,
    val hapticFeedback: Boolean = true,
    val reducedMotion: Boolean = false
)

enum class ThemeVariant {
    LIGHT,
    DARK,
    AUTO,
    HIGH_CONTRAST,
    CUSTOM
}

enum class ColorMode {
    STATIC,
    DYNAMIC,
    ADAPTIVE
}

// ============================================================================
// 🎨 ENHANCED THEME PROVIDER
// ============================================================================

@Composable
fun EnhancedThemeProvider(
    variant: ThemeVariant = ThemeVariant.AUTO,
    colorMode: ColorMode = ColorMode.STATIC,
    customizations: ThemeCustomizations = ThemeCustomizations(),
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    
    val isDarkTheme = when (variant) {
        ThemeVariant.LIGHT -> false
        ThemeVariant.DARK -> true
        ThemeVariant.AUTO -> systemInDarkTheme
        ThemeVariant.HIGH_CONTRAST -> systemInDarkTheme
        ThemeVariant.CUSTOM -> systemInDarkTheme
    }
    
    val colorScheme = when {
        variant == ThemeVariant.HIGH_CONTRAST -> {
            if (isDarkTheme) createHighContrastDarkColorScheme() else createHighContrastLightColorScheme()
        }
        customizations.primaryColor != null -> {
            createCustomColorScheme(customizations.primaryColor, isDarkTheme)
        }
        else -> {
            if (isDarkTheme) createDarkColorScheme() else createLightColorScheme()
        }
    }
    
    val typography = createEnhancedTypography(customizations.fontFamily)
    val designTokens = createDesignTokens(isDarkTheme, customizations)
    
    val themeConfiguration = ThemeConfiguration(
        colorScheme = colorScheme,
        typography = typography,
        designTokens = designTokens,
        isDarkTheme = isDarkTheme,
        isDynamicColor = colorMode == ColorMode.DYNAMIC,
        customizations = customizations
    )
    
    CompositionLocalProvider(
        LocalThemeConfiguration provides themeConfiguration,
        LocalDesignTokens provides designTokens
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            content = content
        )
    }
}

val LocalThemeConfiguration = compositionLocalOf<ThemeConfiguration> {
    error("No ThemeConfiguration provided")
}

val LocalDesignTokens = compositionLocalOf<DesignTokens> {
    error("No DesignTokens provided")
}

// ============================================================================
// 🎨 THEME CUSTOMIZER
// ============================================================================

@Composable
fun ThemeCustomizer(
    currentConfiguration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Colors", "Typography", "Spacing", "Shapes", "Motion")
    
    Column(modifier = modifier.fillMaxSize()) {
        // Tab navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth()
        ) {
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
            0 -> ColorCustomizer(
                configuration = currentConfiguration,
                onConfigurationChange = onConfigurationChange
            )
            1 -> TypographyCustomizer(
                configuration = currentConfiguration,
                onConfigurationChange = onConfigurationChange
            )
            2 -> SpacingCustomizer(
                configuration = currentConfiguration,
                onConfigurationChange = onConfigurationChange
            )
            3 -> ShapeCustomizer(
                configuration = currentConfiguration,
                onConfigurationChange = onConfigurationChange
            )
            4 -> MotionCustomizer(
                configuration = currentConfiguration,
                onConfigurationChange = onConfigurationChange
            )
        }
    }
}

@Composable
private fun ColorCustomizer(
    configuration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Color Customization",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Primary color picker
        item {
            ColorPickerSection(
                title = "Primary Color",
                currentColor = configuration.colorScheme.primary,
                onColorChange = { color ->
                    val newColorScheme = createCustomColorScheme(color, configuration.isDarkTheme)
                    onConfigurationChange(
                        configuration.copy(
                            colorScheme = newColorScheme,
                            customizations = configuration.customizations.copy(primaryColor = color)
                        )
                    )
                }
            )
        }
        
        // Color scheme preview
        item {
            ColorSchemePreview(colorScheme = configuration.colorScheme)
        }
        
        // Preset color schemes
        item {
            PresetColorSchemes(
                onSchemeSelect = { colorScheme ->
                    onConfigurationChange(configuration.copy(colorScheme = colorScheme))
                }
            )
        }
    }
}

@Composable
private fun TypographyCustomizer(
    configuration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Typography Customization",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Font family selector
        item {
            FontFamilySelector(
                currentFontFamily = configuration.customizations.fontFamily,
                onFontFamilyChange = { fontFamily ->
                    val newTypography = createEnhancedTypography(fontFamily)
                    onConfigurationChange(
                        configuration.copy(
                            typography = newTypography,
                            customizations = configuration.customizations.copy(fontFamily = fontFamily)
                        )
                    )
                }
            )
        }
        
        // Typography preview
        item {
            TypographyPreview(typography = configuration.typography)
        }
    }
}

@Composable
private fun SpacingCustomizer(
    configuration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Spacing Customization",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Spacing scale preview
        item {
            SpacingScalePreview(spacing = configuration.designTokens.spacing)
        }
    }
}

@Composable
private fun ShapeCustomizer(
    configuration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Shape Customization",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Corner radius slider
        item {
            CornerRadiusSlider(
                currentRadius = configuration.customizations.cornerRadius ?: 12.dp,
                onRadiusChange = { radius ->
                    onConfigurationChange(
                        configuration.copy(
                            customizations = configuration.customizations.copy(cornerRadius = radius)
                        )
                    )
                }
            )
        }
        
        // Shape preview
        item {
            ShapePreview(shapes = configuration.designTokens.shapes)
        }
    }
}

@Composable
private fun MotionCustomizer(
    configuration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Motion Customization",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Animation toggles
        item {
            AnimationToggles(
                configuration = configuration,
                onConfigurationChange = onConfigurationChange
            )
        }
        
        // Motion preview
        item {
            MotionPreview(motion = configuration.designTokens.motion)
        }
    }
}

// ============================================================================
// 🎨 HELPER COMPONENTS
// ============================================================================

@Composable
private fun ColorPickerSection(
    title: String,
    currentColor: Color,
    onColorChange: (Color) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Color preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(currentColor, RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            )
            
            // Preset colors
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getPresetColors()) { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color, CircleShape)
                            .border(
                                2.dp,
                                if (color == currentColor) MaterialTheme.colorScheme.primary else Color.Transparent,
                                CircleShape
                            )
                            .clickable { onColorChange(color) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorSchemePreview(colorScheme: ColorScheme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Color Scheme Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getColorSchemeColors(colorScheme)) { (name, color) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetColorSchemes(
    onSchemeSelect: (ColorScheme) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preset Color Schemes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(getPresetColorSchemes()) { (name, colorScheme) ->
                    PresetColorSchemeCard(
                        name = name,
                        colorScheme = colorScheme,
                        onClick = { onSchemeSelect(colorScheme) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetColorSchemeCard(
    name: String,
    colorScheme: ColorScheme,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(colorScheme.primary, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(colorScheme.secondary, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(colorScheme.tertiary, CircleShape)
                )
            }
            
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FontFamilySelector(
    currentFontFamily: FontFamily?,
    onFontFamilyChange: (FontFamily?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Font Family",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyColumn(
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(getAvailableFontFamilies()) { (name, fontFamily) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFontFamilyChange(fontFamily) }
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = name,
                            fontFamily = fontFamily,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        RadioButton(
                            selected = currentFontFamily == fontFamily,
                            onClick = { onFontFamilyChange(fontFamily) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypographyPreview(typography: Typography) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Typography Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text("Display Large", style = typography.displayLarge)
            Text("Headline Large", style = typography.headlineLarge)
            Text("Title Large", style = typography.titleLarge)
            Text("Body Large", style = typography.bodyLarge)
            Text("Label Large", style = typography.labelLarge)
        }
    }
}

@Composable
private fun SpacingScalePreview(spacing: SpacingTokens) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Spacing Scale",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            listOf(
                "XS" to spacing.xs,
                "SM" to spacing.sm,
                "MD" to spacing.md,
                "LG" to spacing.lg,
                "XL" to spacing.xl
            ).forEach { (name, size) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(40.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(size)
                            .height(16.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "${size.value.toInt()}dp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ShapePreview(shapes: ShapeTokens) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Shape Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    listOf(
                        "None" to shapes.none,
                        "XS" to shapes.xs,
                        "SM" to shapes.sm,
                        "MD" to shapes.md,
                        "LG" to shapes.lg,
                        "XL" to shapes.xl,
                        "Full" to shapes.full
                    )
                ) { (name, shape) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, shape)
                        )
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CornerRadiusSlider(
    currentRadius: Dp,
    onRadiusChange: (Dp) -> Unit
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
                    text = "Corner Radius",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${currentRadius.value.toInt()}dp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Slider(
                value = currentRadius.value,
                onValueChange = { onRadiusChange(it.dp) },
                valueRange = 0f..32f,
                steps = 31
            )
            
            // Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(currentRadius)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun AnimationToggles(
    configuration: ThemeConfiguration,
    onConfigurationChange: (ThemeConfiguration) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Animation Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Animations")
                Switch(
                    checked = configuration.customizations.animations,
                    onCheckedChange = { enabled ->
                        onConfigurationChange(
                            configuration.copy(
                                customizations = configuration.customizations.copy(animations = enabled)
                            )
                        )
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reduced Motion")
                Switch(
                    checked = configuration.customizations.reducedMotion,
                    onCheckedChange = { enabled ->
                        onConfigurationChange(
                            configuration.copy(
                                customizations = configuration.customizations.copy(reducedMotion = enabled)
                            )
                        )
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Haptic Feedback")
                Switch(
                    checked = configuration.customizations.hapticFeedback,
                    onCheckedChange = { enabled ->
                        onConfigurationChange(
                            configuration.copy(
                                customizations = configuration.customizations.copy(hapticFeedback = enabled)
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun MotionPreview(motion: MotionTokens) {
    var isAnimating by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Motion Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { isAnimating = !isAnimating },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Toggle Animation")
            }
            
            // Animation preview boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    "Fast" to motion.easings.easeIn,
                    "Normal" to motion.easings.easeInOut,
                    "Slow" to motion.easings.easeOut
                ).forEach { (name, easing) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val animatedOffset by animateFloatAsState(
                            targetValue = if (isAnimating) 40f else 0f,
                            animationSpec = tween(
                                durationMillis = motion.durations.normal,
                                easing = easing
                            ),
                            label = "motion_preview"
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .offset(y = animatedOffset.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

fun createDesignTokens(isDarkTheme: Boolean, customizations: ThemeCustomizations): DesignTokens {
    return DesignTokens(
        colors = createColorTokens(isDarkTheme),
        typography = createTypographyTokens(customizations.fontFamily),
        spacing = createSpacingTokens(),
        elevation = createElevationTokens(),
        shapes = createShapeTokens(customizations.cornerRadius),
        motion = createMotionTokens(customizations.reducedMotion)
    )
}

private fun createColorTokens(isDarkTheme: Boolean): ColorTokens {
    return ColorTokens(
        primary = createPrimaryColorScale(),
        secondary = createSecondaryColorScale(),
        tertiary = createTertiaryColorScale(),
        neutral = createNeutralColorScale(),
        error = createErrorColorScale(),
        warning = createWarningColorScale(),
        success = createSuccessColorScale(),
        info = createInfoColorScale()
    )
}

private fun createTypographyTokens(fontFamily: FontFamily?): TypographyTokens {
    return TypographyTokens(
        fontFamilies = FontFamilies(
            primary = fontFamily ?: FontFamily.Default,
            secondary = FontFamily.SansSerif,
            monospace = FontFamily.Monospace,
            display = fontFamily ?: FontFamily.Default
        ),
        fontWeights = FontWeights(
            thin = FontWeight.Thin,
            light = FontWeight.Light,
            regular = FontWeight.Normal,
            medium = FontWeight.Medium,
            semiBold = FontWeight.SemiBold,
            bold = FontWeight.Bold,
            extraBold = FontWeight.ExtraBold,
            black = FontWeight.Black
        ),
        fontSizes = FontSizes(
            xs = 12.sp,
            sm = 14.sp,
            base = 16.sp,
            lg = 18.sp,
            xl = 20.sp,
            xl2 = 24.sp,
            xl3 = 30.sp,
            xl4 = 36.sp,
            xl5 = 48.sp,
            xl6 = 60.sp
        ),
        lineHeights = LineHeights(
            tight = 1.25.em,
            normal = 1.5.em,
            relaxed = 1.625.em,
            loose = 2.em
        ),
        letterSpacing = LetterSpacing(
            tight = (-0.025).em,
            normal = 0.em,
            wide = 0.025.em,
            wider = 0.05.em,
            widest = 0.1.em
        )
    )
}

private fun createSpacingTokens(): SpacingTokens {
    return SpacingTokens(
        xs = 4.dp,
        sm = 8.dp,
        md = 16.dp,
        lg = 24.dp,
        xl = 32.dp,
        xl2 = 48.dp,
        xl3 = 64.dp,
        xl4 = 80.dp,
        xl5 = 96.dp,
        xl6 = 128.dp
    )
}

private fun createElevationTokens(): ElevationTokens {
    return ElevationTokens(
        none = 0.dp,
        xs = 1.dp,
        sm = 2.dp,
        md = 4.dp,
        lg = 8.dp,
        xl = 12.dp,
        xl2 = 16.dp,
        xl3 = 24.dp
    )
}

private fun createShapeTokens(cornerRadius: Dp?): ShapeTokens {
    val baseRadius = cornerRadius ?: 12.dp
    
    return ShapeTokens(
        none = RoundedCornerShape(0.dp),
        xs = RoundedCornerShape(2.dp),
        sm = RoundedCornerShape(4.dp),
        md = RoundedCornerShape(baseRadius),
        lg = RoundedCornerShape(baseRadius * 1.5f),
        xl = RoundedCornerShape(baseRadius * 2f),
        xl2 = RoundedCornerShape(baseRadius * 3f),
        full = RoundedCornerShape(50)
    )
}

private fun createMotionTokens(reducedMotion: Boolean): MotionTokens {
    val durationMultiplier = if (reducedMotion) 0.5f else 1f
    
    return MotionTokens(
        durations = DurationTokens(
            instant = 0,
            fast = (150 * durationMultiplier).toInt(),
            normal = (300 * durationMultiplier).toInt(),
            slow = (500 * durationMultiplier).toInt(),
            slower = (800 * durationMultiplier).toInt()
        ),
        easings = EasingTokens(
            linear = LinearEasing,
            easeIn = FastOutSlowInEasing,
            easeOut = LinearOutSlowInEasing,
            easeInOut = FastOutSlowInEasing,
            spring = FastOutSlowInEasing
        )
    )
}

// Color scale creation functions
private fun createPrimaryColorScale(): ColorScale {
    return ColorScale(
        color50 = Color(0xFFE3F2FD),
        color100 = Color(0xFFBBDEFB),
        color200 = Color(0xFF90CAF9),
        color300 = Color(0xFF64B5F6),
        color400 = Color(0xFF42A5F5),
        color500 = Color(0xFF2196F3),
        color600 = Color(0xFF1E88E5),
        color700 = Color(0xFF1976D2),
        color800 = Color(0xFF1565C0),
        color900 = Color(0xFF0D47A1),
        color950 = Color(0xFF0A3D91)
    )
}

private fun createSecondaryColorScale(): ColorScale {
    return ColorScale(
        color50 = Color(0xFFF3E5F5),
        color100 = Color(0xFFE1BEE7),
        color200 = Color(0xFFCE93D8),
        color300 = Color(0xFFBA68C8),
        color400 = Color(0xFFAB47BC),
        color500 = Color(0xFF9C27B0),
        color600 = Color(0xFF8E24AA),
        color700 = Color(0xFF7B1FA2),
        color800 = Color(0xFF6A1B9A),
        color900 = Color(0xFF4A148C),
        color950 = Color(0xFF3A1078)
    )
}

private fun createTertiaryColorScale(): ColorScale {
    return ColorScale(
        color50 = Color(0xFFE8F5E8),
        color100 = Color(0xFFC8E6C9),
        color200 = Color(0xFFA5D6A7),
        color300 = Color(0xFF81C784),
        color400 = Color(0xFF66BB6A),
        color500 = Color(0xFF4CAF50),
        color600 = Color(0xFF43A047),
        color700 = Color(0xFF388E3C),
        color800 = Color(0xFF2E7D32),
        color900 = Color(0xFF1B5E20),
        color950 = Color(0xFF145A1C)
    )
}

private fun createNeutralColorScale(): ColorScale {
    return ColorScale(
        color50 = Color(0xFFFAFAFA),
        color100 = Color(0xFFF5F5F5),
        color200 = Color(0xFFEEEEEE),
        color300 = Color(0xFFE0E0E0),
        color400 = Color(0xFFBDBDBD),
        color500 = Color(0xFF9E9E9E),
        color600 = Color(0xFF757575),
        color700 = Color(0xFF616161),
        color800 = Color(0xFF424242),
        color900 = Color(0xFF212121),
        color950 = Color(0xFF0F0F0F)
    )
}

private fun createErrorColorScale(): ColorScale {
    return ColorScale(
        color50 = Color(0xFFFFEBEE),
        color100 = Color(0xFFFFCDD2),
        color200 = Color(0xFFEF9A9A),
        color300 = Color(0xFFE57373),
        color400 = Color(0xFFEF5350),
        color500 = Color(0xFFF44336),
        color600 = Color(0xFFE53935),
        color700 = Color(0xFFD32F2F),
        color800 = Color(0xFFC62828),
        color900 = Color(0xFFB71C1C),
        color950 = Color(0xFFA71818)
    )
}

private fun createWarningColorScale(): ColorScale {
    return ColorScale(
        color50 = Color(0xFFFFF3E0),
        color100 = Color(0xFFFFE0B2),
        color200 = Color(0xFFFFCC80),
        color300 = Color(0xFFFFB74D),
        color400 = Color(0xFFFFA726),
        color500 = Color(0xFFFF9800),
        color600 = Color(0xFFFB8C00),
        color700 = Color(0xFFF57C00),
        color800 = Color(0xFFEF6C00),
        color900 = Color(0xFFE65100),
        color950 = Color(0xFFD64700)
    )
}

private fun createSuccessColorScale(): ColorScale {
    return createTertiaryColorScale() // Reuse tertiary (green) for success
}

private fun createInfoColorScale(): ColorScale {
    return createPrimaryColorScale() // Reuse primary (blue) for info
}

private fun createLightColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = Color(0xFF2196F3),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE3F2FD),
        onPrimaryContainer = Color(0xFF0D47A1),
        secondary = Color(0xFF9C27B0),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF3E5F5),
        onSecondaryContainer = Color(0xFF4A148C),
        tertiary = Color(0xFF4CAF50),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE8F5E8),
        onTertiaryContainer = Color(0xFF1B5E20),
        error = Color(0xFFF44336),
        onError = Color.White,
        errorContainer = Color(0xFFFFEBEE),
        onErrorContainer = Color(0xFFB71C1C),
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF212121),
        surface = Color.White,
        onSurface = Color(0xFF212121),
        surfaceVariant = Color(0xFFF5F5F5),
        onSurfaceVariant = Color(0xFF616161),
        outline = Color(0xFFBDBDBD),
        outlineVariant = Color(0xFFE0E0E0)
    )
}

private fun createDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(0xFF64B5F6),
        onPrimary = Color(0xFF0D47A1),
        primaryContainer = Color(0xFF1565C0),
        onPrimaryContainer = Color(0xFFE3F2FD),
        secondary = Color(0xFFCE93D8),
        onSecondary = Color(0xFF4A148C),
        secondaryContainer = Color(0xFF6A1B9A),
        onSecondaryContainer = Color(0xFFF3E5F5),
        tertiary = Color(0xFF81C784),
        onTertiary = Color(0xFF1B5E20),
        tertiaryContainer = Color(0xFF2E7D32),
        onTertiaryContainer = Color(0xFFE8F5E8),
        error = Color(0xFFEF5350),
        onError = Color(0xFFB71C1C),
        errorContainer = Color(0xFFC62828),
        onErrorContainer = Color(0xFFFFEBEE),
        background = Color(0xFF121212),
        onBackground = Color(0xFFFAFAFA),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFFAFAFA),
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFBDBDBD),
        outline = Color(0xFF616161),
        outlineVariant = Color(0xFF424242)
    )
}

private fun createHighContrastLightColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = Color.Black,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0E0E0),
        onPrimaryContainer = Color.Black,
        secondary = Color.Black,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE0E0E0),
        onSecondaryContainer = Color.Black,
        tertiary = Color.Black,
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFE0E0E0),
        onTertiaryContainer = Color.Black,
        error = Color.Black,
        onError = Color.White,
        errorContainer = Color(0xFFE0E0E0),
        onErrorContainer = Color.Black,
        background = Color.White,
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black,
        surfaceVariant = Color(0xFFF5F5F5),
        onSurfaceVariant = Color.Black,
        outline = Color.Black,
        outlineVariant = Color(0xFF616161)
    )
}

private fun createHighContrastDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color.White,
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF424242),
        onPrimaryContainer = Color.White,
        secondary = Color.White,
        onSecondary = Color.Black,
        secondaryContainer = Color(0xFF424242),
        onSecondaryContainer = Color.White,
        tertiary = Color.White,
        onTertiary = Color.Black,
        tertiaryContainer = Color(0xFF424242),
        onTertiaryContainer = Color.White,
        error = Color.White,
        onError = Color.Black,
        errorContainer = Color(0xFF424242),
        onErrorContainer = Color.White,
        background = Color.Black,
        onBackground = Color.White,
        surface = Color.Black,
        onSurface = Color.White,
        surfaceVariant = Color(0xFF212121),
        onSurfaceVariant = Color.White,
        outline = Color.White,
        outlineVariant = Color(0xFFBDBDBD)
    )
}

private fun createCustomColorScheme(primaryColor: Color, isDarkTheme: Boolean): ColorScheme {
    // This would implement a proper color scheme generation algorithm
    // For now, we'll use the primary color and generate a basic scheme
    val baseScheme = if (isDarkTheme) createDarkColorScheme() else createLightColorScheme()
    
    return baseScheme.copy(
        primary = primaryColor,
        primaryContainer = primaryColor.copy(alpha = 0.1f),
        onPrimaryContainer = primaryColor
    )
}

private fun createEnhancedTypography(fontFamily: FontFamily?): Typography {
    val baseTypography = Typography()
    val customFontFamily = fontFamily ?: FontFamily.Default
    
    return baseTypography.copy(
        displayLarge = baseTypography.displayLarge.copy(fontFamily = customFontFamily),
        displayMedium = baseTypography.displayMedium.copy(fontFamily = customFontFamily),
        displaySmall = baseTypography.displaySmall.copy(fontFamily = customFontFamily),
        headlineLarge = baseTypography.headlineLarge.copy(fontFamily = customFontFamily),
        headlineMedium = baseTypography.headlineMedium.copy(fontFamily = customFontFamily),
        headlineSmall = baseTypography.headlineSmall.copy(fontFamily = customFontFamily),
        titleLarge = baseTypography.titleLarge.copy(fontFamily = customFontFamily),
        titleMedium = baseTypography.titleMedium.copy(fontFamily = customFontFamily),
        titleSmall = baseTypography.titleSmall.copy(fontFamily = customFontFamily),
        bodyLarge = baseTypography.bodyLarge.copy(fontFamily = customFontFamily),
        bodyMedium = baseTypography.bodyMedium.copy(fontFamily = customFontFamily),
        bodySmall = baseTypography.bodySmall.copy(fontFamily = customFontFamily),
        labelLarge = baseTypography.labelLarge.copy(fontFamily = customFontFamily),
        labelMedium = baseTypography.labelMedium.copy(fontFamily = customFontFamily),
        labelSmall = baseTypography.labelSmall.copy(fontFamily = customFontFamily)
    )
}

private fun getPresetColors(): List<Color> {
    return listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFF9C27B0), // Purple
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFFF44336), // Red
        Color(0xFF00BCD4), // Cyan
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFE91E63), // Pink
        Color(0xFF3F51B5), // Indigo
        Color(0xFF8BC34A), // Light Green
        Color(0xFFFFEB3B)  // Yellow
    )
}

private fun getColorSchemeColors(colorScheme: ColorScheme): List<Pair<String, Color>> {
    return listOf(
        "Primary" to colorScheme.primary,
        "Secondary" to colorScheme.secondary,
        "Tertiary" to colorScheme.tertiary,
        "Error" to colorScheme.error,
        "Background" to colorScheme.background,
        "Surface" to colorScheme.surface,
        "Outline" to colorScheme.outline,
        "On Primary" to colorScheme.onPrimary
    )
}

private fun getPresetColorSchemes(): List<Pair<String, ColorScheme>> {
    return listOf(
        "Default Light" to createLightColorScheme(),
        "Default Dark" to createDarkColorScheme(),
        "High Contrast Light" to createHighContrastLightColorScheme(),
        "High Contrast Dark" to createHighContrastDarkColorScheme(),
        "Blue" to createCustomColorScheme(Color(0xFF2196F3), false),
        "Purple" to createCustomColorScheme(Color(0xFF9C27B0), false),
        "Green" to createCustomColorScheme(Color(0xFF4CAF50), false),
        "Orange" to createCustomColorScheme(Color(0xFFFF9800), false)
    )
}

private fun getAvailableFontFamilies(): List<Pair<String, FontFamily?>> {
    return listOf(
        "Default" to null,
        "Sans Serif" to FontFamily.SansSerif,
        "Serif" to FontFamily.Serif,
        "Monospace" to FontFamily.Monospace,
        "Cursive" to FontFamily.Cursive
    )
}