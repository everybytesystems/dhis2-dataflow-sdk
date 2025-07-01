package com.everybytesystems.dataflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * DataFlow UI Design System
 * Modern, accessible, and beautiful theme for data-driven applications
 */

// ============================================================================
// 🎨 COLOR PALETTE
// ============================================================================

object DataFlowColors {
    // Primary Colors - Modern Blue Palette
    val Blue50 = Color(0xFFE3F2FD)
    val Blue100 = Color(0xFFBBDEFB)
    val Blue200 = Color(0xFF90CAF9)
    val Blue300 = Color(0xFF64B5F6)
    val Blue400 = Color(0xFF42A5F5)
    val Blue500 = Color(0xFF2196F3) // Primary
    val Blue600 = Color(0xFF1E88E5)
    val Blue700 = Color(0xFF1976D2)
    val Blue800 = Color(0xFF1565C0)
    val Blue900 = Color(0xFF0D47A1)
    
    // Secondary Colors - Vibrant Green
    val Green50 = Color(0xFFE8F5E8)
    val Green100 = Color(0xFFC8E6C9)
    val Green200 = Color(0xFFA5D6A7)
    val Green300 = Color(0xFF81C784)
    val Green400 = Color(0xFF66BB6A)
    val Green500 = Color(0xFF4CAF50) // Secondary
    val Green600 = Color(0xFF43A047)
    val Green700 = Color(0xFF388E3C)
    val Green800 = Color(0xFF2E7D32)
    val Green900 = Color(0xFF1B5E20)
    
    // Accent Colors
    val Purple500 = Color(0xFF9C27B0)
    val Orange500 = Color(0xFFFF9800)
    val Teal500 = Color(0xFF009688)
    val Indigo500 = Color(0xFF3F51B5)
    val Pink500 = Color(0xFFE91E63)
    val Cyan500 = Color(0xFF00BCD4)
    
    // Status Colors
    val Success = Green500
    val Warning = Color(0xFFFF9800)
    val Error = Color(0xFFE53935)
    val Info = Blue500
    
    // Neutral Colors
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFF5F5F5)
    val Gray200 = Color(0xFFEEEEEE)
    val Gray300 = Color(0xFFE0E0E0)
    val Gray400 = Color(0xFFBDBDBD)
    val Gray500 = Color(0xFF9E9E9E)
    val Gray600 = Color(0xFF757575)
    val Gray700 = Color(0xFF616161)
    val Gray800 = Color(0xFF424242)
    val Gray900 = Color(0xFF212121)
    
    // Chart Colors - Vibrant and Accessible
    val chartColors = listOf(
        Blue500, Green500, Orange500, Purple500,
        Teal500, Pink500, Indigo500, Cyan500,
        Color(0xFFFF5722), Color(0xFF795548),
        Color(0xFF607D8B), Color(0xFF8BC34A)
    )
}

// ============================================================================
// 🌈 COLOR SCHEMES
// ============================================================================

private val LightColorScheme = lightColorScheme(
    primary = DataFlowColors.Blue600,
    onPrimary = Color.White,
    primaryContainer = DataFlowColors.Blue100,
    onPrimaryContainer = DataFlowColors.Blue900,
    
    secondary = DataFlowColors.Green600,
    onSecondary = Color.White,
    secondaryContainer = DataFlowColors.Green100,
    onSecondaryContainer = DataFlowColors.Green900,
    
    tertiary = DataFlowColors.Orange500,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFFE65100),
    
    error = DataFlowColors.Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    
    background = Color.White,
    onBackground = DataFlowColors.Gray900,
    surface = Color.White,
    onSurface = DataFlowColors.Gray900,
    surfaceVariant = DataFlowColors.Gray100,
    onSurfaceVariant = DataFlowColors.Gray700,
    
    outline = DataFlowColors.Gray400,
    outlineVariant = DataFlowColors.Gray200,
    scrim = Color.Black.copy(alpha = 0.32f),
    
    inverseSurface = DataFlowColors.Gray800,
    inverseOnSurface = DataFlowColors.Gray100,
    inversePrimary = DataFlowColors.Blue300
)

private val DarkColorScheme = darkColorScheme(
    primary = DataFlowColors.Blue400,
    onPrimary = DataFlowColors.Blue900,
    primaryContainer = DataFlowColors.Blue800,
    onPrimaryContainer = DataFlowColors.Blue100,
    
    secondary = DataFlowColors.Green400,
    onSecondary = DataFlowColors.Green900,
    secondaryContainer = DataFlowColors.Green800,
    onSecondaryContainer = DataFlowColors.Green100,
    
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFFE65100),
    tertiaryContainer = Color(0xFFFF8F00),
    onTertiaryContainer = Color(0xFFFFE0B2),
    
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFEBEE),
    
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
    
    outline = Color(0xFF616161),
    outlineVariant = Color(0xFF424242),
    scrim = Color.Black.copy(alpha = 0.32f),
    
    inverseSurface = DataFlowColors.Gray100,
    inverseOnSurface = DataFlowColors.Gray800,
    inversePrimary = DataFlowColors.Blue600
)

// ============================================================================
// 📝 TYPOGRAPHY
// ============================================================================

val DataFlowTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

// ============================================================================
// 🎨 THEME COMPOSABLE
// ============================================================================

/**
 * DataFlow UI Theme
 * Provides the complete design system for data-driven applications
 */
@Composable
fun DataFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep false for consistent branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DataFlowTypography,
        content = content
    )
}

// ============================================================================
// 🔧 THEME ACCESSORS
// ============================================================================

/**
 * Access current DataFlow colors
 */
object DataFlowThemeColors {
    val current: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}

/**
 * Access current DataFlow typography
 */
object DataFlowThemeTypography {
    val current: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography
}

/**
 * Get chart colors for data visualization
 */
@Composable
fun getChartColors(): List<Color> = DataFlowColors.chartColors

/**
 * Get status color based on type
 */
@Composable
fun getStatusColor(status: String): Color = when (status.lowercase()) {
    "success", "complete", "active", "online" -> DataFlowColors.Success
    "warning", "pending", "in progress" -> DataFlowColors.Warning
    "error", "failed", "offline", "inactive" -> DataFlowColors.Error
    "info", "draft", "new" -> DataFlowColors.Info
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}