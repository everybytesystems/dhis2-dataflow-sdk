package com.everybytesystems.dataflow.ui.patterns

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
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

/**
 * Enhanced UI Patterns
 * Advanced design patterns and layout components
 */

// ============================================================================
// 📱 MASTER-DETAIL PATTERN
// ============================================================================

@Composable
fun <T> MasterDetailPattern(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    masterContent: @Composable (T, Boolean, () -> Unit) -> Unit,
    detailContent: @Composable (T?) -> Unit,
    modifier: Modifier = Modifier,
    masterWeight: Float = 0.4f,
    detailWeight: Float = 0.6f,
    showDetailInDialog: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isCompact = configuration.screenWidthDp < 600
    
    if (isCompact && showDetailInDialog && selectedItem != null) {
        // Show detail in dialog for compact screens
        AlertDialog(
            onDismissRequest = { onItemSelected(selectedItem) },
            title = { Text("Details") },
            text = { detailContent(selectedItem) },
            confirmButton = {
                TextButton(onClick = { onItemSelected(selectedItem) }) {
                    Text("Close")
                }
            }
        )
    }
    
    if (isCompact && !showDetailInDialog) {
        // Stack layout for compact screens
        Box(modifier = modifier.fillMaxSize()) {
            // Master view
            AnimatedVisibility(
                visible = selectedItem == null,
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = slideOutHorizontally(targetOffsetX = { -it })
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(items) { item ->
                        masterContent(item, selectedItem == item) {
                            onItemSelected(item)
                        }
                    }
                }
            }
            
            // Detail view
            AnimatedVisibility(
                visible = selectedItem != null,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it })
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Back button
                    TopAppBar(
                        title = { Text("Details") },
                        navigationIcon = {
                            IconButton(onClick = { onItemSelected(selectedItem!!) }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                    
                    Box(modifier = Modifier.weight(1f)) {
                        detailContent(selectedItem)
                    }
                }
            }
        }
    } else {
        // Side-by-side layout for larger screens
        Row(modifier = modifier.fillMaxSize()) {
            // Master pane
            Card(
                modifier = Modifier
                    .weight(masterWeight)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(items) { item ->
                        masterContent(item, selectedItem == item) {
                            onItemSelected(item)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Detail pane
            Card(
                modifier = Modifier
                    .weight(detailWeight)
                    .fillMaxHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                detailContent(selectedItem)
            }
        }
    }
}

// ============================================================================
// 🧙 WIZARD PATTERN
// ============================================================================

data class WizardStep(
    val id: String,
    val title: String,
    val description: String? = null,
    val icon: ImageVector? = null,
    val isOptional: Boolean = false,
    val canSkip: Boolean = false,
    val validation: (() -> Boolean)? = null,
    val content: @Composable (onNext: () -> Unit, onPrevious: () -> Unit) -> Unit
)

@Composable
fun WizardPattern(
    steps: List<WizardStep>,
    onComplete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    showProgress: Boolean = true,
    allowBackNavigation: Boolean = true,
    showStepNumbers: Boolean = true
) {
    var currentStepIndex by remember { mutableStateOf(0) }
    val currentStep = steps.getOrNull(currentStepIndex)
    
    Column(modifier = modifier.fillMaxSize()) {
        // Progress indicator
        if (showProgress) {
            WizardProgressIndicator(
                steps = steps,
                currentStepIndex = currentStepIndex,
                showStepNumbers = showStepNumbers,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Step content
        currentStep?.let { step ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Step header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        step.icon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Column {
                            Text(
                                text = step.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            step.description?.let { description ->
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Step content
                    Box(modifier = Modifier.weight(1f)) {
                        step.content(
                            onNext = {
                                if (currentStepIndex < steps.size - 1) {
                                    currentStepIndex++
                                } else {
                                    onComplete()
                                }
                            },
                            onPrevious = {
                                if (currentStepIndex > 0) {
                                    currentStepIndex--
                                }
                            }
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Navigation buttons
        WizardNavigationButtons(
            currentStepIndex = currentStepIndex,
            totalSteps = steps.size,
            currentStep = currentStep,
            allowBackNavigation = allowBackNavigation,
            onNext = {
                if (currentStepIndex < steps.size - 1) {
                    currentStepIndex++
                } else {
                    onComplete()
                }
            },
            onPrevious = {
                if (currentStepIndex > 0) {
                    currentStepIndex--
                }
            },
            onCancel = onCancel,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun WizardProgressIndicator(
    steps: List<WizardStep>,
    currentStepIndex: Int,
    showStepNumbers: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                WizardStepIndicator(
                    step = step,
                    stepNumber = index + 1,
                    isActive = index == currentStepIndex,
                    isCompleted = index < currentStepIndex,
                    showStepNumber = showStepNumbers
                )
                
                if (index < steps.size - 1) {
                    Divider(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        color = if (index < currentStepIndex) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WizardStepIndicator(
    step: WizardStep,
    stepNumber: Int,
    isActive: Boolean,
    isCompleted: Boolean,
    showStepNumber: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        isActive -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.outline
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                showStepNumber -> {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
                step.icon != null -> {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = if (isActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        Text(
            text = step.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WizardNavigationButtons(
    currentStepIndex: Int,
    totalSteps: Int,
    currentStep: WizardStep?,
    allowBackNavigation: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cancel button
        TextButton(onClick = onCancel) {
            Text("Cancel")
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Previous button
            if (allowBackNavigation && currentStepIndex > 0) {
                OutlinedButton(onClick = onPrevious) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Previous")
                }
            }
            
            // Next/Finish button
            Button(
                onClick = onNext,
                enabled = currentStep?.validation?.invoke() != false
            ) {
                if (currentStepIndex < totalSteps - 1) {
                    Text("Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Finish")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 📋 KANBAN BOARD PATTERN
// ============================================================================

data class KanbanItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val priority: KanbanPriority = KanbanPriority.MEDIUM,
    val assignee: String? = null,
    val tags: List<String> = emptyList(),
    val dueDate: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class KanbanColumn(
    val id: String,
    val title: String,
    val items: List<KanbanItem>,
    val color: Color = Color.Gray,
    val maxItems: Int? = null,
    val isCollapsed: Boolean = false
)

enum class KanbanPriority(val color: Color, val label: String) {
    LOW(Color(0xFF4CAF50), "Low"),
    MEDIUM(Color(0xFFFF9800), "Medium"),
    HIGH(Color(0xFFF44336), "High"),
    CRITICAL(Color(0xFF9C27B0), "Critical")
}

@Composable
fun KanbanBoardPattern(
    columns: List<KanbanColumn>,
    onItemMoved: (KanbanItem, String, String) -> Unit,
    onItemClick: (KanbanItem) -> Unit,
    modifier: Modifier = Modifier,
    showItemCount: Boolean = true,
    allowReordering: Boolean = true,
    onColumnCollapse: ((String, Boolean) -> Unit)? = null
) {
    LazyRow(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(columns) { column ->
            KanbanColumnCard(
                column = column,
                onItemClick = onItemClick,
                onItemMoved = onItemMoved,
                showItemCount = showItemCount,
                allowReordering = allowReordering,
                onColumnCollapse = onColumnCollapse,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun KanbanColumnCard(
    column: KanbanColumn,
    onItemClick: (KanbanItem) -> Unit,
    onItemMoved: (KanbanItem, String, String) -> Unit,
    showItemCount: Boolean,
    allowReordering: Boolean,
    onColumnCollapse: ((String, Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Column header
            KanbanColumnHeader(
                column = column,
                showItemCount = showItemCount,
                onColumnCollapse = onColumnCollapse,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (!column.isCollapsed) {
                // Column items
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(column.items) { item ->
                        KanbanItemCard(
                            item = item,
                            onClick = { onItemClick(item) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    // Add item placeholder
                    item {
                        AddKanbanItemCard(
                            onAddItem = { /* Handle add item */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanColumnHeader(
    column: KanbanColumn,
    showItemCount: Boolean,
    onColumnCollapse: ((String, Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = column.color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(column.color, CircleShape)
                )
                
                Text(
                    text = column.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (showItemCount) {
                    Badge {
                        Text(
                            text = column.items.size.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            
            if (onColumnCollapse != null) {
                IconButton(
                    onClick = { onColumnCollapse(column.id, !column.isCollapsed) }
                ) {
                    Icon(
                        imageVector = if (column.isCollapsed) {
                            Icons.Default.ExpandMore
                        } else {
                            Icons.Default.ExpandLess
                        },
                        contentDescription = if (column.isCollapsed) "Expand" else "Collapse"
                    )
                }
            }
        }
    }
}

@Composable
private fun KanbanItemCard(
    item: KanbanItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Priority indicator and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(item.priority.color, CircleShape)
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
            
            // Tags
            if (item.tags.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(item.tags) { tag ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
            
            // Footer with assignee and due date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.assignee?.let { assignee ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = assignee,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                item.dueDate?.let { dueDate ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dueDate,
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
private fun AddKanbanItemCard(
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onAddItem() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add item",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

fun generateSampleWizardSteps(): List<WizardStep> {
    return listOf(
        WizardStep(
            id = "welcome",
            title = "Welcome",
            description = "Let's get you started",
            icon = Icons.Default.Waving,
            content = { onNext, _ ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to the setup wizard!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "This wizard will guide you through the initial setup process.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = onNext) {
                        Text("Get Started")
                    }
                }
            }
        ),
        WizardStep(
            id = "profile",
            title = "Profile Setup",
            description = "Tell us about yourself",
            icon = Icons.Default.Person,
            validation = { true }, // Add validation logic
            content = { onNext, onPrevious ->
                var name by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onPrevious,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }
                        Button(
                            onClick = onNext,
                            modifier = Modifier.weight(1f),
                            enabled = name.isNotBlank() && email.isNotBlank()
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        ),
        WizardStep(
            id = "preferences",
            title = "Preferences",
            description = "Customize your experience",
            icon = Icons.Default.Settings,
            content = { onNext, onPrevious ->
                var notifications by remember { mutableStateOf(true) }
                var darkMode by remember { mutableStateOf(false) }
                var analytics by remember { mutableStateOf(true) }
                
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Enable Notifications")
                                Switch(
                                    checked = notifications,
                                    onCheckedChange = { notifications = it }
                                )
                            }
                        }
                    }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dark Mode")
                                Switch(
                                    checked = darkMode,
                                    onCheckedChange = { darkMode = it }
                                )
                            }
                        }
                    }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Analytics")
                                Switch(
                                    checked = analytics,
                                    onCheckedChange = { analytics = it }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onPrevious,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }
                        Button(
                            onClick = onNext,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Finish")
                        }
                    }
                }
            }
        )
    )
}

fun generateSampleKanbanColumns(): List<KanbanColumn> {
    return listOf(
        KanbanColumn(
            id = "todo",
            title = "To Do",
            color = Color(0xFF2196F3),
            items = listOf(
                KanbanItem(
                    id = "1",
                    title = "Design new feature",
                    description = "Create mockups and wireframes for the new dashboard feature",
                    priority = KanbanPriority.HIGH,
                    assignee = "John Doe",
                    tags = listOf("Design", "UI/UX"),
                    dueDate = "Dec 15"
                ),
                KanbanItem(
                    id = "2",
                    title = "Fix login bug",
                    description = "Users are unable to login with special characters in password",
                    priority = KanbanPriority.CRITICAL,
                    assignee = "Jane Smith",
                    tags = listOf("Bug", "Security"),
                    dueDate = "Dec 10"
                )
            )
        ),
        KanbanColumn(
            id = "inprogress",
            title = "In Progress",
            color = Color(0xFFFF9800),
            items = listOf(
                KanbanItem(
                    id = "3",
                    title = "Implement API endpoints",
                    description = "Create REST API for user management",
                    priority = KanbanPriority.MEDIUM,
                    assignee = "Bob Johnson",
                    tags = listOf("Backend", "API"),
                    dueDate = "Dec 20"
                )
            )
        ),
        KanbanColumn(
            id = "review",
            title = "Review",
            color = Color(0xFF9C27B0),
            items = listOf(
                KanbanItem(
                    id = "4",
                    title = "Code review for auth module",
                    description = "Review authentication and authorization implementation",
                    priority = KanbanPriority.MEDIUM,
                    assignee = "Alice Brown",
                    tags = listOf("Review", "Security")
                )
            )
        ),
        KanbanColumn(
            id = "done",
            title = "Done",
            color = Color(0xFF4CAF50),
            items = listOf(
                KanbanItem(
                    id = "5",
                    title = "Setup CI/CD pipeline",
                    description = "Configure automated testing and deployment",
                    priority = KanbanPriority.LOW,
                    assignee = "Charlie Wilson",
                    tags = listOf("DevOps", "Automation")
                ),
                KanbanItem(
                    id = "6",
                    title = "Update documentation",
                    description = "Update API documentation with latest changes",
                    priority = KanbanPriority.LOW,
                    assignee = "Diana Davis",
                    tags = listOf("Documentation")
                )
            )
        )
    )
}