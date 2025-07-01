package com.everybytesystems.dataflow.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

/**
 * Error Handling and Feedback Components
 * Comprehensive error states and user feedback
 */

// ============================================================================
// ❌ ERROR TYPES
// ============================================================================

enum class ErrorType {
    NETWORK,
    VALIDATION,
    PERMISSION,
    NOT_FOUND,
    SERVER,
    TIMEOUT,
    UNKNOWN
}

data class ErrorInfo(
    val type: ErrorType,
    val title: String,
    val message: String,
    val details: String? = null,
    val canRetry: Boolean = true,
    val actionLabel: String? = null
)

// ============================================================================
// 🚨 ERROR DISPLAYS
// ============================================================================

@Composable
fun ErrorState(
    error: ErrorInfo,
    onRetry: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error icon
                Icon(
                    imageVector = getErrorIcon(error.type),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                // Error title
                Text(
                    text = error.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                
                // Error message
                Text(
                    text = error.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
                
                // Error details (expandable)
                if (!error.details.isNullOrEmpty()) {
                    var showDetails by remember { mutableStateOf(false) }
                    
                    TextButton(
                        onClick = { showDetails = !showDetails }
                    ) {
                        Text(
                            text = if (showDetails) "Hide Details" else "Show Details",
                            color = MaterialTheme.colorScheme.error
                        )
                        Icon(
                            imageVector = if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    
                    AnimatedVisibility(
                        visible = showDetails,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                text = error.details,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (error.canRetry && onRetry != null) {
                        Button(
                            onClick = onRetry,
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
                    
                    if (error.actionLabel != null && onAction != null) {
                        OutlinedButton(
                            onClick = onAction,
                            colors = OutlinedButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Text(error.actionLabel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InlineError(
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Error,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 📢 SNACKBAR COMPONENTS
// ============================================================================

@Composable
fun DataFlowSnackbar(
    message: String,
    type: SnackbarType = SnackbarType.INFO,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (type) {
        SnackbarType.SUCCESS -> Color(0xFF4CAF50)
        SnackbarType.WARNING -> Color(0xFFFF9800)
        SnackbarType.ERROR -> MaterialTheme.colorScheme.error
        SnackbarType.INFO -> MaterialTheme.colorScheme.inverseSurface
    }
    
    val contentColor = when (type) {
        SnackbarType.SUCCESS -> Color.White
        SnackbarType.WARNING -> Color.White
        SnackbarType.ERROR -> MaterialTheme.colorScheme.onError
        SnackbarType.INFO -> MaterialTheme.colorScheme.inverseOnSurface
    }
    
    Snackbar(
        modifier = modifier,
        containerColor = backgroundColor,
        contentColor = contentColor,
        action = if (action != null && onAction != null) {
            {
                TextButton(
                    onClick = onAction,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = contentColor
                    )
                ) {
                    Text(action)
                }
            }
        } else null,
        dismissAction = if (onDismiss != null) {
            {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = contentColor
                    )
                }
            }
        } else null
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = getSnackbarIcon(type),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

enum class SnackbarType {
    SUCCESS, WARNING, ERROR, INFO
}

// ============================================================================
// 🔔 NOTIFICATION COMPONENTS
// ============================================================================

@Composable
fun NotificationBanner(
    title: String,
    message: String,
    type: NotificationType = NotificationType.INFO,
    onDismiss: (() -> Unit)? = null,
    onAction: (() -> Unit)? = null,
    actionLabel: String? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (type) {
        NotificationType.SUCCESS -> Color(0xFF4CAF50).copy(alpha = 0.1f)
        NotificationType.WARNING -> Color(0xFFFF9800).copy(alpha = 0.1f)
        NotificationType.ERROR -> MaterialTheme.colorScheme.errorContainer
        NotificationType.INFO -> MaterialTheme.colorScheme.primaryContainer
    }
    
    val borderColor = when (type) {
        NotificationType.SUCCESS -> Color(0xFF4CAF50)
        NotificationType.WARNING -> Color(0xFFFF9800)
        NotificationType.ERROR -> MaterialTheme.colorScheme.error
        NotificationType.INFO -> MaterialTheme.colorScheme.primary
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = getNotificationIcon(type),
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (actionLabel != null && onAction != null) {
                    TextButton(
                        onClick = onAction,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = borderColor
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(actionLabel)
                    }
                }
            }
            
            if (onDismiss != null) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

enum class NotificationType {
    SUCCESS, WARNING, ERROR, INFO
}

// ============================================================================
// 🔧 VALIDATION COMPONENTS
// ============================================================================

@Composable
fun ValidationMessage(
    message: String,
    isError: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isError) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = if (isError) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
        )
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun getErrorIcon(type: ErrorType): ImageVector {
    return when (type) {
        ErrorType.NETWORK -> Icons.Default.WifiOff
        ErrorType.VALIDATION -> Icons.Default.Error
        ErrorType.PERMISSION -> Icons.Default.Lock
        ErrorType.NOT_FOUND -> Icons.Default.SearchOff
        ErrorType.SERVER -> Icons.Default.CloudOff
        ErrorType.TIMEOUT -> Icons.Default.Timer
        ErrorType.UNKNOWN -> Icons.Default.ErrorOutline
    }
}

private fun getSnackbarIcon(type: SnackbarType): ImageVector {
    return when (type) {
        SnackbarType.SUCCESS -> Icons.Default.CheckCircle
        SnackbarType.WARNING -> Icons.Default.Warning
        SnackbarType.ERROR -> Icons.Default.Error
        SnackbarType.INFO -> Icons.Default.Info
    }
}

private fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
        NotificationType.WARNING -> Icons.Default.Warning
        NotificationType.ERROR -> Icons.Default.Error
        NotificationType.INFO -> Icons.Default.Info
    }
}

// ============================================================================
// 🎯 ERROR BOUNDARY
// ============================================================================

@Composable
fun ErrorBoundary(
    onError: (Throwable) -> Unit = {},
    fallback: @Composable (Throwable) -> Unit = { error ->
        ErrorState(
            error = ErrorInfo(
                type = ErrorType.UNKNOWN,
                title = "Something went wrong",
                message = error.message ?: "An unexpected error occurred",
                details = error.stackTraceToString()
            )
        )
    },
    content: @Composable () -> Unit
) {
    var error by remember { mutableStateOf<Throwable?>(null) }
    
    LaunchedEffect(error) {
        error?.let { onError(it) }
    }
    
    if (error != null) {
        fallback(error!!)
    } else {
        try {
            content()
        } catch (e: Throwable) {
            error = e
        }
    }
}