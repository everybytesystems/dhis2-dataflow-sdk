package com.everybytesystems.dataflow.ui.media

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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay

/**
 * Media Components
 * Camera, Voice Recording, File Upload, QR/Barcode Scanner, OCR
 */

// ============================================================================
// 📷 CAMERA COMPONENTS
// ============================================================================

/**
 * Camera capture component with preview and controls
 */
@Composable
fun DataFlowCamera(
    onPhotoCaptured: (String) -> Unit,
    onVideoCaptured: (String) -> Unit,
    modifier: Modifier = Modifier,
    mode: CameraMode = CameraMode.PHOTO,
    showControls: Boolean = true,
    allowModeSwitch: Boolean = true,
    allowCameraSwitch: Boolean = true
) {
    var currentMode by remember { mutableStateOf(mode) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }
    var cameraFacing by remember { mutableStateOf(CameraFacing.BACK) }
    var isFlashOn by remember { mutableStateOf(false) }
    
    // Recording timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000)
                recordingDuration++
            }
        } else {
            recordingDuration = 0
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Camera preview (placeholder)
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            ),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera Preview",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                // Recording indicator
                if (isRecording) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                            .background(
                                Color.Red.copy(alpha = 0.8f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.White, CircleShape)
                        )
                        Text(
                            text = formatDuration(recordingDuration),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        if (showControls) {
            // Top controls
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Flash toggle
                IconButton(
                    onClick = { isFlashOn = !isFlashOn },
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (isFlashOn) Color.Yellow else Color.White
                    )
                }
                
                // Camera switch
                if (allowCameraSwitch) {
                    IconButton(
                        onClick = {
                            cameraFacing = if (cameraFacing == CameraFacing.BACK) {
                                CameraFacing.FRONT
                            } else {
                                CameraFacing.BACK
                            }
                        },
                        modifier = Modifier
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera",
                            tint = Color.White
                        )
                    }
                }
            }
            
            // Bottom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mode selector
                if (allowModeSwitch) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        CameraModeButton(
                            text = "Photo",
                            isSelected = currentMode == CameraMode.PHOTO,
                            onClick = { currentMode = CameraMode.PHOTO }
                        )
                        CameraModeButton(
                            text = "Video",
                            isSelected = currentMode == CameraMode.VIDEO,
                            onClick = { currentMode = CameraMode.VIDEO }
                        )
                    }
                }
                
                // Capture button
                when (currentMode) {
                    CameraMode.PHOTO -> {
                        CaptureButton(
                            onClick = {
                                // Simulate photo capture
                                onPhotoCaptured("photo_${System.currentTimeMillis()}.jpg")
                            }
                        )
                    }
                    CameraMode.VIDEO -> {
                        VideoRecordButton(
                            isRecording = isRecording,
                            onClick = {
                                if (isRecording) {
                                    isRecording = false
                                    onVideoCaptured("video_${System.currentTimeMillis()}.mp4")
                                } else {
                                    isRecording = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraModeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(Color.White, CircleShape)
            .border(4.dp, Color.Gray, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
private fun VideoRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedSize by animateFloatAsState(
        targetValue = if (isRecording) 0.7f else 1f,
        animationSpec = tween(200),
        label = "record_button_size"
    )
    
    Box(
        modifier = modifier
            .size(80.dp)
            .background(Color.White, CircleShape)
            .border(4.dp, Color.Gray, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size((60 * animatedSize).dp)
                .background(
                    if (isRecording) Color.Red else Color.White,
                    if (isRecording) RoundedCornerShape(8.dp) else CircleShape
                )
        )
    }
}

enum class CameraMode {
    PHOTO, VIDEO
}

enum class CameraFacing {
    FRONT, BACK
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

// ============================================================================
// 🎤 VOICE RECORDING COMPONENTS
// ============================================================================

/**
 * Voice recording component with waveform visualization
 */
@Composable
fun DataFlowVoiceRecorder(
    onRecordingComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxDuration: Int = 300, // 5 minutes
    showWaveform: Boolean = true
) {
    var recordingState by remember { mutableStateOf(RecordingState.IDLE) }
    var recordingDuration by remember { mutableStateOf(0) }
    var audioLevels by remember { mutableStateOf(listOf<Float>()) }
    var recordedFilePath by remember { mutableStateOf<String?>(null) }
    
    // Recording timer and audio level simulation
    LaunchedEffect(recordingState) {
        if (recordingState == RecordingState.RECORDING) {
            while (recordingState == RecordingState.RECORDING && recordingDuration < maxDuration) {
                delay(100)
                recordingDuration += 100
                
                // Simulate audio levels
                val newLevel = (0.1f..1.0f).random()
                audioLevels = (audioLevels + newLevel).takeLast(50)
            }
            
            if (recordingDuration >= maxDuration * 1000) {
                recordingState = RecordingState.COMPLETED
                recordedFilePath = "recording_${System.currentTimeMillis()}.wav"
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recording status
            Text(
                text = when (recordingState) {
                    RecordingState.IDLE -> "Ready to record"
                    RecordingState.RECORDING -> "Recording..."
                    RecordingState.PAUSED -> "Recording paused"
                    RecordingState.COMPLETED -> "Recording complete"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = when (recordingState) {
                    RecordingState.RECORDING -> Color.Red
                    RecordingState.COMPLETED -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
            
            // Duration display
            if (recordingDuration > 0) {
                Text(
                    text = formatDuration(recordingDuration / 1000),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Waveform visualization
            if (showWaveform && audioLevels.isNotEmpty()) {
                WaveformVisualization(
                    audioLevels = audioLevels,
                    isRecording = recordingState == RecordingState.RECORDING,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
            
            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (recordingState) {
                    RecordingState.IDLE -> {
                        RecordButton(
                            onClick = {
                                recordingState = RecordingState.RECORDING
                                recordingDuration = 0
                                audioLevels = emptyList()
                            }
                        )
                    }
                    
                    RecordingState.RECORDING -> {
                        PauseButton(
                            onClick = {
                                recordingState = RecordingState.PAUSED
                            }
                        )
                        
                        StopButton(
                            onClick = {
                                recordingState = RecordingState.COMPLETED
                                recordedFilePath = "recording_${System.currentTimeMillis()}.wav"
                            }
                        )
                    }
                    
                    RecordingState.PAUSED -> {
                        ResumeButton(
                            onClick = {
                                recordingState = RecordingState.RECORDING
                            }
                        )
                        
                        StopButton(
                            onClick = {
                                recordingState = RecordingState.COMPLETED
                                recordedFilePath = "recording_${System.currentTimeMillis()}.wav"
                            }
                        )
                    }
                    
                    RecordingState.COMPLETED -> {
                        PlayButton(
                            onClick = {
                                // Play recorded audio
                            }
                        )
                        
                        SaveButton(
                            onClick = {
                                recordedFilePath?.let { path ->
                                    onRecordingComplete(path)
                                    recordingState = RecordingState.IDLE
                                    recordingDuration = 0
                                    audioLevels = emptyList()
                                    recordedFilePath = null
                                }
                            }
                        )
                        
                        DeleteButton(
                            onClick = {
                                recordingState = RecordingState.IDLE
                                recordingDuration = 0
                                audioLevels = emptyList()
                                recordedFilePath = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WaveformVisualization(
    audioLevels: List<Float>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary,
        animationSpec = tween(300),
        label = "waveform_color"
    )
    
    Canvas(modifier = modifier) {
        val barWidth = size.width / audioLevels.size.coerceAtLeast(1)
        val centerY = size.height / 2
        
        audioLevels.forEachIndexed { index, level ->
            val barHeight = level * size.height * 0.8f
            val x = index * barWidth
            
            drawRect(
                color = animatedColor,
                topLeft = Offset(x, centerY - barHeight / 2),
                size = Size(barWidth * 0.8f, barHeight)
            )
        }
    }
}

@Composable
private fun RecordButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.Red,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Start Recording"
        )
    }
}

@Composable
private fun PauseButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(
            imageVector = Icons.Default.Pause,
            contentDescription = "Pause Recording"
        )
    }
}

@Composable
private fun ResumeButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color.Red
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Resume Recording"
        )
    }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.error
    ) {
        Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = "Stop Recording"
        )
    }
}

@Composable
private fun PlayButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play Recording",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Save")
    }
}

@Composable
private fun DeleteButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Delete")
    }
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, COMPLETED
}

// ============================================================================
// 📁 FILE UPLOAD & GALLERY
// ============================================================================

/**
 * Advanced file upload with preview and progress
 */
@Composable
fun DataFlowFileUpload(
    onFilesSelected: (List<MediaFile>) -> Unit,
    modifier: Modifier = Modifier,
    allowedTypes: List<String> = listOf("image/*", "video/*", "audio/*", "application/pdf"),
    maxFileSize: Long = 10 * 1024 * 1024, // 10MB
    maxFiles: Int = 5,
    showPreview: Boolean = true
) {
    var selectedFiles by remember { mutableStateOf<List<MediaFile>>(emptyList()) }
    var uploadProgress by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upload area
        UploadDropZone(
            onFilesDropped = { files ->
                val validFiles = files.filter { file ->
                    file.size <= maxFileSize && 
                    allowedTypes.any { type -> file.mimeType.startsWith(type.removeSuffix("*")) }
                }.take(maxFiles - selectedFiles.size)
                
                selectedFiles = selectedFiles + validFiles
                onFilesSelected(selectedFiles)
            },
            allowedTypes = allowedTypes,
            maxFileSize = maxFileSize
        )
        
        // File list with previews
        if (selectedFiles.isNotEmpty() && showPreview) {
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedFiles) { file ->
                    FilePreviewItem(
                        file = file,
                        uploadProgress = uploadProgress[file.id] ?: 0f,
                        onRemove = {
                            selectedFiles = selectedFiles - file
                            onFilesSelected(selectedFiles)
                        }
                    )
                }
            }
        }
        
        // Upload controls
        if (selectedFiles.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedFiles = emptyList()
                        uploadProgress = emptyMap()
                        onFilesSelected(emptyList())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear All")
                }
                
                Button(
                    onClick = {
                        // Simulate upload progress
                        selectedFiles.forEach { file ->
                            // Start upload simulation
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload")
                }
            }
        }
    }
}

@Composable
private fun UploadDropZone(
    onFilesDropped: (List<MediaFile>) -> Unit,
    allowedTypes: List<String>,
    maxFileSize: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable {
                // Open file picker
                val dummyFiles = listOf(
                    MediaFile(
                        id = "file_${System.currentTimeMillis()}",
                        name = "sample_image.jpg",
                        mimeType = "image/jpeg",
                        size = 1024 * 1024,
                        path = "/path/to/file"
                    )
                )
                onFilesDropped(dummyFiles)
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Drop files here or click to browse",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Max ${formatFileSize(maxFileSize)} per file",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Supported: ${allowedTypes.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FilePreviewItem(
    file: MediaFile,
    uploadProgress: Float,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // File type icon or thumbnail
            FileTypeIcon(
                mimeType = file.mimeType,
                modifier = Modifier.size(48.dp)
            )
            
            // File info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = formatFileSize(file.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Upload progress
                if (uploadProgress > 0f && uploadProgress < 1f) {
                    LinearProgressIndicator(
                        progress = { uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove file",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun FileTypeIcon(
    mimeType: String,
    modifier: Modifier = Modifier
) {
    val icon = when {
        mimeType.startsWith("image/") -> Icons.Default.Image
        mimeType.startsWith("video/") -> Icons.Default.VideoFile
        mimeType.startsWith("audio/") -> Icons.Default.AudioFile
        mimeType == "application/pdf" -> Icons.Default.PictureAsPdf
        else -> Icons.Default.AttachFile
    }
    
    val color = when {
        mimeType.startsWith("image/") -> Color(0xFF4CAF50)
        mimeType.startsWith("video/") -> Color(0xFF2196F3)
        mimeType.startsWith("audio/") -> Color(0xFFFF9800)
        mimeType == "application/pdf" -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier,
        tint = color
    )
}

data class MediaFile(
    val id: String,
    val name: String,
    val mimeType: String,
    val size: Long,
    val path: String,
    val thumbnail: String? = null
)

private fun formatFileSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "%.1f %s".format(size, units[unitIndex])
}

// ============================================================================
// 📱 QR CODE & BARCODE SCANNER
// ============================================================================

/**
 * QR Code and Barcode scanner component
 */
@Composable
fun DataFlowQRScanner(
    onCodeScanned: (String, CodeType) -> Unit,
    modifier: Modifier = Modifier,
    supportedFormats: List<CodeType> = listOf(CodeType.QR_CODE, CodeType.BARCODE),
    showOverlay: Boolean = true,
    continuous: Boolean = false
) {
    var isScanning by remember { mutableStateOf(true) }
    var lastScannedCode by remember { mutableStateOf<String?>(null) }
    var detectedCodeType by remember { mutableStateOf<CodeType?>(null) }
    
    Box(modifier = modifier.fillMaxSize()) {
        // Camera preview (placeholder)
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            ),
            shape = RectangleShape
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera Scanner",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
        
        // Scanning overlay
        if (showOverlay) {
            ScannerOverlay(
                modifier = Modifier.fillMaxSize(),
                isScanning = isScanning
            )
        }
        
        // Scanner controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Last scanned result
            lastScannedCode?.let { code ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scanned ${detectedCodeType?.name ?: "Code"}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = code,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            
            // Scan button
            FloatingActionButton(
                onClick = {
                    if (isScanning) {
                        // Simulate code detection
                        val dummyCode = "https://example.com/qr-${System.currentTimeMillis()}"
                        val codeType = CodeType.QR_CODE
                        
                        lastScannedCode = dummyCode
                        detectedCodeType = codeType
                        onCodeScanned(dummyCode, codeType)
                        
                        if (!continuous) {
                            isScanning = false
                        }
                    } else {
                        isScanning = true
                    }
                },
                containerColor = if (isScanning) Color.Red else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (isScanning) Icons.Default.Stop else Icons.Default.QrCodeScanner,
                    contentDescription = if (isScanning) "Stop Scanning" else "Start Scanning"
                )
            }
        }
    }
}

@Composable
private fun ScannerOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isScanning) 1f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanner_overlay"
    )
    
    Canvas(modifier = modifier) {
        val overlayColor = Color.Black.copy(alpha = 0.5f)
        val scanAreaSize = size.minDimension * 0.7f
        val scanAreaLeft = (size.width - scanAreaSize) / 2
        val scanAreaTop = (size.height - scanAreaSize) / 2
        
        // Draw overlay with cutout
        drawRect(
            color = overlayColor,
            topLeft = Offset.Zero,
            size = Size(size.width, scanAreaTop)
        )
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, scanAreaTop + scanAreaSize),
            size = Size(size.width, size.height - scanAreaTop - scanAreaSize)
        )
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, scanAreaTop),
            size = Size(scanAreaLeft, scanAreaSize)
        )
        drawRect(
            color = overlayColor,
            topLeft = Offset(scanAreaLeft + scanAreaSize, scanAreaTop),
            size = Size(size.width - scanAreaLeft - scanAreaSize, scanAreaSize)
        )
        
        // Draw scan area border
        val borderColor = Color.White.copy(alpha = animatedAlpha)
        val borderWidth = 4.dp.toPx()
        val cornerLength = 30.dp.toPx()
        
        // Top-left corner
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft, scanAreaTop),
            end = Offset(scanAreaLeft + cornerLength, scanAreaTop),
            strokeWidth = borderWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft, scanAreaTop),
            end = Offset(scanAreaLeft, scanAreaTop + cornerLength),
            strokeWidth = borderWidth
        )
        
        // Top-right corner
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft + scanAreaSize - cornerLength, scanAreaTop),
            end = Offset(scanAreaLeft + scanAreaSize, scanAreaTop),
            strokeWidth = borderWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft + scanAreaSize, scanAreaTop),
            end = Offset(scanAreaLeft + scanAreaSize, scanAreaTop + cornerLength),
            strokeWidth = borderWidth
        )
        
        // Bottom-left corner
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft, scanAreaTop + scanAreaSize - cornerLength),
            end = Offset(scanAreaLeft, scanAreaTop + scanAreaSize),
            strokeWidth = borderWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft, scanAreaTop + scanAreaSize),
            end = Offset(scanAreaLeft + cornerLength, scanAreaTop + scanAreaSize),
            strokeWidth = borderWidth
        )
        
        // Bottom-right corner
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft + scanAreaSize, scanAreaTop + scanAreaSize - cornerLength),
            end = Offset(scanAreaLeft + scanAreaSize, scanAreaTop + scanAreaSize),
            strokeWidth = borderWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(scanAreaLeft + scanAreaSize - cornerLength, scanAreaTop + scanAreaSize),
            end = Offset(scanAreaLeft + scanAreaSize, scanAreaTop + scanAreaSize),
            strokeWidth = borderWidth
        )
    }
}

enum class CodeType {
    QR_CODE, BARCODE, DATA_MATRIX, PDF417, AZTEC, CODE_128, CODE_39, EAN_13, EAN_8, UPC_A, UPC_E
}

// ============================================================================
// 🔍 OCR COMPONENT
// ============================================================================

/**
 * OCR (Optical Character Recognition) component
 */
@Composable
fun DataFlowOCR(
    onTextExtracted: (String) -> Unit,
    modifier: Modifier = Modifier,
    supportedLanguages: List<String> = listOf("en", "es", "fr", "de"),
    mode: OCRMode = OCRMode.CAMERA
) {
    var extractedText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(supportedLanguages.first()) }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Language selector
        if (supportedLanguages.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Language:",
                    style = MaterialTheme.typography.labelMedium
                )
                
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage.uppercase(),
                        onValueChange = { },
                        modifier = Modifier.menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        supportedLanguages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language.uppercase()) },
                                onClick = {
                                    selectedLanguage = language
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // OCR input area
        when (mode) {
            OCRMode.CAMERA -> {
                OCRCameraView(
                    onImageCaptured = { imagePath ->
                        isProcessing = true
                        // Simulate OCR processing
                        kotlinx.coroutines.GlobalScope.launch {
                            delay(2000)
                            val dummyText = "This is extracted text from the image using OCR technology."
                            extractedText = dummyText
                            onTextExtracted(dummyText)
                            isProcessing = false
                        }
                    },
                    isProcessing = isProcessing
                )
            }
            
            OCRMode.UPLOAD -> {
                OCRUploadArea(
                    onImageUploaded = { imagePath ->
                        isProcessing = true
                        // Simulate OCR processing
                        kotlinx.coroutines.GlobalScope.launch {
                            delay(2000)
                            val dummyText = "This is extracted text from the uploaded image using OCR."
                            extractedText = dummyText
                            onTextExtracted(dummyText)
                            isProcessing = false
                        }
                    },
                    isProcessing = isProcessing
                )
            }
        }
        
        // Processing indicator
        if (isProcessing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Processing image with OCR...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Extracted text
        if (extractedText.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Extracted Text:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    OutlinedTextField(
                        value = extractedText,
                        onValueChange = { 
                            extractedText = it
                            onTextExtracted(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 8,
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                // Copy to clipboard
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy")
                        }
                        
                        Button(
                            onClick = { 
                                onTextExtracted(extractedText)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Use Text")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OCRCameraView(
    onImageCaptured: (String) -> Unit,
    isProcessing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OCR Camera View",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            
            FloatingActionButton(
                onClick = {
                    if (!isProcessing) {
                        onImageCaptured("captured_image_${System.currentTimeMillis()}.jpg")
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Capture for OCR"
                )
            }
        }
    }
}

@Composable
private fun OCRUploadArea(
    onImageUploaded: (String) -> Unit,
    isProcessing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(enabled = !isProcessing) {
                onImageUploaded("uploaded_image_${System.currentTimeMillis()}.jpg")
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Upload image for OCR",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Supports JPG, PNG, PDF",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class OCRMode {
    CAMERA, UPLOAD
}