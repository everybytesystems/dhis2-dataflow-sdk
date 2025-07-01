package com.everybytesystems.dataflow.ui.notifications

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

/**
 * Notification System Components
 * In-app notifications: toasts, snackbars, banners with actionable features
 */

// ============================================================================
// 🔔 NOTIFICATION DATA MODELS
// ============================================================================

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.INFO,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val actions: List<NotificationAction> = emptyList(),
    val autoHideDelay: Long? = null, // null = no auto hide
    val category: String = "",
    val metadata: Map<String, Any> = emptyMap()
)

data class NotificationAction(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val isPrimary: Boolean = false,
    val isDestructive: Boolean = false
)

enum class NotificationType {
    INFO, SUCCESS, WARNING, ERROR, SYSTEM
}

enum class NotificationPriority {
    LOW, NORMAL, HIGH, URGENT
}

enum class NotificationPosition {
    TOP_START, TOP_CENTER, TOP_END,
    BOTTOM_START, BOTTOM_CENTER, BOTTOM_END,
    CENTER
}

data class NotificationState(
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isNotificationPanelOpen: Boolean = false,
    val toastQueue: List<AppNotification> = emptyList(),
    val bannerNotification: AppNotification? = null
)

// ============================================================================
// 🍞 TOAST NOTIFICATION
// ============================================================================

@Composable
fun ToastNotification(
    notification: AppNotification,
    position: NotificationPosition = NotificationPosition.BOTTOM_CENTER,
    onDismiss: () -> Unit,
    onActionClick: ((NotificationAction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(notification) {
        isVisible = true
        
        // Auto-hide if specified
        notification.autoHideDelay?.let { delay ->
            kotlinx.coroutines.delay(delay)
            isVisible = false
            kotlinx.coroutines.delay(300) // Animation duration
            onDismiss()
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { if (position.name.startsWith("TOP")) -it else it }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { if (position.name.startsWith("TOP")) -it else it }
        ) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 400.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = getNotificationColor(notification.type)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                val icon = notification.icon ?: getDefaultNotificationIcon(notification.type)
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = getNotificationContentColor(notification.type)
                )
                
                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (notification.title.isNotEmpty()) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = getNotificationContentColor(notification.type)
                        )
                    }
                    
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = getNotificationContentColor(notification.type)
                    )
                    
                    // Actions
                    if (notification.actions.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            notification.actions.forEach { action ->
                                ToastActionButton(
                                    action = action,
                                    notificationType = notification.type,
                                    onClick = { onActionClick?.invoke(action) }
                                )
                            }
                        }
                    }
                }
                
                // Dismiss button
                IconButton(
                    onClick = {
                        isVisible = false
                        onDismiss()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        modifier = Modifier.size(16.dp),
                        tint = getNotificationContentColor(notification.type)
                    )
                }
            }
        }
    }
}

@Composable
fun ToastActionButton(
    action: NotificationAction,
    notificationType: NotificationType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonColors = if (action.isPrimary) {
        ButtonDefaults.buttonColors(
            containerColor = getNotificationContentColor(notificationType),
            contentColor = getNotificationColor(notificationType)
        )
    } else {
        ButtonDefaults.textButtonColors(
            contentColor = getNotificationContentColor(notificationType)
        )
    }
    
    if (action.isPrimary) {
        Button(
            onClick = onClick,
            modifier = modifier.height(32.dp),
            colors = buttonColors,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
            action.icon?.let { icon ->
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    } else {
        TextButton(
            onClick = onClick,
            modifier = modifier.height(32.dp),
            colors = buttonColors,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            action.icon?.let { icon ->
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ============================================================================
// 📢 BANNER NOTIFICATION
// ============================================================================

@Composable
fun BannerNotification(
    notification: AppNotification,
    onDismiss: () -> Unit,
    onActionClick: ((NotificationAction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(notification) {
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = getNotificationColor(notification.type)
            ),
            shape = RectangleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                val icon = notification.icon ?: getDefaultNotificationIcon(notification.type)
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = getNotificationContentColor(notification.type)
                )
                
                // Content
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (notification.title.isNotEmpty()) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = getNotificationContentColor(notification.type)
                        )
                    }
                    
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = getNotificationContentColor(notification.type)
                    )
                }
                
                // Actions
                if (notification.actions.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        notification.actions.take(2).forEach { action ->
                            ToastActionButton(
                                action = action,
                                notificationType = notification.type,
                                onClick = { onActionClick?.invoke(action) }
                            )
                        }
                    }
                }
                
                // Dismiss button
                IconButton(
                    onClick = {
                        isVisible = false
                        onDismiss()
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = getNotificationContentColor(notification.type)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🔔 NOTIFICATION PANEL
// ============================================================================

@Composable
fun NotificationPanel(
    state: NotificationState,
    onStateChange: (NotificationState) -> Unit,
    onNotificationClick: ((AppNotification) -> Unit)? = null,
    onNotificationDismiss: ((AppNotification) -> Unit)? = null,
    onActionClick: ((AppNotification, NotificationAction) -> Unit)? = null,
    onMarkAllRead: (() -> Unit)? = null,
    onClearAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            // Header
            NotificationPanelHeader(
                unreadCount = state.unreadCount,
                onMarkAllRead = onMarkAllRead,
                onClearAll = onClearAll,
                onClose = {
                    onStateChange(state.copy(isNotificationPanelOpen = false))
                }
            )
            
            HorizontalDivider()
            
            // Notification list
            if (state.notifications.isEmpty()) {
                NotificationEmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(
                        items = state.notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationListItem(
                            notification = notification,
                            onClick = { onNotificationClick?.invoke(notification) },
                            onDismiss = { onNotificationDismiss?.invoke(notification) },
                            onActionClick = { action ->
                                onActionClick?.invoke(notification, action)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationPanelHeader(
    unreadCount: Int,
    onMarkAllRead: (() -> Unit)?,
    onClearAll: (() -> Unit)?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (unreadCount > 0) {
                Badge {
                    Text(
                        text = unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onMarkAllRead != null && unreadCount > 0) {
                TextButton(onClick = onMarkAllRead) {
                    Text("Mark all read")
                }
            }
            
            if (onClearAll != null) {
                TextButton(onClick = onClearAll) {
                    Text("Clear all")
                }
            }
            
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
    }
}

@Composable
fun NotificationListItem(
    notification: AppNotification,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    onActionClick: (NotificationAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Unread indicator
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                        .padding(top = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Icon
            val icon = notification.icon ?: getDefaultNotificationIcon(notification.type)
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = getNotificationIconColor(notification.type)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (notification.title.isNotEmpty()) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3
                )
                
                Text(
                    text = formatNotificationTime(notification.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Actions
                if (notification.actions.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        notification.actions.take(2).forEach { action ->
                            if (action.isPrimary) {
                                Button(
                                    onClick = { onActionClick(action) },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = action.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onActionClick(action) },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = action.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Dismiss button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun NotificationEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "No notifications",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "You're all caught up!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ============================================================================
// 🔔 NOTIFICATION BADGE
// ============================================================================

@Composable
fun NotificationBadge(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showZero: Boolean = false
) {
    Box(modifier = modifier) {
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }
        
        if (count > 0 || showZero) {
            Badge(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

// ============================================================================
// 📱 SNACKBAR EXTENSIONS
// ============================================================================

@Composable
fun ActionableSnackbar(
    notification: AppNotification,
    onDismiss: () -> Unit,
    onActionClick: ((NotificationAction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        action = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                notification.actions.take(1).forEach { action ->
                    TextButton(
                        onClick = { onActionClick?.invoke(action) }
                    ) {
                        Text(action.label)
                    }
                }
                
                TextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        },
        dismissAction = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss")
            }
        }
    ) {
        Column {
            if (notification.title.isNotEmpty()) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.SUCCESS -> Color(0xFF4CAF50)
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.ERROR -> Color(0xFFF44336)
        NotificationType.INFO -> Color(0xFF2196F3)
        NotificationType.SYSTEM -> Color(0xFF9C27B0)
    }
}

private fun getNotificationContentColor(type: NotificationType): Color {
    return Color.White
}

private fun getNotificationIconColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.SUCCESS -> Color(0xFF4CAF50)
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.ERROR -> Color(0xFFF44336)
        NotificationType.INFO -> Color(0xFF2196F3)
        NotificationType.SYSTEM -> Color(0xFF9C27B0)
    }
}

private fun getDefaultNotificationIcon(type: NotificationType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
        NotificationType.WARNING -> Icons.Default.Warning
        NotificationType.ERROR -> Icons.Default.Error
        NotificationType.INFO -> Icons.Default.Info
        NotificationType.SYSTEM -> Icons.Default.Settings
    }
}

private fun formatNotificationTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> {
            val date = java.util.Date(timestamp)
            java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault()).format(date)
        }
    }
}