package com.everybytesystems.dataflow.ui.charts

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlin.math.*

/**
 * Hierarchical Charts
 * Charts for visualizing hierarchical and tree-structured data
 */

// ============================================================================
// 🌳 DENDROGRAMS
// ============================================================================

data class DendrogramNode(
    val id: String,
    val label: String,
    val children: List<DendrogramNode> = emptyList(),
    val value: Double = 1.0,
    val distance: Double = 0.0, // Distance for clustering
    val color: Color = Color.Blue
)

@Composable
fun Dendrogram(
    root: DendrogramNode,
    modifier: Modifier = Modifier,
    orientation: DendrogramOrientation = DendrogramOrientation.VERTICAL,
    showLabels: Boolean = true,
    showDistances: Boolean = false,
    onNodeClick: ((DendrogramNode) -> Unit)? = null
) {
    var selectedNode by remember { mutableStateOf<DendrogramNode?>(null) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedNode = findDendrogramNodeAtPosition(root, offset, size, orientation)
                    selectedNode = clickedNode
                    clickedNode?.let { onNodeClick?.invoke(it) }
                }
            }
    ) {
        drawDendrogram(
            root = root,
            orientation = orientation,
            showLabels = showLabels,
            showDistances = showDistances,
            selectedNode = selectedNode,
            canvasSize = size
        )
    }
}

enum class DendrogramOrientation {
    VERTICAL, HORIZONTAL
}

private fun DrawScope.drawDendrogram(
    root: DendrogramNode,
    orientation: DendrogramOrientation,
    showLabels: Boolean,
    showDistances: Boolean,
    selectedNode: DendrogramNode?,
    canvasSize: Size
) {
    val padding = 40.dp.toPx()
    val availableWidth = canvasSize.width - 2 * padding
    val availableHeight = canvasSize.height - 2 * padding
    
    when (orientation) {
        DendrogramOrientation.VERTICAL -> {
            val leafNodes = collectLeafNodes(root)
            val leafSpacing = availableWidth / leafNodes.size.coerceAtLeast(1)
            val maxDistance = calculateMaxDistance(root)
            
            drawDendrogramVertical(
                node = root,
                x = padding + availableWidth / 2,
                y = padding,
                leafNodes = leafNodes,
                leafSpacing = leafSpacing,
                maxDistance = maxDistance,
                availableHeight = availableHeight,
                showLabels = showLabels,
                showDistances = showDistances,
                selectedNode = selectedNode
            )
        }
        
        DendrogramOrientation.HORIZONTAL -> {
            val leafNodes = collectLeafNodes(root)
            val leafSpacing = availableHeight / leafNodes.size.coerceAtLeast(1)
            val maxDistance = calculateMaxDistance(root)
            
            drawDendrogramHorizontal(
                node = root,
                x = padding,
                y = padding + availableHeight / 2,
                leafNodes = leafNodes,
                leafSpacing = leafSpacing,
                maxDistance = maxDistance,
                availableWidth = availableWidth,
                showLabels = showLabels,
                showDistances = showDistances,
                selectedNode = selectedNode
            )
        }
    }
}

private fun DrawScope.drawDendrogramVertical(
    node: DendrogramNode,
    x: Float,
    y: Float,
    leafNodes: List<DendrogramNode>,
    leafSpacing: Float,
    maxDistance: Double,
    availableHeight: Float,
    showLabels: Boolean,
    showDistances: Boolean,
    selectedNode: DendrogramNode?
) {
    val isSelected = selectedNode == node
    val nodeColor = if (isSelected) node.color.copy(alpha = 1f) else node.color.copy(alpha = 0.8f)
    
    if (node.children.isEmpty()) {
        // Leaf node
        val leafIndex = leafNodes.indexOf(node)
        val leafX = 40.dp.toPx() + (leafIndex + 0.5f) * leafSpacing
        
        drawCircle(
            color = nodeColor,
            radius = 4.dp.toPx(),
            center = Offset(leafX, y + availableHeight)
        )
        
        if (showLabels) {
            // Note: In a real implementation, you would use proper text rendering
            // This is a placeholder for cross-platform compatibility
        }
    } else {
        // Internal node
        val childPositions = mutableListOf<Offset>()
        
        node.children.forEach { child ->
            val childLeaves = collectLeafNodes(child)
            val childStartIndex = leafNodes.indexOfFirst { it in childLeaves }
            val childEndIndex = leafNodes.indexOfLast { it in childLeaves }
            val childCenterX = 40.dp.toPx() + (childStartIndex + childEndIndex + 1) * leafSpacing / 2
            val childY = y + (node.distance / maxDistance * availableHeight).toFloat()
            
            childPositions.add(Offset(childCenterX, childY))
            
            drawDendrogramVertical(
                node = child,
                x = childCenterX,
                y = childY,
                leafNodes = leafNodes,
                leafSpacing = leafSpacing,
                maxDistance = maxDistance,
                availableHeight = availableHeight,
                showLabels = showLabels,
                showDistances = showDistances,
                selectedNode = selectedNode
            )
        }
        
        // Draw connections
        if (childPositions.size >= 2) {
            val minX = childPositions.minOfOrNull { it.x } ?: x
            val maxX = childPositions.maxOfOrNull { it.x } ?: x
            val nodeY = y + (node.distance / maxDistance * availableHeight).toFloat()
            
            // Horizontal line connecting children
            drawLine(
                color = Color.Black,
                start = Offset(minX, nodeY),
                end = Offset(maxX, nodeY),
                strokeWidth = 1.dp.toPx()
            )
            
            // Vertical lines to children
            childPositions.forEach { childPos ->
                drawLine(
                    color = Color.Black,
                    start = Offset(childPos.x, nodeY),
                    end = childPos,
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        
        // Draw node
        val nodeX = (childPositions.minOfOrNull { it.x } ?: x + childPositions.maxOfOrNull { it.x } ?: x) / 2
        val nodeY = y + (node.distance / maxDistance * availableHeight).toFloat()
        
        drawCircle(
            color = nodeColor,
            radius = 3.dp.toPx(),
            center = Offset(nodeX, nodeY)
        )
    }
}

private fun DrawScope.drawDendrogramHorizontal(
    node: DendrogramNode,
    x: Float,
    y: Float,
    leafNodes: List<DendrogramNode>,
    leafSpacing: Float,
    maxDistance: Double,
    availableWidth: Float,
    showLabels: Boolean,
    showDistances: Boolean,
    selectedNode: DendrogramNode?
) {
    // Similar implementation for horizontal orientation
    // Implementation details omitted for brevity
}

// ============================================================================
// ☀️ SUNBURST CHARTS
// ============================================================================

data class SunburstNode(
    val id: String,
    val label: String,
    val value: Double,
    val children: List<SunburstNode> = emptyList(),
    val color: Color = Color.Blue,
    val level: Int = 0
)

@Composable
fun SunburstChart(
    root: SunburstNode,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    innerRadius: Float = 0.2f,
    onSegmentClick: ((SunburstNode) -> Unit)? = null
) {
    var selectedSegment by remember { mutableStateOf<SunburstNode?>(null) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedSegment = findSunburstSegmentAtPosition(root, offset, size, innerRadius)
                    selectedSegment = clickedSegment
                    clickedSegment?.let { onSegmentClick?.invoke(it) }
                }
            }
    ) {
        drawSunburstChart(
            root = root,
            showLabels = showLabels,
            innerRadius = innerRadius,
            selectedSegment = selectedSegment,
            canvasSize = size
        )
    }
}

private fun DrawScope.drawSunburstChart(
    root: SunburstNode,
    showLabels: Boolean,
    innerRadius: Float,
    selectedSegment: SunburstNode?,
    canvasSize: Size
) {
    val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
    val maxRadius = minOf(canvasSize.width, canvasSize.height) / 2 * 0.9f
    val maxLevel = calculateMaxLevel(root)
    
    drawSunburstNode(
        node = root,
        center = center,
        startAngle = 0f,
        sweepAngle = 360f,
        innerRadius = maxRadius * innerRadius,
        outerRadius = maxRadius,
        maxLevel = maxLevel,
        showLabels = showLabels,
        selectedSegment = selectedSegment
    )
}

private fun DrawScope.drawSunburstNode(
    node: SunburstNode,
    center: Offset,
    startAngle: Float,
    sweepAngle: Float,
    innerRadius: Float,
    outerRadius: Float,
    maxLevel: Int,
    showLabels: Boolean,
    selectedSegment: SunburstNode?
) {
    val isSelected = selectedSegment == node
    val segmentColor = if (isSelected) {
        node.color.copy(alpha = 1f)
    } else {
        node.color.copy(alpha = 0.8f)
    }
    
    val levelThickness = (outerRadius - innerRadius) / (maxLevel + 1)
    val nodeInnerRadius = innerRadius + node.level * levelThickness
    val nodeOuterRadius = nodeInnerRadius + levelThickness
    
    // Draw the segment
    drawArc(
        color = segmentColor,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - nodeOuterRadius, center.y - nodeOuterRadius),
        size = Size(nodeOuterRadius * 2, nodeOuterRadius * 2),
        style = Stroke(width = levelThickness)
    )
    
    // Draw segment border
    drawArc(
        color = Color.White,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(center.x - nodeOuterRadius, center.y - nodeOuterRadius),
        size = Size(nodeOuterRadius * 2, nodeOuterRadius * 2),
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw children
    if (node.children.isNotEmpty()) {
        val totalChildValue = node.children.sumOf { it.value }
        var currentAngle = startAngle
        
        node.children.forEach { child ->
            val childSweepAngle = if (totalChildValue > 0) {
                (child.value / totalChildValue * sweepAngle).toFloat()
            } else {
                sweepAngle / node.children.size
            }
            
            drawSunburstNode(
                node = child.copy(level = node.level + 1),
                center = center,
                startAngle = currentAngle,
                sweepAngle = childSweepAngle,
                innerRadius = innerRadius,
                outerRadius = outerRadius,
                maxLevel = maxLevel,
                showLabels = showLabels,
                selectedSegment = selectedSegment
            )
            
            currentAngle += childSweepAngle
        }
    }
    
    // Draw labels if requested
    if (showLabels && sweepAngle > 10f) { // Only show labels for segments large enough
        val labelAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
        val labelRadius = (nodeInnerRadius + nodeOuterRadius) / 2
        val labelX = center.x + cos(labelAngle).toFloat() * labelRadius
        val labelY = center.y + sin(labelAngle).toFloat() * labelRadius
        
        // Note: In a real implementation, you would use proper text rendering
        // This is a placeholder for cross-platform compatibility
    }
}

// ============================================================================
// 🧊 ICICLE CHARTS
// ============================================================================

data class IcicleNode(
    val id: String,
    val label: String,
    val value: Double,
    val children: List<IcicleNode> = emptyList(),
    val color: Color = Color.Blue,
    val level: Int = 0
)

@Composable
fun IcicleChart(
    root: IcicleNode,
    modifier: Modifier = Modifier,
    orientation: IcicleOrientation = IcicleOrientation.VERTICAL,
    showLabels: Boolean = true,
    onSegmentClick: ((IcicleNode) -> Unit)? = null
) {
    var selectedSegment by remember { mutableStateOf<IcicleNode?>(null) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedSegment = findIcicleSegmentAtPosition(root, offset, size, orientation)
                    selectedSegment = clickedSegment
                    clickedSegment?.let { onSegmentClick?.invoke(it) }
                }
            }
    ) {
        drawIcicleChart(
            root = root,
            orientation = orientation,
            showLabels = showLabels,
            selectedSegment = selectedSegment,
            canvasSize = size
        )
    }
}

enum class IcicleOrientation {
    VERTICAL, HORIZONTAL
}

private fun DrawScope.drawIcicleChart(
    root: IcicleNode,
    orientation: IcicleOrientation,
    showLabels: Boolean,
    selectedSegment: IcicleNode?,
    canvasSize: Size
) {
    val padding = 20.dp.toPx()
    val availableWidth = canvasSize.width - 2 * padding
    val availableHeight = canvasSize.height - 2 * padding
    val maxLevel = calculateMaxIcicleLevel(root)
    
    when (orientation) {
        IcicleOrientation.VERTICAL -> {
            val levelHeight = availableHeight / (maxLevel + 1)
            drawIcicleNodeVertical(
                node = root,
                x = padding,
                y = padding,
                width = availableWidth,
                levelHeight = levelHeight,
                showLabels = showLabels,
                selectedSegment = selectedSegment
            )
        }
        
        IcicleOrientation.HORIZONTAL -> {
            val levelWidth = availableWidth / (maxLevel + 1)
            drawIcicleNodeHorizontal(
                node = root,
                x = padding,
                y = padding,
                height = availableHeight,
                levelWidth = levelWidth,
                showLabels = showLabels,
                selectedSegment = selectedSegment
            )
        }
    }
}

private fun DrawScope.drawIcicleNodeVertical(
    node: IcicleNode,
    x: Float,
    y: Float,
    width: Float,
    levelHeight: Float,
    showLabels: Boolean,
    selectedSegment: IcicleNode?
) {
    val isSelected = selectedSegment == node
    val segmentColor = if (isSelected) {
        node.color.copy(alpha = 1f)
    } else {
        node.color.copy(alpha = 0.8f)
    }
    
    // Draw the segment
    drawRect(
        color = segmentColor,
        topLeft = Offset(x, y),
        size = Size(width, levelHeight)
    )
    
    // Draw segment border
    drawRect(
        color = Color.White,
        topLeft = Offset(x, y),
        size = Size(width, levelHeight),
        style = Stroke(width = 1.dp.toPx())
    )
    
    // Draw children
    if (node.children.isNotEmpty()) {
        val totalChildValue = node.children.sumOf { it.value }
        var currentX = x
        
        node.children.forEach { child ->
            val childWidth = if (totalChildValue > 0) {
                (child.value / totalChildValue * width).toFloat()
            } else {
                width / node.children.size
            }
            
            drawIcicleNodeVertical(
                node = child.copy(level = node.level + 1),
                x = currentX,
                y = y + levelHeight,
                width = childWidth,
                levelHeight = levelHeight,
                showLabels = showLabels,
                selectedSegment = selectedSegment
            )
            
            currentX += childWidth
        }
    }
    
    // Draw labels if requested
    if (showLabels && width > 50.dp.toPx()) {
        // Note: In a real implementation, you would use proper text rendering
        // This is a placeholder for cross-platform compatibility
    }
}

private fun DrawScope.drawIcicleNodeHorizontal(
    node: IcicleNode,
    x: Float,
    y: Float,
    height: Float,
    levelWidth: Float,
    showLabels: Boolean,
    selectedSegment: IcicleNode?
) {
    val isSelected = selectedSegment == node
    val segmentColor = if (isSelected) {
        node.color.copy(alpha = 1f)
    } else {
        node.color.copy(alpha = 0.8f)
    }
    
    // Draw the segment
    drawRect(
        color = segmentColor,
        topLeft = Offset(x, y),
        size = Size(levelWidth, height)
    )
    
    // Draw segment border
    drawRect(
        color = Color.White,
        topLeft = Offset(x, y),
        size = Size(levelWidth, height),
        style = Stroke(width = 1.dp.toPx())
    )
    
    // Draw children
    if (node.children.isNotEmpty()) {
        val totalChildValue = node.children.sumOf { it.value }
        var currentY = y
        
        node.children.forEach { child ->
            val childHeight = if (totalChildValue > 0) {
                (child.value / totalChildValue * height).toFloat()
            } else {
                height / node.children.size
            }
            
            drawIcicleNodeHorizontal(
                node = child.copy(level = node.level + 1),
                x = x + levelWidth,
                y = currentY,
                height = childHeight,
                levelWidth = levelWidth,
                showLabels = showLabels,
                selectedSegment = selectedSegment
            )
            
            currentY += childHeight
        }
    }
}

// ============================================================================
// ⭕ CIRCLE PACKING
// ============================================================================

data class CirclePackingNode(
    val id: String,
    val label: String,
    val value: Double,
    val children: List<CirclePackingNode> = emptyList(),
    val color: Color = Color.Blue,
    val x: Float = 0f,
    val y: Float = 0f,
    val radius: Float = 0f
)

@Composable
fun CirclePackingChart(
    root: CirclePackingNode,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    onCircleClick: ((CirclePackingNode) -> Unit)? = null
) {
    var selectedCircle by remember { mutableStateOf<CirclePackingNode?>(null) }
    
    val packedRoot = remember(root) {
        packCircles(root)
    }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedCircle = findCircleAtPosition(packedRoot, offset, size)
                    selectedCircle = clickedCircle
                    clickedCircle?.let { onCircleClick?.invoke(it) }
                }
            }
    ) {
        drawCirclePacking(
            root = packedRoot,
            showLabels = showLabels,
            selectedCircle = selectedCircle,
            canvasSize = size
        )
    }
}

private fun DrawScope.drawCirclePacking(
    root: CirclePackingNode,
    showLabels: Boolean,
    selectedCircle: CirclePackingNode?,
    canvasSize: Size
) {
    val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
    val scale = minOf(canvasSize.width, canvasSize.height) / (root.radius * 2) * 0.9f
    
    drawCirclePackingNode(
        node = root,
        center = center,
        scale = scale,
        showLabels = showLabels,
        selectedCircle = selectedCircle
    )
}

private fun DrawScope.drawCirclePackingNode(
    node: CirclePackingNode,
    center: Offset,
    scale: Float,
    showLabels: Boolean,
    selectedCircle: CirclePackingNode?
) {
    val isSelected = selectedCircle == node
    val circleColor = if (isSelected) {
        node.color.copy(alpha = 1f)
    } else {
        node.color.copy(alpha = 0.6f)
    }
    
    val scaledRadius = node.radius * scale
    val scaledCenter = Offset(
        center.x + node.x * scale,
        center.y + node.y * scale
    )
    
    // Draw circle
    if (node.children.isEmpty()) {
        // Leaf node - filled circle
        drawCircle(
            color = circleColor,
            radius = scaledRadius,
            center = scaledCenter
        )
    } else {
        // Internal node - outline only
        drawCircle(
            color = circleColor,
            radius = scaledRadius,
            center = scaledCenter,
            style = Stroke(width = 2.dp.toPx())
        )
    }
    
    // Draw children
    node.children.forEach { child ->
        drawCirclePackingNode(
            node = child,
            center = center,
            scale = scale,
            showLabels = showLabels,
            selectedCircle = selectedCircle
        )
    }
    
    // Draw labels if requested
    if (showLabels && scaledRadius > 20.dp.toPx()) {
        // Note: In a real implementation, you would use proper text rendering
        // This is a placeholder for cross-platform compatibility
    }
}

// ============================================================================
// 📊 PARTITION CHARTS
// ============================================================================

data class PartitionNode(
    val id: String,
    val label: String,
    val value: Double,
    val children: List<PartitionNode> = emptyList(),
    val color: Color = Color.Blue,
    val level: Int = 0
)

@Composable
fun PartitionChart(
    root: PartitionNode,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    onSegmentClick: ((PartitionNode) -> Unit)? = null
) {
    var selectedSegment by remember { mutableStateOf<PartitionNode?>(null) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedSegment = findPartitionSegmentAtPosition(root, offset, size)
                    selectedSegment = clickedSegment
                    clickedSegment?.let { onSegmentClick?.invoke(it) }
                }
            }
    ) {
        drawPartitionChart(
            root = root,
            showLabels = showLabels,
            selectedSegment = selectedSegment,
            canvasSize = size
        )
    }
}

private fun DrawScope.drawPartitionChart(
    root: PartitionNode,
    showLabels: Boolean,
    selectedSegment: PartitionNode?,
    canvasSize: Size
) {
    val padding = 20.dp.toPx()
    val availableWidth = canvasSize.width - 2 * padding
    val availableHeight = canvasSize.height - 2 * padding
    
    drawPartitionNode(
        node = root,
        x = padding,
        y = padding,
        width = availableWidth,
        height = availableHeight,
        showLabels = showLabels,
        selectedSegment = selectedSegment
    )
}

private fun DrawScope.drawPartitionNode(
    node: PartitionNode,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    showLabels: Boolean,
    selectedSegment: PartitionNode?
) {
    val isSelected = selectedSegment == node
    val segmentColor = if (isSelected) {
        node.color.copy(alpha = 1f)
    } else {
        node.color.copy(alpha = 0.8f)
    }
    
    // Draw the segment
    drawRect(
        color = segmentColor,
        topLeft = Offset(x, y),
        size = Size(width, height)
    )
    
    // Draw segment border
    drawRect(
        color = Color.White,
        topLeft = Offset(x, y),
        size = Size(width, height),
        style = Stroke(width = 1.dp.toPx())
    )
    
    // Draw children
    if (node.children.isNotEmpty()) {
        val totalChildValue = node.children.sumOf { it.value }
        
        if (width > height) {
            // Split horizontally
            var currentX = x
            node.children.forEach { child ->
                val childWidth = if (totalChildValue > 0) {
                    (child.value / totalChildValue * width).toFloat()
                } else {
                    width / node.children.size
                }
                
                drawPartitionNode(
                    node = child.copy(level = node.level + 1),
                    x = currentX,
                    y = y,
                    width = childWidth,
                    height = height,
                    showLabels = showLabels,
                    selectedSegment = selectedSegment
                )
                
                currentX += childWidth
            }
        } else {
            // Split vertically
            var currentY = y
            node.children.forEach { child ->
                val childHeight = if (totalChildValue > 0) {
                    (child.value / totalChildValue * height).toFloat()
                } else {
                    height / node.children.size
                }
                
                drawPartitionNode(
                    node = child.copy(level = node.level + 1),
                    x = x,
                    y = currentY,
                    width = width,
                    height = childHeight,
                    showLabels = showLabels,
                    selectedSegment = selectedSegment
                )
                
                currentY += childHeight
            }
        }
    }
    
    // Draw labels if requested
    if (showLabels && width > 50.dp.toPx() && height > 20.dp.toPx()) {
        // Note: In a real implementation, you would use proper text rendering
        // This is a placeholder for cross-platform compatibility
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private fun collectLeafNodes(node: DendrogramNode): List<DendrogramNode> {
    return if (node.children.isEmpty()) {
        listOf(node)
    } else {
        node.children.flatMap { collectLeafNodes(it) }
    }
}

private fun calculateMaxDistance(node: DendrogramNode): Double {
    return if (node.children.isEmpty()) {
        node.distance
    } else {
        maxOf(node.distance, node.children.maxOfOrNull { calculateMaxDistance(it) } ?: 0.0)
    }
}

private fun calculateMaxLevel(node: SunburstNode): Int {
    return if (node.children.isEmpty()) {
        node.level
    } else {
        node.children.maxOfOrNull { calculateMaxLevel(it) } ?: node.level
    }
}

private fun calculateMaxIcicleLevel(node: IcicleNode): Int {
    return if (node.children.isEmpty()) {
        node.level
    } else {
        node.children.maxOfOrNull { calculateMaxIcicleLevel(it) } ?: node.level
    }
}

private fun packCircles(root: CirclePackingNode): CirclePackingNode {
    // Simplified circle packing algorithm
    // In a real implementation, you would use a proper circle packing algorithm
    return root.copy(
        radius = sqrt(root.value).toFloat() * 10,
        children = root.children.mapIndexed { index, child ->
            val angle = (index * 2 * PI / root.children.size).toFloat()
            val distance = sqrt(root.value).toFloat() * 5
            child.copy(
                x = cos(angle) * distance,
                y = sin(angle) * distance,
                radius = sqrt(child.value).toFloat() * 5
            )
        }
    )
}

private fun findDendrogramNodeAtPosition(
    root: DendrogramNode,
    offset: Offset,
    canvasSize: Size,
    orientation: DendrogramOrientation
): DendrogramNode? {
    // Simplified hit testing for dendrogram nodes
    return root
}

private fun findSunburstSegmentAtPosition(
    root: SunburstNode,
    offset: Offset,
    canvasSize: Size,
    innerRadius: Float
): SunburstNode? {
    // Simplified hit testing for sunburst segments
    return root
}

private fun findIcicleSegmentAtPosition(
    root: IcicleNode,
    offset: Offset,
    canvasSize: Size,
    orientation: IcicleOrientation
): IcicleNode? {
    // Simplified hit testing for icicle segments
    return root
}

private fun findCircleAtPosition(
    root: CirclePackingNode,
    offset: Offset,
    canvasSize: Size
): CirclePackingNode? {
    val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
    val scale = minOf(canvasSize.width, canvasSize.height) / (root.radius * 2) * 0.9f
    
    fun findInNode(node: CirclePackingNode): CirclePackingNode? {
        val scaledCenter = Offset(
            center.x + node.x * scale,
            center.y + node.y * scale
        )
        val scaledRadius = node.radius * scale
        
        val distance = sqrt(
            (offset.x - scaledCenter.x).pow(2) + (offset.y - scaledCenter.y).pow(2)
        )
        
        if (distance <= scaledRadius) {
            // Check children first (smaller circles on top)
            node.children.forEach { child ->
                val childResult = findInNode(child)
                if (childResult != null) return childResult
            }
            return node
        }
        
        return null
    }
    
    return findInNode(root)
}

private fun findPartitionSegmentAtPosition(
    root: PartitionNode,
    offset: Offset,
    canvasSize: Size
): PartitionNode? {
    // Simplified hit testing for partition segments
    return root
}