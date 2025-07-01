package com.everybytesystems.dataflow.ui.lists

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.hapticfeedback.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.math.*

/**
 * Enhanced List Components
 * Advanced list interactions, animations, and layouts
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

enum class SwipeDirection {
    LEFT,
    RIGHT,
    BOTH
}

enum class ListItemType {
    SIMPLE,
    TWO_LINE,
    THREE_LINE,
    CUSTOM
}

enum class TimelineEventType {
    MESSAGE,
    CALL,
    EMAIL,
    MEETING,
    TASK,
    MILESTONE,
    SYSTEM,
    ERROR,
    WARNING,
    SUCCESS
}

data class SwipeAction(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val backgroundColor: Color,
    val action: () -> Unit
)

data class ListItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val badge: String? = null,
    val timestamp: Instant? = null,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
    val metadata: Map<String, Any> = emptyMap()
)

data class TimelineEvent(
    val id: String,
    val title: String,
    val description: String,
    val timestamp: Instant,
    val type: TimelineEventType,
    val user: String? = null,
    val avatar: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val isRead: Boolean = false
)

data class DragDropState(
    val isDragging: Boolean = false,
    val draggedItemId: String? = null,
    val dragOffset: Offset = Offset.Zero,
    val dropTargetIndex: Int? = null
)

// ============================================================================
// 🔄 SWIPEABLE LIST
// ============================================================================

@Composable
fun SwipeableList(
    items: List<ListItem>,
    onItemClick: (ListItem) -> Unit,
    modifier: Modifier = Modifier,
    leftActions: List<SwipeAction> = emptyList(),
    rightActions: List<SwipeAction> = emptyList(),
    swipeThreshold: Dp = 80.dp,
    enableHapticFeedback: Boolean = true,
    showDividers: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val haptic = LocalHapticFeedback.current
    
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(if (showDividers) 0.dp else 4.dp)
    ) {
        itemsIndexed(items) { index, item ->
            SwipeableListItem(
                item = item,
                leftActions = leftActions,
                rightActions = rightActions,
                swipeThreshold = swipeThreshold,
                onItemClick = { onItemClick(item) },
                onSwipeAction = { action ->
                    if (enableHapticFeedback) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    action.action()
                }
            )
            
            if (showDividers && index < items.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun SwipeableListItem(
    item: ListItem,
    leftActions: List<SwipeAction>,
    rightActions: List<SwipeAction>,
    swipeThreshold: Dp,
    onItemClick: () -> Unit,
    onSwipeAction: (SwipeAction) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isRevealed by remember { mutableStateOf(false) }
    var revealedSide by remember { mutableStateOf<SwipeDirection?>(null) }
    
    val thresholdPx = with(LocalDensity.current) { swipeThreshold.toPx() }
    val maxSwipeDistance = thresholdPx * 2
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
    ) {
        // Background actions
        if (leftActions.isNotEmpty()) {
            SwipeActionsBackground(
                actions = leftActions,
                isVisible = offsetX > 0,
                progress = (offsetX / maxSwipeDistance).coerceIn(0f, 1f),
                onActionClick = { action ->
                    onSwipeAction(action)
                    offsetX = 0f
                    isRevealed = false
                    revealedSide = null
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxHeight()
            )
        }
        
        if (rightActions.isNotEmpty()) {
            SwipeActionsBackground(
                actions = rightActions,
                isVisible = offsetX < 0,
                progress = (abs(offsetX) / maxSwipeDistance).coerceIn(0f, 1f),
                onActionClick = { action ->
                    onSwipeAction(action)
                    offsetX = 0f
                    isRevealed = false
                    revealedSide = null
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
            )
        }
        
        // Main list item
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val targetOffset = when {
                                offsetX > thresholdPx && leftActions.isNotEmpty() -> {
                                    isRevealed = true
                                    revealedSide = SwipeDirection.LEFT
                                    maxSwipeDistance
                                }
                                offsetX < -thresholdPx && rightActions.isNotEmpty() -> {
                                    isRevealed = true
                                    revealedSide = SwipeDirection.RIGHT
                                    -maxSwipeDistance
                                }
                                else -> {
                                    isRevealed = false
                                    revealedSide = null
                                    0f
                                }
                            }
                            
                            // Animate to target position
                            // In a real implementation, you would use Animatable
                            offsetX = targetOffset
                        }
                    ) { _, dragAmount ->
                        val newOffset = offsetX + dragAmount
                        val maxLeft = if (leftActions.isNotEmpty()) maxSwipeDistance else 0f
                        val maxRight = if (rightActions.isNotEmpty()) -maxSwipeDistance else 0f
                        
                        offsetX = newOffset.coerceIn(maxRight, maxLeft)
                    }
                }
                .clickable(enabled = !isRevealed) { onItemClick() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isRevealed) 4.dp else 0.dp
            )
        ) {
            EnhancedListItemContent(
                item = item,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun SwipeActionsBackground(
    actions: List<SwipeAction>,
    isVisible: Boolean,
    progress: Float,
    onActionClick: (SwipeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            actions.forEach { action ->
                SwipeActionButton(
                    action = action,
                    progress = progress,
                    onClick = { onActionClick(action) }
                )
            }
        }
    }
}

@Composable
private fun SwipeActionButton(
    action: SwipeAction,
    progress: Float,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (progress > 0.5f) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = action.backgroundColor,
            contentColor = action.color
        ),
        shape = RectangleShape,
        contentPadding = PaddingValues(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

// ============================================================================
// 🔄 DRAG & DROP LIST
// ============================================================================

@Composable
fun DragDropList(
    items: List<ListItem>,
    onItemsReordered: (List<ListItem>) -> Unit,
    onItemClick: (ListItem) -> Unit,
    modifier: Modifier = Modifier,
    enableDragDrop: Boolean = true,
    showDragHandle: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var dragDropState by remember { mutableStateOf(DragDropState()) }
    var reorderedItems by remember(items) { mutableStateOf(items) }
    val haptic = LocalHapticFeedback.current
    
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(reorderedItems) { index, item ->
            DragDropListItem(
                item = item,
                index = index,
                isDragging = dragDropState.draggedItemId == item.id,
                isDropTarget = dragDropState.dropTargetIndex == index,
                showDragHandle = showDragHandle && enableDragDrop,
                onItemClick = { onItemClick(item) },
                onDragStart = {
                    if (enableDragDrop) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        dragDropState = dragDropState.copy(
                            isDragging = true,
                            draggedItemId = item.id
                        )
                    }
                },
                onDragEnd = { targetIndex ->
                    if (enableDragDrop && targetIndex != null && targetIndex != index) {
                        val newItems = reorderedItems.toMutableList()
                        val draggedItem = newItems.removeAt(index)
                        newItems.add(targetIndex, draggedItem)
                        reorderedItems = newItems
                        onItemsReordered(newItems)
                    }
                    
                    dragDropState = DragDropState()
                }
            )
        }
    }
}

@Composable
private fun DragDropListItem(
    item: ListItem,
    index: Int,
    isDragging: Boolean,
    isDropTarget: Boolean,
    showDragHandle: Boolean,
    onItemClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: (Int?) -> Unit
) {
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 2.dp,
        animationSpec = spring()
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        animationSpec = spring()
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(
                if (isDropTarget) {
                    Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    )
                } else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Drag handle
            if (showDragHandle) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag Handle",
                    modifier = Modifier
                        .size(24.dp)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { onDragStart() },
                                onDragEnd = { onDragEnd(null) }
                            ) { _, _ ->
                                // Handle drag gesture
                            }
                        },
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // List item content
            EnhancedListItemContent(
                item = item,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ============================================================================
// 📋 ENHANCED LIST ITEM CONTENT
// ============================================================================

@Composable
fun EnhancedListItemContent(
    item: ListItem,
    modifier: Modifier = Modifier,
    showTimestamp: Boolean = true,
    showBadge: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Leading icon
        item.leadingIcon?.let { icon ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (item.isEnabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.weight(1f)
                )
                
                // Timestamp
                if (showTimestamp && item.timestamp != null) {
                    Text(
                        text = formatRelativeTime(item.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Subtitle
            item.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Description
            item.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        // Trailing content
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Badge
            if (showBadge && item.badge != null) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
            
            // Trailing icon
            item.trailingIcon?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 📅 TIMELINE LIST
// ============================================================================

@Composable
fun TimelineList(
    events: List<TimelineEvent>,
    onEventClick: (TimelineEvent) -> Unit,
    modifier: Modifier = Modifier,
    showUserAvatars: Boolean = true,
    groupByDate: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val groupedEvents = if (groupByDate) {
        events.groupBy { event ->
            val instant = event.timestamp
            instant.toString().substringBefore('T') // Simple date grouping
        }
    } else {
        mapOf("" to events)
    }
    
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedEvents.forEach { (date, dayEvents) ->
            if (groupByDate && date.isNotEmpty()) {
                item {
                    TimelineDateHeader(date = date)
                }
            }
            
            items(dayEvents) { event ->
                TimelineEventItem(
                    event = event,
                    showAvatar = showUserAvatars,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
private fun TimelineDateHeader(date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.weight(1f))
        Text(
            text = date,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TimelineEventItem(
    event: TimelineEvent,
    showAvatar: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (event.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Timeline indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TimelineEventIcon(type = event.type)
                
                // Timeline line (simplified)
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(1.dp)
                        )
                )
            }
            
            // Event content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (event.isRead) FontWeight.Normal else FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = formatRelativeTime(event.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Description
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // User info
                if (showAvatar && event.user != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Avatar placeholder
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = event.user.first().uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        
                        Text(
                            text = event.user,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineEventIcon(type: TimelineEventType) {
    val (icon, color) = when (type) {
        TimelineEventType.MESSAGE -> Icons.Default.Message to Color(0xFF2196F3)
        TimelineEventType.CALL -> Icons.Default.Call to Color(0xFF4CAF50)
        TimelineEventType.EMAIL -> Icons.Default.Email to Color(0xFFFF9800)
        TimelineEventType.MEETING -> Icons.Default.Event to Color(0xFF9C27B0)
        TimelineEventType.TASK -> Icons.Default.Task to Color(0xFF607D8B)
        TimelineEventType.MILESTONE -> Icons.Default.Flag to Color(0xFFFFEB3B)
        TimelineEventType.SYSTEM -> Icons.Default.Settings to Color(0xFF795548)
        TimelineEventType.ERROR -> Icons.Default.Error to Color(0xFFF44336)
        TimelineEventType.WARNING -> Icons.Default.Warning to Color(0xFFFF9800)
        TimelineEventType.SUCCESS -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
    }
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color.copy(alpha = 0.1f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = type.name,
            modifier = Modifier.size(16.dp),
            tint = color
        )
    }
}

// ============================================================================
// 🔍 SEARCHABLE LIST
// ============================================================================

@Composable
fun SearchableList(
    items: List<ListItem>,
    onItemClick: (ListItem) -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Search...",
    enableFiltering: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    var searchQuery by remember { mutableStateOf("") }
    var filteredItems by remember(items, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) {
                items
            } else {
                items.filter { item ->
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle?.contains(searchQuery, ignoreCase = true) == true ||
                    item.description?.contains(searchQuery, ignoreCase = true) == true
                }
            }
        )
    }
    
    Column(modifier = modifier) {
        // Search bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = searchPlaceholder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Results count
        if (searchQuery.isNotBlank()) {
            Text(
                text = "${filteredItems.size} results found",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        
        // List
        LazyColumn(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    EnhancedListItemContent(
                        item = item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (filteredItems.isEmpty() && searchQuery.isNotBlank()) {
                item {
                    EmptySearchResults(
                        query = searchQuery,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        modifier = modifier,
        singleLine = true,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun EmptySearchResults(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Try adjusting your search for \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================================
// ♾️ INFINITE SCROLL LIST
// ============================================================================

@Composable
fun InfiniteScrollList(
    items: List<ListItem>,
    onLoadMore: () -> Unit,
    onItemClick: (ListItem) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    hasMoreItems: Boolean = true,
    loadThreshold: Int = 5,
    contentPadding: PaddingValues = PaddingValues(16.dp)
) {
    val listState = rememberLazyListState()
    
    // Trigger load more when approaching end
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = items.size
                
                if (hasMoreItems && !isLoading && 
                    lastVisibleIndex >= totalItems - loadThreshold) {
                    onLoadMore()
                }
            }
    }
    
    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                EnhancedListItemContent(
                    item = item,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        if (isLoading) {
            item {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        
        if (!hasMoreItems && items.isNotEmpty()) {
            item {
                EndOfListIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Loading more...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EndOfListIndicator(
    modifier: Modifier = Modifier
) {
    Text(
        text = "You've reached the end",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private fun formatRelativeTime(instant: Instant): String {
    val now = Clock.System.now()
    val duration = now - instant
    
    return when {
        duration.inWholeMinutes < 1 -> "Just now"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m ago"
        duration.inWholeHours < 24 -> "${duration.inWholeHours}h ago"
        duration.inWholeDays < 7 -> "${duration.inWholeDays}d ago"
        else -> instant.toString().substringBefore('T')
    }
}

// Sample data generators for testing
fun generateSampleListItems(count: Int = 20): List<ListItem> {
    return (1..count).map { index ->
        ListItem(
            id = "item_$index",
            title = "Item $index",
            subtitle = "Subtitle for item $index",
            description = "This is a longer description for item $index that provides more context.",
            leadingIcon = when (index % 4) {
                0 -> Icons.Default.Person
                1 -> Icons.Default.Email
                2 -> Icons.Default.Call
                else -> Icons.Default.Message
            },
            trailingIcon = Icons.Default.ChevronRight,
            badge = if (index % 5 == 0) "${(1..9).random()}" else null,
            timestamp = Clock.System.now().minus((index * 3600).seconds),
            isSelected = index % 7 == 0
        )
    }
}

fun generateSampleTimelineEvents(count: Int = 15): List<TimelineEvent> {
    val types = TimelineEventType.values()
    val users = listOf("Alice", "Bob", "Charlie", "Diana", "Eve")
    
    return (1..count).map { index ->
        TimelineEvent(
            id = "event_$index",
            title = "Event $index",
            description = "Description for timeline event $index with some additional context.",
            timestamp = Clock.System.now().minus((index * 1800).seconds),
            type = types[index % types.size],
            user = users[index % users.size],
            isRead = index % 3 != 0
        )
    }
}

fun generateSampleSwipeActions(): Pair<List<SwipeAction>, List<SwipeAction>> {
    val leftActions = listOf(
        SwipeAction(
            id = "archive",
            label = "Archive",
            icon = Icons.Default.Archive,
            color = Color.White,
            backgroundColor = Color(0xFF2196F3),
            action = { println("Archive action") }
        ),
        SwipeAction(
            id = "mark_read",
            label = "Read",
            icon = Icons.Default.MarkEmailRead,
            color = Color.White,
            backgroundColor = Color(0xFF4CAF50),
            action = { println("Mark as read action") }
        )
    )
    
    val rightActions = listOf(
        SwipeAction(
            id = "delete",
            label = "Delete",
            icon = Icons.Default.Delete,
            color = Color.White,
            backgroundColor = Color(0xFFF44336),
            action = { println("Delete action") }
        ),
        SwipeAction(
            id = "more",
            label = "More",
            icon = Icons.Default.MoreVert,
            color = Color.White,
            backgroundColor = Color(0xFF9C27B0),
            action = { println("More actions") }
        )
    )
    
    return Pair(leftActions, rightActions)
}