package com.everybytesystems.dataflow.ui.dashboard

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.everybytesystems.dataflow.ui.components.*
import com.everybytesystems.dataflow.ui.charts.*
import com.everybytesystems.dataflow.ui.theme.DataFlowColors
import com.everybytesystems.dataflow.ui.theme.getChartColors
import kotlinx.coroutines.delay

/**
 * DataFlow Dashboard Components
 * Executive dashboards and analytics interfaces for data applications
 */

// ============================================================================
// 📊 DASHBOARD MODELS
// ============================================================================

/**
 * Dashboard widget configuration
 */
data class DashboardWidget(
    val id: String,
    val title: String,
    val type: WidgetType,
    val size: WidgetSize = WidgetSize.MEDIUM,
    val data: Any? = null,
    val config: Map<String, Any> = emptyMap(),
    val refreshInterval: Long? = null
)

enum class WidgetType {
    METRIC_CARD,
    LINE_CHART,
    BAR_CHART,
    PIE_CHART,
    GAUGE_CHART,
    DATA_TABLE,
    PROGRESS_INDICATOR,
    STATUS_GRID,
    SPARKLINE,
    TEXT_WIDGET,
    IMAGE_WIDGET
}

enum class WidgetSize {
    SMALL,   // 1x1
    MEDIUM,  // 2x1
    LARGE,   // 2x2
    WIDE,    // 3x1
    EXTRA_WIDE // 4x1
}

/**
 * Dashboard configuration
 */
data class DashboardConfig(
    val title: String,
    val subtitle: String? = null,
    val layout: DashboardLayout = DashboardLayout.GRID,
    val columns: Int = 3,
    val refreshInterval: Long = 30000, // 30 seconds
    val allowEdit: Boolean = false,
    val showHeader: Boolean = true
)

enum class DashboardLayout {
    GRID,
    MASONRY,
    FLOW,
    CUSTOM
}

// ============================================================================
// 📱 EXECUTIVE DASHBOARD
// ============================================================================

/**
 * Modern executive dashboard with real-time updates
 */
@Composable
fun DataFlowExecutiveDashboard(
    widgets: List<DashboardWidget>,
    config: DashboardConfig,
    modifier: Modifier = Modifier,
    onWidgetClick: ((DashboardWidget) -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
    onEditMode: ((Boolean) -> Unit)? = null
) {
    var isRefreshing by remember { mutableStateOf(false) }
    var lastRefresh by remember { mutableStateOf(System.currentTimeMillis()) }
    var editMode by remember { mutableStateOf(false) }
    
    // Auto-refresh logic
    LaunchedEffect(config.refreshInterval) {
        while (true) {
            delay(config.refreshInterval)
            if (!editMode) {
                isRefreshing = true
                onRefresh?.invoke()
                delay(1000) // Show refresh indicator
                isRefreshing = false
                lastRefresh = System.currentTimeMillis()
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Dashboard header
        if (config.showHeader) {
            DashboardHeader(
                config = config,
                isRefreshing = isRefreshing,
                lastRefresh = lastRefresh,
                editMode = editMode,
                onRefresh = {
                    isRefreshing = true
                    onRefresh?.invoke()
                },
                onEditMode = { 
                    editMode = it
                    onEditMode?.invoke(it)
                }
            )
        }
        
        // Dashboard content
        when (config.layout) {
            DashboardLayout.GRID -> {
                GridDashboard(
                    widgets = widgets,
                    columns = config.columns,
                    editMode = editMode,
                    onWidgetClick = onWidgetClick,
                    modifier = Modifier.weight(1f)
                )
            }
            DashboardLayout.MASONRY -> {
                MasonryDashboard(
                    widgets = widgets,
                    editMode = editMode,
                    onWidgetClick = onWidgetClick,
                    modifier = Modifier.weight(1f)
                )
            }
            DashboardLayout.FLOW -> {
                FlowDashboard(
                    widgets = widgets,
                    editMode = editMode,
                    onWidgetClick = onWidgetClick,
                    modifier = Modifier.weight(1f)
                )
            }
            DashboardLayout.CUSTOM -> {
                CustomDashboard(
                    widgets = widgets,
                    editMode = editMode,
                    onWidgetClick = onWidgetClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    config: DashboardConfig,
    isRefreshing: Boolean,
    lastRefresh: Long,
    editMode: Boolean,
    onRefresh: () -> Unit,
    onEditMode: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = config.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                config.subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Last refresh indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Last updated: ${formatTime(lastRefresh)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit mode toggle
                if (config.allowEdit) {
                    IconButton(
                        onClick = { onEditMode(!editMode) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (editMode) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit mode",
                            tint = if (editMode) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                
                // Refresh button
                IconButton(
                    onClick = onRefresh,
                    enabled = !isRefreshing
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = if (isRefreshing) {
                            Modifier.rotate(360f)
                        } else Modifier
                    )
                }
                
                // More options
                IconButton(onClick = { /* More options */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }
        }
    }
}

// ============================================================================
// 📊 DASHBOARD LAYOUTS
// ============================================================================

@Composable
private fun GridDashboard(
    widgets: List<DashboardWidget>,
    columns: Int,
    editMode: Boolean,
    onWidgetClick: ((DashboardWidget) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(widgets.size) { index ->
            val widget = widgets[index]
            
            DashboardWidgetContainer(
                widget = widget,
                editMode = editMode,
                onClick = { onWidgetClick?.invoke(widget) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun MasonryDashboard(
    widgets: List<DashboardWidget>,
    editMode: Boolean,
    onWidgetClick: ((DashboardWidget) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(widgets.size) { index ->
            val widget = widgets[index]
            
            DashboardWidgetContainer(
                widget = widget,
                editMode = editMode,
                onClick = { onWidgetClick?.invoke(widget) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun FlowDashboard(
    widgets: List<DashboardWidget>,
    editMode: Boolean,
    onWidgetClick: ((DashboardWidget) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(widgets.size) { index ->
            val widget = widgets[index]
            
            DashboardWidgetContainer(
                widget = widget,
                editMode = editMode,
                onClick = { onWidgetClick?.invoke(widget) },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement()
            )
        }
    }
}

@Composable
private fun CustomDashboard(
    widgets: List<DashboardWidget>,
    editMode: Boolean,
    onWidgetClick: ((DashboardWidget) -> Unit)?,
    modifier: Modifier = Modifier
) {
    // Custom layout implementation
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Custom Dashboard Layout",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// 🎯 WIDGET CONTAINER
// ============================================================================

@Composable
private fun DashboardWidgetContainer(
    widget: DashboardWidget,
    editMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val height = when (widget.size) {
        WidgetSize.SMALL -> 200.dp
        WidgetSize.MEDIUM -> 300.dp
        WidgetSize.LARGE -> 400.dp
        WidgetSize.WIDE -> 250.dp
        WidgetSize.EXTRA_WIDE -> 280.dp
    }
    
    Card(
        modifier = modifier
            .height(height)
            .clickable { onClick() }
            .then(
                if (editMode) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(12.dp)
                    )
                } else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Widget header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = widget.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (editMode) {
                        IconButton(
                            onClick = { /* Move widget */ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Move widget",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { /* Widget menu */ },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Widget menu",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Divider()
            
            // Widget content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (widget.type) {
                    WidgetType.METRIC_CARD -> {
                        MetricCardWidgetContent(widget)
                    }
                    WidgetType.LINE_CHART -> {
                        LineChartWidgetContent(widget)
                    }
                    WidgetType.BAR_CHART -> {
                        BarChartWidgetContent(widget)
                    }
                    WidgetType.PIE_CHART -> {
                        PieChartWidgetContent(widget)
                    }
                    WidgetType.GAUGE_CHART -> {
                        GaugeWidgetContent(widget)
                    }
                    WidgetType.DATA_TABLE -> {
                        DataTableWidgetContent(widget)
                    }
                    WidgetType.PROGRESS_INDICATOR -> {
                        ProgressWidgetContent(widget)
                    }
                    WidgetType.STATUS_GRID -> {
                        StatusGridWidgetContent(widget)
                    }
                    WidgetType.SPARKLINE -> {
                        SparklineWidgetContent(widget)
                    }
                    WidgetType.TEXT_WIDGET -> {
                        TextWidgetContent(widget)
                    }
                    else -> {
                        PlaceholderWidgetContent(widget)
                    }
                }
            }
        }
    }
}

// ============================================================================
// 🎨 WIDGET CONTENT IMPLEMENTATIONS
// ============================================================================

@Composable
private fun MetricCardWidgetContent(widget: DashboardWidget) {
    val value = "1,234"
    val trend = MetricTrend(12.5f, TrendDirection.UP, "vs last month")
    
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = DataFlowColors.Success,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "+${trend.percentage}%",
                style = MaterialTheme.typography.titleMedium,
                color = DataFlowColors.Success,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = trend.period,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LineChartWidgetContent(widget: DashboardWidget) {
    val chartColors = getChartColors()
    val series = listOf(
        ChartSeries(
            name = "Sales",
            data = listOf(
                ChartDataPoint(1f, 100f, "Jan", "100"),
                ChartDataPoint(2f, 150f, "Feb", "150"),
                ChartDataPoint(3f, 120f, "Mar", "120"),
                ChartDataPoint(4f, 180f, "Apr", "180"),
                ChartDataPoint(5f, 200f, "May", "200"),
                ChartDataPoint(6f, 170f, "Jun", "170")
            ),
            color = chartColors[0]
        )
    )
    
    DataFlowLineChart(
        series = series,
        config = ChartConfig(showLegend = false, showGrid = false),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun BarChartWidgetContent(widget: DashboardWidget) {
    val chartColors = getChartColors()
    val data = listOf(
        ChartDataPoint(1f, 80f, "Q1", "80", chartColors[0]),
        ChartDataPoint(2f, 120f, "Q2", "120", chartColors[1]),
        ChartDataPoint(3f, 95f, "Q3", "95", chartColors[2]),
        ChartDataPoint(4f, 150f, "Q4", "150", chartColors[3])
    )
    
    DataFlowBarChart(
        data = data,
        config = ChartConfig(showLegend = false),
        horizontal = true,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun PieChartWidgetContent(widget: DashboardWidget) {
    val chartColors = getChartColors()
    val data = listOf(
        ChartDataPoint(1f, 40f, "Complete", "40%", chartColors[0]),
        ChartDataPoint(2f, 30f, "In Progress", "30%", chartColors[1]),
        ChartDataPoint(3f, 20f, "Pending", "20%", chartColors[2]),
        ChartDataPoint(4f, 10f, "Failed", "10%", chartColors[3])
    )
    
    DataFlowPieChart(
        data = data,
        config = ChartConfig(showLegend = false),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun GaugeWidgetContent(widget: DashboardWidget) {
    DataFlowGaugeChart(
        value = 75f,
        maxValue = 100f,
        title = "",
        subtitle = "Completion Rate",
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun DataTableWidgetContent(widget: DashboardWidget) {
    val columns = listOf(
        TableColumn("name", "Name"),
        TableColumn("value", "Value"),
        TableColumn("status", "Status")
    )
    val rows = listOf(
        TableRow("1", listOf(
            TableCell("Item 1"),
            TableCell("123"),
            TableCell("Active", type = CellType.STATUS)
        )),
        TableRow("2", listOf(
            TableCell("Item 2"),
            TableCell("456"),
            TableCell("Pending", type = CellType.STATUS)
        )),
        TableRow("3", listOf(
            TableCell("Item 3"),
            TableCell("789"),
            TableCell("Complete", type = CellType.STATUS)
        ))
    )
    
    DataFlowDataTable(
        columns = columns,
        data = rows,
        searchable = false,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ProgressWidgetContent(widget: DashboardWidget) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DataFlowProgressIndicator(
            progress = 0.75f,
            label = "Project Alpha",
            color = DataFlowColors.Blue500
        )
        DataFlowProgressIndicator(
            progress = 0.60f,
            label = "Project Beta",
            color = DataFlowColors.Green500
        )
        DataFlowProgressIndicator(
            progress = 0.40f,
            label = "Project Gamma",
            color = DataFlowColors.Orange500
        )
    }
}

@Composable
private fun StatusGridWidgetContent(widget: DashboardWidget) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(4) { index ->
            val statuses = listOf("Active", "Pending", "Complete", "Error")
            val types = listOf(StatusType.SUCCESS, StatusType.WARNING, StatusType.INFO, StatusType.ERROR)
            
            Card(
                modifier = Modifier.aspectRatio(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DataFlowStatusChip(
                        status = statuses[index],
                        type = types[index]
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(index + 1) * 25}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SparklineWidgetContent(widget: DashboardWidget) {
    val data = listOf(10f, 15f, 12f, 18f, 20f, 16f, 22f, 25f, 23f, 28f)
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "28",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Simple sparkline representation
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { value ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height((value * 3).dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Weekly Trend",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TextWidgetContent(widget: DashboardWidget) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to DataFlow",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Your data visualization platform",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlaceholderWidgetContent(widget: DashboardWidget) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Dashboard,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Widget: ${widget.type}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> "${diff / 86400000}d ago"
    }
}