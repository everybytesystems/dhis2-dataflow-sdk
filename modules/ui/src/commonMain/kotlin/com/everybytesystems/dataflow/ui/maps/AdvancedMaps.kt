package com.everybytesystems.dataflow.ui.maps

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
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlin.math.*

/**
 * Advanced Map Components
 * Comprehensive mapping and geospatial visualization
 */

// ============================================================================
// 🗺️ MAP DATA MODELS
// ============================================================================

data class MapBounds(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double
)

data class MapPoint(
    val latitude: Double,
    val longitude: Double,
    val label: String = "",
    val value: Double = 0.0,
    val color: Color = Color.Blue,
    val size: Float = 1.0f,
    val metadata: Map<String, Any> = emptyMap()
)

data class MapRegion(
    val id: String,
    val name: String,
    val coordinates: List<Offset>,
    val value: Double = 0.0,
    val color: Color = Color.Blue,
    val metadata: Map<String, Any> = emptyMap()
)

data class MapCluster(
    val centerLatitude: Double,
    val centerLongitude: Double,
    val points: List<MapPoint>,
    val radius: Float = 50f
)

// ============================================================================
// 🌍 INTERACTIVE MAP
// ============================================================================

@Composable
fun InteractiveMap(
    points: List<MapPoint> = emptyList(),
    regions: List<MapRegion> = emptyList(),
    modifier: Modifier = Modifier,
    initialBounds: MapBounds = MapBounds(90.0, -90.0, 180.0, -180.0),
    onPointClick: ((MapPoint) -> Unit)? = null,
    onRegionClick: ((MapRegion) -> Unit)? = null,
    showControls: Boolean = true,
    enableClustering: Boolean = true,
    clusterThreshold: Int = 10
) {
    var zoomLevel by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }
    var selectedPoint by remember { mutableStateOf<MapPoint?>(null) }
    var selectedRegion by remember { mutableStateOf<MapRegion?>(null) }
    
    val clusters = remember(points, zoomLevel, enableClustering) {
        if (enableClustering && points.size > clusterThreshold) {
            clusterPoints(points, zoomLevel)
        } else {
            emptyList()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomLevel = (zoomLevel * zoom).coerceIn(0.5f, 5f)
                        panOffset += pan
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val adjustedOffset = (offset - panOffset) / zoomLevel
                        
                        // Check for point clicks
                        val clickedPoint = findPointAtPosition(points, adjustedOffset, size)
                        if (clickedPoint != null) {
                            selectedPoint = clickedPoint
                            onPointClick?.invoke(clickedPoint)
                        }
                        
                        // Check for region clicks
                        val clickedRegion = findRegionAtPosition(regions, adjustedOffset)
                        if (clickedRegion != null) {
                            selectedRegion = clickedRegion
                            onRegionClick?.invoke(clickedRegion)
                        }
                    }
                }
        ) {
            val transform = androidx.compose.ui.graphics.Matrix().apply {
                translate(panOffset.x, panOffset.y)
                scale(zoomLevel, zoomLevel)
            }
            
            // Draw regions
            regions.forEach { region ->
                drawMapRegion(region, selectedRegion == region, transform)
            }
            
            // Draw clusters or individual points
            if (clusters.isNotEmpty()) {
                clusters.forEach { cluster ->
                    drawCluster(cluster, transform, size)
                }
            } else {
                points.forEach { point ->
                    drawMapPoint(point, selectedPoint == point, transform, size)
                }
            }
        }
        
        // Map controls
        if (showControls) {
            MapControls(
                zoomLevel = zoomLevel,
                onZoomIn = { zoomLevel = (zoomLevel * 1.2f).coerceAtMost(5f) },
                onZoomOut = { zoomLevel = (zoomLevel / 1.2f).coerceAtLeast(0.5f) },
                onReset = {
                    zoomLevel = 1f
                    panOffset = Offset.Zero
                },
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        
        // Selected point info
        selectedPoint?.let { point ->
            PointInfoCard(
                point = point,
                onDismiss = { selectedPoint = null },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
        
        // Selected region info
        selectedRegion?.let { region ->
            RegionInfoCard(
                region = region,
                onDismiss = { selectedRegion = null },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

// ============================================================================
// 🔥 HEAT MAP
// ============================================================================

@Composable
fun HeatMap(
    points: List<MapPoint>,
    modifier: Modifier = Modifier,
    intensity: Float = 1f,
    radius: Float = 50f,
    colorScheme: List<Color> = listOf(
        Color.Transparent,
        Color(0xFF0000FF).copy(alpha = 0.6f),
        Color(0xFF00FF00).copy(alpha = 0.8f),
        Color(0xFFFFFF00).copy(alpha = 0.9f),
        Color(0xFFFF0000).copy(alpha = 1f)
    ),
    onPointClick: ((MapPoint) -> Unit)? = null
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedPoint = findPointAtPosition(points, offset, size)
                    clickedPoint?.let { onPointClick?.invoke(it) }
                }
            }
    ) {
        // Create heat map effect
        points.forEach { point ->
            val screenPos = latLngToScreen(point.latitude, point.longitude, size)
            val heatIntensity = (point.value * intensity).coerceIn(0.0, 1.0).toFloat()
            
            // Draw heat circle with gradient
            val gradient = RadialGradientShader(
                center = screenPos,
                radius = radius,
                colors = colorScheme,
                colorStops = listOf(0f, 0.3f, 0.6f, 0.8f, 1f)
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = colorScheme,
                    center = screenPos,
                    radius = radius
                ),
                radius = radius * heatIntensity,
                center = screenPos,
                alpha = heatIntensity
            )
        }
    }
}

// ============================================================================
// 🎯 CLUSTER MAP
// ============================================================================

@Composable
fun ClusterMap(
    points: List<MapPoint>,
    modifier: Modifier = Modifier,
    clusterRadius: Float = 60f,
    minClusterSize: Int = 2,
    onClusterClick: ((MapCluster) -> Unit)? = null,
    onPointClick: ((MapPoint) -> Unit)? = null
) {
    var zoomLevel by remember { mutableStateOf(1f) }
    var selectedCluster by remember { mutableStateOf<MapCluster?>(null) }
    
    val clusters = remember(points, zoomLevel, clusterRadius) {
        clusterPoints(points, zoomLevel, clusterRadius, minClusterSize)
    }
    
    val individualPoints = remember(points, clusters) {
        points.filter { point ->
            clusters.none { cluster -> cluster.points.contains(point) }
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        zoomLevel = (zoomLevel * zoom).coerceIn(0.5f, 5f)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Check cluster clicks
                        val clickedCluster = findClusterAtPosition(clusters, offset, size)
                        if (clickedCluster != null) {
                            selectedCluster = clickedCluster
                            onClusterClick?.invoke(clickedCluster)
                            return@detectTapGestures
                        }
                        
                        // Check individual point clicks
                        val clickedPoint = findPointAtPosition(individualPoints, offset, size)
                        clickedPoint?.let { onPointClick?.invoke(it) }
                    }
                }
        ) {
            // Draw clusters
            clusters.forEach { cluster ->
                drawCluster(cluster, androidx.compose.ui.graphics.Matrix(), size)
            }
            
            // Draw individual points
            individualPoints.forEach { point ->
                drawMapPoint(point, false, androidx.compose.ui.graphics.Matrix(), size)
            }
        }
        
        // Cluster details
        selectedCluster?.let { cluster ->
            ClusterInfoCard(
                cluster = cluster,
                onDismiss = { selectedCluster = null },
                onPointClick = onPointClick,
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

// ============================================================================
// 🌈 CHOROPLETH MAP
// ============================================================================

@Composable
fun ChoroplethMap(
    regions: List<MapRegion>,
    modifier: Modifier = Modifier,
    colorScheme: List<Color> = listOf(
        Color(0xFFE3F2FD),
        Color(0xFF90CAF9),
        Color(0xFF42A5F5),
        Color(0xFF1E88E5),
        Color(0xFF1565C0)
    ),
    showLegend: Boolean = true,
    onRegionClick: ((MapRegion) -> Unit)? = null
) {
    val maxValue = regions.maxOfOrNull { it.value } ?: 1.0
    val minValue = regions.minOfOrNull { it.value } ?: 0.0
    val valueRange = maxValue - minValue
    
    var selectedRegion by remember { mutableStateOf<MapRegion?>(null) }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val clickedRegion = findRegionAtPosition(regions, offset)
                        if (clickedRegion != null) {
                            selectedRegion = clickedRegion
                            onRegionClick?.invoke(clickedRegion)
                        }
                    }
                }
        ) {
            regions.forEach { region ->
                val intensity = if (valueRange > 0) {
                    ((region.value - minValue) / valueRange).toFloat()
                } else 0f
                
                val regionColor = interpolateColor(colorScheme, intensity)
                val isSelected = selectedRegion == region
                
                drawMapRegion(
                    region.copy(color = regionColor),
                    isSelected,
                    androidx.compose.ui.graphics.Matrix()
                )
            }
        }
        
        // Legend
        if (showLegend) {
            ChoroplethLegend(
                colorScheme = colorScheme,
                minValue = minValue,
                maxValue = maxValue,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        
        // Selected region info
        selectedRegion?.let { region ->
            RegionInfoCard(
                region = region,
                onDismiss = { selectedRegion = null },
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

// ============================================================================
// 🎛️ MAP CONTROLS
// ============================================================================

@Composable
private fun MapControls(
    zoomLevel: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onZoomIn) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom In"
                )
            }
            
            Text(
                text = "${(zoomLevel * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            IconButton(onClick = onZoomOut) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out"
                )
            }
            
            Divider()
            
            IconButton(onClick = onReset) {
                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = "Reset View"
                )
            }
        }
    }
}

// ============================================================================
// 📋 INFO CARDS
// ============================================================================

@Composable
private fun PointInfoCard(
    point: MapPoint,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = point.label.ifEmpty { "Point" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = "Lat: ${point.latitude}, Lng: ${point.longitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (point.value != 0.0) {
                Text(
                    text = "Value: ${point.value}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (point.metadata.isNotEmpty()) {
                point.metadata.forEach { (key, value) ->
                    Text(
                        text = "$key: $value",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionInfoCard(
    region: MapRegion,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = region.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = "ID: ${region.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (region.value != 0.0) {
                Text(
                    text = "Value: ${region.value}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (region.metadata.isNotEmpty()) {
                region.metadata.forEach { (key, value) ->
                    Text(
                        text = "$key: $value",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ClusterInfoCard(
    cluster: MapCluster,
    onDismiss: () -> Unit,
    onPointClick: ((MapPoint) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Cluster (${cluster.points.size} points)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = "Center: ${cluster.centerLatitude}, ${cluster.centerLongitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(cluster.points) { point ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPointClick?.invoke(point) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = point.label.ifEmpty { "Point" },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (point.value != 0.0) {
                                Text(
                                    text = "Value: ${point.value}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoroplethLegend(
    colorScheme: List<Color>,
    minValue: Double,
    maxValue: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Legend",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            
            colorScheme.forEachIndexed { index, color ->
                val value = minValue + (maxValue - minValue) * index / (colorScheme.size - 1)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color, RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "%.1f".format(value),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun DrawScope.drawMapPoint(
    point: MapPoint,
    isSelected: Boolean,
    transform: androidx.compose.ui.graphics.Matrix,
    canvasSize: Size
) {
    val screenPos = latLngToScreen(point.latitude, point.longitude, canvasSize)
    val transformedPos = transform.map(screenPos)
    
    val radius = 8.dp.toPx() * point.size
    val strokeWidth = if (isSelected) 3.dp.toPx() else 1.dp.toPx()
    
    drawCircle(
        color = point.color,
        radius = radius,
        center = transformedPos
    )
    
    drawCircle(
        color = if (isSelected) Color.Red else Color.White,
        radius = radius,
        center = transformedPos,
        style = Stroke(width = strokeWidth)
    )
}

private fun DrawScope.drawMapRegion(
    region: MapRegion,
    isSelected: Boolean,
    transform: androidx.compose.ui.graphics.Matrix
) {
    if (region.coordinates.isEmpty()) return
    
    val path = androidx.compose.ui.graphics.Path()
    region.coordinates.forEachIndexed { index, point ->
        val transformedPoint = transform.map(point)
        if (index == 0) {
            path.moveTo(transformedPoint.x, transformedPoint.y)
        } else {
            path.lineTo(transformedPoint.x, transformedPoint.y)
        }
    }
    path.close()
    
    drawPath(
        path = path,
        color = region.color,
        style = Fill
    )
    
    drawPath(
        path = path,
        color = if (isSelected) Color.Red else Color.Black.copy(alpha = 0.3f),
        style = Stroke(width = if (isSelected) 3.dp.toPx() else 1.dp.toPx())
    )
}

private fun DrawScope.drawCluster(
    cluster: MapCluster,
    transform: androidx.compose.ui.graphics.Matrix,
    canvasSize: Size
) {
    val screenPos = latLngToScreen(cluster.centerLatitude, cluster.centerLongitude, canvasSize)
    val transformedPos = transform.map(screenPos)
    
    val radius = (20 + cluster.points.size * 2).dp.toPx().coerceAtMost(50.dp.toPx())
    
    // Draw cluster background
    drawCircle(
        color = Color.Blue.copy(alpha = 0.7f),
        radius = radius,
        center = transformedPos
    )
    
    // Draw cluster border
    drawCircle(
        color = Color.Blue,
        radius = radius,
        center = transformedPos,
        style = Stroke(width = 2.dp.toPx())
    )
    
    // Draw count text (simplified - in real implementation would use proper text drawing)
    drawCircle(
        color = Color.White,
        radius = radius * 0.6f,
        center = transformedPos
    )
}

private fun latLngToScreen(latitude: Double, longitude: Double, canvasSize: Size): Offset {
    val x = ((longitude + 180) / 360) * canvasSize.width
    val y = ((90 - latitude) / 180) * canvasSize.height
    return Offset(x.toFloat(), y.toFloat())
}

private fun findPointAtPosition(
    points: List<MapPoint>,
    offset: Offset,
    canvasSize: Size
): MapPoint? {
    return points.find { point ->
        val screenPos = latLngToScreen(point.latitude, point.longitude, canvasSize)
        val distance = sqrt((offset.x - screenPos.x).pow(2) + (offset.y - screenPos.y).pow(2))
        distance <= 20f // Hit test radius
    }
}

private fun findRegionAtPosition(
    regions: List<MapRegion>,
    offset: Offset
): MapRegion? {
    return regions.find { region ->
        // Simplified point-in-polygon test
        region.coordinates.isNotEmpty() &&
        offset.x >= region.coordinates.minOf { it.x } &&
        offset.x <= region.coordinates.maxOf { it.x } &&
        offset.y >= region.coordinates.minOf { it.y } &&
        offset.y <= region.coordinates.maxOf { it.y }
    }
}

private fun findClusterAtPosition(
    clusters: List<MapCluster>,
    offset: Offset,
    canvasSize: Size
): MapCluster? {
    return clusters.find { cluster ->
        val screenPos = latLngToScreen(cluster.centerLatitude, cluster.centerLongitude, canvasSize)
        val distance = sqrt((offset.x - screenPos.x).pow(2) + (offset.y - screenPos.y).pow(2))
        distance <= cluster.radius
    }
}

private fun clusterPoints(
    points: List<MapPoint>,
    zoomLevel: Float,
    clusterRadius: Float = 60f,
    minClusterSize: Int = 2
): List<MapCluster> {
    val clusters = mutableListOf<MapCluster>()
    val processed = mutableSetOf<MapPoint>()
    
    points.forEach { point ->
        if (point in processed) return@forEach
        
        val nearbyPoints = points.filter { other ->
            if (other == point || other in processed) return@filter false
            
            val distance = calculateDistance(
                point.latitude, point.longitude,
                other.latitude, other.longitude
            )
            distance <= clusterRadius / zoomLevel
        }
        
        if (nearbyPoints.size >= minClusterSize - 1) {
            val clusterPoints = listOf(point) + nearbyPoints
            val centerLat = clusterPoints.map { it.latitude }.average()
            val centerLng = clusterPoints.map { it.longitude }.average()
            
            clusters.add(
                MapCluster(
                    centerLatitude = centerLat,
                    centerLongitude = centerLng,
                    points = clusterPoints,
                    radius = clusterRadius
                )
            )
            
            processed.addAll(clusterPoints)
        }
    }
    
    return clusters
}

private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadius = 6371000.0 // meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

private fun interpolateColor(colors: List<Color>, factor: Float): Color {
    if (colors.isEmpty()) return Color.Gray
    if (colors.size == 1) return colors[0]
    
    val clampedFactor = factor.coerceIn(0f, 1f)
    val scaledFactor = clampedFactor * (colors.size - 1)
    val index = scaledFactor.toInt()
    val remainder = scaledFactor - index
    
    if (index >= colors.size - 1) return colors.last()
    
    val color1 = colors[index]
    val color2 = colors[index + 1]
    
    return Color(
        red = color1.red + (color2.red - color1.red) * remainder,
        green = color1.green + (color2.green - color1.green) * remainder,
        blue = color1.blue + (color2.blue - color1.blue) * remainder,
        alpha = color1.alpha + (color2.alpha - color1.alpha) * remainder
    )
}