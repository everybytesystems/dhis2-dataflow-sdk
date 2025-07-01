package com.everybytesystems.dataflow.ui.charts

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
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlin.math.*

/**
 * Advanced Charts Implementation
 * Comprehensive charting library with animations and interactions
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

data class ChartDataPoint(
    val x: Float,
    val y: Float,
    val label: String = "",
    val color: Color? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class ChartSeries(
    val name: String,
    val data: List<ChartDataPoint>,
    val color: Color,
    val strokeWidth: Float = 2.dp.value,
    val fillAlpha: Float = 0.3f,
    val visible: Boolean = true
)

data class ChartAxis(
    val min: Float,
    val max: Float,
    val step: Float,
    val label: String = "",
    val formatter: (Float) -> String = { it.toString() }
)

data class ChartConfig(
    val title: String = "",
    val subtitle: String = "",
    val showLegend: Boolean = true,
    val showGrid: Boolean = true,
    val showAxes: Boolean = true,
    val animationDuration: Int = 1000,
    val interactive: Boolean = true,
    val backgroundColor: Color = Color.Transparent,
    val gridColor: Color = Color.Gray.copy(alpha = 0.3f),
    val textColor: Color = Color.Black
)

// ============================================================================
// 📈 LINE CHARTS
// ============================================================================

@Composable
fun AdvancedLineChart(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    xAxis: ChartAxis? = null,
    yAxis: ChartAxis? = null,
    onPointClick: ((ChartSeries, ChartDataPoint) -> Unit)? = null
) {
    var selectedPoint by remember { mutableStateOf<Pair<ChartSeries, ChartDataPoint>?>(null) }
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDuration),
        label = "line_chart_animation"
    )
    
    Column(modifier = modifier) {
        // Title and subtitle
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        
        if (config.subtitle.isNotEmpty()) {
            Text(
                text = config.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = config.textColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        if (config.interactive) {
                            detectTapGestures { offset ->
                                // Find closest point to tap
                                val closestPoint = findClosestPoint(series, offset, size)
                                closestPoint?.let { (seriesData, point) ->
                                    selectedPoint = seriesData to point
                                    onPointClick?.invoke(seriesData, point)
                                }
                            }
                        }
                    }
            ) {
                drawLineChart(
                    series = series,
                    config = config,
                    xAxis = xAxis,
                    yAxis = yAxis,
                    animationProgress = animationProgress,
                    selectedPoint = selectedPoint
                )
            }
            
            // Tooltip for selected point
            selectedPoint?.let { (seriesData, point) ->
                ChartTooltip(
                    series = seriesData,
                    point = point,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
        
        // Legend
        if (config.showLegend && series.isNotEmpty()) {
            ChartLegend(
                series = series,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun DrawScope.drawLineChart(
    series: List<ChartSeries>,
    config: ChartConfig,
    xAxis: ChartAxis?,
    yAxis: ChartAxis?,
    animationProgress: Float,
    selectedPoint: Pair<ChartSeries, ChartDataPoint>?
) {
    val padding = 40.dp.toPx()
    val chartWidth = size.width - 2 * padding
    val chartHeight = size.height - 2 * padding
    
    // Calculate bounds
    val allPoints = series.flatMap { it.data }
    val minX = xAxis?.min ?: allPoints.minOfOrNull { it.x } ?: 0f
    val maxX = xAxis?.max ?: allPoints.maxOfOrNull { it.x } ?: 1f
    val minY = yAxis?.min ?: allPoints.minOfOrNull { it.y } ?: 0f
    val maxY = yAxis?.max ?: allPoints.maxOfOrNull { it.y } ?: 1f
    
    // Draw grid
    if (config.showGrid) {
        drawGrid(
            minX = minX, maxX = maxX, minY = minY, maxY = maxY,
            chartWidth = chartWidth, chartHeight = chartHeight,
            padding = padding, gridColor = config.gridColor
        )
    }
    
    // Draw axes
    if (config.showAxes) {
        drawAxes(
            xAxis = xAxis, yAxis = yAxis,
            minX = minX, maxX = maxX, minY = minY, maxY = maxY,
            chartWidth = chartWidth, chartHeight = chartHeight,
            padding = padding, textColor = config.textColor
        )
    }
    
    // Draw series
    series.filter { it.visible }.forEach { seriesData ->
        drawLineSeries(
            series = seriesData,
            minX = minX, maxX = maxX, minY = minY, maxY = maxY,
            chartWidth = chartWidth, chartHeight = chartHeight,
            padding = padding, animationProgress = animationProgress,
            selectedPoint = selectedPoint
        )
    }
}

private fun DrawScope.drawLineSeries(
    series: ChartSeries,
    minX: Float, maxX: Float, minY: Float, maxY: Float,
    chartWidth: Float, chartHeight: Float, padding: Float,
    animationProgress: Float, selectedPoint: Pair<ChartSeries, ChartDataPoint>?
) {
    if (series.data.isEmpty()) return
    
    val path = Path()
    val points = mutableListOf<Offset>()
    
    series.data.forEachIndexed { index, point ->
        val x = padding + (point.x - minX) / (maxX - minX) * chartWidth
        val y = padding + chartHeight - (point.y - minY) / (maxY - minY) * chartHeight
        val offset = Offset(x, y)
        points.add(offset)
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    
    // Animate path drawing
    val animatedPath = Path()
    val pathMeasure = PathMeasure()
    pathMeasure.setPath(path, false)
    val pathLength = pathMeasure.length
    val animatedLength = pathLength * animationProgress
    
    pathMeasure.getSegment(0f, animatedLength, animatedPath, true)
    
    // Draw line
    drawPath(
        path = animatedPath,
        color = series.color,
        style = Stroke(width = series.strokeWidth.dp.toPx())
    )
    
    // Draw fill area
    if (series.fillAlpha > 0f && animationProgress > 0.5f) {
        val fillPath = Path().apply {
            addPath(animatedPath)
            lineTo(points.last().x, padding + chartHeight)
            lineTo(points.first().x, padding + chartHeight)
            close()
        }
        
        drawPath(
            path = fillPath,
            color = series.color.copy(alpha = series.fillAlpha)
        )
    }
    
    // Draw points
    points.take((points.size * animationProgress).toInt()).forEach { point ->
        val isSelected = selectedPoint?.let { (selectedSeries, selectedPoint) ->
            selectedSeries == series && 
            series.data.any { it.x == selectedPoint.x && it.y == selectedPoint.y }
        } ?: false
        
        drawCircle(
            color = series.color,
            radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
            center = point
        )
        
        if (isSelected) {
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = point
            )
        }
    }
}

// ============================================================================
// 📊 BAR CHARTS
// ============================================================================

@Composable
fun AdvancedBarChart(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    orientation: ChartOrientation = ChartOrientation.VERTICAL,
    barSpacing: Dp = 4.dp,
    groupSpacing: Dp = 16.dp,
    onBarClick: ((ChartSeries, ChartDataPoint) -> Unit)? = null
) {
    var selectedBar by remember { mutableStateOf<Pair<ChartSeries, ChartDataPoint>?>(null) }
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDuration),
        label = "bar_chart_animation"
    )
    
    Column(modifier = modifier) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        if (config.interactive) {
                            detectTapGestures { offset ->
                                val clickedBar = findClickedBar(series, offset, size, orientation)
                                clickedBar?.let { (seriesData, point) ->
                                    selectedBar = seriesData to point
                                    onBarClick?.invoke(seriesData, point)
                                }
                            }
                        }
                    }
            ) {
                drawBarChart(
                    series = series,
                    config = config,
                    orientation = orientation,
                    barSpacing = barSpacing.toPx(),
                    groupSpacing = groupSpacing.toPx(),
                    animationProgress = animationProgress,
                    selectedBar = selectedBar
                )
            }
        }
        
        // Legend
        if (config.showLegend) {
            ChartLegend(
                series = series,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun DrawScope.drawBarChart(
    series: List<ChartSeries>,
    config: ChartConfig,
    orientation: ChartOrientation,
    barSpacing: Float,
    groupSpacing: Float,
    animationProgress: Float,
    selectedBar: Pair<ChartSeries, ChartDataPoint>?
) {
    val padding = 40.dp.toPx()
    val chartWidth = size.width - 2 * padding
    val chartHeight = size.height - 2 * padding
    
    val allPoints = series.flatMap { it.data }
    val maxY = allPoints.maxOfOrNull { it.y } ?: 1f
    val categories = allPoints.map { it.label }.distinct()
    
    val barWidth = (chartWidth - (categories.size - 1) * groupSpacing - 
                   categories.size * (series.size - 1) * barSpacing) / 
                   (categories.size * series.size)
    
    categories.forEachIndexed { categoryIndex, category ->
        val categoryX = padding + categoryIndex * (chartWidth / categories.size)
        
        series.forEachIndexed { seriesIndex, seriesData ->
            val point = seriesData.data.find { it.label == category }
            if (point != null) {
                val barHeight = (point.y / maxY) * chartHeight * animationProgress
                val barX = categoryX + seriesIndex * (barWidth + barSpacing)
                val barY = padding + chartHeight - barHeight
                
                val isSelected = selectedBar?.let { (selectedSeries, selectedPoint) ->
                    selectedSeries == seriesData && selectedPoint == point
                } ?: false
                
                val barColor = if (isSelected) {
                    seriesData.color.copy(alpha = 0.8f)
                } else {
                    seriesData.color
                }
                
                when (orientation) {
                    ChartOrientation.VERTICAL -> {
                        drawRect(
                            color = barColor,
                            topLeft = Offset(barX, barY),
                            size = Size(barWidth, barHeight)
                        )
                    }
                    ChartOrientation.HORIZONTAL -> {
                        drawRect(
                            color = barColor,
                            topLeft = Offset(padding, barX),
                            size = Size(barHeight, barWidth)
                        )
                    }
                }
            }
        }
    }
    
    // Draw category labels
    categories.forEachIndexed { index, category ->
        val x = padding + index * (chartWidth / categories.size) + (chartWidth / categories.size) / 2
        val y = padding + chartHeight + 20.dp.toPx()
        
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                category,
                x,
                y,
                android.graphics.Paint().apply {
                    color = config.textColor.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

// ============================================================================
// 🥧 PIE CHARTS
// ============================================================================

@Composable
fun AdvancedPieChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    showPercentages: Boolean = true,
    showLabels: Boolean = true,
    innerRadius: Float = 0f, // 0f for pie, >0f for donut
    onSliceClick: ((ChartDataPoint) -> Unit)? = null
) {
    var selectedSlice by remember { mutableStateOf<ChartDataPoint?>(null) }
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDuration),
        label = "pie_chart_animation"
    )
    
    Column(modifier = modifier) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        if (config.interactive) {
                            detectTapGestures { offset ->
                                val clickedSlice = findClickedSlice(data, offset, size)
                                clickedSlice?.let { slice ->
                                    selectedSlice = slice
                                    onSliceClick?.invoke(slice)
                                }
                            }
                        }
                    }
            ) {
                drawPieChart(
                    data = data,
                    config = config,
                    showPercentages = showPercentages,
                    showLabels = showLabels,
                    innerRadius = innerRadius,
                    animationProgress = animationProgress,
                    selectedSlice = selectedSlice
                )
            }
        }
        
        // Legend
        if (config.showLegend) {
            PieChartLegend(
                data = data,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun DrawScope.drawPieChart(
    data: List<ChartDataPoint>,
    config: ChartConfig,
    showPercentages: Boolean,
    showLabels: Boolean,
    innerRadius: Float,
    animationProgress: Float,
    selectedSlice: ChartDataPoint?
) {
    val total = data.sumOf { it.y.toDouble() }.toFloat()
    val center = Offset(size.width / 2, size.height / 2)
    val radius = minOf(size.width, size.height) / 2 * 0.8f
    
    var startAngle = -90f
    
    data.forEach { point ->
        val sweepAngle = (point.y / total) * 360f * animationProgress
        val isSelected = selectedSlice == point
        val currentRadius = if (isSelected) radius * 1.1f else radius
        
        val color = point.color ?: Color.Blue
        
        // Draw slice
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = innerRadius == 0f,
            topLeft = Offset(
                center.x - currentRadius,
                center.y - currentRadius
            ),
            size = Size(currentRadius * 2, currentRadius * 2)
        )
        
        // Draw inner circle for donut chart
        if (innerRadius > 0f) {
            drawCircle(
                color = config.backgroundColor,
                radius = innerRadius * radius,
                center = center
            )
        }
        
        // Draw labels
        if (showLabels || showPercentages) {
            val labelAngle = startAngle + sweepAngle / 2
            val labelRadius = radius * 0.7f
            val labelX = center.x + cos(Math.toRadians(labelAngle.toDouble())).toFloat() * labelRadius
            val labelY = center.y + sin(Math.toRadians(labelAngle.toDouble())).toFloat() * labelRadius
            
            val text = when {
                showLabels && showPercentages -> "${point.label}\n${(point.y / total * 100).toInt()}%"
                showLabels -> point.label
                showPercentages -> "${(point.y / total * 100).toInt()}%"
                else -> ""
            }
            
            if (text.isNotEmpty()) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        text,
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            color = Color.White.toArgb()
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
        
        startAngle += sweepAngle
    }
}

// ============================================================================
// 📈 AREA CHARTS
// ============================================================================

@Composable
fun AdvancedAreaChart(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    stacked: Boolean = false,
    onAreaClick: ((ChartSeries, ChartDataPoint) -> Unit)? = null
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDuration),
        label = "area_chart_animation"
    )
    
    Column(modifier = modifier) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawAreaChart(
                    series = series,
                    config = config,
                    stacked = stacked,
                    animationProgress = animationProgress
                )
            }
        }
        
        // Legend
        if (config.showLegend) {
            ChartLegend(
                series = series,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun DrawScope.drawAreaChart(
    series: List<ChartSeries>,
    config: ChartConfig,
    stacked: Boolean,
    animationProgress: Float
) {
    val padding = 40.dp.toPx()
    val chartWidth = size.width - 2 * padding
    val chartHeight = size.height - 2 * padding
    
    val allPoints = series.flatMap { it.data }
    val minX = allPoints.minOfOrNull { it.x } ?: 0f
    val maxX = allPoints.maxOfOrNull { it.x } ?: 1f
    val maxY = if (stacked) {
        // For stacked charts, calculate the maximum sum at any x point
        val xValues = allPoints.map { it.x }.distinct().sorted()
        xValues.maxOfOrNull { x ->
            series.sumOf { seriesData ->
                seriesData.data.find { it.x == x }?.y?.toDouble() ?: 0.0
            }.toFloat()
        } ?: 1f
    } else {
        allPoints.maxOfOrNull { it.y } ?: 1f
    }
    
    if (stacked) {
        drawStackedAreas(series, minX, maxX, maxY, chartWidth, chartHeight, padding, animationProgress)
    } else {
        series.forEach { seriesData ->
            drawSingleArea(seriesData, minX, maxX, maxY, chartWidth, chartHeight, padding, animationProgress)
        }
    }
}

private fun DrawScope.drawSingleArea(
    series: ChartSeries,
    minX: Float, maxX: Float, maxY: Float,
    chartWidth: Float, chartHeight: Float, padding: Float,
    animationProgress: Float
) {
    if (series.data.isEmpty()) return
    
    val path = Path()
    val sortedData = series.data.sortedBy { it.x }
    
    // Start from bottom-left
    val firstPoint = sortedData.first()
    val firstX = padding + (firstPoint.x - minX) / (maxX - minX) * chartWidth
    val baseY = padding + chartHeight
    
    path.moveTo(firstX, baseY)
    
    // Draw to first point
    val firstY = padding + chartHeight - (firstPoint.y / maxY) * chartHeight * animationProgress
    path.lineTo(firstX, firstY)
    
    // Draw through all points
    sortedData.forEach { point ->
        val x = padding + (point.x - minX) / (maxX - minX) * chartWidth
        val y = padding + chartHeight - (point.y / maxY) * chartHeight * animationProgress
        path.lineTo(x, y)
    }
    
    // Close path at bottom-right
    val lastPoint = sortedData.last()
    val lastX = padding + (lastPoint.x - minX) / (maxX - minX) * chartWidth
    path.lineTo(lastX, baseY)
    path.close()
    
    // Draw filled area
    drawPath(
        path = path,
        color = series.color.copy(alpha = series.fillAlpha)
    )
    
    // Draw border line
    val borderPath = Path()
    sortedData.forEachIndexed { index, point ->
        val x = padding + (point.x - minX) / (maxX - minX) * chartWidth
        val y = padding + chartHeight - (point.y / maxY) * chartHeight * animationProgress
        
        if (index == 0) {
            borderPath.moveTo(x, y)
        } else {
            borderPath.lineTo(x, y)
        }
    }
    
    drawPath(
        path = borderPath,
        color = series.color,
        style = Stroke(width = series.strokeWidth.dp.toPx())
    )
}

// ============================================================================
// 📊 SCATTER PLOTS
// ============================================================================

@Composable
fun AdvancedScatterPlot(
    series: List<ChartSeries>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    pointSize: Dp = 6.dp,
    showTrendLine: Boolean = false,
    onPointClick: ((ChartSeries, ChartDataPoint) -> Unit)? = null
) {
    var selectedPoint by remember { mutableStateOf<Pair<ChartSeries, ChartDataPoint>?>(null) }
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDuration),
        label = "scatter_animation"
    )
    
    Column(modifier = modifier) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        if (config.interactive) {
                            detectTapGestures { offset ->
                                val closestPoint = findClosestPoint(series, offset, size)
                                closestPoint?.let { (seriesData, point) ->
                                    selectedPoint = seriesData to point
                                    onPointClick?.invoke(seriesData, point)
                                }
                            }
                        }
                    }
            ) {
                drawScatterPlot(
                    series = series,
                    config = config,
                    pointSize = pointSize.toPx(),
                    showTrendLine = showTrendLine,
                    animationProgress = animationProgress,
                    selectedPoint = selectedPoint
                )
            }
        }
        
        // Legend
        if (config.showLegend) {
            ChartLegend(
                series = series,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun DrawScope.drawScatterPlot(
    series: List<ChartSeries>,
    config: ChartConfig,
    pointSize: Float,
    showTrendLine: Boolean,
    animationProgress: Float,
    selectedPoint: Pair<ChartSeries, ChartDataPoint>?
) {
    val padding = 40.dp.toPx()
    val chartWidth = size.width - 2 * padding
    val chartHeight = size.height - 2 * padding
    
    val allPoints = series.flatMap { it.data }
    val minX = allPoints.minOfOrNull { it.x } ?: 0f
    val maxX = allPoints.maxOfOrNull { it.x } ?: 1f
    val minY = allPoints.minOfOrNull { it.y } ?: 0f
    val maxY = allPoints.maxOfOrNull { it.y } ?: 1f
    
    // Draw grid
    if (config.showGrid) {
        drawGrid(minX, maxX, minY, maxY, chartWidth, chartHeight, padding, config.gridColor)
    }
    
    series.forEach { seriesData ->
        // Draw trend line if requested
        if (showTrendLine && seriesData.data.size > 1) {
            drawTrendLine(seriesData, minX, maxX, minY, maxY, chartWidth, chartHeight, padding)
        }
        
        // Draw points
        seriesData.data.take((seriesData.data.size * animationProgress).toInt()).forEach { point ->
            val x = padding + (point.x - minX) / (maxX - minX) * chartWidth
            val y = padding + chartHeight - (point.y - minY) / (maxY - minY) * chartHeight
            
            val isSelected = selectedPoint?.let { (selectedSeries, selectedPoint) ->
                selectedSeries == seriesData && selectedPoint == point
            } ?: false
            
            val currentPointSize = if (isSelected) pointSize * 1.5f else pointSize
            val pointColor = point.color ?: seriesData.color
            
            drawCircle(
                color = pointColor,
                radius = currentPointSize,
                center = Offset(x, y)
            )
            
            if (isSelected) {
                drawCircle(
                    color = Color.White,
                    radius = currentPointSize * 0.4f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// ============================================================================
// 📊 RADAR CHARTS
// ============================================================================

@Composable
fun AdvancedRadarChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    maxValue: Float? = null,
    showGrid: Boolean = true,
    gridLevels: Int = 5
) {
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(config.animationDuration),
        label = "radar_animation"
    )
    
    Column(modifier = modifier) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
                .padding(32.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRadarChart(
                    data = data,
                    config = config,
                    maxValue = maxValue ?: data.maxOfOrNull { it.y } ?: 1f,
                    showGrid = showGrid,
                    gridLevels = gridLevels,
                    animationProgress = animationProgress
                )
            }
        }
    }
}

private fun DrawScope.drawRadarChart(
    data: List<ChartDataPoint>,
    config: ChartConfig,
    maxValue: Float,
    showGrid: Boolean,
    gridLevels: Int,
    animationProgress: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = minOf(size.width, size.height) / 2 * 0.8f
    val angleStep = 360f / data.size
    
    // Draw grid
    if (showGrid) {
        repeat(gridLevels) { level ->
            val levelRadius = radius * (level + 1) / gridLevels
            drawCircle(
                color = config.gridColor,
                radius = levelRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        
        // Draw axis lines
        data.forEachIndexed { index, _ ->
            val angle = Math.toRadians((index * angleStep - 90).toDouble())
            val endX = center.x + cos(angle).toFloat() * radius
            val endY = center.y + sin(angle).toFloat() * radius
            
            drawLine(
                color = config.gridColor,
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
    
    // Draw data polygon
    val path = Path()
    data.forEachIndexed { index, point ->
        val angle = Math.toRadians((index * angleStep - 90).toDouble())
        val distance = (point.y / maxValue) * radius * animationProgress
        val x = center.x + cos(angle).toFloat() * distance
        val y = center.y + sin(angle).toFloat() * distance
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    path.close()
    
    // Fill area
    drawPath(
        path = path,
        color = Color.Blue.copy(alpha = 0.3f)
    )
    
    // Draw border
    drawPath(
        path = path,
        color = Color.Blue,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw points
    data.forEachIndexed { index, point ->
        val angle = Math.toRadians((index * angleStep - 90).toDouble())
        val distance = (point.y / maxValue) * radius * animationProgress
        val x = center.x + cos(angle).toFloat() * distance
        val y = center.y + sin(angle).toFloat() * distance
        
        drawCircle(
            color = Color.Blue,
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
    }
    
    // Draw labels
    data.forEachIndexed { index, point ->
        val angle = Math.toRadians((index * angleStep - 90).toDouble())
        val labelDistance = radius + 20.dp.toPx()
        val x = center.x + cos(angle).toFloat() * labelDistance
        val y = center.y + sin(angle).toFloat() * labelDistance
        
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                point.label,
                x,
                y,
                android.graphics.Paint().apply {
                    color = config.textColor.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}

// ============================================================================
// 🔥 HEATMAPS
// ============================================================================

data class HeatmapDataPoint(
    val x: Int,
    val y: Int,
    val value: Float,
    val label: String = ""
)

@Composable
fun AdvancedHeatmap(
    data: List<HeatmapDataPoint>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    colorScheme: List<Color> = listOf(Color.Blue, Color.Green, Color.Yellow, Color.Red),
    showValues: Boolean = true,
    onCellClick: ((HeatmapDataPoint) -> Unit)? = null
) {
    var selectedCell by remember { mutableStateOf<HeatmapDataPoint?>(null) }
    
    Column(modifier = modifier) {
        // Title
        if (config.title.isNotEmpty()) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = config.textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Heatmap
        val maxX = data.maxOfOrNull { it.x } ?: 0
        val maxY = data.maxOfOrNull { it.y } ?: 0
        val minValue = data.minOfOrNull { it.value } ?: 0f
        val maxValue = data.maxOfOrNull { it.value } ?: 1f
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(config.backgroundColor, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            items(maxY + 1) { y ->
                LazyRow {
                    items(maxX + 1) { x ->
                        val dataPoint = data.find { it.x == x && it.y == y }
                        val value = dataPoint?.value ?: 0f
                        val normalizedValue = (value - minValue) / (maxValue - minValue)
                        val color = interpolateColor(colorScheme, normalizedValue)
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color, RoundedCornerShape(4.dp))
                                .border(
                                    1.dp,
                                    if (selectedCell == dataPoint) Color.Black else Color.Transparent,
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable {
                                    dataPoint?.let {
                                        selectedCell = it
                                        onCellClick?.invoke(it)
                                    }
                                }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (showValues && dataPoint != null) {
                                Text(
                                    text = value.toInt().toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (normalizedValue > 0.5f) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Color scale legend
        HeatmapColorScale(
            colorScheme = colorScheme,
            minValue = minValue,
            maxValue = maxValue,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun HeatmapColorScale(
    colorScheme: List<Color>,
    minValue: Float,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = minValue.toInt().toString(),
            style = MaterialTheme.typography.labelSmall
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(20.dp)
                .padding(horizontal = 8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                
                for (i in 0 until width.toInt()) {
                    val progress = i / width
                    val color = interpolateColor(colorScheme, progress)
                    drawLine(
                        color = color,
                        start = Offset(i.toFloat(), 0f),
                        end = Offset(i.toFloat(), height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
        }
        
        Text(
            text = maxValue.toInt().toString(),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// ============================================================================
// 📊 HELPER FUNCTIONS
// ============================================================================

private fun DrawScope.drawGrid(
    minX: Float, maxX: Float, minY: Float, maxY: Float,
    chartWidth: Float, chartHeight: Float, padding: Float,
    gridColor: Color
) {
    val xSteps = 5
    val ySteps = 5
    
    // Vertical grid lines
    repeat(xSteps + 1) { i ->
        val x = padding + (i.toFloat() / xSteps) * chartWidth
        drawLine(
            color = gridColor,
            start = Offset(x, padding),
            end = Offset(x, padding + chartHeight),
            strokeWidth = 1.dp.toPx()
        )
    }
    
    // Horizontal grid lines
    repeat(ySteps + 1) { i ->
        val y = padding + (i.toFloat() / ySteps) * chartHeight
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawAxes(
    xAxis: ChartAxis?, yAxis: ChartAxis?,
    minX: Float, maxX: Float, minY: Float, maxY: Float,
    chartWidth: Float, chartHeight: Float, padding: Float,
    textColor: Color
) {
    // X-axis
    drawLine(
        color = textColor,
        start = Offset(padding, padding + chartHeight),
        end = Offset(padding + chartWidth, padding + chartHeight),
        strokeWidth = 2.dp.toPx()
    )
    
    // Y-axis
    drawLine(
        color = textColor,
        start = Offset(padding, padding),
        end = Offset(padding, padding + chartHeight),
        strokeWidth = 2.dp.toPx()
    )
}

private fun findClosestPoint(
    series: List<ChartSeries>,
    tapOffset: Offset,
    canvasSize: Size
): Pair<ChartSeries, ChartDataPoint>? {
    val padding = 40.dp.value
    val chartWidth = canvasSize.width - 2 * padding
    val chartHeight = canvasSize.height - 2 * padding
    
    var closestDistance = Float.MAX_VALUE
    var closestPoint: Pair<ChartSeries, ChartDataPoint>? = null
    
    series.forEach { seriesData ->
        seriesData.data.forEach { point ->
            val allPoints = series.flatMap { it.data }
            val minX = allPoints.minOfOrNull { it.x } ?: 0f
            val maxX = allPoints.maxOfOrNull { it.x } ?: 1f
            val minY = allPoints.minOfOrNull { it.y } ?: 0f
            val maxY = allPoints.maxOfOrNull { it.y } ?: 1f
            
            val x = padding + (point.x - minX) / (maxX - minX) * chartWidth
            val y = padding + chartHeight - (point.y - minY) / (maxY - minY) * chartHeight
            
            val distance = sqrt((tapOffset.x - x).pow(2) + (tapOffset.y - y).pow(2))
            if (distance < closestDistance && distance < 30.dp.value) {
                closestDistance = distance
                closestPoint = seriesData to point
            }
        }
    }
    
    return closestPoint
}

private fun findClickedBar(
    series: List<ChartSeries>,
    tapOffset: Offset,
    canvasSize: Size,
    orientation: ChartOrientation
): Pair<ChartSeries, ChartDataPoint>? {
    // Implementation for finding clicked bar
    return null
}

private fun findClickedSlice(
    data: List<ChartDataPoint>,
    tapOffset: Offset,
    canvasSize: Size
): ChartDataPoint? {
    // Implementation for finding clicked pie slice
    return null
}

private fun interpolateColor(colors: List<Color>, progress: Float): Color {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val scaledProgress = clampedProgress * (colors.size - 1)
    val index = scaledProgress.toInt()
    val fraction = scaledProgress - index
    
    return if (index >= colors.size - 1) {
        colors.last()
    } else {
        val startColor = colors[index]
        val endColor = colors[index + 1]
        Color(
            red = startColor.red + (endColor.red - startColor.red) * fraction,
            green = startColor.green + (endColor.green - startColor.green) * fraction,
            blue = startColor.blue + (endColor.blue - startColor.blue) * fraction,
            alpha = startColor.alpha + (endColor.alpha - startColor.alpha) * fraction
        )
    }
}

private fun DrawScope.drawTrendLine(
    series: ChartSeries,
    minX: Float, maxX: Float, minY: Float, maxY: Float,
    chartWidth: Float, chartHeight: Float, padding: Float
) {
    // Simple linear regression for trend line
    val n = series.data.size
    val sumX = series.data.sumOf { it.x.toDouble() }
    val sumY = series.data.sumOf { it.y.toDouble() }
    val sumXY = series.data.sumOf { (it.x * it.y).toDouble() }
    val sumXX = series.data.sumOf { (it.x * it.x).toDouble() }
    
    val slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
    val intercept = (sumY - slope * sumX) / n
    
    val startX = padding
    val endX = padding + chartWidth
    val startY = padding + chartHeight - ((slope * minX + intercept - minY) / (maxY - minY) * chartHeight).toFloat()
    val endY = padding + chartHeight - ((slope * maxX + intercept - minY) / (maxY - minY) * chartHeight).toFloat()
    
    drawLine(
        color = series.color.copy(alpha = 0.7f),
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
    )
}

private fun DrawScope.drawStackedAreas(
    series: List<ChartSeries>,
    minX: Float, maxX: Float, maxY: Float,
    chartWidth: Float, chartHeight: Float, padding: Float,
    animationProgress: Float
) {
    val xValues = series.flatMap { it.data }.map { it.x }.distinct().sorted()
    
    xValues.forEach { x ->
        var cumulativeY = 0f
        val xPos = padding + (x - minX) / (maxX - minX) * chartWidth
        
        series.forEach { seriesData ->
            val point = seriesData.data.find { it.x == x }
            val value = point?.y ?: 0f
            val animatedValue = value * animationProgress
            
            val startY = padding + chartHeight - (cumulativeY / maxY) * chartHeight
            val endY = padding + chartHeight - ((cumulativeY + animatedValue) / maxY) * chartHeight
            
            drawRect(
                color = seriesData.color.copy(alpha = seriesData.fillAlpha),
                topLeft = Offset(xPos - 2.dp.toPx(), endY),
                size = Size(4.dp.toPx(), startY - endY)
            )
            
            cumulativeY += animatedValue
        }
    }
}

@Composable
private fun ChartTooltip(
    series: ChartSeries,
    point: ChartDataPoint,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = series.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "X: ${point.x}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Y: ${point.y}",
                style = MaterialTheme.typography.bodySmall
            )
            if (point.label.isNotEmpty()) {
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.bodySmall
                )
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
        items(series) { seriesData ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(seriesData.color, CircleShape)
                )
                Text(
                    text = seriesData.name,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun PieChartLegend(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(data) { point ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(point.color ?: Color.Blue, CircleShape)
                )
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = point.y.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

enum class ChartOrientation {
    VERTICAL, HORIZONTAL
}

// ============================================================================
// 📊 REAL-TIME CHARTS
// ============================================================================

@Composable
fun RealTimeLineChart(
    data: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    config: ChartConfig = ChartConfig(),
    maxDataPoints: Int = 50,
    updateInterval: Long = 1000L,
    onDataUpdate: () -> List<ChartDataPoint>
) {
    var chartData by remember { mutableStateOf(data) }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(updateInterval)
            val newData = onDataUpdate()
            chartData = newData.takeLast(maxDataPoints)
        }
    }
    
    AdvancedLineChart(
        series = listOf(
            ChartSeries(
                name = "Real-time Data",
                data = chartData,
                color = Color.Blue
            )
        ),
        modifier = modifier,
        config = config
    )
}