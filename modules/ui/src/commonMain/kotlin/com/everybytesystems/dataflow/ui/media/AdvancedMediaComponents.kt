package com.everybytesystems.dataflow.ui.media

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.math.*

/**
 * Advanced Camera & Media Components
 * Professional-grade media capture, editing, and management
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

enum class CameraMode {
    PHOTO,
    VIDEO,
    SCAN,
    PORTRAIT,
    PANORAMA,
    SLOW_MOTION,
    TIME_LAPSE
}

enum class CameraLens {
    WIDE,
    ULTRA_WIDE,
    TELEPHOTO,
    FRONT,
    BACK
}

enum class FlashMode {
    OFF,
    ON,
    AUTO,
    TORCH
}

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    UNKNOWN
}

enum class MediaQuality {
    LOW,
    MEDIUM,
    HIGH,
    ULTRA_HIGH
}

enum class CompressionLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    MAXIMUM
}

data class CameraSettings(
    val mode: CameraMode = CameraMode.PHOTO,
    val lens: CameraLens = CameraLens.BACK,
    val flashMode: FlashMode = FlashMode.AUTO,
    val quality: MediaQuality = MediaQuality.HIGH,
    val enableHDR: Boolean = false,
    val enableStabilization: Boolean = true,
    val gridLines: Boolean = false,
    val timer: Int = 0, // seconds
    val aspectRatio: String = "4:3",
    val zoom: Float = 1.0f
)

data class MediaItem(
    val id: String,
    val uri: String,
    val type: MediaType,
    val name: String,
    val size: Long,
    val duration: kotlin.time.Duration? = null,
    val dimensions: Pair<Int, Int>? = null,
    val createdAt: Instant,
    val thumbnail: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val isSelected: Boolean = false,
    val uploadProgress: Float? = null,
    val isUploading: Boolean = false,
    val isProcessing: Boolean = false
)

data class MediaFilter(
    val name: String,
    val displayName: String,
    val intensity: Float = 1.0f,
    val preview: String? = null
)

data class CropArea(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val rotation: Float = 0f
)

data class MediaEditState(
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val saturation: Float = 0f,
    val exposure: Float = 0f,
    val highlights: Float = 0f,
    val shadows: Float = 0f,
    val temperature: Float = 0f,
    val tint: Float = 0f,
    val sharpness: Float = 0f,
    val noise: Float = 0f,
    val vignette: Float = 0f,
    val selectedFilter: MediaFilter? = null,
    val cropArea: CropArea? = null
)

// ============================================================================
// 📷 ADVANCED CAMERA INTERFACE
// ============================================================================

@Composable
fun AdvancedCameraInterface(
    onMediaCaptured: (MediaItem) -> Unit,
    onSettingsChange: (CameraSettings) -> Unit,
    modifier: Modifier = Modifier,
    initialSettings: CameraSettings = CameraSettings(),
    showControls: Boolean = true,
    enableGalleryAccess: Boolean = true
) {
    var settings by remember { mutableStateOf(initialSettings) }
    var isCapturing by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var captureProgress by remember { mutableStateOf(0f) }
    var focusPoint by remember { mutableStateOf<Offset?>(null) }
    var exposurePoint by remember { mutableStateOf<Offset?>(null) }
    
    LaunchedEffect(settings) {
        onSettingsChange(settings)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Camera preview (placeholder - would integrate with actual camera API)
        CameraPreview(
            settings = settings,
            focusPoint = focusPoint,
            exposurePoint = exposurePoint,
            onTap = { offset ->
                focusPoint = offset
                // Auto-focus logic would go here
            },
            onLongPress = { offset ->
                exposurePoint = offset
                // Exposure adjustment logic would go here
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Camera controls overlay
        if (showControls) {
            CameraControlsOverlay(
                settings = settings,
                isCapturing = isCapturing,
                captureProgress = captureProgress,
                onSettingsChange = { settings = it },
                onCapture = {
                    isCapturing = true
                    // Simulate capture process
                    // In real implementation, this would trigger actual camera capture
                    simulateCapture { mediaItem ->
                        onMediaCaptured(mediaItem)
                        isCapturing = false
                    }
                },
                onShowSettings = { showSettings = true },
                enableGalleryAccess = enableGalleryAccess,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Settings panel
        if (showSettings) {
            CameraSettingsPanel(
                settings = settings,
                onSettingsChange = { settings = it },
                onDismiss = { showSettings = false },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        
        // Focus and exposure indicators
        focusPoint?.let { point ->
            FocusIndicator(
                position = point,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        exposurePoint?.let { point ->
            ExposureIndicator(
                position = point,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CameraPreview(
    settings: CameraSettings,
    focusPoint: Offset?,
    exposurePoint: Offset?,
    onTap: (Offset) -> Unit,
    onLongPress: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = onTap,
                    onLongPress = onLongPress
                )
            }
    ) {
        // Camera preview placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1A1A1A),
                            Color(0xFF000000)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Preview",
                    modifier = Modifier.size(64.dp),
                    tint = Color.White.copy(alpha = 0.5f)
                )
                Text(
                    text = "Camera Preview",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${settings.mode.name} • ${settings.lens.name}",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        
        // Grid lines overlay
        if (settings.gridLines) {
            GridLinesOverlay(modifier = Modifier.fillMaxSize())
        }
        
        // Zoom indicator
        if (settings.zoom > 1.0f) {
            ZoomIndicator(
                zoom = settings.zoom,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 60.dp)
            )
        }
    }
}

@Composable
private fun CameraControlsOverlay(
    settings: CameraSettings,
    isCapturing: Boolean,
    captureProgress: Float,
    onSettingsChange: (CameraSettings) -> Unit,
    onCapture: () -> Unit,
    onShowSettings: () -> Unit,
    enableGalleryAccess: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Top controls
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash control
            FlashModeButton(
                flashMode = settings.flashMode,
                onFlashModeChange = { 
                    onSettingsChange(settings.copy(flashMode = it))
                }
            )
            
            // Camera mode selector
            CameraModeSelector(
                currentMode = settings.mode,
                onModeChange = {
                    onSettingsChange(settings.copy(mode = it))
                }
            )
            
            // Settings button
            IconButton(onClick = onShowSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }
        
        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Zoom slider (for video mode)
            if (settings.mode == CameraMode.VIDEO) {
                ZoomSlider(
                    zoom = settings.zoom,
                    onZoomChange = {
                        onSettingsChange(settings.copy(zoom = it))
                    },
                    modifier = Modifier.width(200.dp)
                )
            }
            
            // Main control row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery access button
                if (enableGalleryAccess) {
                    GalleryAccessButton(
                        onClick = { /* Open gallery */ }
                    )
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }
                
                // Capture button
                CaptureButton(
                    mode = settings.mode,
                    isCapturing = isCapturing,
                    progress = captureProgress,
                    onClick = onCapture,
                    modifier = Modifier.size(80.dp)
                )
                
                // Camera switch button
                CameraSwitchButton(
                    currentLens = settings.lens,
                    onLensChange = {
                        onSettingsChange(settings.copy(lens = it))
                    }
                )
            }
        }
    }
}

@Composable
private fun FlashModeButton(
    flashMode: FlashMode,
    onFlashModeChange: (FlashMode) -> Unit
) {
    val icon = when (flashMode) {
        FlashMode.OFF -> Icons.Default.FlashOff
        FlashMode.ON -> Icons.Default.FlashOn
        FlashMode.AUTO -> Icons.Default.FlashAuto
        FlashMode.TORCH -> Icons.Default.Flashlight
    }
    
    IconButton(
        onClick = {
            val nextMode = when (flashMode) {
                FlashMode.OFF -> FlashMode.AUTO
                FlashMode.AUTO -> FlashMode.ON
                FlashMode.ON -> FlashMode.TORCH
                FlashMode.TORCH -> FlashMode.OFF
            }
            onFlashModeChange(nextMode)
        }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Flash: ${flashMode.name}",
            tint = if (flashMode == FlashMode.OFF) Color.White.copy(alpha = 0.6f) else Color.Yellow
        )
    }
}

@Composable
private fun CameraModeSelector(
    currentMode: CameraMode,
    onModeChange: (CameraMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = currentMode.name,
                style = MaterialTheme.typography.labelMedium
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CameraMode.values().forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.name) },
                    onClick = {
                        onModeChange(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ZoomSlider(
    zoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${zoom.toInt()}x",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
        
        Slider(
            value = zoom,
            onValueChange = onZoomChange,
            valueRange = 1f..10f,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White,
                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun CaptureButton(
    mode: CameraMode,
    isCapturing: Boolean,
    progress: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Progress ring for video recording
        if (isCapturing && mode == CameraMode.VIDEO) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                color = Color.Red,
                strokeWidth = 4.dp
            )
        }
        
        // Capture button
        Button(
            onClick = onClick,
            enabled = !isCapturing,
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCapturing && mode == CameraMode.VIDEO) Color.Red else Color.White,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            val icon = when {
                isCapturing && mode == CameraMode.VIDEO -> Icons.Default.Stop
                mode == CameraMode.VIDEO -> Icons.Default.Videocam
                else -> Icons.Default.CameraAlt
            }
            
            Icon(
                imageVector = icon,
                contentDescription = "Capture",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun GalleryAccessButton(
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, Color.White),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = "Gallery",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun CameraSwitchButton(
    currentLens: CameraLens,
    onLensChange: (CameraLens) -> Unit
) {
    IconButton(
        onClick = {
            val nextLens = when (currentLens) {
                CameraLens.BACK -> CameraLens.FRONT
                CameraLens.FRONT -> CameraLens.BACK
                else -> CameraLens.BACK
            }
            onLensChange(nextLens)
        }
    ) {
        Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Switch Camera",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

// ============================================================================
// ⚙️ CAMERA SETTINGS PANEL
// ============================================================================

@Composable
private fun CameraSettingsPanel(
    settings: CameraSettings,
    onSettingsChange: (CameraSettings) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(300.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Camera Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            // Quality setting
            QualitySelector(
                quality = settings.quality,
                onQualityChange = {
                    onSettingsChange(settings.copy(quality = it))
                }
            )
            
            // Aspect ratio
            AspectRatioSelector(
                aspectRatio = settings.aspectRatio,
                onAspectRatioChange = {
                    onSettingsChange(settings.copy(aspectRatio = it))
                }
            )
            
            // Timer
            TimerSelector(
                timer = settings.timer,
                onTimerChange = {
                    onSettingsChange(settings.copy(timer = it))
                }
            )
            
            // Toggle settings
            SettingsToggle(
                label = "HDR",
                checked = settings.enableHDR,
                onCheckedChange = {
                    onSettingsChange(settings.copy(enableHDR = it))
                }
            )
            
            SettingsToggle(
                label = "Image Stabilization",
                checked = settings.enableStabilization,
                onCheckedChange = {
                    onSettingsChange(settings.copy(enableStabilization = it))
                }
            )
            
            SettingsToggle(
                label = "Grid Lines",
                checked = settings.gridLines,
                onCheckedChange = {
                    onSettingsChange(settings.copy(gridLines = it))
                }
            )
        }
    }
}

@Composable
private fun QualitySelector(
    quality: MediaQuality,
    onQualityChange: (MediaQuality) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Quality",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MediaQuality.values().forEach { q ->
                FilterChip(
                    onClick = { onQualityChange(q) },
                    label = { Text(q.name) },
                    selected = quality == q,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AspectRatioSelector(
    aspectRatio: String,
    onAspectRatioChange: (String) -> Unit
) {
    val ratios = listOf("4:3", "16:9", "1:1", "3:2")
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Aspect Ratio",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ratios.forEach { ratio ->
                FilterChip(
                    onClick = { onAspectRatioChange(ratio) },
                    label = { Text(ratio) },
                    selected = aspectRatio == ratio,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TimerSelector(
    timer: Int,
    onTimerChange: (Int) -> Unit
) {
    val timers = listOf(0, 3, 5, 10)
    
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Timer",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timers.forEach { t ->
                FilterChip(
                    onClick = { onTimerChange(t) },
                    label = { Text(if (t == 0) "Off" else "${t}s") },
                    selected = timer == t,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

// ============================================================================
// 🎯 FOCUS & EXPOSURE INDICATORS
// ============================================================================

@Composable
private fun FocusIndicator(
    position: Offset,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    
    LaunchedEffect(position) {
        visible = true
        delay(2000)
        visible = false
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Canvas(modifier = modifier) {
            val center = position
            val size = 60.dp.toPx()
            
            drawCircle(
                color = Color.White,
                radius = size / 2,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            
            // Cross lines
            drawLine(
                color = Color.White,
                start = Offset(center.x - size / 4, center.y),
                end = Offset(center.x + size / 4, center.y),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(center.x, center.y - size / 4),
                end = Offset(center.x, center.y + size / 4),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Composable
private fun ExposureIndicator(
    position: Offset,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    
    LaunchedEffect(position) {
        visible = true
        delay(2000)
        visible = false
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Canvas(modifier = modifier) {
            val center = position
            val size = 40.dp.toPx()
            
            drawRect(
                color = Color.Yellow,
                topLeft = Offset(center.x - size / 2, center.y - size / 2),
                size = Size(size, size),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun GridLinesOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Vertical lines
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(width / 3, 0f),
            end = Offset(width / 3, height),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(2 * width / 3, 0f),
            end = Offset(2 * width / 3, height),
            strokeWidth = 1.dp.toPx()
        )
        
        // Horizontal lines
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(0f, height / 3),
            end = Offset(width, height / 3),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = Color.White.copy(alpha = 0.3f),
            start = Offset(0f, 2 * height / 3),
            end = Offset(width, 2 * height / 3),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
private fun ZoomIndicator(
    zoom: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = "${zoom.toInt()}x",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// ============================================================================
// 🖼️ ADVANCED GALLERY PICKER
// ============================================================================

@Composable
fun AdvancedGalleryPicker(
    onMediaSelected: (List<MediaItem>) -> Unit,
    modifier: Modifier = Modifier,
    allowMultiSelect: Boolean = true,
    maxSelections: Int = 10,
    mediaTypes: Set<MediaType> = setOf(MediaType.IMAGE, MediaType.VIDEO),
    showPreview: Boolean = true
) {
    var selectedItems by remember { mutableStateOf<Set<String>>(emptySet()) }
    var mediaItems by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showPreviewDialog by remember { mutableStateOf(false) }
    var previewItem by remember { mutableStateOf<MediaItem?>(null) }
    
    LaunchedEffect(Unit) {
        // Simulate loading media items
        delay(1000)
        mediaItems = generateSampleMediaItems()
        isLoading = false
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Header with selection info
        if (allowMultiSelect) {
            GalleryHeader(
                selectedCount = selectedItems.size,
                maxSelections = maxSelections,
                onClearSelection = { selectedItems = emptySet() },
                onConfirmSelection = {
                    val selected = mediaItems.filter { it.id in selectedItems }
                    onMediaSelected(selected)
                }
            )
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(mediaItems.filter { it.type in mediaTypes }) { item ->
                    MediaThumbnail(
                        mediaItem = item,
                        isSelected = item.id in selectedItems,
                        showSelection = allowMultiSelect,
                        onClick = {
                            if (allowMultiSelect) {
                                selectedItems = if (item.id in selectedItems) {
                                    selectedItems - item.id
                                } else if (selectedItems.size < maxSelections) {
                                    selectedItems + item.id
                                } else {
                                    selectedItems
                                }
                            } else {
                                onMediaSelected(listOf(item))
                            }
                        },
                        onLongClick = if (showPreview) {
                            {
                                previewItem = item
                                showPreviewDialog = true
                            }
                        } else null
                    )
                }
            }
        }
    }
    
    // Preview dialog
    if (showPreviewDialog && previewItem != null) {
        MediaPreviewDialog(
            mediaItem = previewItem!!,
            onDismiss = { 
                showPreviewDialog = false
                previewItem = null
            },
            onSelect = {
                if (allowMultiSelect) {
                    selectedItems = if (previewItem!!.id in selectedItems) {
                        selectedItems - previewItem!!.id
                    } else if (selectedItems.size < maxSelections) {
                        selectedItems + previewItem!!.id
                    } else {
                        selectedItems
                    }
                } else {
                    onMediaSelected(listOf(previewItem!!))
                }
                showPreviewDialog = false
                previewItem = null
            }
        )
    }
}

@Composable
private fun GalleryHeader(
    selectedCount: Int,
    maxSelections: Int,
    onClearSelection: () -> Unit,
    onConfirmSelection: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Selected: $selectedCount/$maxSelections",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (selectedCount > 0) {
                    Text(
                        text = "Tap to confirm selection",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selectedCount > 0) {
                    OutlinedButton(onClick = onClearSelection) {
                        Text("Clear")
                    }
                    
                    Button(onClick = onConfirmSelection) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaThumbnail(
    mediaItem: MediaItem,
    isSelected: Boolean,
    showSelection: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .then(
                if (onLongClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onClick() },
                            onLongPress = { onLongClick() }
                        )
                    }
                } else Modifier
            )
    ) {
        // Thumbnail placeholder
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (mediaItem.type) {
                    MediaType.IMAGE -> Icons.Default.Image
                    MediaType.VIDEO -> Icons.Default.VideoFile
                    MediaType.AUDIO -> Icons.Default.AudioFile
                    else -> Icons.Default.InsertDriveFile
                },
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Video duration overlay
        if (mediaItem.type == MediaType.VIDEO && mediaItem.duration != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = formatDuration(mediaItem.duration),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        
        // Selection indicator
        if (showSelection) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.7f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        // Upload progress overlay
        if (mediaItem.isUploading && mediaItem.uploadProgress != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = mediaItem.uploadProgress,
                    color = Color.White
                )
            }
        }
        
        // Processing overlay
        if (mediaItem.isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

// ============================================================================
// 🔍 MEDIA PREVIEW DIALOG
// ============================================================================

@Composable
private fun MediaPreviewDialog(
    mediaItem: MediaItem,
    onDismiss: () -> Unit,
    onSelect: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(mediaItem.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Preview placeholder
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
                    Icon(
                        imageVector = when (mediaItem.type) {
                            MediaType.IMAGE -> Icons.Default.Image
                            MediaType.VIDEO -> Icons.Default.VideoFile
                            MediaType.AUDIO -> Icons.Default.AudioFile
                            else -> Icons.Default.InsertDriveFile
                        },
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Media info
                Text(
                    text = "Size: ${formatFileSize(mediaItem.size)}",
                    style = MaterialTheme.typography.bodySmall
                )
                mediaItem.dimensions?.let { (width, height) ->
                    Text(
                        text = "Dimensions: ${width}x${height}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                mediaItem.duration?.let { duration ->
                    Text(
                        text = "Duration: ${formatDuration(duration)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    text = "Created: ${formatDateTime(mediaItem.createdAt)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onSelect) {
                Text("Select")
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
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private suspend fun simulateCapture(onComplete: (MediaItem) -> Unit) {
    delay(500) // Simulate capture delay
    val mediaItem = MediaItem(
        id = "captured_${System.currentTimeMillis()}",
        uri = "file://captured_image.jpg",
        type = MediaType.IMAGE,
        name = "Captured Image",
        size = 2048576L,
        dimensions = Pair(1920, 1080),
        createdAt = Clock.System.now()
    )
    onComplete(mediaItem)
}

private fun generateSampleMediaItems(): List<MediaItem> {
    return (1..50).map { index ->
        val type = if (index % 4 == 0) MediaType.VIDEO else MediaType.IMAGE
        MediaItem(
            id = "media_$index",
            uri = "file://media_$index.${if (type == MediaType.VIDEO) "mp4" else "jpg"}",
            type = type,
            name = "Media $index",
            size = (1024..5120).random() * 1024L,
            dimensions = Pair(1920, 1080),
            duration = if (type == MediaType.VIDEO) kotlin.time.Duration.parse("PT${(30..300).random()}S") else null,
            createdAt = Clock.System.now().minus((index * 3600).seconds)
        )
    }
}

private fun formatDuration(duration: kotlin.time.Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> "%.1f GB".format(gb)
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}

private fun formatDateTime(instant: Instant): String {
    return instant.toString().substringBefore('T')
}