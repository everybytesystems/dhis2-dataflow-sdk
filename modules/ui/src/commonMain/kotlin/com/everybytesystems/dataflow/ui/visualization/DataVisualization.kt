package com.everybytesystems.dataflow.ui.visualization

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
import kotlin.math.*

/**
 * Advanced Data Visualization Components
 * Specialized visualizations for complex data analysis
 */

// ============================================================================
// 🌐 NETWORK GRAPHS
// ============================================================================

data class NetworkNode(
    val id: String,
    val label: String,
    val value: Double = 1.0,
    val color: Color = Color.Blue,
    val size: Float = 20f,
    val position: Offset? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class NetworkEdge(
    val from: String,
    val to: String,
    val weight: Double = 1.0,
    val color: Color = Color.Gray,
    val width: Float = 2f,
    val label: String = "",
    val directed: Boolean = false
)

@Composable
fun NetworkGraph(
    nodes: List<NetworkNode>,
    edges: List<NetworkEdge>,
    modifier: Modifier = Modifier,
    layout: NetworkLayout = NetworkLayout.FORCE_DIRECTED,
    showLabels: Boolean = true,
    interactive: Boolean = true,
    onNodeClick: ((NetworkNode) -> Unit)? = null,
    onEdgeClick: ((NetworkEdge) -> Unit)? = null
) {
    var nodePositions by remember { mutableStateOf<Map<String, Offset>>(emptyMap()) }
    var selectedNode by remember { mutableStateOf<NetworkNode?>(null) }
    var selectedEdge by remember { mutableStateOf<NetworkEdge?>(null) }
    
    LaunchedEffect(nodes, edges, layout) {
        nodePositions = calculateNodePositions(nodes, edges, layout)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (interactive) {
                        detectTapGestures { offset ->
                            val clickedNode = findNodeAtPosition(nodes, nodePositions, offset)
                            val clickedEdge = findEdgeAtPosition(edges, nodePositions, offset)
                            
                            when {
                                clickedNode != null -> {
                                    selectedNode = clickedNode
                                    selectedEdge = null
                                    onNodeClick?.invoke(clickedNode)
                                }
                                clickedEdge != null -> {
                                    selectedEdge = clickedEdge
                                    selectedNode = null
                                    onEdgeClick?.invoke(clickedEdge)
                                }
                                else -> {
                                    selectedNode = null
                                    selectedEdge = null
                                }
                            }
                        }
                    }
                }
        ) {
            drawNetworkGraph(
                nodes = nodes,
                edges = edges,
                positions = nodePositions,
                showLabels = showLabels,
                selectedNode = selectedNode,
                selectedEdge = selectedEdge
            )
        }
        
        // Node/Edge info panel
        selectedNode?.let { node ->
            NodeInfoPanel(
                node = node,
                onClose = { selectedNode = null },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        
        selectedEdge?.let { edge ->
            EdgeInfoPanel(
                edge = edge,
                onClose = { selectedEdge = null },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

private fun DrawScope.drawNetworkGraph(
    nodes: List<NetworkNode>,
    edges: List<NetworkEdge>,
    positions: Map<String, Offset>,
    showLabels: Boolean,
    selectedNode: NetworkNode?,
    selectedEdge: NetworkEdge?
) {
    // Draw edges first (behind nodes)
    edges.forEach { edge ->
        val fromPos = positions[edge.from]
        val toPos = positions[edge.to]
        
        if (fromPos != null && toPos != null) {
            val isSelected = selectedEdge == edge
            val edgeColor = if (isSelected) edge.color.copy(alpha = 1f) else edge.color.copy(alpha = 0.7f)
            val edgeWidth = if (isSelected) edge.width * 1.5f else edge.width
            
            // Draw edge line
            drawLine(
                color = edgeColor,
                start = fromPos,
                end = toPos,
                strokeWidth = edgeWidth.dp.toPx()
            )
            
            // Draw arrow for directed edges
            if (edge.directed) {
                drawArrowHead(fromPos, toPos, edgeColor, edgeWidth)
            }
            
            // Draw edge label
            if (showLabels && edge.label.isNotEmpty()) {
                val midPoint = Offset(
                    (fromPos.x + toPos.x) / 2,
                    (fromPos.y + toPos.y) / 2
                )
                drawEdgeLabel(edge.label, midPoint)
            }
        }
    }
    
    // Draw nodes
    nodes.forEach { node ->
        val position = positions[node.id]
        if (position != null) {
            val isSelected = selectedNode == node
            val nodeSize = if (isSelected) node.size * 1.2f else node.size
            val nodeColor = if (isSelected) node.color.copy(alpha = 1f) else node.color
            
            // Draw node shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.2f),
                radius = nodeSize.dp.toPx() / 2 + 2.dp.toPx(),
                center = Offset(position.x + 2.dp.toPx(), position.y + 2.dp.toPx())
            )
            
            // Draw node
            drawCircle(
                color = nodeColor,
                radius = nodeSize.dp.toPx() / 2,
                center = position
            )
            
            // Draw node border
            drawCircle(
                color = Color.White,
                radius = nodeSize.dp.toPx() / 2,
                center = position,
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Draw node label
            if (showLabels) {
                drawNodeLabel(node.label, position, nodeSize)
            }
        }
    }
}

private fun DrawScope.drawArrowHead(
    from: Offset,
    to: Offset,
    color: Color,
    width: Float
) {
    val angle = atan2(to.y - from.y, to.x - from.x)
    val arrowLength = 15.dp.toPx()
    val arrowAngle = PI / 6
    
    val arrowEnd = Offset(
        to.x - cos(angle) * 20.dp.toPx(),
        to.y - sin(angle) * 20.dp.toPx()
    )
    
    val arrowPoint1 = Offset(
        arrowEnd.x - cos(angle - arrowAngle) * arrowLength,
        arrowEnd.y - sin(angle - arrowAngle) * arrowLength
    )
    
    val arrowPoint2 = Offset(
        arrowEnd.x - cos(angle + arrowAngle) * arrowLength,
        arrowEnd.y - sin(angle + arrowAngle) * arrowLength
    )
    
    val path = Path().apply {
        moveTo(arrowEnd.x, arrowEnd.y)
        lineTo(arrowPoint1.x, arrowPoint1.y)
        lineTo(arrowPoint2.x, arrowPoint2.y)
        close()
    }
    
    drawPath(
        path = path,
        color = color
    )
}

private fun DrawScope.drawNodeLabel(label: String, position: Offset, nodeSize: Float) {
    val textY = position.y + nodeSize.dp.toPx() / 2 + 20.dp.toPx()
    
    drawContext.canvas.nativeCanvas.apply {
        drawText(
            label,
            position.x,
            textY,
            android.graphics.Paint().apply {
                color = Color.Black.toArgb()
                textSize = 12.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
        )
    }
}

private fun DrawScope.drawEdgeLabel(label: String, position: Offset) {
    drawContext.canvas.nativeCanvas.apply {
        drawText(
            label,
            position.x,
            position.y,
            android.graphics.Paint().apply {
                color = Color.Black.toArgb()
                textSize = 10.sp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                bgColor = Color.White.copy(alpha = 0.8f).toArgb()
            }
        )
    }
}

@Composable
private fun NodeInfoPanel(
    node: NetworkNode,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Node: ${node.label}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text("ID: ${node.id}", style = MaterialTheme.typography.bodySmall)
            Text("Value: ${node.value}", style = MaterialTheme.typography.bodySmall)
            
            if (node.metadata.isNotEmpty()) {
                Text("Metadata:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                node.metadata.forEach { (key, value) ->
                    Text("$key: $value", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun EdgeInfoPanel(
    edge: NetworkEdge,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Edge",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text("From: ${edge.from}", style = MaterialTheme.typography.bodySmall)
            Text("To: ${edge.to}", style = MaterialTheme.typography.bodySmall)
            Text("Weight: ${edge.weight}", style = MaterialTheme.typography.bodySmall)
            Text("Directed: ${edge.directed}", style = MaterialTheme.typography.bodySmall)
            
            if (edge.label.isNotEmpty()) {
                Text("Label: ${edge.label}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

// ============================================================================
// 🌳 TREE DIAGRAMS
// ============================================================================

data class TreeNode(
    val id: String,
    val label: String,
    val value: Double = 0.0,
    val children: List<TreeNode> = emptyList(),
    val color: Color = Color.Blue,
    val expanded: Boolean = true,
    val metadata: Map<String, Any> = emptyMap()
)

@Composable
fun TreeDiagram(
    root: TreeNode,
    modifier: Modifier = Modifier,
    orientation: TreeOrientation = TreeOrientation.VERTICAL,
    nodeSpacing: Dp = 80.dp,
    levelSpacing: Dp = 100.dp,
    showValues: Boolean = true,
    onNodeClick: ((TreeNode) -> Unit)? = null
) {
    var selectedNode by remember { mutableStateOf<TreeNode?>(null) }
    val nodePositions = remember(root, orientation) {
        calculateTreePositions(root, orientation, nodeSpacing.value, levelSpacing.value)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val clickedNode = findTreeNodeAtPosition(root, nodePositions, offset)
                        selectedNode = clickedNode
                        clickedNode?.let { onNodeClick?.invoke(it) }
                    }
                }
        ) {
            drawTreeDiagram(
                root = root,
                positions = nodePositions,
                orientation = orientation,
                showValues = showValues,
                selectedNode = selectedNode
            )
        }
        
        selectedNode?.let { node ->
            TreeNodeInfoPanel(
                node = node,
                onClose = { selectedNode = null },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

private fun DrawScope.drawTreeDiagram(
    root: TreeNode,
    positions: Map<String, Offset>,
    orientation: TreeOrientation,
    showValues: Boolean,
    selectedNode: TreeNode?
) {
    fun drawNode(node: TreeNode) {
        val position = positions[node.id] ?: return
        val isSelected = selectedNode == node
        val nodeSize = if (isSelected) 25.dp.toPx() else 20.dp.toPx()
        
        // Draw connections to children
        node.children.forEach { child ->
            val childPosition = positions[child.id]
            if (childPosition != null) {
                drawLine(
                    color = Color.Gray,
                    start = position,
                    end = childPosition,
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        
        // Draw node
        drawCircle(
            color = if (isSelected) node.color.copy(alpha = 1f) else node.color,
            radius = nodeSize,
            center = position
        )
        
        // Draw node border
        drawCircle(
            color = Color.White,
            radius = nodeSize,
            center = position,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Draw label
        val labelOffset = when (orientation) {
            TreeOrientation.VERTICAL -> Offset(position.x, position.y + nodeSize + 15.dp.toPx())
            TreeOrientation.HORIZONTAL -> Offset(position.x + nodeSize + 15.dp.toPx(), position.y)
        }
        
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                node.label,
                labelOffset.x,
                labelOffset.y,
                android.graphics.Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
        
        // Draw value if enabled
        if (showValues && node.value != 0.0) {
            val valueOffset = when (orientation) {
                TreeOrientation.VERTICAL -> Offset(position.x, position.y + nodeSize + 30.dp.toPx())
                TreeOrientation.HORIZONTAL -> Offset(position.x + nodeSize + 15.dp.toPx(), position.y + 15.dp.toPx())
            }
            
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    node.value.toString(),
                    valueOffset.x,
                    valueOffset.y,
                    android.graphics.Paint().apply {
                        color = Color.Gray.toArgb()
                        textSize = 10.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
        
        // Recursively draw children
        if (node.expanded) {
            node.children.forEach { child ->
                drawNode(child)
            }
        }
    }
    
    drawNode(root)
}

@Composable
private fun TreeNodeInfoPanel(
    node: TreeNode,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text("Value: ${node.value}", style = MaterialTheme.typography.bodySmall)
            Text("Children: ${node.children.size}", style = MaterialTheme.typography.bodySmall)
            
            if (node.metadata.isNotEmpty()) {
                Text("Metadata:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                node.metadata.forEach { (key, value) ->
                    Text("$key: $value", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// ============================================================================
// 🗺️ TREEMAP VISUALIZATION
// ============================================================================

data class TreemapItem(
    val id: String,
    val label: String,
    val value: Double,
    val color: Color = Color.Blue,
    val children: List<TreemapItem> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)

@Composable
fun TreemapVisualization(
    data: List<TreemapItem>,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    showValues: Boolean = true,
    onItemClick: ((TreemapItem) -> Unit)? = null
) {
    var selectedItem by remember { mutableStateOf<TreemapItem?>(null) }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val clickedItem = findTreemapItemAtPosition(data, offset, size)
                        selectedItem = clickedItem
                        clickedItem?.let { onItemClick?.invoke(it) }
                    }
                }
        ) {
            drawTreemap(
                items = data,
                bounds = Rect(Offset.Zero, size),
                showLabels = showLabels,
                showValues = showValues,
                selectedItem = selectedItem
            )
        }
        
        selectedItem?.let { item ->
            TreemapItemInfoPanel(
                item = item,
                onClose = { selectedItem = null },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

private fun DrawScope.drawTreemap(
    items: List<TreemapItem>,
    bounds: Rect,
    showLabels: Boolean,
    showValues: Boolean,
    selectedItem: TreemapItem?
) {
    val rectangles = calculateTreemapLayout(items, bounds)
    
    rectangles.forEach { (item, rect) ->
        val isSelected = selectedItem == item
        val itemColor = if (isSelected) item.color.copy(alpha = 0.8f) else item.color.copy(alpha = 0.6f)
        
        // Draw rectangle
        drawRect(
            color = itemColor,
            topLeft = rect.topLeft,
            size = rect.size
        )
        
        // Draw border
        drawRect(
            color = Color.White,
            topLeft = rect.topLeft,
            size = rect.size,
            style = Stroke(width = if (isSelected) 3.dp.toPx() else 1.dp.toPx())
        )
        
        // Draw label and value
        if (showLabels && rect.width > 50.dp.toPx() && rect.height > 30.dp.toPx()) {
            val textX = rect.center.x
            val textY = rect.center.y
            
            drawContext.canvas.nativeCanvas.apply {
                // Draw label
                drawText(
                    item.label,
                    textX,
                    textY - 5.dp.toPx(),
                    android.graphics.Paint().apply {
                        color = Color.White.toArgb()
                        textSize = 12.sp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                )
                
                // Draw value
                if (showValues) {
                    drawText(
                        item.value.toString(),
                        textX,
                        textY + 10.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = Color.White.toArgb()
                            textSize = 10.sp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TreemapItemInfoPanel(
    item: TreemapItem,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text("Value: ${item.value}", style = MaterialTheme.typography.bodySmall)
            
            if (item.metadata.isNotEmpty()) {
                Text("Details:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                item.metadata.forEach { (key, value) ->
                    Text("$key: $value", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// ============================================================================
// 📊 SANKEY DIAGRAMS
// ============================================================================

data class SankeyNode(
    val id: String,
    val label: String,
    val color: Color = Color.Blue,
    val metadata: Map<String, Any> = emptyMap()
)

data class SankeyFlow(
    val source: String,
    val target: String,
    val value: Double,
    val color: Color = Color.Blue.copy(alpha = 0.6f)
)

@Composable
fun SankeyDiagram(
    nodes: List<SankeyNode>,
    flows: List<SankeyFlow>,
    modifier: Modifier = Modifier,
    nodeWidth: Dp = 20.dp,
    nodePadding: Dp = 10.dp,
    onNodeClick: ((SankeyNode) -> Unit)? = null,
    onFlowClick: ((SankeyFlow) -> Unit)? = null
) {
    var selectedNode by remember { mutableStateOf<SankeyNode?>(null) }
    var selectedFlow by remember { mutableStateOf<SankeyFlow?>(null) }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Handle clicks on nodes and flows
                        val clickedNode = findSankeyNodeAtPosition(nodes, offset)
                        val clickedFlow = findSankeyFlowAtPosition(flows, offset)
                        
                        when {
                            clickedNode != null -> {
                                selectedNode = clickedNode
                                selectedFlow = null
                                onNodeClick?.invoke(clickedNode)
                            }
                            clickedFlow != null -> {
                                selectedFlow = clickedFlow
                                selectedNode = null
                                onFlowClick?.invoke(clickedFlow)
                            }
                            else -> {
                                selectedNode = null
                                selectedFlow = null
                            }
                        }
                    }
                }
        ) {
            drawSankeyDiagram(
                nodes = nodes,
                flows = flows,
                nodeWidth = nodeWidth.toPx(),
                nodePadding = nodePadding.toPx(),
                selectedNode = selectedNode,
                selectedFlow = selectedFlow
            )
        }
        
        selectedNode?.let { node ->
            SankeyNodeInfoPanel(
                node = node,
                flows = flows,
                onClose = { selectedNode = null },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

private fun DrawScope.drawSankeyDiagram(
    nodes: List<SankeyNode>,
    flows: List<SankeyFlow>,
    nodeWidth: Float,
    nodePadding: Float,
    selectedNode: SankeyNode?,
    selectedFlow: SankeyFlow?
) {
    val layout = calculateSankeyLayout(nodes, flows, size, nodeWidth, nodePadding)
    
    // Draw flows first (behind nodes)
    layout.flows.forEach { (flow, path) ->
        val isSelected = selectedFlow == flow
        val flowColor = if (isSelected) flow.color.copy(alpha = 0.8f) else flow.color
        
        drawPath(
            path = path,
            color = flowColor
        )
    }
    
    // Draw nodes
    layout.nodes.forEach { (node, rect) ->
        val isSelected = selectedNode == node
        val nodeColor = if (isSelected) node.color.copy(alpha = 1f) else node.color
        
        // Draw node rectangle
        drawRect(
            color = nodeColor,
            topLeft = rect.topLeft,
            size = rect.size
        )
        
        // Draw node label
        val labelX = if (rect.left < size.width / 2) {
            rect.right + 10.dp.toPx()
        } else {
            rect.left - 10.dp.toPx()
        }
        
        drawContext.canvas.nativeCanvas.apply {
            drawText(
                node.label,
                labelX,
                rect.center.y,
                android.graphics.Paint().apply {
                    color = Color.Black.toArgb()
                    textSize = 12.sp.toPx()
                    textAlign = if (rect.left < size.width / 2) {
                        android.graphics.Paint.Align.LEFT
                    } else {
                        android.graphics.Paint.Align.RIGHT
                    }
                }
            )
        }
    }
}

@Composable
private fun SankeyNodeInfoPanel(
    node: SankeyNode,
    flows: List<SankeyFlow>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val incomingFlows = flows.filter { it.target == node.id }
    val outgoingFlows = flows.filter { it.source == node.id }
    val totalIncoming = incomingFlows.sumOf { it.value }
    val totalOutgoing = outgoingFlows.sumOf { it.value }
    
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text("Incoming: $totalIncoming", style = MaterialTheme.typography.bodySmall)
            Text("Outgoing: $totalOutgoing", style = MaterialTheme.typography.bodySmall)
            Text("Net: ${totalIncoming - totalOutgoing}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

enum class NetworkLayout {
    FORCE_DIRECTED, CIRCULAR, HIERARCHICAL, GRID
}

enum class TreeOrientation {
    VERTICAL, HORIZONTAL
}

private fun calculateNodePositions(
    nodes: List<NetworkNode>,
    edges: List<NetworkEdge>,
    layout: NetworkLayout
): Map<String, Offset> {
    // Simplified layout calculation
    return when (layout) {
        NetworkLayout.CIRCULAR -> calculateCircularLayout(nodes)
        NetworkLayout.GRID -> calculateGridLayout(nodes)
        NetworkLayout.HIERARCHICAL -> calculateHierarchicalLayout(nodes, edges)
        NetworkLayout.FORCE_DIRECTED -> calculateForceDirectedLayout(nodes, edges)
    }
}

private fun calculateCircularLayout(nodes: List<NetworkNode>): Map<String, Offset> {
    val positions = mutableMapOf<String, Offset>()
    val radius = 200f
    val centerX = 300f
    val centerY = 300f
    
    nodes.forEachIndexed { index, node ->
        val angle = (2 * PI * index / nodes.size).toFloat()
        positions[node.id] = Offset(
            centerX + radius * cos(angle),
            centerY + radius * sin(angle)
        )
    }
    
    return positions
}

private fun calculateGridLayout(nodes: List<NetworkNode>): Map<String, Offset> {
    val positions = mutableMapOf<String, Offset>()
    val cols = ceil(sqrt(nodes.size.toDouble())).toInt()
    val spacing = 100f
    
    nodes.forEachIndexed { index, node ->
        val row = index / cols
        val col = index % cols
        positions[node.id] = Offset(
            col * spacing + 50f,
            row * spacing + 50f
        )
    }
    
    return positions
}

private fun calculateHierarchicalLayout(
    nodes: List<NetworkNode>,
    edges: List<NetworkEdge>
): Map<String, Offset> {
    // Simplified hierarchical layout
    return calculateGridLayout(nodes)
}

private fun calculateForceDirectedLayout(
    nodes: List<NetworkNode>,
    edges: List<NetworkEdge>
): Map<String, Offset> {
    // Simplified force-directed layout
    return calculateCircularLayout(nodes)
}

private fun calculateTreePositions(
    root: TreeNode,
    orientation: TreeOrientation,
    nodeSpacing: Float,
    levelSpacing: Float
): Map<String, Offset> {
    val positions = mutableMapOf<String, Offset>()
    
    fun positionNode(node: TreeNode, x: Float, y: Float, level: Int): Float {
        positions[node.id] = Offset(x, y)
        
        if (node.children.isEmpty()) return x
        
        var currentX = x - (node.children.size - 1) * nodeSpacing / 2
        node.children.forEach { child ->
            val childX = when (orientation) {
                TreeOrientation.VERTICAL -> currentX
                TreeOrientation.HORIZONTAL -> x + levelSpacing
            }
            val childY = when (orientation) {
                TreeOrientation.VERTICAL -> y + levelSpacing
                TreeOrientation.HORIZONTAL -> currentX
            }
            
            positionNode(child, childX, childY, level + 1)
            currentX += nodeSpacing
        }
        
        return currentX
    }
    
    positionNode(root, 300f, 100f, 0)
    return positions
}

private fun calculateTreemapLayout(
    items: List<TreemapItem>,
    bounds: Rect
): List<Pair<TreemapItem, Rect>> {
    // Simplified treemap layout using squarified algorithm
    val result = mutableListOf<Pair<TreemapItem, Rect>>()
    val totalValue = items.sumOf { it.value }
    
    var currentY = bounds.top
    items.forEach { item ->
        val height = (item.value / totalValue * bounds.height).toFloat()
        val rect = Rect(
            offset = Offset(bounds.left, currentY),
            size = Size(bounds.width, height)
        )
        result.add(item to rect)
        currentY += height
    }
    
    return result
}

private data class SankeyLayout(
    val nodes: List<Pair<SankeyNode, Rect>>,
    val flows: List<Pair<SankeyFlow, Path>>
)

private fun calculateSankeyLayout(
    nodes: List<SankeyNode>,
    flows: List<SankeyFlow>,
    canvasSize: Size,
    nodeWidth: Float,
    nodePadding: Float
): SankeyLayout {
    // Simplified Sankey layout
    val nodeRects = mutableListOf<Pair<SankeyNode, Rect>>()
    val flowPaths = mutableListOf<Pair<SankeyFlow, Path>>()
    
    // Position nodes in columns
    val columns = 3 // Simplified to 3 columns
    val columnWidth = canvasSize.width / columns
    
    nodes.forEachIndexed { index, node ->
        val column = index % columns
        val row = index / columns
        val x = column * columnWidth + columnWidth / 2 - nodeWidth / 2
        val y = row * 100f + 50f
        
        nodeRects.add(
            node to Rect(
                offset = Offset(x, y),
                size = Size(nodeWidth, 80f)
            )
        )
    }
    
    // Create flow paths (simplified)
    flows.forEach { flow ->
        val sourcRect = nodeRects.find { it.first.id == flow.source }?.second
        val targetRect = nodeRects.find { it.first.id == flow.target }?.second
        
        if (sourcRect != null && targetRect != null) {
            val path = Path().apply {
                moveTo(sourcRect.right, sourcRect.center.y)
                cubicTo(
                    sourcRect.right + 50f, sourcRect.center.y,
                    targetRect.left - 50f, targetRect.center.y,
                    targetRect.left, targetRect.center.y
                )
            }
            flowPaths.add(flow to path)
        }
    }
    
    return SankeyLayout(nodeRects, flowPaths)
}

private fun findNodeAtPosition(
    nodes: List<NetworkNode>,
    positions: Map<String, Offset>,
    tapPosition: Offset
): NetworkNode? {
    return nodes.find { node ->
        val position = positions[node.id] ?: return@find false
        val distance = sqrt(
            (tapPosition.x - position.x).pow(2) + (tapPosition.y - position.y).pow(2)
        )
        distance <= node.size
    }
}

private fun findEdgeAtPosition(
    edges: List<NetworkEdge>,
    positions: Map<String, Offset>,
    tapPosition: Offset
): NetworkEdge? {
    // Simplified edge detection
    return null
}

private fun findTreeNodeAtPosition(
    root: TreeNode,
    positions: Map<String, Offset>,
    tapPosition: Offset
): TreeNode? {
    fun searchNode(node: TreeNode): TreeNode? {
        val position = positions[node.id] ?: return null
        val distance = sqrt(
            (tapPosition.x - position.x).pow(2) + (tapPosition.y - position.y).pow(2)
        )
        
        if (distance <= 25f) return node
        
        node.children.forEach { child ->
            searchNode(child)?.let { return it }
        }
        
        return null
    }
    
    return searchNode(root)
}

private fun findTreemapItemAtPosition(
    items: List<TreemapItem>,
    tapPosition: Offset,
    canvasSize: Size
): TreemapItem? {
    // Simplified treemap item detection
    return items.firstOrNull()
}

private fun findSankeyNodeAtPosition(
    nodes: List<SankeyNode>,
    tapPosition: Offset
): SankeyNode? {
    // Simplified Sankey node detection
    return null
}

private fun findSankeyFlowAtPosition(
    flows: List<SankeyFlow>,
    tapPosition: Offset
): SankeyFlow? {
    // Simplified Sankey flow detection
    return null
}