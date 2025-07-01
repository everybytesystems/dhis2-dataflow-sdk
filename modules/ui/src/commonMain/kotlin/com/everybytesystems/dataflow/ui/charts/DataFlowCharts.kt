package com.everybytesystems.dataflow.ui.charts

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
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.everybytesystems.dataflow.ui.components.*
import com.everybytesystems.dataflow.ui.theme.DataFlowColors
import com.everybytesystems.dataflow.ui.theme.getChartColors
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * DataFlow Chart Components
 * Beautiful, interactive charts for data visualization
 */

// ============================================================================
// 📊 CHART DATA MODELS
// ============================================================================

/**
 * Chart data point
 */
data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val label: String = "",
    val value: String = "",
    val color: Color? = null,
    val metadata: Map<String, Any> = emptyMap()
)

/**
 * Chart series for multi-series charts
 */
data class ChartSeries(
    val name: String,
    val data: List<ChartDataPoint>,
    val color: Color,
    val visible: Boolean = true,
    val lineWidth: Float = 3f,
    val pointSize: Float = 6f
)

/**
 * Chart configuration
 */
data class ChartConfig(
    val title: String = "",
    val subtitle: String = "",
    val showLegend: Boolean = true,
    val showGrid: Boolean = true,
    val animated: Boolean = true,
    val interactive: Boolean = true,
    val colors: List<Color> = emptyList()
)

// ============================================================================
// 📈 LINE CHART
// ============================================================================

/**
 * Modern animated line chart
 */
@Composable
fun DataFlowLineChart(
    series: List<ChartSeries>,
    config: ChartConfig = ChartConfig(),
    modifier: Modifier = Modifier,
    onDataPointClick: ((ChartDataPoint, ChartSeries) -> Unit)? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var selectedPoint by remember { mutableStateOf<Pair<ChartDataPoint, ChartSeries>?>(null) }
    
    // Simulate loading
    LaunchedEffect(series) {
        isLoading = true
        delay(500)
        isLoading = false
    }
    
    DataFlowChartContainer(
        title = config.title,
        subtitle = config.subtitle,
        isLoading = isLoading,
        modifier = modifier,
        actions = {
            IconButton(onClick = { /* Export */ }) {
                Icon(Icons.Default.Download, contentDescription = "Export")
            }
            IconButton(onClick = { /* Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    ) {
        if (series.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                LineChartCanvas(
                    series = series,
                    config = config,
                    onDataPointClick = { point, seriesData ->
                        selectedPoint = point to seriesData
                        onDataPointClick?.invoke(point, seriesData)
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Legend
                if (config.showLegend) {
                    ChartLegend(
                        series = series,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }
                
                // Tooltip
                selectedPoint?.let { (point, seriesData) ->
                    ChartTooltip(
                        point = point,
                        series = seriesData,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
            }
        } else {
            DataFlowErrorState(
                message = "No data available",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun LineChartCanvas(
    series: List<ChartSeries>,
    config: ChartConfig,
    onDataPointClick: (ChartDataPoint, ChartSeries) -> Unit,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (config.animated) 1f else 1f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val padding = 60.dp.toPx()
        val chartWidth = size.width - (padding * 2)
        val chartHeight = size.height - (padding * 2)
        
        if (series.isEmpty()) return@Canvas
        
        // Calculate bounds
        val allPoints = series.flatMap { it.data }
        if (allPoints.isEmpty()) return@Canvas
        
        val minX = allPoints.minOfOrNull { it.x } ?: 0f
        val maxX = allPoints.maxOfOrNull { it.x } ?: 1f
        val minY = allPoints.minOfOrNull { it.y } ?: 0f
        val maxY = allPoints.maxOfOrNull { it.y } ?: 1f
        
        val xRange = maxX - minX
        val yRange = maxY - minY
        
        // Draw grid
        if (config.showGrid) {
            drawGrid(
                bounds = Rect(padding, padding, size.width - padding, size.height - padding),
                color = Color.Gray.copy(alpha = 0.2f)
            )
        }
        
        // Draw series
        series.forEachIndexed { seriesIndex, chartSeries ->
            if (!chartSeries.visible || chartSeries.data.isEmpty()) return@forEachIndexed
            
            val screenPoints = chartSeries.data.map { point ->
                Offset(
                    x = padding + ((point.x - minX) / xRange) * chartWidth,
                    y = padding + chartHeight - ((point.y - minY) / yRange) * chartHeight * animationProgress
                )
            }
            
            // Draw line
            if (screenPoints.size > 1) {
                val path = Path().apply {
                    moveTo(screenPoints.first().x, screenPoints.first().y)
                    screenPoints.drop(1).forEach { point ->
                        lineTo(point.x, point.y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = chartSeries.color,
                    style = Stroke(
                        width = chartSeries.lineWidth.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            
            // Draw points
            screenPoints.forEachIndexed { pointIndex, screenPoint ->
                drawCircle(
                    color = chartSeries.color,
                    radius = chartSeries.pointSize.dp.toPx(),
                    center = screenPoint
                )
                
                // Draw point border
                drawCircle(
                    color = Color.White,
                    radius = chartSeries.pointSize.dp.toPx(),
                    center = screenPoint,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

// ============================================================================
// 📊 BAR CHART
// ============================================================================

/**
 * Modern animated bar chart
 */
@Composable
fun DataFlowBarChart(
    data: List<ChartDataPoint>,
    config: ChartConfig = ChartConfig(),
    modifier: Modifier = Modifier,
    horizontal: Boolean = false,
    grouped: Boolean = false,
    onBarClick: ((ChartDataPoint) -> Unit)? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(data) {
        isLoading = true
        delay(300)
        isLoading = false
    }
    
    DataFlowChartContainer(
        title = config.title,
        subtitle = config.subtitle,
        isLoading = isLoading,
        modifier = modifier,
        actions = {
            IconButton(onClick = { /* Toggle orientation */ }) {
                Icon(
                    if (horizontal) Icons.Default.ViewColumn else Icons.Default.ViewStream,
                    contentDescription = "Toggle orientation"
                )
            }
        }
    ) {
        if (data.isNotEmpty()) {
            if (horizontal) {
                HorizontalBarChart(
                    data = data,
                    config = config,
                    onBarClick = onBarClick
                )
            } else {
                VerticalBarChart(
                    data = data,
                    config = config,
                    onBarClick = onBarClick
                )
            }
        } else {
            DataFlowErrorState(message = "No data available")
        }
    }
}

@Composable
private fun HorizontalBarChart(
    data: List<ChartDataPoint>,
    config: ChartConfig,
    onBarClick: ((ChartDataPoint) -> Unit)?
) {
    val maxValue = data.maxOfOrNull { it.y } ?: 1f
    val chartColors = getChartColors()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(data.size) { index ->
            val item = data[index]
            val animatedProgress by animateFloatAsState(
                targetValue = item.y / maxValue,
                animationSpec = tween(
                    durationMillis = 1000 + (index * 100),
                    easing = FastOutSlowInEasing
                )
            )
            
            HorizontalBarItem(
                dataPoint = item,
                progress = animatedProgress,
                color = item.color ?: chartColors[index % chartColors.size],
                onClick = { onBarClick?.invoke(item) }
            )
        }
    }
}

@Composable
private fun HorizontalBarItem(
    dataPoint: ChartDataPoint,
    progress: Float,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label
            Text(
                text = dataPoint.label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(120.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Progress bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
            ) {
                // Background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = color.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
                
                // Progress
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(color, color.copy(alpha = 0.8f))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
                
                // Value text overlay
                Text(
                    text = dataPoint.value.ifEmpty { dataPoint.y.toString() },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Value
            Text(
                text = dataPoint.value.ifEmpty { dataPoint.y.toString() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(60.dp),
                textAlign = TextAlign.End,
                color = color
            )
        }
    }
}

@Composable
private fun VerticalBarChart(
    data: List<ChartDataPoint>,
    config: ChartConfig,
    onBarClick: ((ChartDataPoint) -> Unit)?
) {
    val maxValue = data.maxOfOrNull { it.y } ?: 1f
    val chartColors = getChartColors()
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Chart area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEachIndexed { index, item ->
                val animatedHeight by animateFloatAsState(
                    targetValue = item.y / maxValue,
                    animationSpec = tween(
                        durationMillis = 1000 + (index * 100),
                        easing = FastOutSlowInEasing
                    )
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Value label
                    Text(
                        text = item.value.ifEmpty { item.y.toString() },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(animatedHeight)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        item.color ?: chartColors[index % chartColors.size],
                                        (item.color ?: chartColors[index % chartColors.size]).copy(alpha = 0.7f)
                                    )
                                ),
                                shape = RoundedCornerShape(
                                    topStart = 8.dp,
                                    topEnd = 8.dp
                                )
                            )
                            .clickable { onBarClick?.invoke(item) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            data.forEach { item ->
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============================================================================
// 🥧 PIE CHART
// ============================================================================

/**
 * Modern animated pie chart
 */
@Composable
fun DataFlowPieChart(
    data: List<ChartDataPoint>,
    config: ChartConfig = ChartConfig(),
    modifier: Modifier = Modifier,
    donutMode: Boolean = true,
    onSliceClick: ((ChartDataPoint) -> Unit)? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var selectedSlice by remember { mutableStateOf<ChartDataPoint?>(null) }
    
    LaunchedEffect(data) {
        isLoading = true
        delay(400)
        isLoading = false
    }
    
    DataFlowChartContainer(
        title = config.title,
        subtitle = config.subtitle,
        isLoading = isLoading,
        modifier = modifier
    ) {
        if (data.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Pie chart
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    PieChartCanvas(
                        data = data,
                        config = config,
                        donutMode = donutMode,
                        selectedSlice = selectedSlice,
                        onSliceClick = { slice ->
                            selectedSlice = if (selectedSlice == slice) null else slice
                            onSliceClick?.invoke(slice)
                        }
                    )
                }
                
                // Legend
                if (config.showLegend) {
                    PieChartLegend(
                        data = data,
                        config = config,
                        selectedSlice = selectedSlice,
                        onSliceClick = { slice ->
                            selectedSlice = if (selectedSlice == slice) null else slice
                            onSliceClick?.invoke(slice)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            DataFlowErrorState(message = "No data available")
        }
    }
}

@Composable
private fun PieChartCanvas(
    data: List<ChartDataPoint>,
    config: ChartConfig,
    donutMode: Boolean,
    selectedSlice: ChartDataPoint?,
    onSliceClick: (ChartDataPoint) -> Unit
) {
    val total = data.sumOf { it.y.toDouble() }.toFloat()
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    val chartColors = getChartColors()
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clickable { /* Handle click */ }
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2 * 0.8f
        
        var startAngle = -90f
        
        data.forEachIndexed { index, slice ->
            val sweepAngle = (slice.y / total) * 360f * animationProgress
            val isSelected = slice == selectedSlice
            val sliceRadius = if (isSelected) radius * 1.05f else radius
            val color = slice.color ?: chartColors[index % chartColors.size]
            
            // Draw slice
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = !donutMode,
                topLeft = Offset(
                    center.x - sliceRadius,
                    center.y - sliceRadius
                ),
                size = Size(sliceRadius * 2, sliceRadius * 2)
            )
            
            // Draw slice border
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = !donutMode,
                style = Stroke(width = 2.dp.toPx()),
                topLeft = Offset(
                    center.x - sliceRadius,
                    center.y - sliceRadius
                ),
                size = Size(sliceRadius * 2, sliceRadius * 2)
            )
            
            startAngle += sweepAngle
        }
        
        // Draw center circle for donut mode
        if (donutMode) {
            drawCircle(
                color = Color.White,
                radius = radius * 0.5f,
                center = center
            )
        }
    }
}

@Composable
private fun PieChartLegend(
    data: List<ChartDataPoint>,
    config: ChartConfig,
    selectedSlice: ChartDataPoint?,
    onSliceClick: (ChartDataPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val chartColors = getChartColors()
    
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data.size) { index ->
            val slice = data[index]
            val isSelected = slice == selectedSlice
            val color = slice.color ?: chartColors[index % chartColors.size]
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSliceClick(slice) },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 1.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Color indicator
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color, CircleShape)
                    )
                    
                    // Label and value
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = slice.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = slice.value.ifEmpty { slice.y.toString() },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📊 GAUGE CHART
// ============================================================================

/**
 * Modern gauge chart for KPIs
 */
@Composable
fun DataFlowGaugeChart(
    value: Float,
    maxValue: Float = 100f,
    title: String = "",
    subtitle: String = "",
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    showValue: Boolean = true
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(1500, easing = FastOutSlowInEasing)
    )
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                GaugeCanvas(
                    value = animatedValue,
                    maxValue = maxValue,
                    color = color
                )
                
                if (showValue) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${animatedValue.toInt()}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = "/ ${maxValue.toInt()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun GaugeCanvas(
    value: Float,
    maxValue: Float,
    color: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = minOf(size.width, size.height) / 2 * 0.8f
        val strokeWidth = 20.dp.toPx()
        
        // Background arc
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
        
        // Progress arc
        val progress = (value / maxValue).coerceIn(0f, 1f)
        drawArc(
            color = color,
            startAngle = 135f,
            sweepAngle = 270f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}

// ============================================================================
// 🎯 UTILITY COMPONENTS
// ============================================================================

/**
 * Chart container with loading and error states
 */
@Composable
fun DataFlowChartContainer(
    title: String,
    subtitle: String? = null,
    isLoading: Boolean = false,
    error: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Header
            if (title.isNotEmpty() || subtitle != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        if (title.isNotEmpty()) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        content = actions
                    )
                }
                
                Divider()
            }
            
            // Content area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(min = 200.dp)
            ) {
                when {
                    isLoading -> {
                        DataFlowLoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    error != null -> {
                        DataFlowErrorState(
                            message = error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> content()
                }
            }
        }
    }
}

@Composable
private fun ChartLegend(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(series.size) { index ->
            val seriesData = series[index]
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(seriesData.color, CircleShape)
                )
                
                Text(
                    text = seriesData.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChartTooltip(
    point: ChartDataPoint,
    series: ChartSeries,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = series.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = point.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            Text(
                text = point.value.ifEmpty { point.y.toString() },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun DrawScope.drawGrid(
    bounds: Rect,
    color: Color,
    gridLines: Int = 5
) {
    val stepX = bounds.width / gridLines
    val stepY = bounds.height / gridLines
    
    // Vertical lines
    for (i in 0..gridLines) {
        val x = bounds.left + (stepX * i)
        drawLine(
            color = color,
            start = Offset(x, bounds.top),
            end = Offset(x, bounds.bottom),
            strokeWidth = 1.dp.toPx()
        )
    }
    
    // Horizontal lines
    for (i in 0..gridLines) {
        val y = bounds.top + (stepY * i)
        drawLine(
            color = color,
            start = Offset(bounds.left, y),
            end = Offset(bounds.right, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}