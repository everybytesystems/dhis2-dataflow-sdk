package com.everybytesystems.dataflow.ui.patterns

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlin.math.*

/**
 * Data Visualization Patterns
 * Advanced patterns for data visualization and dashboard layouts
 */

// ============================================================================
// 📊 DASHBOARD PATTERNS
// ============================================================================

data class DashboardWidget(
    val id: String,
    val title: String,
    val type: WidgetType,
    val size: WidgetSize,
    val data: Any? = null,
    val config: Map<String, Any> = emptyMap(),
    val position: WidgetPosition = WidgetPosition(0, 0)
)

data class WidgetPosition(
    val row: Int,
    val column: Int
)

enum class WidgetType {
    CHART,
    METRIC,
    TABLE,
    MAP,
    TEXT,
    IMAGE,
    CUSTOM
}

enum class WidgetSize {
    SMALL,    // 1x1
    MEDIUM,   // 2x1
    LARGE,    // 2x2
    WIDE,     // 3x1
    TALL,     // 1x3
    EXTRA_LARGE // 3x2
}

@Composable
fun DashboardGrid(
    widgets: List<DashboardWidget>,
    onWidgetClick: (DashboardWidget) -> Unit,
    onWidgetMove: (DashboardWidget, WidgetPosition) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 4,
    isEditable: Boolean = false
) {
    val cellSize = 120.dp
    val spacing = 8.dp
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(spacing),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(widgets) { widget ->
            DashboardWidgetCard(
                widget = widget,
                onClick = { onWidgetClick(widget) },
                onMove = { newPosition -> onWidgetMove(widget, newPosition) },
                isEditable = isEditable,
                modifier = Modifier.size(
                    width = getWidgetWidth(widget.size, cellSize, spacing),
                    height = getWidgetHeight(widget.size, cellSize, spacing)
                )
            )
        }
    }
}

@Composable
private fun DashboardWidgetCard(
    widget: DashboardWidget,
    onClick: () -> Unit,
    onMove: (WidgetPosition) -> Unit,
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    var isDragging by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .clickable { onClick() }
            .pointerInput(isEditable) {
                if (isEditable) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false }
                    ) { _, _ ->
                        // Handle drag for repositioning
                    }
                }
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Widget header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = widget.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                if (isEditable) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "Drag to move",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Widget content
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (widget.type) {
                    WidgetType.CHART -> ChartWidgetContent(widget)
                    WidgetType.METRIC -> MetricWidgetContent(widget)
                    WidgetType.TABLE -> TableWidgetContent(widget)
                    WidgetType.MAP -> MapWidgetContent(widget)
                    WidgetType.TEXT -> TextWidgetContent(widget)
                    WidgetType.IMAGE -> ImageWidgetContent(widget)
                    WidgetType.CUSTOM -> CustomWidgetContent(widget)
                }
            }
        }
    }
}

// ============================================================================
// 📈 INTERACTIVE CHARTS
// ============================================================================

@Composable
fun InteractiveLineChart(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier,
    onPointClick: ((Pair<Float, Float>) -> Unit)? = null,
    showTooltip: Boolean = true,
    animateOnLoad: Boolean = true
) {
    var selectedPoint by remember { mutableStateOf<Pair<Float, Float>?>(null) }
    var animationProgress by remember { mutableStateOf(if (animateOnLoad) 0f else 1f) }
    
    LaunchedEffect(animateOnLoad) {
        if (animateOnLoad) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1000, easing = EaseOutCubic)
            ) { value, _ ->
                animationProgress = value
            }
        }
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Find nearest point to tap
                    val nearestPoint = findNearestPoint(data, offset, size)
                    nearestPoint?.let { point ->
                        selectedPoint = point
                        onPointClick?.invoke(point)
                    }
                }
            }
    ) {
        if (data.isEmpty()) return@Canvas
        
        val padding = 40.dp.toPx()
        val chartWidth = size.width - 2 * padding
        val chartHeight = size.height - 2 * padding
        
        val minX = data.minOf { it.first }
        val maxX = data.maxOf { it.first }
        val minY = data.minOf { it.second }
        val maxY = data.maxOf { it.second }
        
        val xRange = maxX - minX
        val yRange = maxY - minY
        
        // Draw animated line
        val path = Path()
        val animatedData = data.take((data.size * animationProgress).toInt())
        
        animatedData.forEachIndexed { index, point ->
            val x = padding + ((point.first - minX) / xRange) * chartWidth
            val y = padding + chartHeight - ((point.second - minY) / yRange) * chartHeight
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        // Draw line
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw points
        animatedData.forEach { point ->
            val x = padding + ((point.first - minX) / xRange) * chartWidth
            val y = padding + chartHeight - ((point.second - minY) / yRange) * chartHeight
            
            val isSelected = selectedPoint == point
            val pointRadius = if (isSelected) 8.dp.toPx() else 4.dp.toPx()
            
            drawCircle(
                color = if (isSelected) Color.Red else Color.Blue,
                radius = pointRadius,
                center = Offset(x, y)
            )
            
            if (isSelected) {
                drawCircle(
                    color = Color.White,
                    radius = pointRadius * 0.5f,
                    center = Offset(x, y)
                )
            }
        }
    }
    
    // Tooltip
    if (showTooltip && selectedPoint != null) {
        TooltipOverlay(
            point = selectedPoint!!,
            onDismiss = { selectedPoint = null }
        )
    }
}

@Composable
fun AnimatedBarChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    animateOnLoad: Boolean = true
) {
    var animationProgress by remember { mutableStateOf(if (animateOnLoad) 0f else 1f) }
    
    LaunchedEffect(animateOnLoad) {
        if (animateOnLoad) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1000, easing = EaseOutCubic)
            ) { value, _ ->
                animationProgress = value
            }
        }
    }
    
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        if (data.isEmpty()) return@Canvas
        
        val padding = 40.dp.toPx()
        val chartWidth = size.width - 2 * padding
        val chartHeight = size.height - 2 * padding
        
        val maxValue = data.maxOf { it.second }
        val barWidth = chartWidth / data.size * 0.8f
        val barSpacing = chartWidth / data.size * 0.2f
        
        data.forEachIndexed { index, (label, value) ->
            val barHeight = (value / maxValue) * chartHeight * animationProgress
            val x = padding + index * (barWidth + barSpacing)
            val y = padding + chartHeight - barHeight
            
            // Draw bar
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
            
            // Draw value label
            drawContext.canvas.nativeCanvas.apply {
                // Text drawing would be platform-specific
                // This is a simplified representation
            }
        }
    }
}

// ============================================================================
// 🎯 DATA EXPLORATION
// ============================================================================

@Composable
fun DataExplorer(
    data: List<Map<String, Any>>,
    onFilterChange: (Map<String, Any>) -> Unit,
    onSortChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilters by remember { mutableStateOf(mapOf<String, Any>()) }
    var sortColumn by remember { mutableStateOf<String?>(null) }
    var sortAscending by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf(ViewMode.TABLE) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View mode toggle
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ViewMode.values().forEach { mode ->
                        FilterChip(
                            onClick = { viewMode = mode },
                            label = { Text(mode.name) },
                            selected = viewMode == mode
                        )
                    }
                }
                
                // Data summary
                Text(
                    text = "${data.size} records",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Data view
        when (viewMode) {
            ViewMode.TABLE -> DataTable(
                data = data,
                onSort = { column, ascending ->
                    sortColumn = column
                    sortAscending = ascending
                    onSortChange(column, ascending)
                }
            )
            ViewMode.CARDS -> DataCards(data = data)
            ViewMode.CHART -> DataChart(data = data)
        }
    }
}

enum class ViewMode {
    TABLE, CARDS, CHART
}

@Composable
private fun DataTable(
    data: List<Map<String, Any>>,
    onSort: (String, Boolean) -> Unit
) {
    if (data.isEmpty()) return
    
    val columns = data.first().keys.toList()
    
    LazyColumn {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    columns.forEach { column ->
                        Text(
                            text = column,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSort(column, true) }
                        )
                    }
                }
            }
        }
        
        // Data rows
        items(data) { row ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    columns.forEach { column ->
                        Text(
                            text = row[column]?.toString() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DataCards(data: List<Map<String, Any>>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 200.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DataChart(data: List<Map<String, Any>>) {
    // Simplified chart representation
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Chart visualization of ${data.size} data points",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// ============================================================================
// 🎨 WIDGET CONTENT COMPONENTS
// ============================================================================

@Composable
private fun ChartWidgetContent(widget: DashboardWidget) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Simplified chart representation
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            // Draw a simple line chart
            val path = Path()
            val points = listOf(
                Offset(0f, size.height * 0.8f),
                Offset(size.width * 0.3f, size.height * 0.4f),
                Offset(size.width * 0.6f, size.height * 0.6f),
                Offset(size.width, size.height * 0.2f)
            )
            
            points.forEachIndexed { index, point ->
                if (index == 0) path.moveTo(point.x, point.y)
                else path.lineTo(point.x, point.y)
            }
            
            drawPath(
                path = path,
                color = Color.Blue,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun MetricWidgetContent(widget: DashboardWidget) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "1,234",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Total Users",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "+12%",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Green
            )
        }
    }
}

@Composable
private fun TableWidgetContent(widget: DashboardWidget) {
    Column {
        repeat(3) { index ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Item ${index + 1}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${(index + 1) * 100}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            if (index < 2) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun MapWidgetContent(widget: DashboardWidget) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Blue.copy(alpha = 0.1f),
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = "Map",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun TextWidgetContent(widget: DashboardWidget) {
    Text(
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        style = MaterialTheme.typography.bodySmall,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
private fun ImageWidgetContent(widget: DashboardWidget) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Image",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun CustomWidgetContent(widget: DashboardWidget) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Custom Widget",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// ============================================================================
// 🔧 UTILITY COMPONENTS
// ============================================================================

@Composable
private fun TooltipOverlay(
    point: Pair<Float, Float>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "X: ${point.first}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Y: ${point.second}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun getWidgetWidth(size: WidgetSize, cellSize: Dp, spacing: Dp): Dp {
    return when (size) {
        WidgetSize.SMALL, WidgetSize.TALL -> cellSize
        WidgetSize.MEDIUM, WidgetSize.LARGE -> cellSize * 2 + spacing
        WidgetSize.WIDE, WidgetSize.EXTRA_LARGE -> cellSize * 3 + spacing * 2
    }
}

private fun getWidgetHeight(size: WidgetSize, cellSize: Dp, spacing: Dp): Dp {
    return when (size) {
        WidgetSize.SMALL, WidgetSize.MEDIUM, WidgetSize.WIDE -> cellSize
        WidgetSize.LARGE, WidgetSize.EXTRA_LARGE -> cellSize * 2 + spacing
        WidgetSize.TALL -> cellSize * 3 + spacing * 2
    }
}

private fun findNearestPoint(
    data: List<Pair<Float, Float>>,
    tapOffset: Offset,
    canvasSize: Size
): Pair<Float, Float>? {
    if (data.isEmpty()) return null
    
    val padding = 40f
    val chartWidth = canvasSize.width - 2 * padding
    val chartHeight = canvasSize.height - 2 * padding
    
    val minX = data.minOf { it.first }
    val maxX = data.maxOf { it.first }
    val minY = data.minOf { it.second }
    val maxY = data.maxOf { it.second }
    
    val xRange = maxX - minX
    val yRange = maxY - minY
    
    var nearestPoint: Pair<Float, Float>? = null
    var minDistance = Float.MAX_VALUE
    
    data.forEach { point ->
        val x = padding + ((point.first - minX) / xRange) * chartWidth
        val y = padding + chartHeight - ((point.second - minY) / yRange) * chartHeight
        
        val distance = sqrt((tapOffset.x - x).pow(2) + (tapOffset.y - y).pow(2))
        
        if (distance < minDistance && distance < 50f) { // 50px hit radius
            minDistance = distance
            nearestPoint = point
        }
    }
    
    return nearestPoint
}