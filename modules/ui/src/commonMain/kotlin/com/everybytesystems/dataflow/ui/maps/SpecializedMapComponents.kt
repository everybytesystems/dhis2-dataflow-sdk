package com.everybytesystems.dataflow.ui.maps

import androidx.compose.animation.*
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
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * UI Components for Specialized Maps
 * Supporting components for data presentation, surveillance, and climate maps
 */

// ============================================================================
// 📊 STATISTICAL DATA MAP COMPONENTS
// ============================================================================

@Composable
fun StatisticalDataLegend(
    dataPoints: List<StatisticalDataPoint>,
    config: DataVisualizationConfig,
    modifier: Modifier = Modifier
) {
    val minValue = dataPoints.minOfOrNull { it.value } ?: 0.0
    val maxValue = dataPoints.maxOfOrNull { it.value } ?: 1.0
    
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Data Legend",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Color gradient
            Canvas(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
            ) {
                val gradient = Brush.horizontalGradient(config.colorScheme)
                drawRect(gradient)
                drawRect(
                    color = Color.Black,
                    style = Stroke(width = 1.dp.toPx())
                )
            }
            
            // Value labels
            Row(
                modifier = Modifier.width(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format("%.1f", minValue),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = String.format("%.1f", maxValue),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            // Legend items
            if (config.showTrends) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Increasing",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Decreasing",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun DataPointInfoPanel(
    dataPoint: StatisticalDataPoint,
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
                    text = dataPoint.regionName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text(
                text = "Value: ${String.format("%.2f", dataPoint.value)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Category: ${dataPoint.category}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (dataPoint.trend != 0.0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        if (dataPoint.trend > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (dataPoint.trend > 0) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Trend: ${String.format("%.1f%%", dataPoint.trend * 100)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            LinearProgressIndicator(
                progress = dataPoint.confidence,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Confidence: ${String.format("%.0f%%", dataPoint.confidence * 100)}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ============================================================================
// 🕵️ CONTACT TRACING MAP COMPONENTS
// ============================================================================

@Composable
fun ContactTracingTimeControls(
    currentTime: Long,
    timeRange: LongRange,
    isPlaying: Boolean,
    onTimeChange: (Long) -> Unit,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Timeline Control",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Time display
            Text(
                text = dateFormat.format(Date(currentTime)),
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Time slider
            Slider(
                value = (currentTime - timeRange.first).toFloat(),
                onValueChange = { onTimeChange(timeRange.first + it.toLong()) },
                valueRange = 0f..(timeRange.last - timeRange.first).toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlayPause,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isPlaying) "Pause" else "Play")
                }
                
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Replay, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun ContactInfoPanel(
    contact: ContactNode,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    
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
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            // Status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (contact.status) {
                                ContactStatus.CONFIRMED -> Color.Red
                                ContactStatus.SUSPECTED -> Color.Orange
                                ContactStatus.EXPOSED -> Color.Yellow
                                ContactStatus.RECOVERED -> Color.Green
                                ContactStatus.DECEASED -> Color.Black
                                ContactStatus.UNKNOWN -> Color.Gray
                            },
                            shape = CircleShape
                        )
                )
                Text(
                    text = contact.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Risk level
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = when (contact.riskLevel) {
                        RiskLevel.CRITICAL -> Color.Red
                        RiskLevel.HIGH -> Color.Orange
                        RiskLevel.MEDIUM -> Color.Yellow
                        RiskLevel.LOW -> Color.Green
                    },
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Risk: ${contact.riskLevel.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Last contact
            Text(
                text = "Last Contact: ${dateFormat.format(Date(contact.lastContact))}",
                style = MaterialTheme.typography.bodySmall
            )
            
            // Quarantine status
            if (contact.quarantineStatus != QuarantineStatus.NONE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = when (contact.quarantineStatus) {
                            QuarantineStatus.MANDATORY -> Color.Red
                            QuarantineStatus.VOLUNTARY -> Color.Blue
                            QuarantineStatus.COMPLETED -> Color.Green
                            else -> Color.Gray
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Quarantine: ${contact.quarantineStatus.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Symptoms
            if (contact.symptoms.isNotEmpty()) {
                Text(
                    text = "Symptoms: ${contact.symptoms.joinString()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Test results
            if (contact.testResults.isNotEmpty()) {
                val latestTest = contact.testResults.maxByOrNull { it.date }
                latestTest?.let { test ->
                    Text(
                        text = "Latest Test: ${test.result} (${dateFormat.format(Date(test.date))})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ConnectionInfoPanel(
    connection: ContactConnection,
    contacts: List<ContactNode>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val fromContact = contacts.find { it.id == connection.fromId }
    val toContact = contacts.find { it.id == connection.toId }
    
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
                    text = "Contact Connection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            // Contact participants
            fromContact?.let { from ->
                Text(
                    text = "From: ${from.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            toContact?.let { to ->
                Text(
                    text = "To: ${to.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Contact details
            Text(
                text = "Date: ${dateFormat.format(Date(connection.contactDate))}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Duration: ${connection.duration} minutes",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Distance: ${String.format("%.1f", connection.distance)} meters",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Type: ${connection.contactType.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Transmission risk
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = connection.transmissionRisk,
                    modifier = Modifier.weight(1f),
                    color = when {
                        connection.transmissionRisk > 0.7f -> Color.Red
                        connection.transmissionRisk > 0.4f -> Color.Orange
                        else -> Color.Green
                    }
                )
                Text(
                    text = "${String.format("%.0f", connection.transmissionRisk * 100)}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Transmission Risk",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ============================================================================
// 🌍 GEOGRAPHICAL ANALYSIS COMPONENTS
// ============================================================================

@Composable
fun ElevationIndicator(
    elevation: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Terrain,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "${String.format("%.0f", elevation)}m",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Elevation",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun GeographicalFeaturePanel(
    feature: GeographicalFeature,
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
                    text = feature.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    when (feature.type) {
                        FeatureType.RIVER -> Icons.Default.Water
                        FeatureType.LAKE -> Icons.Default.Water
                        FeatureType.MOUNTAIN -> Icons.Default.Terrain
                        FeatureType.VALLEY -> Icons.Default.Landscape
                        FeatureType.COASTLINE -> Icons.Default.Beach
                        FeatureType.BORDER -> Icons.Default.BorderAll
                        FeatureType.ROAD -> Icons.Default.Road
                        FeatureType.RAILWAY -> Icons.Default.Train
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = feature.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = "Points: ${feature.coordinates.size}",
                style = MaterialTheme.typography.bodySmall
            )
            
            // Feature properties
            feature.properties.forEach { (key, value) ->
                Text(
                    text = "$key: $value",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// ============================================================================
// 🌡️ CLIMATE MAP COMPONENTS
// ============================================================================

@Composable
fun ClimateMapControls(
    visualizationType: ClimateVisualizationType,
    showWindVectors: Boolean,
    showIsobars: Boolean,
    showWeatherFronts: Boolean,
    timeRange: LongRange?,
    currentTime: Long,
    isAnimating: Boolean,
    onVisualizationTypeChange: (ClimateVisualizationType) -> Unit,
    onTimeChange: (Long) -> Unit,
    onAnimationToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Climate Visualization Controls",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Visualization type selector
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ClimateVisualizationType.values()) { type ->
                    FilterChip(
                        selected = visualizationType == type,
                        onClick = { onVisualizationTypeChange(type) },
                        label = {
                            Text(
                                text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
            
            // Display options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showWindVectors,
                        onCheckedChange = { /* Handle change */ }
                    )
                    Text(
                        text = "Wind Vectors",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showIsobars,
                        onCheckedChange = { /* Handle change */ }
                    )
                    Text(
                        text = "Isobars",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showWeatherFronts,
                        onCheckedChange = { /* Handle change */ }
                    )
                    Text(
                        text = "Weather Fronts",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            // Time controls
            timeRange?.let { range ->
                val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
                
                Text(
                    text = dateFormat.format(Date(currentTime)),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Slider(
                    value = (currentTime - range.first).toFloat(),
                    onValueChange = { onTimeChange(range.first + it.toLong()) },
                    valueRange = 0f..(range.last - range.first).toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(
                    onClick = onAnimationToggle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (isAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isAnimating) "Pause Animation" else "Start Animation")
                }
            }
        }
    }
}

@Composable
fun ClimateDataPanel(
    dataPoint: ClimateDataPoint,
    visualizationType: ClimateVisualizationType,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    
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
                    text = "Climate Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Text(
                text = dateFormat.format(Date(dataPoint.timestamp)),
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Primary data based on visualization type
            when (visualizationType) {
                ClimateVisualizationType.TEMPERATURE -> {
                    ClimateDataRow(
                        icon = Icons.Default.Thermostat,
                        label = "Temperature",
                        value = "${String.format("%.1f", dataPoint.temperature)}°C"
                    )
                }
                ClimateVisualizationType.HUMIDITY -> {
                    ClimateDataRow(
                        icon = Icons.Default.Water,
                        label = "Humidity",
                        value = "${String.format("%.1f", dataPoint.humidity)}%"
                    )
                }
                ClimateVisualizationType.PRESSURE -> {
                    ClimateDataRow(
                        icon = Icons.Default.Speed,
                        label = "Pressure",
                        value = "${String.format("%.1f", dataPoint.pressure)} hPa"
                    )
                }
                ClimateVisualizationType.WIND -> {
                    ClimateDataRow(
                        icon = Icons.Default.Air,
                        label = "Wind Speed",
                        value = "${String.format("%.1f", dataPoint.windSpeed)} m/s"
                    )
                    ClimateDataRow(
                        icon = Icons.Default.Explore,
                        label = "Wind Direction",
                        value = "${String.format("%.0f", dataPoint.windDirection)}°"
                    )
                }
                ClimateVisualizationType.PRECIPITATION -> {
                    ClimateDataRow(
                        icon = Icons.Default.WaterDrop,
                        label = "Precipitation",
                        value = "${String.format("%.1f", dataPoint.precipitation)} mm"
                    )
                }
                ClimateVisualizationType.AIR_QUALITY -> {
                    dataPoint.airQuality?.let { aq ->
                        ClimateDataRow(
                            icon = Icons.Default.Air,
                            label = "AQI",
                            value = aq.aqi.toString()
                        )
                        ClimateDataRow(
                            icon = Icons.Default.Grain,
                            label = "PM2.5",
                            value = "${String.format("%.1f", aq.pm25)} μg/m³"
                        )
                    }
                }
                ClimateVisualizationType.UV_INDEX -> {
                    ClimateDataRow(
                        icon = Icons.Default.WbSunny,
                        label = "UV Index",
                        value = String.format("%.1f", dataPoint.uvIndex)
                    )
                }
            }
            
            // Additional data
            Divider()
            
            ClimateDataRow(
                icon = Icons.Default.Visibility,
                label = "Visibility",
                value = "${String.format("%.1f", dataPoint.visibility)} km"
            )
        }
    }
}

@Composable
private fun ClimateDataRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================================
// 🌱 ENVIRONMENTAL MONITORING COMPONENTS
// ============================================================================

@Composable
fun EnvironmentalAlertsPanel(
    alertingSensors: Set<String>,
    sensors: List<EnvironmentalSensor>,
    parameter: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Environmental Alerts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
            
            Text(
                text = "${alertingSensors.size} sensors exceeding thresholds for $parameter",
                style = MaterialTheme.typography.bodySmall
            )
            
            alertingSensors.take(3).forEach { sensorId ->
                val sensor = sensors.find { it.id == sensorId }
                sensor?.let {
                    val latestReading = it.readings
                        .filter { reading -> reading.parameter == parameter }
                        .maxByOrNull { reading -> reading.timestamp }
                    
                    latestReading?.let { reading ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Sensor ${it.id}",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "${String.format("%.1f", reading.value)} ${reading.unit}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            if (alertingSensors.size > 3) {
                Text(
                    text = "... and ${alertingSensors.size - 3} more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EnvironmentalSensorPanel(
    sensor: EnvironmentalSensor,
    selectedParameter: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    
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
                    text = "Sensor ${sensor.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            // Sensor status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (sensor.status) {
                                SensorStatus.ACTIVE -> Color.Green
                                SensorStatus.INACTIVE -> Color.Gray
                                SensorStatus.MAINTENANCE -> Color.Yellow
                                SensorStatus.ERROR -> Color.Red
                            },
                            shape = CircleShape
                        )
                )
                Text(
                    text = sensor.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Text(
                text = "Type: ${sensor.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Last Update: ${dateFormat.format(Date(sensor.lastUpdate))}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Divider()
            
            // Latest reading for selected parameter
            val latestReading = sensor.readings
                .filter { it.parameter == selectedParameter }
                .maxByOrNull { it.timestamp }
            
            latestReading?.let { reading ->
                Text(
                    text = "Latest $selectedParameter Reading:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${String.format("%.2f", reading.value)} ${reading.unit}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = when (reading.quality) {
                                    DataQuality.EXCELLENT -> Color.Green
                                    DataQuality.GOOD -> Color.Blue
                                    DataQuality.FAIR -> Color.Yellow
                                    DataQuality.POOR -> Color.Orange
                                    DataQuality.INVALID -> Color.Red
                                },
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = "Quality: ${reading.quality.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                
                Text(
                    text = dateFormat.format(Date(reading.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 🧮 UTILITY FUNCTIONS
// ============================================================================

private fun List<String>.joinString(): String = this.joinToString(", ")