package com.everybytesystems.dataflow.ui.components

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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.everybytesystems.dataflow.ui.theme.DataFlowColors
import com.everybytesystems.dataflow.ui.theme.getStatusColor
import kotlinx.coroutines.delay

/**
 * DataFlow UI Components
 * Complete set of modern, accessible UI components for data applications
 */

// ============================================================================
// 📊 METRIC CARDS
// ============================================================================

/**
 * Modern metric card with trend indicators and animations
 */
@Composable
fun DataFlowMetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    trend: MetricTrend? = null,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with icon and trend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                trend?.let { TrendIndicator(trend = it) }
            }
            
            // Main value
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
            
            // Subtitle
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Metric trend data
 */
data class MetricTrend(
    val percentage: Float,
    val direction: TrendDirection,
    val period: String = ""
)

enum class TrendDirection {
    UP, DOWN, STABLE
}

@Composable
private fun TrendIndicator(trend: MetricTrend) {
    val color = when (trend.direction) {
        TrendDirection.UP -> DataFlowColors.Success
        TrendDirection.DOWN -> DataFlowColors.Error
        TrendDirection.STABLE -> DataFlowColors.Gray500
    }
    
    val icon = when (trend.direction) {
        TrendDirection.UP -> Icons.Default.TrendingUp
        TrendDirection.DOWN -> Icons.Default.TrendingDown
        TrendDirection.STABLE -> Icons.Default.TrendingFlat
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "${if (trend.percentage > 0) "+" else ""}${trend.percentage}%",
            style = MaterialTheme.typography.labelLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================================
// 📋 DATA TABLES
// ============================================================================

/**
 * Modern data table with sorting, filtering, and selection
 */
@Composable
fun DataFlowDataTable(
    columns: List<TableColumn>,
    data: List<TableRow>,
    modifier: Modifier = Modifier,
    selectable: Boolean = false,
    sortable: Boolean = true,
    searchable: Boolean = true,
    onRowClick: ((TableRow) -> Unit)? = null,
    onSelectionChange: ((List<TableRow>) -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortColumn by remember { mutableStateOf<Int?>(null) }
    var sortAscending by remember { mutableStateOf(true) }
    var selectedRows by remember { mutableStateOf(setOf<String>()) }
    
    // Filter and sort data
    val filteredData = remember(data, searchQuery, sortColumn, sortAscending) {
        var result = data
        
        // Apply search filter
        if (searchQuery.isNotEmpty()) {
            result = result.filter { row ->
                row.cells.any { cell ->
                    cell.value.contains(searchQuery, ignoreCase = true)
                }
            }
        }
        
        // Apply sorting
        sortColumn?.let { columnIndex ->
            result = result.sortedWith { row1, row2 ->
                val cell1 = row1.cells.getOrNull(columnIndex)?.value ?: ""
                val cell2 = row2.cells.getOrNull(columnIndex)?.value ?: ""
                
                val comparison = cell1.compareTo(cell2, ignoreCase = true)
                if (sortAscending) comparison else -comparison
            }
        }
        
        result
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Search bar
            if (searchable) {
                DataFlowSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search table...",
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            // Table header
            DataFlowTableHeader(
                columns = columns,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                selectable = selectable,
                allSelected = selectedRows.size == filteredData.size,
                onSort = if (sortable) { columnIndex ->
                    if (sortColumn == columnIndex) {
                        sortAscending = !sortAscending
                    } else {
                        sortColumn = columnIndex
                        sortAscending = true
                    }
                } else null,
                onSelectAll = if (selectable) { selectAll ->
                    selectedRows = if (selectAll) {
                        filteredData.map { it.id }.toSet()
                    } else {
                        emptySet()
                    }
                    onSelectionChange?.invoke(
                        filteredData.filter { it.id in selectedRows }
                    )
                } else null
            )
            
            Divider()
            
            // Table content
            LazyColumn {
                items(filteredData.size) { index ->
                    val row = filteredData[index]
                    val isSelected = row.id in selectedRows
                    
                    DataFlowTableRow(
                        row = row,
                        columns = columns,
                        isSelected = isSelected,
                        selectable = selectable,
                        onClick = { onRowClick?.invoke(row) },
                        onSelectionChange = if (selectable) { selected ->
                            selectedRows = if (selected) {
                                selectedRows + row.id
                            } else {
                                selectedRows - row.id
                            }
                            onSelectionChange?.invoke(
                                filteredData.filter { it.id in selectedRows }
                            )
                        } else null
                    )
                    
                    if (index < filteredData.size - 1) {
                        Divider()
                    }
                }
            }
        }
    }
}

/**
 * Table column definition
 */
data class TableColumn(
    val key: String,
    val title: String,
    val width: TableColumnWidth = TableColumnWidth.Flexible(1f),
    val alignment: Alignment.Horizontal = Alignment.Start,
    val sortable: Boolean = true
)

sealed class TableColumnWidth {
    data class Fixed(val width: Dp) : TableColumnWidth()
    data class Flexible(val weight: Float) : TableColumnWidth()
}

/**
 * Table row data
 */
data class TableRow(
    val id: String,
    val cells: List<TableCell>
)

/**
 * Table cell data
 */
data class TableCell(
    val value: String,
    val displayValue: String = value,
    val type: CellType = CellType.TEXT,
    val metadata: Map<String, Any> = emptyMap()
)

enum class CellType {
    TEXT, NUMBER, DATE, STATUS, PROGRESS, LINK, IMAGE
}

@Composable
private fun DataFlowTableHeader(
    columns: List<TableColumn>,
    sortColumn: Int?,
    sortAscending: Boolean,
    selectable: Boolean,
    allSelected: Boolean,
    onSort: ((Int) -> Unit)?,
    onSelectAll: ((Boolean) -> Unit)?
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox
            if (selectable && onSelectAll != null) {
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = onSelectAll,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
            
            // Column headers
            columns.forEachIndexed { index, column ->
                when (column.width) {
                    is TableColumnWidth.Fixed -> {
                        Box(
                            modifier = Modifier.width(column.width.width)
                        ) {
                            HeaderCell(
                                column = column,
                                index = index,
                                sortColumn = sortColumn,
                                sortAscending = sortAscending,
                                onSort = onSort
                            )
                        }
                    }
                    is TableColumnWidth.Flexible -> {
                        Box(
                            modifier = Modifier.weight(column.width.weight)
                        ) {
                            HeaderCell(
                                column = column,
                                index = index,
                                sortColumn = sortColumn,
                                sortAscending = sortAscending,
                                onSort = onSort
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(
    column: TableColumn,
    index: Int,
    sortColumn: Int?,
    sortAscending: Boolean,
    onSort: ((Int) -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onSort != null && column.sortable) {
                    Modifier.clickable { onSort(index) }
                } else Modifier
            ),
        horizontalArrangement = when (column.alignment) {
            Alignment.Start -> Arrangement.Start
            Alignment.CenterHorizontally -> Arrangement.Center
            Alignment.End -> Arrangement.End
            else -> Arrangement.Start
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = column.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (onSort != null && column.sortable) {
            Spacer(modifier = Modifier.width(4.dp))
            
            val sortIcon = when {
                sortColumn == index && sortAscending -> Icons.Default.KeyboardArrowUp
                sortColumn == index && !sortAscending -> Icons.Default.KeyboardArrowDown
                else -> Icons.Default.UnfoldMore
            }
            
            Icon(
                imageVector = sortIcon,
                contentDescription = "Sort",
                modifier = Modifier.size(16.dp),
                tint = if (sortColumn == index) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun DataFlowTableRow(
    row: TableRow,
    columns: List<TableColumn>,
    isSelected: Boolean,
    selectable: Boolean,
    onClick: () -> Unit,
    onSelectionChange: ((Boolean) -> Unit)?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Selection checkbox
            if (selectable && onSelectionChange != null) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = onSelectionChange,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
            
            // Cell content
            row.cells.forEachIndexed { index, cell ->
                val column = columns.getOrNull(index)
                if (column != null) {
                    when (column.width) {
                        is TableColumnWidth.Fixed -> {
                            Box(
                                modifier = Modifier.width(column.width.width)
                            ) {
                                TableCellContent(cell, column)
                            }
                        }
                        is TableColumnWidth.Flexible -> {
                            Box(
                                modifier = Modifier.weight(column.width.weight)
                            ) {
                                TableCellContent(cell, column)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableCellContent(
    cell: TableCell,
    column: TableColumn
) {
    when (cell.type) {
        CellType.TEXT -> {
            Text(
                text = cell.displayValue,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = when (column.alignment) {
                    Alignment.Start -> TextAlign.Start
                    Alignment.CenterHorizontally -> TextAlign.Center
                    Alignment.End -> TextAlign.End
                    else -> TextAlign.Start
                }
            )
        }
        CellType.NUMBER -> {
            Text(
                text = cell.displayValue,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.End
            )
        }
        CellType.STATUS -> {
            DataFlowStatusChip(
                status = cell.displayValue,
                type = when (cell.value.lowercase()) {
                    "active", "success", "complete" -> StatusType.SUCCESS
                    "pending", "warning" -> StatusType.WARNING
                    "error", "failed" -> StatusType.ERROR
                    else -> StatusType.INFO
                }
            )
        }
        CellType.PROGRESS -> {
            val progress = cell.value.toFloatOrNull() ?: 0f
            DataFlowProgressIndicator(
                progress = progress / 100f,
                showPercentage = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        else -> {
            Text(
                text = cell.displayValue,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ============================================================================
// 🔍 SEARCH COMPONENTS
// ============================================================================

/**
 * Modern search bar with suggestions
 */
@Composable
fun DataFlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    suggestions: List<String> = emptyList(),
    onSuggestionClick: ((String) -> Unit)? = null
) {
    var showSuggestions by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { 
                onQueryChange(it)
                showSuggestions = it.isNotEmpty() && suggestions.isNotEmpty()
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { 
                        onQueryChange("")
                        showSuggestions = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        // Suggestions dropdown
        if (showSuggestions && onSuggestionClick != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(suggestions.take(5)) { suggestion ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSuggestionClick(suggestion)
                                    showSuggestions = false
                                }
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 🎯 STATUS COMPONENTS
// ============================================================================

/**
 * Status chip with color coding
 */
@Composable
fun DataFlowStatusChip(
    status: String,
    type: StatusType = StatusType.INFO,
    modifier: Modifier = Modifier
) {
    val colors = when (type) {
        StatusType.SUCCESS -> Pair(DataFlowColors.Success, Color.White)
        StatusType.WARNING -> Pair(DataFlowColors.Warning, Color.White)
        StatusType.ERROR -> Pair(DataFlowColors.Error, Color.White)
        StatusType.INFO -> Pair(MaterialTheme.colorScheme.primary, Color.White)
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = colors.first
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall,
            color = colors.second,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

enum class StatusType {
    SUCCESS, WARNING, ERROR, INFO
}

// ============================================================================
// 📊 PROGRESS COMPONENTS
// ============================================================================

/**
 * Progress indicator with customizable styling
 */
@Composable
fun DataFlowProgressIndicator(
    progress: Float,
    label: String? = null,
    modifier: Modifier = Modifier,
    showPercentage: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (label != null || showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                label?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
        
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = trackColor
        )
    }
}

// ============================================================================
// 🔄 LOADING COMPONENTS
// ============================================================================

/**
 * Modern loading indicator
 */
@Composable
fun DataFlowLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(size)
                .rotate(rotation),
            color = color,
            strokeWidth = 3.dp
        )
    }
}

/**
 * Skeleton loading placeholder
 */
@Composable
fun DataFlowSkeletonLoader(
    modifier: Modifier = Modifier,
    height: Dp = 20.dp,
    width: Dp? = null
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier
            .then(
                if (width != null) Modifier.width(width) else Modifier.fillMaxWidth()
            )
            .height(height)
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                shape = RoundedCornerShape(4.dp)
            )
    )
}

// ============================================================================
// ⚠️ ERROR STATES
// ============================================================================

/**
 * Error state with retry option
 */
@Composable
fun DataFlowErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        onRetry?.let {
            Button(
                onClick = it,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

// ============================================================================
// 🎨 UTILITY COMPONENTS
// ============================================================================

/**
 * Animated visibility wrapper
 */
@Composable
fun DataFlowAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + slideInVertically(),
    exit: ExitTransition = fadeOut() + slideOutVertically(),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit,
        content = content
    )
}

/**
 * Pulsing animation modifier
 */
@Composable
fun Modifier.pulse(): Modifier {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    return this.alpha(alpha)
}