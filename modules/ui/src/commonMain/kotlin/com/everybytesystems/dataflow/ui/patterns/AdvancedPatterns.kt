package com.everybytesystems.dataflow.ui.patterns

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlin.math.*

/**
 * Advanced UI Patterns
 * Sophisticated UI patterns and interactions
 */

// ============================================================================
// 🎭 ADAPTIVE LAYOUTS
// ============================================================================

enum class WindowSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSizeClass.COMPACT
        configuration.screenWidthDp < 840 -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

@Composable
fun AdaptiveLayout(
    compactContent: @Composable () -> Unit,
    mediumContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = rememberWindowSizeClass()
    
    Box(modifier = modifier) {
        when (windowSizeClass) {
            WindowSizeClass.COMPACT -> compactContent()
            WindowSizeClass.MEDIUM -> mediumContent()
            WindowSizeClass.EXPANDED -> expandedContent()
        }
    }
}

@Composable
fun ResponsiveGrid(
    items: List<Any>,
    itemContent: @Composable (Any) -> Unit,
    modifier: Modifier = Modifier,
    minItemWidth: Dp = 200.dp,
    spacing: Dp = 8.dp
) {
    val windowSizeClass = rememberWindowSizeClass()
    
    val columns = when (windowSizeClass) {
        WindowSizeClass.COMPACT -> 1
        WindowSizeClass.MEDIUM -> 2
        WindowSizeClass.EXPANDED -> 3
    }
    
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalItemSpacing = spacing
    ) {
        items(items) { item ->
            itemContent(item)
        }
    }
}

// ============================================================================
// 🎨 ADVANCED ANIMATIONS
// ============================================================================

@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    animationSpec: AnimationSpec<Float> = tween(1000, easing = EaseOutCubic),
    style: TextStyle = MaterialTheme.typography.headlineLarge
) {
    var animatedValue by remember { mutableStateOf(0f) }
    
    LaunchedEffect(targetValue) {
        animate(
            initialValue = animatedValue,
            targetValue = targetValue.toFloat(),
            animationSpec = animationSpec
        ) { value, _ ->
            animatedValue = value
        }
    }
    
    Text(
        text = animatedValue.toInt().toString(),
        style = style,
        modifier = modifier
    )
}

@Composable
fun PulsingEffect(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    duration: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier.scale(scale)
    ) {
        content()
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    highlightColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
) {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val brush = Brush.linearGradient(
        colors = listOf(
            color,
            highlightColor,
            color
        ),
        start = Offset(shimmerTranslateAnim - 200f, shimmerTranslateAnim - 200f),
        end = Offset(shimmerTranslateAnim, shimmerTranslateAnim)
    )
    
    Box(
        modifier = modifier
            .background(brush)
            .fillMaxSize()
    )
}

// ============================================================================
// 🎯 INTERACTIVE COMPONENTS
// ============================================================================

@Composable
fun SwipeToReveal(
    backgroundContent: @Composable () -> Unit,
    foregroundContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onSwipeComplete: () -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    val maxSwipeDistance = 200.dp
    val density = LocalDensity.current
    val maxSwipeDistancePx = with(density) { maxSwipeDistance.toPx() }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > maxSwipeDistancePx * 0.5f) {
                            onSwipeComplete()
                        }
                        offsetX = 0f
                    }
                ) { _, dragAmount ->
                    offsetX = (offsetX + dragAmount).coerceIn(-maxSwipeDistancePx, maxSwipeDistancePx)
                }
            }
    ) {
        // Background content
        backgroundContent()
        
        // Foreground content
        Box(
            modifier = Modifier.offset(x = with(density) { offsetX.toDp() })
        ) {
            foregroundContent()
        }
    }
}

@Composable
fun DragAndDropContainer(
    items: List<Any>,
    onItemMoved: (Int, Int) -> Unit,
    itemContent: @Composable (Any, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var draggedItem by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    
    LazyColumn(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Determine which item is being dragged
                        // This is simplified - real implementation would need proper hit testing
                    },
                    onDragEnd = {
                        draggedItem?.let { fromIndex ->
                            // Calculate drop position and call onItemMoved
                            // This is simplified
                        }
                        draggedItem = null
                        dragOffset = Offset.Zero
                    }
                ) { _, dragAmount ->
                    dragOffset += dragAmount
                }
            }
    ) {
        itemsIndexed(items) { index, item ->
            val isDragged = draggedItem == index
            
            Box(
                modifier = if (isDragged) {
                    Modifier.offset(
                        x = with(LocalDensity.current) { dragOffset.x.toDp() },
                        y = with(LocalDensity.current) { dragOffset.y.toDp() }
                    )
                } else {
                    Modifier
                }
            ) {
                itemContent(item, isDragged)
            }
        }
    }
}

// ============================================================================
// 🎪 COMPLEX LAYOUTS
// ============================================================================

@Composable
fun MasonryLayout(
    items: List<Any>,
    columns: Int = 2,
    itemContent: @Composable (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(items) { item ->
            itemContent(item)
        }
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun StickyHeader(
    header: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Placeholder for header height
            Spacer(modifier = Modifier.height(56.dp))
            
            content()
        }
        
        // Sticky header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = if (scrollState.value > 0) 4.dp else 0.dp
        ) {
            header()
        }
    }
}

// ============================================================================
// 🎨 VISUAL EFFECTS
// ============================================================================

@Composable
fun GlassEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun NeuomorphicCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    val lightShadowColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val darkShadowColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .drawBehind {
                val elevationPx = elevation.toPx()
                
                // Light shadow (top-left)
                drawRoundRect(
                    color = lightShadowColor,
                    topLeft = Offset(-elevationPx, -elevationPx),
                    size = Size(size.width + elevationPx, size.height + elevationPx),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
                
                // Dark shadow (bottom-right)
                drawRoundRect(
                    color = darkShadowColor,
                    topLeft = Offset(elevationPx, elevationPx),
                    size = Size(size.width - elevationPx, size.height - elevationPx),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun ParallaxEffect(
    backgroundContent: @Composable () -> Unit,
    foregroundContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    parallaxRatio: Float = 0.5f
) {
    val scrollState = rememberScrollState()
    
    Box(modifier = modifier.fillMaxSize()) {
        // Background with parallax
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (scrollState.value * parallaxRatio).dp)
        ) {
            backgroundContent()
        }
        
        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            foregroundContent()
        }
    }
}

// ============================================================================
// 🎯 GESTURE COMPONENTS
// ============================================================================

@Composable
fun PinchToZoom(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    minScale: Float = 0.5f,
    maxScale: Float = 3f
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(minScale, maxScale)
                    offset += pan
                }
            }
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .offset(
                    x = with(LocalDensity.current) { offset.x.toDp() },
                    y = with(LocalDensity.current) { offset.y.toDp() }
                )
        ) {
            content()
        }
    }
}

@Composable
fun SwipeGestures(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // Determine swipe direction based on drag distance
                        // This is simplified - real implementation would track velocity
                    }
                ) { _, _ ->
                    // Track drag for swipe detection
                }
            }
    ) {
        content()
    }
}

// ============================================================================
// 🎪 MODAL COMPONENTS
// ============================================================================

@Composable
fun BottomSheetModal(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300, easing = EaseOutCubic)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300, easing = EaseInCubic)
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) { /* Prevent dismiss on content click */ },
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                shadowElevation = 8.dp
            ) {
                Column {
                    // Handle bar
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                RoundedCornerShape(2.dp)
                            )
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp)
                    )
                    
                    content()
                }
            }
        }
    }
}

@Composable
fun SidePanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    side: PanelSide = PanelSide.RIGHT,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = when (side) {
            PanelSide.LEFT -> slideInHorizontally(initialOffsetX = { -it })
            PanelSide.RIGHT -> slideInHorizontally(initialOffsetX = { it })
        } + fadeIn(),
        exit = when (side) {
            PanelSide.LEFT -> slideOutHorizontally(targetOffsetX = { -it })
            PanelSide.RIGHT -> slideOutHorizontally(targetOffsetX = { it })
        } + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() },
            contentAlignment = when (side) {
                PanelSide.LEFT -> Alignment.CenterStart
                PanelSide.RIGHT -> Alignment.CenterEnd
            }
        ) {
            Surface(
                modifier = modifier
                    .fillMaxHeight()
                    .width(320.dp)
                    .clickable(enabled = false) { /* Prevent dismiss on content click */ },
                shadowElevation = 8.dp
            ) {
                content()
            }
        }
    }
}

enum class PanelSide {
    LEFT, RIGHT
}

// ============================================================================
// 🎨 LOADING PATTERNS
// ============================================================================

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape
            )
    ) {
        ShimmerEffect()
    }
}

@Composable
fun WaveLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 4f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 100),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(animatedHeight.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun PulseLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Dp = 40.dp
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * scale)
                .background(
                    color.copy(alpha = 1f - scale),
                    CircleShape
                )
        )
    }
}

// ============================================================================
// 🎯 UTILITY FUNCTIONS
// ============================================================================

@Composable
fun rememberDebouncedCallback(
    callback: () -> Unit,
    delayMillis: Long = 300L
): () -> Unit {
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    
    return remember {
        {
            debounceJob?.cancel()
            debounceJob = scope.launch {
                delay(delayMillis)
                callback()
            }
        }
    }
}

@Composable
fun <T> rememberPrevious(current: T): T? {
    val ref = remember { mutableStateOf<T?>(null) }
    
    LaunchedEffect(current) {
        ref.value = current
    }
    
    return ref.value
}