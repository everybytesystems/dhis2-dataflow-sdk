package com.everybytesystems.dataflow.ui.realtime

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
import kotlinx.datetime.*
import kotlin.time.Duration

/**
 * Live Data & Real-Time Support Components
 * Real-time communication, live data streaming, and collaboration features
 */

// ============================================================================
// 🔴 DATA MODELS
// ============================================================================

enum class ConnectionType {
    WEBSOCKET,
    SSE,
    POLLING,
    WEBRTC
}

enum class MessageType {
    DATA_UPDATE,
    USER_MESSAGE,
    SYSTEM_MESSAGE,
    WARNING,
    ERROR,
    NOTIFICATION
}

enum class MessagePriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}

enum class UserStatus {
    ONLINE,
    AWAY,
    BUSY,
    OFFLINE
}

data class ConnectionStatus(
    val isConnected: Boolean,
    val connectionType: ConnectionType,
    val lastConnected: Instant?,
    val latency: Duration? = null,
    val reconnectAttempts: Int = 0,
    val error: String? = null
)

data class RealTimeMessage(
    val id: String,
    val type: MessageType,
    val content: String,
    val timestamp: Instant,
    val sender: String,
    val priority: MessagePriority = MessagePriority.NORMAL,
    val metadata: Map<String, Any> = emptyMap()
)

data class LiveDataPoint(
    val timestamp: Instant,
    val value: Double,
    val metadata: Map<String, Any> = emptyMap()
)

data class StreamingDataset(
    val id: String,
    val name: String,
    val points: List<LiveDataPoint>,
    val color: Color,
    val isActive: Boolean = true,
    val maxPoints: Int = 100
)

data class ChatUser(
    val id: String,
    val name: String,
    val avatar: String? = null,
    val status: UserStatus = UserStatus.OFFLINE
)

data class ChatMessage(
    val id: String,
    val content: String,
    val sender: ChatUser,
    val timestamp: Instant,
    val reactions: Map<String, List<String>> = emptyMap(), // emoji -> user IDs
    val replyTo: String? = null,
    val attachments: List<String> = emptyList()
)

data class PushNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Instant,
    val type: MessageType = MessageType.NOTIFICATION,
    val actions: List<NotificationAction> = emptyList(),
    val isRead: Boolean = false
)

data class NotificationAction(
    val id: String,
    val label: String,
    val action: () -> Unit
)

// ============================================================================
// 🔗 CONNECTION STATUS INDICATOR
// ============================================================================

@Composable
fun ConnectionStatusIndicator(
    status: ConnectionStatus,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (status.isConnected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connection indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (status.isConnected) Color(0xFF4CAF50) else Color(0xFFF44336),
                            shape = CircleShape
                        )
                )
                
                Column {
                    Text(
                        text = if (status.isConnected) "Connected" else "Disconnected",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = status.connectionType.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (status.latency != null) {
                            Text(
                                text = "• ${status.latency.inWholeMilliseconds}ms",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            if (!status.isConnected) {
                TextButton(onClick = onReconnect) {
                    Text("Reconnect")
                }
            }
        }
    }
}

// ============================================================================
// 📨 REAL-TIME MESSAGE FEED
// ============================================================================

@Composable
fun RealTimeMessageFeed(
    messages: List<RealTimeMessage>,
    onMessageClick: (RealTimeMessage) -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Messages (${messages.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (messages.isNotEmpty()) {
                TextButton(onClick = onClearMessages) {
                    Text("Clear All")
                }
            }
        }
        
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "No messages yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                reverseLayout = true, // Show newest messages at bottom
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(messages.reversed()) { message ->
                    RealTimeMessageCard(
                        message = message,
                        onClick = { onMessageClick(message) }
                    )
                }
            }
        }
    }
}

@Composable
fun RealTimeMessageCard(
    message: RealTimeMessage,
    onClick: () -> Unit
) {
    val (backgroundColor, iconColor, icon) = when (message.type) {
        MessageType.DATA_UPDATE -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            Icons.Default.Update
        )
        MessageType.USER_MESSAGE -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondary,
            Icons.Default.Person
        )
        MessageType.SYSTEM_MESSAGE -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            Icons.Default.Computer
        )
        MessageType.WARNING -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFFF9800),
            Icons.Default.Warning
        )
        MessageType.ERROR -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            Icons.Default.Error
        )
        MessageType.NOTIFICATION -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.tertiary,
            Icons.Default.Notifications
        )
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = formatRelativeTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (message.priority != MessagePriority.NORMAL) {
                    MessagePriorityChip(priority = message.priority)
                }
            }
        }
    }
}

// ============================================================================
// 📊 LIVE DATA CHART
// ============================================================================

@Composable
fun LiveDataChart(
    datasets: List<StreamingDataset>,
    modifier: Modifier = Modifier,
    showLegend: Boolean = true,
    animateUpdates: Boolean = true
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (showLegend) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(datasets) { dataset ->
                    DatasetLegendItem(
                        dataset = dataset,
                        isActive = dataset.isActive
                    )
                }
            }
        }
        
        // Simplified chart representation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Live Data Streaming",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${datasets.count { it.isActive }} active datasets",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (animateUpdates) {
                        LinearProgressIndicator(
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DatasetLegendItem(
    dataset: StreamingDataset,
    isActive: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = if (isActive) dataset.color else dataset.color.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        
        Text(
            text = dataset.name,
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "(${dataset.points.size})",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// 💬 CHAT INTERFACE
// ============================================================================

@Composable
fun ChatInterface(
    messages: List<ChatMessage>,
    currentUser: ChatUser,
    onSendMessage: (String) -> Unit,
    onMessageReaction: (ChatMessage, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Messages list
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(messages.reversed()) { message ->
                ChatMessageBubble(
                    message = message,
                    isCurrentUser = message.sender.id == currentUser.id,
                    onReaction = { emoji -> onMessageReaction(message, emoji) }
                )
            }
        }
        
        // Message input
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send message"
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isCurrentUser: Boolean,
    onReaction: (String) -> Unit
) {
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isCurrentUser) 
        MaterialTheme.colorScheme.primary 
    else 
        MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isCurrentUser) 
        MaterialTheme.colorScheme.onPrimary 
    else 
        MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (!isCurrentUser) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserStatusIndicator(status = message.sender.status)
                    Text(
                        text = message.sender.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                    bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    
                    Text(
                        text = formatRelativeTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Reactions
            if (message.reactions.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(message.reactions.entries.toList()) { (emoji, userIds) ->
                        AssistChip(
                            onClick = { onReaction(emoji) },
                            label = { 
                                Text(
                                    text = "$emoji ${userIds.size}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// 🔔 PUSH NOTIFICATION DISPLAY
// ============================================================================

@Composable
fun PushNotificationDisplay(
    notifications: List<PushNotification>,
    onNotificationClick: (PushNotification) -> Unit,
    onNotificationDismiss: (PushNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(notifications) { notification ->
            PushNotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                onDismiss = { onNotificationDismiss(notification) }
            )
        }
    }
}

@Composable
fun PushNotificationCard(
    notification: PushNotification,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = formatRelativeTime(notification.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (notification.actions.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notification.actions) { action ->
                            TextButton(onClick = action.action) {
                                Text(action.label)
                            }
                        }
                    }
                }
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss"
                )
            }
        }
    }
}

// ============================================================================
// 📡 STREAMING STATUS MONITOR
// ============================================================================

@Composable
fun StreamingStatusMonitor(
    streams: Map<String, Boolean>,
    onToggleStream: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Data Streams",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            streams.forEach { (streamName, isActive) ->
                StreamStatusItem(
                    name = streamName,
                    isActive = isActive,
                    onToggle = { onToggleStream(streamName, !isActive) }
                )
            }
        }
    }
}

@Composable
fun StreamStatusItem(
    name: String,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                        shape = CircleShape
                    )
            )
            
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Switch(
            checked = isActive,
            onCheckedChange = { onToggle() }
        )
    }
}

// ============================================================================
// 🎨 HELPER COMPONENTS
// ============================================================================

@Composable
fun UserStatusIndicator(status: UserStatus) {
    val (color, size) = when (status) {
        UserStatus.ONLINE -> Color(0xFF4CAF50) to 8.dp
        UserStatus.AWAY -> Color(0xFFFF9800) to 8.dp
        UserStatus.BUSY -> Color(0xFFF44336) to 8.dp
        UserStatus.OFFLINE -> Color(0xFF9E9E9E) to 6.dp
    }
    
    Box(
        modifier = Modifier
            .size(size)
            .background(color = color, shape = CircleShape)
    )
}

@Composable
fun MessagePriorityChip(priority: MessagePriority) {
    val (color, text) = when (priority) {
        MessagePriority.LOW -> Color(0xFF9E9E9E) to "Low"
        MessagePriority.NORMAL -> Color(0xFF2196F3) to "Normal"
        MessagePriority.HIGH -> Color(0xFFFF9800) to "High"
        MessagePriority.CRITICAL -> Color(0xFFF44336) to "Critical"
    }
    
    AssistChip(
        onClick = { },
        label = { 
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        modifier = Modifier.height(20.dp)
    )
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

fun formatRelativeTime(instant: Instant): String {
    val now = Clock.System.now()
    val duration = now - instant
    
    return when {
        duration.inWholeSeconds < 60 -> "Just now"
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m ago"
        duration.inWholeHours < 24 -> "${duration.inWholeHours}h ago"
        duration.inWholeDays < 7 -> "${duration.inWholeDays}d ago"
        else -> "${duration.inWholeDays / 7}w ago"
    }
}