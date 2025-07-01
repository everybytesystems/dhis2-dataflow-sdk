package com.everybytesystems.dataflow.ui.maps

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
 * DataFlow Maps Implementation
 * Interactive maps with markers, overlays, and geospatial data visualization
 */

// ============================================================================
// 🗺️ DATA MODELS
// ============================================================================

data class LatLng(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun distance(point1: LatLng, point2: LatLng): Double {
            val earthRadius = 6371000.0 // Earth radius in meters
            val lat1Rad = Math.toRadians(point1.latitude)
            val lat2Rad = Math.toRadians(point2.latitude)
            val deltaLatRad = Math.toRadians(point2.latitude - point1.latitude)
            val deltaLngRad = Math.toRadians(point2.longitude - point1.longitude)
            
            val a = sin(deltaLatRad / 2).pow(2) + 
                    cos(lat1Rad) * cos(lat2Rad) * sin(deltaLngRad / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            
            return earthRadius * c
        }
    }
}

data class MapBounds(
    val northeast: LatLng,
    val southwest: LatLng
) {
    fun contains(point: LatLng): Boolean {
        return point.latitude <= northeast.latitude &&
               point.latitude >= southwest.latitude &&
               point.longitude <= northeast.longitude &&
               point.longitude >= southwest.longitude
    }
    
    fun center(): LatLng {
        return LatLng(
            latitude = (northeast.latitude + southwest.latitude) / 2,
            longitude = (northeast.longitude + southwest.longitude) / 2
        )
    }
}

data class MapMarker(
    val id: String,
    val position: LatLng,
    val title: String = "",
    val snippet: String = "",
    val icon: ImageVector = Icons.Default.LocationOn,
    val color: Color = Color.Red,
    val size: Dp = 24.dp,
    val draggable: Boolean = false,
    val visible: Boolean = true,
    val zIndex: Float = 0f,
    val metadata: Map<String, Any> = emptyMap()
)

data class MapPolyline(
    val id: String,
    val points: List<LatLng>,
    val color: Color = Color.Blue,
    val width: Dp = 2.dp,
    val pattern: List<Float> = emptyList(), // Dash pattern
    val visible: Boolean = true,
    val zIndex: Float = 0f
)

data class MapPolygon(
    val id: String,
    val points: List<LatLng>,
    val fillColor: Color = Color.Blue.copy(alpha = 0.3f),
    val strokeColor: Color = Color.Blue,
    val strokeWidth: Dp = 2.dp,
    val visible: Boolean = true,
    val zIndex: Float = 0f
)

data class MapCircle(
    val id: String,
    val center: LatLng,
    val radius: Double, // in meters
    val fillColor: Color = Color.Blue.copy(alpha = 0.3f),
    val strokeColor: Color = Color.Blue,
    val strokeWidth: Dp = 2.dp,
    val visible: Boolean = true,
    val zIndex: Float = 0f
)

data class HeatmapPoint(
    val position: LatLng,
    val intensity: Float = 1f,
    val radius: Double = 1000.0 // in meters
)

enum class MapType {
    NORMAL, SATELLITE, TERRAIN, HYBRID
}

data class MapConfig(
    val mapType: MapType = MapType.NORMAL,
    val showZoomControls: Boolean = true,
    val showCompass: Boolean = true,
    val showMyLocation: Boolean = false,
    val showTraffic: Boolean = false,
    val gesturesEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val rotateGesturesEnabled: Boolean = true,
    val tiltGesturesEnabled: Boolean = true,
    val minZoom: Float = 2f,
    val maxZoom: Float = 20f,
    val initialZoom: Float = 10f,
    val initialCenter: LatLng = LatLng(0.0, 0.0)
)

// ============================================================================
// 🗺️ MAIN MAP COMPONENT
// ============================================================================

@Composable
fun DataFlowMap(
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    markers: List<MapMarker> = emptyList(),
    polylines: List<MapPolyline> = emptyList(),
    polygons: List<MapPolygon> = emptyList(),
    circles: List<MapCircle> = emptyList(),
    heatmapPoints: List<HeatmapPoint> = emptyList(),
    onMapClick: ((LatLng) -> Unit)? = null,
    onMarkerClick: ((MapMarker) -> Unit)? = null,
    onMapReady: (() -> Unit)? = null,
    onCameraMove: ((LatLng, Float) -> Unit)? = null
) {
    var currentCenter by remember { mutableStateOf(config.initialCenter) }
    var currentZoom by remember { mutableStateOf(config.initialZoom) }
    var selectedMarker by remember { mutableStateOf<MapMarker?>(null) }
    var isMapReady by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isMapReady = true
        onMapReady?.invoke()
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Map canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    if (config.gesturesEnabled) {
                        detectTapGestures { offset ->
                            val latLng = screenToLatLng(offset, size, currentCenter, currentZoom)
                            
                            // Check if tap is on a marker
                            val clickedMarker = findMarkerAtPosition(markers, offset, size, currentCenter, currentZoom)
                            if (clickedMarker != null) {
                                selectedMarker = clickedMarker
                                onMarkerClick?.invoke(clickedMarker)
                            } else {
                                selectedMarker = null
                                onMapClick?.invoke(latLng)
                            }
                        }
                    }
                }
        ) {
            if (isMapReady) {
                drawMap(
                    config = config,
                    center = currentCenter,
                    zoom = currentZoom,
                    markers = markers,
                    polylines = polylines,
                    polygons = polygons,
                    circles = circles,
                    heatmapPoints = heatmapPoints,
                    selectedMarker = selectedMarker
                )
            }
        }
        
        // Map controls
        MapControls(
            config = config,
            currentZoom = currentZoom,
            onZoomIn = { 
                if (currentZoom < config.maxZoom) {
                    currentZoom = (currentZoom + 1f).coerceAtMost(config.maxZoom)
                    onCameraMove?.invoke(currentCenter, currentZoom)
                }
            },
            onZoomOut = { 
                if (currentZoom > config.minZoom) {
                    currentZoom = (currentZoom - 1f).coerceAtLeast(config.minZoom)
                    onCameraMove?.invoke(currentCenter, currentZoom)
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
        
        // Marker info window
        selectedMarker?.let { marker ->
            MarkerInfoWindow(
                marker = marker,
                onClose = { selectedMarker = null },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
        
        // Map type selector
        MapTypeSelector(
            currentType = config.mapType,
            onTypeChange = { /* Handle map type change */ },
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

private fun DrawScope.drawMap(
    config: MapConfig,
    center: LatLng,
    zoom: Float,
    markers: List<MapMarker>,
    polylines: List<MapPolyline>,
    polygons: List<MapPolygon>,
    circles: List<MapCircle>,
    heatmapPoints: List<HeatmapPoint>,
    selectedMarker: MapMarker?
) {
    // Draw base map
    drawMapBackground(config.mapType)
    
    // Draw map grid/tiles (simplified)
    drawMapGrid(center, zoom)
    
    // Draw overlays in order of z-index
    val allOverlays = listOf(
        polygons.map { it to "polygon" },
        circles.map { it to "circle" },
        polylines.map { it to "polyline" },
        markers.map { it to "marker" }
    ).flatten().sortedBy { 
        when (it.second) {
            "polygon" -> (it.first as MapPolygon).zIndex
            "circle" -> (it.first as MapCircle).zIndex
            "polyline" -> (it.first as MapPolyline).zIndex
            "marker" -> (it.first as MapMarker).zIndex
            else -> 0f
        }
    }
    
    allOverlays.forEach { (overlay, type) ->
        when (type) {
            "polygon" -> drawPolygon(overlay as MapPolygon, center, zoom)
            "circle" -> drawCircle(overlay as MapCircle, center, zoom)
            "polyline" -> drawPolyline(overlay as MapPolyline, center, zoom)
            "marker" -> drawMarker(overlay as MapMarker, center, zoom, overlay == selectedMarker)
        }
    }
    
    // Draw heatmap
    if (heatmapPoints.isNotEmpty()) {
        drawHeatmap(heatmapPoints, center, zoom)
    }
}

private fun DrawScope.drawMapBackground(mapType: MapType) {
    val backgroundColor = when (mapType) {
        MapType.NORMAL -> Color(0xFFF5F5DC) // Beige
        MapType.SATELLITE -> Color(0xFF2F4F2F) // Dark green
        MapType.TERRAIN -> Color(0xFFDEB887) // Burlywood
        MapType.HYBRID -> Color(0xFF2F4F2F) // Dark green
    }
    
    drawRect(
        color = backgroundColor,
        topLeft = Offset.Zero,
        size = size
    )
}

private fun DrawScope.drawMapGrid(center: LatLng, zoom: Float) {
    val gridColor = Color.Gray.copy(alpha = 0.3f)
    val gridSpacing = 50.dp.toPx() / zoom
    
    // Draw vertical lines
    var x = 0f
    while (x < size.width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx()
        )
        x += gridSpacing
    }
    
    // Draw horizontal lines
    var y = 0f
    while (y < size.height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
        )
        y += gridSpacing
    }
}

private fun DrawScope.drawMarker(
    marker: MapMarker,
    center: LatLng,
    zoom: Float,
    isSelected: Boolean
) {
    if (!marker.visible) return
    
    val screenPos = latLngToScreen(marker.position, size, center, zoom)
    val markerSize = if (isSelected) marker.size.toPx() * 1.2f else marker.size.toPx()
    
    // Draw marker shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = markerSize / 2 + 2.dp.toPx(),
        center = Offset(screenPos.x + 2.dp.toPx(), screenPos.y + 2.dp.toPx())
    )
    
    // Draw marker
    drawCircle(
        color = marker.color,
        radius = markerSize / 2,
        center = screenPos
    )
    
    // Draw marker icon (simplified as a dot)
    drawCircle(
        color = Color.White,
        radius = markerSize / 4,
        center = screenPos
    )
    
    // Draw selection indicator
    if (isSelected) {
        drawCircle(
            color = marker.color,
            radius = markerSize / 2 + 4.dp.toPx(),
            center = screenPos,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

private fun DrawScope.drawPolyline(
    polyline: MapPolyline,
    center: LatLng,
    zoom: Float
) {
    if (!polyline.visible || polyline.points.size < 2) return
    
    val path = Path()
    polyline.points.forEachIndexed { index, point ->
        val screenPos = latLngToScreen(point, size, center, zoom)
        if (index == 0) {
            path.moveTo(screenPos.x, screenPos.y)
        } else {
            path.lineTo(screenPos.x, screenPos.y)
        }
    }
    
    val pathEffect = if (polyline.pattern.isNotEmpty()) {
        PathEffect.dashPathEffect(polyline.pattern.toFloatArray())
    } else null
    
    drawPath(
        path = path,
        color = polyline.color,
        style = Stroke(
            width = polyline.width.toPx(),
            pathEffect = pathEffect
        )
    )
}

private fun DrawScope.drawPolygon(
    polygon: MapPolygon,
    center: LatLng,
    zoom: Float
) {
    if (!polygon.visible || polygon.points.size < 3) return
    
    val path = Path()
    polygon.points.forEachIndexed { index, point ->
        val screenPos = latLngToScreen(point, size, center, zoom)
        if (index == 0) {
            path.moveTo(screenPos.x, screenPos.y)
        } else {
            path.lineTo(screenPos.x, screenPos.y)
        }
    }
    path.close()
    
    // Draw fill
    drawPath(
        path = path,
        color = polygon.fillColor
    )
    
    // Draw stroke
    drawPath(
        path = path,
        color = polygon.strokeColor,
        style = Stroke(width = polygon.strokeWidth.toPx())
    )
}

private fun DrawScope.drawCircle(
    circle: MapCircle,
    center: LatLng,
    zoom: Float
) {
    if (!circle.visible) return
    
    val screenCenter = latLngToScreen(circle.center, size, center, zoom)
    val radiusInPixels = metersToPixels(circle.radius, circle.center.latitude, zoom)
    
    // Draw fill
    drawCircle(
        color = circle.fillColor,
        radius = radiusInPixels,
        center = screenCenter
    )
    
    // Draw stroke
    drawCircle(
        color = circle.strokeColor,
        radius = radiusInPixels,
        center = screenCenter,
        style = Stroke(width = circle.strokeWidth.toPx())
    )
}

private fun DrawScope.drawHeatmap(
    heatmapPoints: List<HeatmapPoint>,
    center: LatLng,
    zoom: Float
) {
    heatmapPoints.forEach { point ->
        val screenPos = latLngToScreen(point.position, size, center, zoom)
        val radiusInPixels = metersToPixels(point.radius, point.position.latitude, zoom)
        
        // Create gradient for heatmap effect
        val gradient = Brush.radialGradient(
            colors = listOf(
                Color.Red.copy(alpha = point.intensity * 0.8f),
                Color.Yellow.copy(alpha = point.intensity * 0.4f),
                Color.Transparent
            ),
            center = screenPos,
            radius = radiusInPixels
        )
        
        drawCircle(
            brush = gradient,
            radius = radiusInPixels,
            center = screenPos
        )
    }
}

// ============================================================================
// 🎛️ MAP CONTROLS
// ============================================================================

@Composable
private fun MapControls(
    config: MapConfig,
    currentZoom: Float,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!config.showZoomControls) return
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Zoom in button
        FloatingActionButton(
            onClick = onZoomIn,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Zoom In"
            )
        }
        
        // Zoom level indicator
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text(
                text = "${currentZoom.toInt()}x",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
        
        // Zoom out button
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Zoom Out"
            )
        }
    }
}

@Composable
private fun MapTypeSelector(
    currentType: MapType,
    onTypeChange: (MapType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = modifier.padding(16.dp)) {
        FloatingActionButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Icon(
                imageVector = Icons.Default.Layers,
                contentDescription = "Map Type"
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MapType.values().forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onTypeChange(type)
                        expanded = false
                    },
                    leadingIcon = {
                        if (type == currentType) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MarkerInfoWindow(
    marker: MapMarker,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = marker.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
            
            if (marker.snippet.isNotEmpty()) {
                Text(
                    text = marker.snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Text(
                text = "Lat: ${marker.position.latitude}, Lng: ${marker.position.longitude}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// ============================================================================
// 🌍 SPECIALIZED MAP COMPONENTS
// ============================================================================

@Composable
fun ClusterMap(
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    clusterRadius: Double = 100.0, // meters
    onClusterClick: ((List<MapMarker>) -> Unit)? = null,
    onMarkerClick: ((MapMarker) -> Unit)? = null
) {
    var currentZoom by remember { mutableStateOf(config.initialZoom) }
    val clusteredMarkers = remember(markers, currentZoom) {
        clusterMarkers(markers, clusterRadius, currentZoom)
    }
    
    DataFlowMap(
        modifier = modifier,
        config = config,
        markers = clusteredMarkers,
        onMarkerClick = { marker ->
            if (marker.metadata.containsKey("cluster")) {
                val clusterMarkers = marker.metadata["clusterMarkers"] as? List<MapMarker>
                clusterMarkers?.let { onClusterClick?.invoke(it) }
            } else {
                onMarkerClick?.invoke(marker)
            }
        },
        onCameraMove = { _, zoom ->
            currentZoom = zoom
        }
    )
}

@Composable
fun HeatMap(
    points: List<HeatmapPoint>,
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    gradient: List<Color> = listOf(Color.Blue, Color.Green, Color.Yellow, Color.Red),
    radius: Double = 1000.0,
    opacity: Float = 0.6f
) {
    DataFlowMap(
        modifier = modifier,
        config = config,
        heatmapPoints = points.map { point ->
            point.copy(
                radius = radius,
                intensity = point.intensity * opacity
            )
        }
    )
}

@Composable
fun RouteMap(
    waypoints: List<LatLng>,
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    routeColor: Color = Color.Blue,
    routeWidth: Dp = 4.dp,
    showDirections: Boolean = true,
    onRouteClick: (() -> Unit)? = null
) {
    val routePolyline = MapPolyline(
        id = "route",
        points = waypoints,
        color = routeColor,
        width = routeWidth
    )
    
    val routeMarkers = waypoints.mapIndexed { index, point ->
        MapMarker(
            id = "waypoint_$index",
            position = point,
            title = when (index) {
                0 -> "Start"
                waypoints.size - 1 -> "End"
                else -> "Waypoint ${index + 1}"
            },
            color = when (index) {
                0 -> Color.Green
                waypoints.size - 1 -> Color.Red
                else -> Color.Blue
            }
        )
    }
    
    DataFlowMap(
        modifier = modifier,
        config = config,
        markers = routeMarkers,
        polylines = listOf(routePolyline)
    )
}

@Composable
fun GeofenceMap(
    geofences: List<MapCircle>,
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    onGeofenceEnter: ((MapCircle) -> Unit)? = null,
    onGeofenceExit: ((MapCircle) -> Unit)? = null,
    currentLocation: LatLng? = null
) {
    val locationMarker = currentLocation?.let { location ->
        MapMarker(
            id = "current_location",
            position = location,
            title = "Current Location",
            color = Color.Blue,
            icon = Icons.Default.MyLocation
        )
    }
    
    DataFlowMap(
        modifier = modifier,
        config = config,
        markers = listOfNotNull(locationMarker),
        circles = geofences
    )
}

// ============================================================================
// 🧮 UTILITY FUNCTIONS
// ============================================================================

private fun latLngToScreen(
    latLng: LatLng,
    canvasSize: Size,
    center: LatLng,
    zoom: Float
): Offset {
    val scale = 256 * 2.0.pow(zoom.toDouble())
    
    val centerX = (center.longitude + 180) / 360 * scale
    val centerY = (1 - ln(tan(Math.toRadians(center.latitude)) + 1 / cos(Math.toRadians(center.latitude))) / PI) / 2 * scale
    
    val pointX = (latLng.longitude + 180) / 360 * scale
    val pointY = (1 - ln(tan(Math.toRadians(latLng.latitude)) + 1 / cos(Math.toRadians(latLng.latitude))) / PI) / 2 * scale
    
    val screenX = canvasSize.width / 2 + (pointX - centerX)
    val screenY = canvasSize.height / 2 + (pointY - centerY)
    
    return Offset(screenX.toFloat(), screenY.toFloat())
}

private fun screenToLatLng(
    screenPos: Offset,
    canvasSize: Size,
    center: LatLng,
    zoom: Float
): LatLng {
    val scale = 256 * 2.0.pow(zoom.toDouble())
    
    val centerX = (center.longitude + 180) / 360 * scale
    val centerY = (1 - ln(tan(Math.toRadians(center.latitude)) + 1 / cos(Math.toRadians(center.latitude))) / PI) / 2 * scale
    
    val pointX = centerX + (screenPos.x - canvasSize.width / 2)
    val pointY = centerY + (screenPos.y - canvasSize.height / 2)
    
    val longitude = pointX / scale * 360 - 180
    val latitude = Math.toDegrees(atan(sinh(PI * (1 - 2 * pointY / scale))))
    
    return LatLng(latitude, longitude)
}

private fun metersToPixels(meters: Double, latitude: Double, zoom: Float): Float {
    val metersPerPixel = 156543.03392 * cos(Math.toRadians(latitude)) / 2.0.pow(zoom.toDouble())
    return (meters / metersPerPixel).toFloat()
}

private fun findMarkerAtPosition(
    markers: List<MapMarker>,
    screenPos: Offset,
    canvasSize: Size,
    center: LatLng,
    zoom: Float
): MapMarker? {
    return markers.find { marker ->
        if (!marker.visible) return@find false
        
        val markerScreenPos = latLngToScreen(marker.position, canvasSize, center, zoom)
        val distance = sqrt(
            (screenPos.x - markerScreenPos.x).pow(2) + 
            (screenPos.y - markerScreenPos.y).pow(2)
        )
        
        distance <= marker.size.value * 1.5f // Touch tolerance
    }
}

private fun clusterMarkers(
    markers: List<MapMarker>,
    clusterRadius: Double,
    zoom: Float
): List<MapMarker> {
    if (zoom > 15f) return markers // Don't cluster at high zoom levels
    
    val clustered = mutableListOf<MapMarker>()
    val processed = mutableSetOf<String>()
    
    markers.forEach { marker ->
        if (marker.id in processed) return@forEach
        
        val nearbyMarkers = markers.filter { other ->
            other.id != marker.id && 
            other.id !in processed &&
            LatLng.distance(marker.position, other.position) <= clusterRadius
        }
        
        if (nearbyMarkers.isNotEmpty()) {
            // Create cluster marker
            val allMarkers = listOf(marker) + nearbyMarkers
            val centerLat = allMarkers.map { it.position.latitude }.average()
            val centerLng = allMarkers.map { it.position.longitude }.average()
            
            clustered.add(
                MapMarker(
                    id = "cluster_${marker.id}",
                    position = LatLng(centerLat, centerLng),
                    title = "${allMarkers.size} markers",
                    color = Color.Orange,
                    size = 32.dp,
                    metadata = mapOf(
                        "cluster" to true,
                        "clusterMarkers" to allMarkers
                    )
                )
            )
            
            processed.addAll(allMarkers.map { it.id })
        } else {
            clustered.add(marker)
            processed.add(marker.id)
        }
    }
    
    return clustered
}

// ============================================================================
// 📍 LOCATION SERVICES
// ============================================================================

@Composable
fun LocationPicker(
    initialLocation: LatLng? = null,
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    onLocationSelected: (LatLng) -> Unit
) {
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    
    Column(modifier = modifier) {
        DataFlowMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            config = config.copy(
                initialCenter = initialLocation ?: config.initialCenter
            ),
            markers = selectedLocation?.let { location ->
                listOf(
                    MapMarker(
                        id = "selected_location",
                        position = location,
                        title = "Selected Location",
                        color = Color.Red,
                        draggable = true
                    )
                )
            } ?: emptyList(),
            onMapClick = { latLng ->
                selectedLocation = latLng
            }
        )
        
        // Location info and actions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                selectedLocation?.let { location ->
                    Text(
                        text = "Selected Location:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lat: ${location.latitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Lng: ${location.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { onLocationSelected(location) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Location")
                    }
                } ?: run {
                    Text(
                        text = "Tap on the map to select a location",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DistanceMeasurer(
    modifier: Modifier = Modifier,
    config: MapConfig = MapConfig(),
    onDistanceMeasured: (Double) -> Unit
) {
    var waypoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var totalDistance by remember { mutableStateOf(0.0) }
    
    LaunchedEffect(waypoints) {
        totalDistance = if (waypoints.size > 1) {
            waypoints.zipWithNext { a, b ->
                LatLng.distance(a, b)
            }.sum()
        } else {
            0.0
        }
        onDistanceMeasured(totalDistance)
    }
    
    Column(modifier = modifier) {
        DataFlowMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            config = config,
            markers = waypoints.mapIndexed { index, point ->
                MapMarker(
                    id = "waypoint_$index",
                    position = point,
                    title = "Point ${index + 1}",
                    color = if (index == 0) Color.Green else Color.Red
                )
            },
            polylines = if (waypoints.size > 1) {
                listOf(
                    MapPolyline(
                        id = "measurement_line",
                        points = waypoints,
                        color = Color.Blue,
                        width = 3.dp,
                        pattern = listOf(10f, 5f)
                    )
                )
            } else emptyList(),
            onMapClick = { latLng ->
                waypoints = waypoints + latLng
            }
        )
        
        // Distance info and controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Distance: ${formatDistance(totalDistance)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${waypoints.size} points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { 
                            if (waypoints.isNotEmpty()) {
                                waypoints = waypoints.dropLast(1)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = waypoints.isNotEmpty()
                    ) {
                        Text("Undo")
                    }
                    
                    Button(
                        onClick = { waypoints = emptyList() },
                        modifier = Modifier.weight(1f),
                        enabled = waypoints.isNotEmpty()
                    ) {
                        Text("Clear")
                    }
                }
            }
        }
    }
}

private fun formatDistance(meters: Double): String {
    return when {
        meters < 1000 -> "${meters.toInt()} m"
        meters < 10000 -> "${"%.1f".format(meters / 1000)} km"
        else -> "${(meters / 1000).toInt()} km"
    }
}