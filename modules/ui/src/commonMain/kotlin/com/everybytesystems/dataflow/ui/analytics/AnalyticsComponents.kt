package com.everybytesystems.dataflow.ui.analytics

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*
import kotlin.math.*

/**
 * Advanced Analytics Components
 * Comprehensive analytics dashboard with KPIs, trends, and insights
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

data class KPIMetric(
    val id: String,
    val title: String,
    val value: Double,
    val previousValue: Double? = null,
    val target: Double? = null,
    val unit: String = "",
    val format: MetricFormat = MetricFormat.NUMBER,
    val trend: TrendDirection = TrendDirection.NEUTRAL,
    val color: Color = Color.Blue,
    val icon: ImageVector? = null,
    val description: String = ""
)

data class TimeSeriesPoint(
    val timestamp: Instant,
    val value: Double,
    val metadata: Map<String, Any> = emptyMap()
)

data class TimeSeriesData(
    val id: String,
    val name: String,
    val points: List<TimeSeriesPoint>,
    val color: Color = Color.Blue,
    val unit: String = ""
)

data class CategoryData(
    val category: String,
    val value: Double,
    val percentage: Double = 0.0,
    val color: Color = Color.Blue,
    val metadata: Map<String, Any> = emptyMap()
)

data class ComparisonData(
    val label: String,
    val current: Double,
    val previous: Double,
    val target: Double? = null,
    val unit: String = ""
)

data class FunnelStage(
    val name: String,
    val value: Double,
    val conversionRate: Double = 0.0,
    val color: Color = Color.Blue
)

data class CohortData(
    val cohort: String,
    val periods: List<Double>, // Retention rates for each period
    val size: Int
)

enum class MetricFormat {
    NUMBER, PERCENTAGE, CURRENCY, DURATION, BYTES
}

enum class TrendDirection {
    UP, DOWN, NEUTRAL
}

enum class DateRange {
    TODAY, YESTERDAY, LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS, CUSTOM
}

// ============================================================================
// 📈 KPI DASHBOARD
// ============================================================================

@Composable
fun KPIDashboard(
    metrics: List<KPIMetric>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    showComparison: Boolean = true,
    onMetricClick: ((KPIMetric) -> Unit)? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(metrics) { metric ->
            KPICard(
                metric = metric,
                showComparison = showComparison,
                onClick = { onMetricClick?.invoke(metric) }
            )
        }
    }
}

@Composable
private fun KPICard(
    metric: KPIMetric,
    showComparison: Boolean,
    onClick: () -> Unit
) {
    val animatedValue by animateFloatAsState(
        targetValue = metric.value.toFloat(),
        animationSpec = tween(1000),
        label = "kpi_animation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header with icon and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = metric.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                
                metric.icon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = metric.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Main value
            Text(
                text = formatMetricValue(animatedValue.toDouble(), metric.format, metric.unit),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = metric.color
            )
            
            // Comparison and trend
            if (showComparison && metric.previousValue != null) {
                val change = metric.value - metric.previousValue
                val changePercentage = if (metric.previousValue != 0.0) {
                    (change / metric.previousValue) * 100
                } else 0.0
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when {
                            changePercentage > 0 -> Icons.Default.TrendingUp
                            changePercentage < 0 -> Icons.Default.TrendingDown
                            else -> Icons.Default.TrendingFlat
                        },
                        contentDescription = null,
                        tint = when {
                            changePercentage > 0 -> Color.Green
                            changePercentage < 0 -> Color.Red
                            else -> Color.Gray
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Text(
                        text = "${if (changePercentage >= 0) "+" else ""}${"%.1f".format(changePercentage)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            changePercentage > 0 -> Color.Green
                            changePercentage < 0 -> Color.Red
                            else -> Color.Gray
                        }
                    )
                    
                    Text(
                        text = "vs previous",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Target progress
            metric.target?.let { target ->
                val progress = (metric.value / target).coerceIn(0.0, 1.0)
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Target",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier.fillMaxWidth(),
                        color = metric.color,
                        trackColor = metric.color.copy(alpha = 0.2f)
                    )
                }
            }
            
            // Description
            if (metric.description.isNotEmpty()) {
                Text(
                    text = metric.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 📊 TREND ANALYSIS
// ============================================================================

@Composable
fun TrendAnalysis(
    data: List<TimeSeriesData>,
    modifier: Modifier = Modifier,
    dateRange: DateRange = DateRange.LAST_30_DAYS,
    showPrediction: Boolean = false,
    onDateRangeChange: ((DateRange) -> Unit)? = null
) {
    var selectedSeries by remember { mutableStateOf<TimeSeriesData?>(null) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with date range selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trend Analysis",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            DateRangeSelector(
                selectedRange = dateRange,
                onRangeChange = { onDateRangeChange?.invoke(it) }
            )
        }
        
        // Trend chart
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                TrendChart(
                    data = data,
                    showPrediction = showPrediction,
                    onSeriesClick = { series ->
                        selectedSeries = if (selectedSeries == series) null else series
                    }
                )
            }
        }
        
        // Series legend and statistics
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(data) { series ->
                TrendSeriesCard(
                    series = series,
                    isSelected = selectedSeries == series,
                    onClick = { selectedSeries = if (selectedSeries == series) null else series }
                )
            }
        }
        
        // Detailed statistics for selected series
        selectedSeries?.let { series ->
            TrendStatistics(series = series)
        }
    }
}

@Composable
private fun DateRangeSelector(
    selectedRange: DateRange,
    onRangeChange: (DateRange) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        OutlinedButton(
            onClick = { expanded = true }
        ) {
            Text(selectedRange.name.replace("_", " "))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DateRange.values().forEach { range ->
                DropdownMenuItem(
                    text = { Text(range.name.replace("_", " ")) },
                    onClick = {
                        onRangeChange(range)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TrendChart(
    data: List<TimeSeriesData>,
    showPrediction: Boolean,
    onSeriesClick: (TimeSeriesData) -> Unit
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.isEmpty()) return@Canvas
        
        val padding = 40.dp.toPx()
        val chartWidth = size.width - 2 * padding
        val chartHeight = size.height - 2 * padding
        
        // Calculate bounds
        val allPoints = data.flatMap { it.points }
        if (allPoints.isEmpty()) return@Canvas
        
        val minTime = allPoints.minOf { it.timestamp.epochSeconds }
        val maxTime = allPoints.maxOf { it.timestamp.epochSeconds }
        val minValue = allPoints.minOf { it.value }
        val maxValue = allPoints.maxOf { it.value }
        
        // Draw grid
        drawTrendGrid(
            minTime, maxTime, minValue, maxValue,
            chartWidth, chartHeight, padding
        )
        
        // Draw series
        data.forEach { series ->
            drawTrendSeries(
                series, minTime, maxTime, minValue, maxValue,
                chartWidth, chartHeight, padding, showPrediction
            )
        }
    }
}

private fun DrawScope.drawTrendGrid(
    minTime: Long, maxTime: Long, minValue: Double, maxValue: Double,
    chartWidth: Float, chartHeight: Float, padding: Float
) {
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    
    // Horizontal grid lines (value axis)
    repeat(5) { i ->
        val y = padding + (i.toFloat() / 4) * chartHeight
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(padding + chartWidth, y),
            strokeWidth = 1.dp.toPx()
        )
    }
    
    // Vertical grid lines (time axis)
    repeat(6) { i ->
        val x = padding + (i.toFloat() / 5) * chartWidth
        drawLine(
            color = gridColor,
            start = Offset(x, padding),
            end = Offset(x, padding + chartHeight),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawTrendSeries(
    series: TimeSeriesData,
    minTime: Long, maxTime: Long, minValue: Double, maxValue: Double,
    chartWidth: Float, chartHeight: Float, padding: Float,
    showPrediction: Boolean
) {
    if (series.points.isEmpty()) return
    
    val path = Path()
    val points = mutableListOf<Offset>()
    
    series.points.sortedBy { it.timestamp }.forEachIndexed { index, point ->
        val x = padding + ((point.timestamp.epochSeconds - minTime).toFloat() / (maxTime - minTime)) * chartWidth
        val y = padding + chartHeight - ((point.value - minValue).toFloat() / (maxValue - minValue)) * chartHeight
        val offset = Offset(x, y)
        points.add(offset)
        
        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
    
    // Draw line
    drawPath(
        path = path,
        color = series.color,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw points
    points.forEach { point ->
        drawCircle(
            color = series.color,
            radius = 3.dp.toPx(),
            center = point
        )
    }
    
    // Draw prediction if enabled
    if (showPrediction && points.size > 2) {
        drawPredictionLine(points, series.color, chartWidth, padding)
    }
}

private fun DrawScope.drawPredictionLine(
    points: List<Offset>,
    color: Color,
    chartWidth: Float,
    padding: Float
) {
    // Simple linear prediction based on last few points
    val lastPoints = points.takeLast(3)
    if (lastPoints.size < 2) return
    
    val avgSlope = lastPoints.zipWithNext { a, b ->
        (b.y - a.y) / (b.x - a.x)
    }.average()
    
    val lastPoint = points.last()
    val predictionLength = chartWidth * 0.2f
    val endX = (lastPoint.x + predictionLength).coerceAtMost(padding + chartWidth)
    val endY = lastPoint.y + avgSlope * (endX - lastPoint.x)
    
    drawLine(
        color = color.copy(alpha = 0.5f),
        start = lastPoint,
        end = Offset(endX, endY),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
    )
}

@Composable
private fun TrendSeriesCard(
    series: TimeSeriesData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val currentValue = series.points.lastOrNull()?.value ?: 0.0
    val previousValue = series.points.getOrNull(series.points.size - 2)?.value ?: currentValue
    val change = currentValue - previousValue
    val changePercentage = if (previousValue != 0.0) (change / previousValue) * 100 else 0.0
    
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(series.color, CircleShape)
                )
                Text(
                    text = series.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Text(
                text = formatMetricValue(currentValue, MetricFormat.NUMBER, series.unit),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (changePercentage >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (changePercentage >= 0) Color.Green else Color.Red,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${if (changePercentage >= 0) "+" else ""}${"%.1f".format(changePercentage)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (changePercentage >= 0) Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
private fun TrendStatistics(series: TimeSeriesData) {
    val values = series.points.map { it.value }
    val average = values.average()
    val min = values.minOrNull() ?: 0.0
    val max = values.maxOrNull() ?: 0.0
    val standardDeviation = sqrt(values.map { (it - average).pow(2) }.average())
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${series.name} Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem("Average", formatMetricValue(average, MetricFormat.NUMBER, series.unit))
                StatisticItem("Min", formatMetricValue(min, MetricFormat.NUMBER, series.unit))
                StatisticItem("Max", formatMetricValue(max, MetricFormat.NUMBER, series.unit))
                StatisticItem("Std Dev", formatMetricValue(standardDeviation, MetricFormat.NUMBER, series.unit))
            }
        }
    }
}

@Composable
private fun StatisticItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================================
// 🔄 FUNNEL ANALYSIS
// ============================================================================

@Composable
fun FunnelAnalysis(
    stages: List<FunnelStage>,
    modifier: Modifier = Modifier,
    orientation: FunnelOrientation = FunnelOrientation.VERTICAL,
    showConversionRates: Boolean = true,
    onStageClick: ((FunnelStage) -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Conversion Funnel",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            when (orientation) {
                FunnelOrientation.VERTICAL -> VerticalFunnel(stages, showConversionRates, onStageClick)
                FunnelOrientation.HORIZONTAL -> HorizontalFunnel(stages, showConversionRates, onStageClick)
            }
            
            // Summary statistics
            FunnelSummary(stages)
        }
    }
}

@Composable
private fun VerticalFunnel(
    stages: List<FunnelStage>,
    showConversionRates: Boolean,
    onStageClick: ((FunnelStage) -> Unit)?
) {
    val maxValue = stages.maxOfOrNull { it.value } ?: 1.0
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stages.forEachIndexed { index, stage ->
            val widthFraction = (stage.value / maxValue).toFloat()
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Stage bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .height(60.dp)
                        .background(stage.color, RoundedCornerShape(8.dp))
                        .clickable { onStageClick?.invoke(stage) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stage.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatMetricValue(stage.value, MetricFormat.NUMBER, ""),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
                
                // Conversion rate
                if (showConversionRates && index > 0) {
                    Text(
                        text = "${"%.1f".format(stage.conversionRate)}% conversion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Drop-off indicator
                if (index < stages.size - 1) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalFunnel(
    stages: List<FunnelStage>,
    showConversionRates: Boolean,
    onStageClick: ((FunnelStage) -> Unit)?
) {
    val maxValue = stages.maxOfOrNull { it.value } ?: 1.0
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        stages.forEachIndexed { index, stage ->
            val heightFraction = (stage.value / maxValue).toFloat()
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Stage bar
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight(heightFraction)
                            .background(stage.color, RoundedCornerShape(8.dp))
                            .clickable { onStageClick?.invoke(stage) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stage.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatMetricValue(stage.value, MetricFormat.NUMBER, ""),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                    
                    // Conversion rate
                    if (showConversionRates && index > 0) {
                        Text(
                            text = "${"%.1f".format(stage.conversionRate)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Arrow
                if (index < stages.size - 1) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FunnelSummary(stages: List<FunnelStage>) {
    val totalConversion = if (stages.isNotEmpty()) {
        (stages.last().value / stages.first().value) * 100
    } else 0.0
    
    val avgConversion = stages.drop(1).map { it.conversionRate }.average()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatisticItem("Total Conversion", "${"%.1f".format(totalConversion)}%")
        StatisticItem("Avg Step Conversion", "${"%.1f".format(avgConversion)}%")
        StatisticItem("Total Stages", stages.size.toString())
    }
}

enum class FunnelOrientation {
    VERTICAL, HORIZONTAL
}

// ============================================================================
// 📊 COHORT ANALYSIS
// ============================================================================

@Composable
fun CohortAnalysis(
    cohorts: List<CohortData>,
    modifier: Modifier = Modifier,
    periodLabels: List<String> = listOf("Week 1", "Week 2", "Week 3", "Week 4"),
    colorScheme: List<Color> = listOf(Color.Red, Color.Orange, Color.Yellow, Color.Green)
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cohort Retention Analysis",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Cohort table
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Cohort",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(
                            text = "Size",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(60.dp)
                        )
                        periodLabels.forEach { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(60.dp)
                            )
                        }
                    }
                }
                
                // Cohort rows
                items(cohorts) { cohort ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cohort.cohort,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(
                            text = cohort.size.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(60.dp)
                        )
                        cohort.periods.forEach { retention ->
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(32.dp)
                                    .background(
                                        getRetentionColor(retention, colorScheme),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${(retention * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (retention > 0.5) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
            
            // Color legend
            CohortColorLegend(colorScheme)
        }
    }
}

@Composable
private fun CohortColorLegend(colorScheme: List<Color>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Retention Rate:",
            style = MaterialTheme.typography.labelSmall
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("0%", style = MaterialTheme.typography.labelSmall)
            colorScheme.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, RoundedCornerShape(2.dp))
                )
            }
            Text("100%", style = MaterialTheme.typography.labelSmall)
        }
    }
}

private fun getRetentionColor(retention: Double, colorScheme: List<Color>): Color {
    val index = (retention * (colorScheme.size - 1)).toInt().coerceIn(0, colorScheme.size - 1)
    return colorScheme[index]
}

// ============================================================================
// 📈 COMPARISON ANALYSIS
// ============================================================================

@Composable
fun ComparisonAnalysis(
    comparisons: List<ComparisonData>,
    modifier: Modifier = Modifier,
    title: String = "Performance Comparison",
    showTargets: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(comparisons) { comparison ->
                    ComparisonItem(
                        comparison = comparison,
                        showTarget = showTargets
                    )
                }
            }
        }
    }
}

@Composable
private fun ComparisonItem(
    comparison: ComparisonData,
    showTarget: Boolean
) {
    val change = comparison.current - comparison.previous
    val changePercentage = if (comparison.previous != 0.0) {
        (change / comparison.previous) * 100
    } else 0.0
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = comparison.label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatMetricValue(comparison.current, MetricFormat.NUMBER, comparison.unit),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = if (changePercentage >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (changePercentage >= 0) Color.Green else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = "${if (changePercentage >= 0) "+" else ""}${"%.1f".format(changePercentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (changePercentage >= 0) Color.Green else Color.Red
                )
            }
        }
        
        // Progress bars
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Previous:",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )
            
            LinearProgressIndicator(
                progress = { 0.7f }, // Normalized previous value
                modifier = Modifier.weight(1f),
                color = Color.Gray,
                trackColor = Color.Gray.copy(alpha = 0.2f)
            )
            
            Text(
                text = formatMetricValue(comparison.previous, MetricFormat.NUMBER, comparison.unit),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current:",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )
            
            LinearProgressIndicator(
                progress = { 0.8f }, // Normalized current value
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            
            Text(
                text = formatMetricValue(comparison.current, MetricFormat.NUMBER, comparison.unit),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(60.dp)
            )
        }
        
        // Target line
        if (showTarget && comparison.target != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Target:",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(60.dp)
                )
                
                LinearProgressIndicator(
                    progress = { 1f }, // Target is 100%
                    modifier = Modifier.weight(1f),
                    color = Color.Green,
                    trackColor = Color.Green.copy(alpha = 0.2f)
                )
                
                Text(
                    text = formatMetricValue(comparison.target, MetricFormat.NUMBER, comparison.unit),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(60.dp)
                )
            }
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun formatMetricValue(value: Double, format: MetricFormat, unit: String): String {
    return when (format) {
        MetricFormat.NUMBER -> {
            when {
                value >= 1_000_000 -> "${"%.1f".format(value / 1_000_000)}M"
                value >= 1_000 -> "${"%.1f".format(value / 1_000)}K"
                else -> value.toInt().toString()
            } + if (unit.isNotEmpty()) " $unit" else ""
        }
        MetricFormat.PERCENTAGE -> "${"%.1f".format(value)}%"
        MetricFormat.CURRENCY -> "$${"%.2f".format(value)}"
        MetricFormat.DURATION -> {
            val hours = (value / 3600).toInt()
            val minutes = ((value % 3600) / 60).toInt()
            "${hours}h ${minutes}m"
        }
        MetricFormat.BYTES -> {
            when {
                value >= 1_073_741_824 -> "${"%.1f".format(value / 1_073_741_824)} GB"
                value >= 1_048_576 -> "${"%.1f".format(value / 1_048_576)} MB"
                value >= 1_024 -> "${"%.1f".format(value / 1_024)} KB"
                else -> "${value.toInt()} B"
            }
        }
    }
}