package com.everybytesystems.dataflow.ui.workflow

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
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.datetime.*
import kotlin.math.*

/**
 * Workflow & Process Management Components
 * Advanced workflow visualization, process tracking, and business process management
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

enum class WorkflowStatus {
    DRAFT,
    ACTIVE,
    PAUSED,
    COMPLETED,
    CANCELLED,
    ERROR
}

enum class StepStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    SKIPPED,
    BLOCKED
}

enum class StepType {
    START,
    TASK,
    DECISION,
    PARALLEL,
    MERGE,
    END,
    SUBPROCESS,
    TIMER,
    MESSAGE
}

enum class ProcessPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}

enum class ApprovalStatus {
    PENDING,
    APPROVED,
    REJECTED,
    ESCALATED,
    CANCELLED
}

data class WorkflowStep(
    val id: String,
    val name: String,
    val description: String = "",
    val type: StepType,
    val status: StepStatus = StepStatus.PENDING,
    val assignee: String? = null,
    val dueDate: Instant? = null,
    val completedAt: Instant? = null,
    val duration: kotlin.time.Duration? = null,
    val dependencies: List<String> = emptyList(),
    val outputs: List<String> = emptyList(),
    val position: Offset = Offset.Zero,
    val metadata: Map<String, Any> = emptyMap()
)

data class WorkflowConnection(
    val id: String,
    val from: String,
    val to: String,
    val condition: String? = null,
    val label: String = "",
    val isActive: Boolean = false
)

data class Workflow(
    val id: String,
    val name: String,
    val description: String,
    val version: String = "1.0",
    val status: WorkflowStatus,
    val steps: List<WorkflowStep>,
    val connections: List<WorkflowConnection>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val createdBy: String,
    val tags: List<String> = emptyList(),
    val variables: Map<String, Any> = emptyMap()
)

data class ProcessInstance(
    val id: String,
    val workflowId: String,
    val workflowName: String,
    val status: WorkflowStatus,
    val priority: ProcessPriority = ProcessPriority.NORMAL,
    val startedAt: Instant,
    val completedAt: Instant? = null,
    val currentStep: String? = null,
    val progress: Float = 0f,
    val assignee: String? = null,
    val initiator: String,
    val variables: Map<String, Any> = emptyMap(),
    val history: List<ProcessHistoryEntry> = emptyList()
)

data class ProcessHistoryEntry(
    val id: String,
    val stepId: String,
    val stepName: String,
    val action: String,
    val timestamp: Instant,
    val user: String,
    val comment: String = "",
    val duration: kotlin.time.Duration? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class ApprovalRequest(
    val id: String,
    val title: String,
    val description: String,
    val requestedBy: String,
    val requestedAt: Instant,
    val dueDate: Instant? = null,
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val approvers: List<String>,
    val currentApprover: String? = null,
    val priority: ProcessPriority = ProcessPriority.NORMAL,
    val attachments: List<String> = emptyList(),
    val comments: List<ApprovalComment> = emptyList(),
    val processInstanceId: String? = null
)

data class ApprovalComment(
    val id: String,
    val author: String,
    val content: String,
    val timestamp: Instant,
    val isInternal: Boolean = false
)

// ============================================================================
// 🔄 WORKFLOW DESIGNER
// ============================================================================

@Composable
fun WorkflowDesigner(
    workflow: Workflow,
    onWorkflowChange: (Workflow) -> Unit,
    onStepClick: (WorkflowStep) -> Unit,
    onConnectionClick: (WorkflowConnection) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    var selectedStep by remember { mutableStateOf<WorkflowStep?>(null) }
    var selectedConnection by remember { mutableStateOf<WorkflowConnection?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Workflow canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (isEditable) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val step = findStepAtPosition(workflow.steps, offset)
                                if (step != null) {
                                    selectedStep = step
                                    isDragging = true
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                selectedStep?.let { step ->
                                    val updatedSteps = workflow.steps.map { 
                                        if (it.id == step.id) it.copy(position = step.position + dragOffset)
                                        else it
                                    }
                                    onWorkflowChange(workflow.copy(steps = updatedSteps))
                                }
                                dragOffset = Offset.Zero
                            },
                            onDrag = { change ->
                                dragOffset += change
                            }
                        )
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val clickedStep = findStepAtPosition(workflow.steps, offset)
                        val clickedConnection = findConnectionAtPosition(workflow.connections, workflow.steps, offset)
                        
                        when {
                            clickedStep != null -> {
                                selectedStep = clickedStep
                                selectedConnection = null
                                onStepClick(clickedStep)
                            }
                            clickedConnection != null -> {
                                selectedConnection = clickedConnection
                                selectedStep = null
                                onConnectionClick(clickedConnection)
                            }
                            else -> {
                                selectedStep = null
                                selectedConnection = null
                            }
                        }
                    }
                }
        ) {
            drawWorkflowDiagram(
                workflow = workflow,
                selectedStep = selectedStep,
                selectedConnection = selectedConnection,
                dragOffset = if (isDragging) dragOffset else Offset.Zero
            )
        }
        
        // Workflow toolbar
        if (isEditable) {
            WorkflowToolbar(
                onAddStep = { type ->
                    val newStep = WorkflowStep(
                        id = "step_${System.currentTimeMillis()}",
                        name = "New ${type.name}",
                        type = type,
                        position = Offset(100f, 100f)
                    )
                    onWorkflowChange(workflow.copy(steps = workflow.steps + newStep))
                },
                modifier = Modifier.align(Alignment.TopStart)
            )
        }
        
        // Step properties panel
        selectedStep?.let { step ->
            StepPropertiesPanel(
                step = step,
                onStepChange = { updatedStep ->
                    val updatedSteps = workflow.steps.map { 
                        if (it.id == step.id) updatedStep else it
                    }
                    onWorkflowChange(workflow.copy(steps = updatedSteps))
                },
                onClose = { selectedStep = null },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

private fun DrawScope.drawWorkflowDiagram(
    workflow: Workflow,
    selectedStep: WorkflowStep?,
    selectedConnection: WorkflowConnection?,
    dragOffset: Offset
) {
    // Draw connections first (behind steps)
    workflow.connections.forEach { connection ->
        val fromStep = workflow.steps.find { it.id == connection.from }
        val toStep = workflow.steps.find { it.id == connection.to }
        
        if (fromStep != null && toStep != null) {
            val fromPos = fromStep.position + if (selectedStep?.id == fromStep.id) dragOffset else Offset.Zero
            val toPos = toStep.position + if (selectedStep?.id == toStep.id) dragOffset else Offset.Zero
            
            drawConnection(
                connection = connection,
                fromPos = fromPos,
                toPos = toPos,
                isSelected = selectedConnection?.id == connection.id
            )
        }
    }
    
    // Draw steps
    workflow.steps.forEach { step ->
        val position = step.position + if (selectedStep?.id == step.id) dragOffset else Offset.Zero
        drawWorkflowStep(
            step = step,
            position = position,
            isSelected = selectedStep?.id == step.id
        )
    }
}

private fun DrawScope.drawWorkflowStep(
    step: WorkflowStep,
    position: Offset,
    isSelected: Boolean
) {
    val stepSize = 80.dp.toPx()
    val cornerRadius = when (step.type) {
        StepType.START, StepType.END -> stepSize / 2 // Circle
        StepType.DECISION -> 0f // Diamond (simplified as square)
        else -> 8.dp.toPx() // Rounded rectangle
    }
    
    val stepColor = when (step.status) {
        StepStatus.PENDING -> Color(0xFFE0E0E0)
        StepStatus.IN_PROGRESS -> Color(0xFF2196F3)
        StepStatus.COMPLETED -> Color(0xFF4CAF50)
        StepStatus.FAILED -> Color(0xFFF44336)
        StepStatus.SKIPPED -> Color(0xFFFF9800)
        StepStatus.BLOCKED -> Color(0xFF9C27B0)
    }
    
    val borderColor = if (isSelected) Color(0xFF1976D2) else Color.White
    val borderWidth = if (isSelected) 3.dp.toPx() else 1.dp.toPx()
    
    // Draw step shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.1f),
        topLeft = Offset(position.x + 2.dp.toPx(), position.y + 2.dp.toPx()),
        size = Size(stepSize, stepSize),
        cornerRadius = CornerRadius(cornerRadius)
    )
    
    // Draw step background
    drawRoundRect(
        color = stepColor,
        topLeft = position,
        size = Size(stepSize, stepSize),
        cornerRadius = CornerRadius(cornerRadius)
    )
    
    // Draw step border
    drawRoundRect(
        color = borderColor,
        topLeft = position,
        size = Size(stepSize, stepSize),
        cornerRadius = CornerRadius(cornerRadius),
        style = Stroke(width = borderWidth)
    )
    
    // Draw step icon (simplified)
    val iconSize = 24.dp.toPx()
    val iconPosition = Offset(
        position.x + (stepSize - iconSize) / 2,
        position.y + (stepSize - iconSize) / 2
    )
    
    val iconColor = Color.White
    when (step.type) {
        StepType.START -> drawCircle(iconColor, iconSize / 4, iconPosition + Offset(iconSize / 2, iconSize / 2))
        StepType.END -> drawCircle(iconColor, iconSize / 4, iconPosition + Offset(iconSize / 2, iconSize / 2))
        StepType.DECISION -> {
            // Draw diamond shape (simplified as rotated square)
            val path = Path().apply {
                moveTo(iconPosition.x + iconSize / 2, iconPosition.y)
                lineTo(iconPosition.x + iconSize, iconPosition.y + iconSize / 2)
                lineTo(iconPosition.x + iconSize / 2, iconPosition.y + iconSize)
                lineTo(iconPosition.x, iconPosition.y + iconSize / 2)
                close()
            }
            drawPath(path, iconColor)
        }
        else -> {
            // Draw rectangle for other types
            drawRect(
                iconColor,
                iconPosition,
                Size(iconSize, iconSize / 2)
            )
        }
    }
}

private fun DrawScope.drawConnection(
    connection: WorkflowConnection,
    fromPos: Offset,
    toPos: Offset,
    isSelected: Boolean
) {
    val stepSize = 80.dp.toPx()
    val startPos = Offset(fromPos.x + stepSize / 2, fromPos.y + stepSize / 2)
    val endPos = Offset(toPos.x + stepSize / 2, toPos.y + stepSize / 2)
    
    val connectionColor = if (connection.isActive) Color(0xFF4CAF50) else Color(0xFF757575)
    val strokeWidth = if (isSelected) 3.dp.toPx() else 2.dp.toPx()
    
    // Draw connection line
    drawLine(
        color = connectionColor,
        start = startPos,
        end = endPos,
        strokeWidth = strokeWidth
    )
    
    // Draw arrow head
    val angle = atan2(endPos.y - startPos.y, endPos.x - startPos.x)
    val arrowLength = 15.dp.toPx()
    val arrowAngle = PI / 6
    
    val arrowEnd = Offset(
        endPos.x - cos(angle) * (stepSize / 2 + 5.dp.toPx()),
        endPos.y - sin(angle) * (stepSize / 2 + 5.dp.toPx())
    )
    
    val arrowPoint1 = Offset(
        arrowEnd.x - cos(angle - arrowAngle) * arrowLength,
        arrowEnd.y - sin(angle - arrowAngle) * arrowLength
    )
    
    val arrowPoint2 = Offset(
        arrowEnd.x - cos(angle + arrowAngle) * arrowLength,
        arrowEnd.y - sin(angle + arrowAngle) * arrowLength
    )
    
    val arrowPath = Path().apply {
        moveTo(arrowEnd.x, arrowEnd.y)
        lineTo(arrowPoint1.x, arrowPoint1.y)
        lineTo(arrowPoint2.x, arrowPoint2.y)
        close()
    }
    
    drawPath(arrowPath, connectionColor)
}

// ============================================================================
// 🛠️ WORKFLOW TOOLBAR
// ============================================================================

@Composable
fun WorkflowToolbar(
    onAddStep: (StepType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(StepType.values()) { stepType ->
                WorkflowStepButton(
                    stepType = stepType,
                    onClick = { onAddStep(stepType) }
                )
            }
        }
    }
}

@Composable
fun WorkflowStepButton(
    stepType: StepType,
    onClick: () -> Unit
) {
    val (icon, label) = when (stepType) {
        StepType.START -> Icons.Default.PlayArrow to "Start"
        StepType.TASK -> Icons.Default.Assignment to "Task"
        StepType.DECISION -> Icons.Default.Help to "Decision"
        StepType.PARALLEL -> Icons.Default.CallSplit to "Parallel"
        StepType.MERGE -> Icons.Default.CallMerge to "Merge"
        StepType.END -> Icons.Default.Stop to "End"
        StepType.SUBPROCESS -> Icons.Default.Layers to "Subprocess"
        StepType.TIMER -> Icons.Default.Timer to "Timer"
        StepType.MESSAGE -> Icons.Default.Message to "Message"
    }
    
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(width = 80.dp, height = 60.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

// ============================================================================
// ⚙️ STEP PROPERTIES PANEL
// ============================================================================

@Composable
fun StepPropertiesPanel(
    step: WorkflowStep,
    onStepChange: (WorkflowStep) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editedStep by remember(step) { mutableStateOf(step) }
    
    Card(
        modifier = modifier
            .padding(16.dp)
            .width(300.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step Properties",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            // Step name
            OutlinedTextField(
                value = editedStep.name,
                onValueChange = { editedStep = editedStep.copy(name = it) },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Step description
            OutlinedTextField(
                value = editedStep.description,
                onValueChange = { editedStep = editedStep.copy(description = it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            
            // Step type (read-only)
            OutlinedTextField(
                value = editedStep.type.name,
                onValueChange = { },
                label = { Text("Type") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            
            // Assignee
            OutlinedTextField(
                value = editedStep.assignee ?: "",
                onValueChange = { editedStep = editedStep.copy(assignee = it.takeIf { it.isNotBlank() }) },
                label = { Text("Assignee") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Status
            var statusExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it }
            ) {
                OutlinedTextField(
                    value = editedStep.status.name,
                    onValueChange = { },
                    label = { Text("Status") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) }
                )
                
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    StepStatus.values().forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.name) },
                            onClick = {
                                editedStep = editedStep.copy(status = status)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                
                Button(
                    onClick = {
                        onStepChange(editedStep)
                        onClose()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

// ============================================================================
// 📋 PROCESS INSTANCE MANAGER
// ============================================================================

@Composable
fun ProcessInstanceManager(
    instances: List<ProcessInstance>,
    onInstanceClick: (ProcessInstance) -> Unit,
    onStartProcess: (String) -> Unit,
    onCancelProcess: (ProcessInstance) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Process Instances (${instances.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = { /* Show workflow selection dialog */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Process")
                }
            }
        }
        
        items(instances) { instance ->
            ProcessInstanceCard(
                instance = instance,
                onClick = { onInstanceClick(instance) },
                onCancel = { onCancelProcess(instance) }
            )
        }
    }
}

@Composable
fun ProcessInstanceCard(
    instance: ProcessInstance,
    onClick: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = instance.workflowName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ID: ${instance.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProcessStatusChip(status = instance.status)
                    ProcessPriorityChip(priority = instance.priority)
                }
            }
            
            // Progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${(instance.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                LinearProgressIndicator(
                    progress = instance.progress,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Instance details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Started by: ${instance.initiator}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Started: ${formatDateTime(instance.startedAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                instance.currentStep?.let { currentStep ->
                    Text(
                        text = "Current: $currentStep",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Action buttons
            if (instance.status in listOf(WorkflowStatus.ACTIVE, WorkflowStatus.PAUSED)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    
                    Button(onClick = onClick) {
                        Text("View Details")
                    }
                }
            }
        }
    }
}

// ============================================================================
// ✅ APPROVAL WORKFLOW
// ============================================================================

@Composable
fun ApprovalWorkflow(
    requests: List<ApprovalRequest>,
    onApprove: (ApprovalRequest, String) -> Unit,
    onReject: (ApprovalRequest, String) -> Unit,
    onEscalate: (ApprovalRequest) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Approval Requests (${requests.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        items(requests) { request ->
            ApprovalRequestCard(
                request = request,
                onApprove = { comment -> onApprove(request, comment) },
                onReject = { comment -> onReject(request, comment) },
                onEscalate = { onEscalate(request) }
            )
        }
    }
}

@Composable
fun ApprovalRequestCard(
    request: ApprovalRequest,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onEscalate: () -> Unit
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<String?>(null) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Requested by: ${request.requestedBy}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ApprovalStatusChip(status = request.status)
                    ProcessPriorityChip(priority = request.priority)
                }
            }
            
            Text(
                text = request.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Request details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Requested: ${formatDateTime(request.requestedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                request.dueDate?.let { dueDate ->
                    Text(
                        text = "Due: ${formatDateTime(dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dueDate < Clock.System.now()) 
                            MaterialTheme.colorScheme.error 
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Approvers
            if (request.approvers.isNotEmpty()) {
                Text(
                    text = "Approvers: ${request.approvers.joinString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Action buttons (only for pending requests)
            if (request.status == ApprovalStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            pendingAction = "reject"
                            showCommentDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject")
                    }
                    
                    OutlinedButton(
                        onClick = onEscalate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Escalate")
                    }
                    
                    Button(
                        onClick = {
                            pendingAction = "approve"
                            showCommentDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Approve")
                    }
                }
            }
        }
    }
    
    // Comment dialog
    if (showCommentDialog) {
        ApprovalCommentDialog(
            action = pendingAction ?: "",
            onConfirm = { comment ->
                when (pendingAction) {
                    "approve" -> onApprove(comment)
                    "reject" -> onReject(comment)
                }
                showCommentDialog = false
                pendingAction = null
            },
            onDismiss = {
                showCommentDialog = false
                pendingAction = null
            }
        )
    }
}

@Composable
fun ApprovalCommentDialog(
    action: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var comment by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${action.replaceFirstChar { it.uppercase() }} Request") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Please provide a comment for this $action:")
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comment") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(comment) },
                enabled = comment.isNotBlank()
            ) {
                Text(action.replaceFirstChar { it.uppercase() })
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ============================================================================
// 📊 PROCESS ANALYTICS
// ============================================================================

@Composable
fun ProcessAnalyticsDashboard(
    analytics: ProcessAnalytics,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Process Analytics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Key metrics
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(analytics.keyMetrics) { metric ->
                    ProcessMetricCard(metric = metric)
                }
            }
        }
        
        // Process performance chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Process Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simplified chart placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Process Performance Chart",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Bottleneck analysis
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bottleneck Analysis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    analytics.bottlenecks.forEach { bottleneck ->
                        BottleneckItem(bottleneck = bottleneck)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProcessMetricCard(metric: ProcessMetric) {
    Card(
        modifier = Modifier.width(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = metric.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = metric.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = metric.color
            )
            
            if (metric.trend != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (metric.trend > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (metric.trend > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Text(
                        text = "${kotlin.math.abs(metric.trend)}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (metric.trend > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }
        }
    }
}

@Composable
fun BottleneckItem(bottleneck: ProcessBottleneck) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bottleneck.stepName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Avg. duration: ${bottleneck.averageDuration}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "${bottleneck.impactPercentage}%",
            style = MaterialTheme.typography.labelMedium,
            color = when {
                bottleneck.impactPercentage > 50 -> Color(0xFFF44336)
                bottleneck.impactPercentage > 25 -> Color(0xFFFF9800)
                else -> Color(0xFF4CAF50)
            }
        )
    }
}

// ============================================================================
// 🎨 HELPER COMPONENTS
// ============================================================================

@Composable
fun ProcessStatusChip(status: WorkflowStatus) {
    val (color, text) = when (status) {
        WorkflowStatus.DRAFT -> Color(0xFF9E9E9E) to "Draft"
        WorkflowStatus.ACTIVE -> Color(0xFF2196F3) to "Active"
        WorkflowStatus.PAUSED -> Color(0xFFFF9800) to "Paused"
        WorkflowStatus.COMPLETED -> Color(0xFF4CAF50) to "Completed"
        WorkflowStatus.CANCELLED -> Color(0xFF757575) to "Cancelled"
        WorkflowStatus.ERROR -> Color(0xFFF44336) to "Error"
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
        modifier = Modifier.height(24.dp)
    )
}

@Composable
fun ProcessPriorityChip(priority: ProcessPriority) {
    val (color, text) = when (priority) {
        ProcessPriority.LOW -> Color(0xFF4CAF50) to "Low"
        ProcessPriority.NORMAL -> Color(0xFF2196F3) to "Normal"
        ProcessPriority.HIGH -> Color(0xFFFF9800) to "High"
        ProcessPriority.CRITICAL -> Color(0xFFF44336) to "Critical"
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
        modifier = Modifier.height(24.dp)
    )
}

@Composable
fun ApprovalStatusChip(status: ApprovalStatus) {
    val (color, text) = when (status) {
        ApprovalStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        ApprovalStatus.APPROVED -> Color(0xFF4CAF50) to "Approved"
        ApprovalStatus.REJECTED -> Color(0xFFF44336) to "Rejected"
        ApprovalStatus.ESCALATED -> Color(0xFF9C27B0) to "Escalated"
        ApprovalStatus.CANCELLED -> Color(0xFF757575) to "Cancelled"
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
        modifier = Modifier.height(24.dp)
    )
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private fun findStepAtPosition(steps: List<WorkflowStep>, position: Offset): WorkflowStep? {
    val stepSize = 80f
    return steps.find { step ->
        val stepBounds = Rect(
            step.position,
            Size(stepSize, stepSize)
        )
        stepBounds.contains(position)
    }
}

private fun findConnectionAtPosition(
    connections: List<WorkflowConnection>,
    steps: List<WorkflowStep>,
    position: Offset
): WorkflowConnection? {
    // Simplified connection hit testing
    return connections.find { connection ->
        val fromStep = steps.find { it.id == connection.from }
        val toStep = steps.find { it.id == connection.to }
        
        if (fromStep != null && toStep != null) {
            val distance = distanceToLine(
                position,
                fromStep.position,
                toStep.position
            )
            distance < 10f
        } else false
    }
}

private fun distanceToLine(point: Offset, lineStart: Offset, lineEnd: Offset): Float {
    val A = point.x - lineStart.x
    val B = point.y - lineStart.y
    val C = lineEnd.x - lineStart.x
    val D = lineEnd.y - lineStart.y
    
    val dot = A * C + B * D
    val lenSq = C * C + D * D
    
    if (lenSq == 0f) return sqrt(A * A + B * B)
    
    val param = dot / lenSq
    
    val xx = if (param < 0) lineStart.x else if (param > 1) lineEnd.x else lineStart.x + param * C
    val yy = if (param < 0) lineStart.y else if (param > 1) lineEnd.y else lineStart.y + param * D
    
    val dx = point.x - xx
    val dy = point.y - yy
    
    return sqrt(dx * dx + dy * dy)
}

fun formatDateTime(instant: Instant): String {
    // Simplified date formatting for cross-platform compatibility
    return instant.toString().substringBefore('T')
}

// ============================================================================
// 📊 DATA CLASSES FOR ANALYTICS
// ============================================================================

data class ProcessAnalytics(
    val keyMetrics: List<ProcessMetric>,
    val bottlenecks: List<ProcessBottleneck>,
    val performanceData: List<ProcessPerformancePoint>
)

data class ProcessMetric(
    val name: String,
    val value: String,
    val color: Color = MaterialTheme.colorScheme.primary,
    val trend: Float? = null
)

data class ProcessBottleneck(
    val stepName: String,
    val averageDuration: String,
    val impactPercentage: Int
)

data class ProcessPerformancePoint(
    val timestamp: Instant,
    val value: Double,
    val metric: String
)