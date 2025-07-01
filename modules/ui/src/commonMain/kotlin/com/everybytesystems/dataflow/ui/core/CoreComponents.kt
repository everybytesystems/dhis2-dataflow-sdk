package com.everybytesystems.dataflow.ui.core

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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay

/**
 * Core UI Components
 * Unified & Modern UI Components for all platforms
 */

// ============================================================================
// 🎯 CORE WIDGETS
// ============================================================================

/**
 * Enhanced Button with loading state and icons
 */
@Composable
fun DataFlowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit
) {
    val colors = when (variant) {
        ButtonVariant.PRIMARY -> ButtonDefaults.buttonColors()
        ButtonVariant.SECONDARY -> ButtonDefaults.outlinedButtonColors()
        ButtonVariant.TERTIARY -> ButtonDefaults.textButtonColors()
        ButtonVariant.DESTRUCTIVE -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    }
    
    val contentPadding = when (size) {
        ButtonSize.SMALL -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ButtonSize.MEDIUM -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ButtonSize.LARGE -> PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    }
    
    val buttonContent: @Composable RowScope.() -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        content()
        
        trailingIcon?.let { icon ->
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
    
    when (variant) {
        ButtonVariant.PRIMARY, ButtonVariant.DESTRUCTIVE -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !loading,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }
        ButtonVariant.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !loading,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }
        ButtonVariant.TERTIARY -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled && !loading,
                colors = colors,
                contentPadding = contentPadding,
                content = buttonContent
            )
        }
    }
}

enum class ButtonVariant {
    PRIMARY, SECONDARY, TERTIARY, DESTRUCTIVE
}

enum class ButtonSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Enhanced Card with hover effects and actions
 */
@Composable
fun DataFlowCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable { onClick() }
            } else Modifier
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        shape = shape,
        colors = colors,
        border = border,
        content = content
    )
}

/**
 * Enhanced Dialog with animations
 */
@Composable
fun DataFlowDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: ImageVector? = null,
    content: @Composable (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title?.let {
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    icon?.let { iconVector ->
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        },
        text = content,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Snackbar with actions
 */
@Composable
fun DataFlowSnackbar(
    message: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    type: SnackbarType = SnackbarType.DEFAULT
) {
    val backgroundColor = when (type) {
        SnackbarType.DEFAULT -> MaterialTheme.colorScheme.inverseSurface
        SnackbarType.SUCCESS -> Color(0xFF4CAF50)
        SnackbarType.WARNING -> Color(0xFFFF9800)
        SnackbarType.ERROR -> MaterialTheme.colorScheme.error
    }
    
    Snackbar(
        modifier = Modifier.padding(16.dp),
        action = if (actionLabel != null && onActionClick != null) {
            {
                TextButton(onClick = onActionClick) {
                    Text(actionLabel)
                }
            }
        } else null,
        containerColor = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(message)
    }
}

enum class SnackbarType {
    DEFAULT, SUCCESS, WARNING, ERROR
}

/**
 * Bottom Sheet component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFlowBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
    content: @Composable ColumnScope.() -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = bottomSheetState,
        dragHandle = dragHandle,
        windowInsets = windowInsets,
        content = content
    )
}

/**
 * Tab component with enhanced styling
 */
@Composable
fun DataFlowTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = { tabPositions ->
        if (selectedTabIndex < tabPositions.size) {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                color = MaterialTheme.colorScheme.primary
            )
        }
    },
    divider: @Composable () -> Unit = {},
    tabs: @Composable () -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = indicator,
        divider = divider,
        tabs = tabs
    )
}

/**
 * Enhanced Tab with icons and badges
 */
@Composable
fun DataFlowTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    icon: ImageVector? = null,
    badge: String? = null
) {
    Tab(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = text?.let {
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon?.let { iconVector ->
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null
                        )
                    }
                    Text(it)
                    badge?.let { badgeText ->
                        Badge {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    )
}

/**
 * Enhanced Toolbar/TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFlowTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigation"
                    )
                }
            }
        },
        actions = actions,
        colors = colors,
        scrollBehavior = scrollBehavior
    )
}

/**
 * Grid layout component
 */
@Composable
fun DataFlowGrid(
    columns: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    content: LazyGridScope.() -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

/**
 * Popover component
 */
@Composable
fun DataFlowPopover(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
        content = { content() }
    )
}

/**
 * Stepper/Progress Tracker component
 */
@Composable
fun DataFlowStepper(
    steps: List<StepData>,
    currentStep: Int,
    modifier: Modifier = Modifier,
    orientation: StepperOrientation = StepperOrientation.HORIZONTAL
) {
    when (orientation) {
        StepperOrientation.HORIZONTAL -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, step ->
                    StepItem(
                        step = step,
                        isActive = index == currentStep,
                        isCompleted = index < currentStep,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (index < steps.size - 1) {
                        Divider(
                            modifier = Modifier
                                .weight(0.5f)
                                .height(2.dp),
                            color = if (index < currentStep) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            }
                        )
                    }
                }
            }
        }
        StepperOrientation.VERTICAL -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                steps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StepIndicator(
                            stepNumber = index + 1,
                            isActive = index == currentStep,
                            isCompleted = index < currentStep
                        )
                        
                        Column {
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (index == currentStep) FontWeight.Bold else FontWeight.Normal
                            )
                            if (step.description.isNotEmpty()) {
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    step: StepData,
    isActive: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StepIndicator(
            stepNumber = step.number,
            isActive = isActive,
            isCompleted = isCompleted
        )
        
        Text(
            text = step.title,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun StepIndicator(
    stepNumber: Int,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val backgroundColor = when {
        isCompleted -> MaterialTheme.colorScheme.primary
        isActive -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.outline
    }
    
    val contentColor = when {
        isCompleted -> MaterialTheme.colorScheme.onPrimary
        isActive -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completed",
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = stepNumber.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

data class StepData(
    val number: Int,
    val title: String,
    val description: String = ""
)

enum class StepperOrientation {
    HORIZONTAL, VERTICAL
}

/**
 * Enhanced Loading Indicators
 */
@Composable
fun DataFlowLoader(
    modifier: Modifier = Modifier,
    type: LoaderType = LoaderType.CIRCULAR,
    size: Dp = 40.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    when (type) {
        LoaderType.CIRCULAR -> {
            CircularProgressIndicator(
                modifier = modifier.size(size),
                color = color,
                strokeWidth = 4.dp
            )
        }
        LoaderType.LINEAR -> {
            LinearProgressIndicator(
                modifier = modifier.fillMaxWidth(),
                color = color
            )
        }
        LoaderType.DOTS -> {
            DotsLoader(
                modifier = modifier,
                color = color,
                size = size
            )
        }
        LoaderType.PULSE -> {
            PulseLoader(
                modifier = modifier,
                color = color,
                size = size
            )
        }
    }
}

@Composable
private fun DotsLoader(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            
            Box(
                modifier = Modifier
                    .size(size / 4)
                    .background(color.copy(alpha = alpha), CircleShape)
            )
        }
    }
}

@Composable
private fun PulseLoader(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

enum class LoaderType {
    CIRCULAR, LINEAR, DOTS, PULSE
}

/**
 * Enhanced Progress Indicators
 */
@Composable
fun DataFlowProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    type: ProgressType = ProgressType.LINEAR,
    showPercentage: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp = 8.dp
) {
    when (type) {
        ProgressType.LINEAR -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(strokeWidth)
                        .clip(RoundedCornerShape(strokeWidth / 2)),
                    color = color,
                    trackColor = backgroundColor
                )
            }
        }
        ProgressType.CIRCULAR -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(60.dp),
                    color = color,
                    strokeWidth = strokeWidth,
                    trackColor = backgroundColor
                )
                
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        ProgressType.RING -> {
            Box(
                modifier = modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = strokeWidth.toPx()
                    val radius = (size.minDimension - strokeWidthPx) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Background circle
                    drawCircle(
                        color = backgroundColor,
                        radius = radius,
                        center = center,
                        style = Stroke(strokeWidthPx)
                    )
                    
                    // Progress arc
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(strokeWidthPx, cap = StrokeCap.Round),
                        topLeft = Offset(
                            center.x - radius,
                            center.y - radius
                        ),
                        size = Size(radius * 2, radius * 2)
                    )
                }
                
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

enum class ProgressType {
    LINEAR, CIRCULAR, RING
}

/**
 * Skeleton Loader for content placeholders
 */
@Composable
fun DataFlowSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .background(color.copy(alpha = alpha), shape)
    )
}