package com.everybytesystems.dataflow.ui.voice

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
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.*

/**
 * Voice Recording Components
 * Record, pause, resume, and manage audio recordings with waveform visualization
 */

// ============================================================================
// 🎤 DATA MODELS
// ============================================================================

data class VoiceRecorderState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val isPlaying: Boolean = false,
    val currentRecording: VoiceRecording? = null,
    val recordings: List<VoiceRecording> = emptyList(),
    val duration: Long = 0L, // in milliseconds
    val amplitude: Float = 0f, // current amplitude for waveform
    val waveformData: List<Float> = emptyList(),
    val audioFormat: AudioFormat = AudioFormat.MP3,
    val audioQuality: AudioQuality = AudioQuality.MEDIUM,
    val maxDuration: Long = 300000L, // 5 minutes default
    val estimatedFileSize: Long = 0L
)

data class VoiceRecording(
    val id: String,
    val fileName: String,
    val filePath: String,
    val duration: Long,
    val fileSize: Long,
    val format: AudioFormat,
    val quality: AudioQuality,
    val timestamp: Long = System.currentTimeMillis(),
    val waveformData: List<Float> = emptyList(),
    val transcription: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)

enum class AudioFormat(val extension: String, val mimeType: String) {
    WAV("wav", "audio/wav"),
    MP3("mp3", "audio/mpeg"),
    AAC("aac", "audio/aac"),
    OGG("ogg", "audio/ogg"),
    M4A("m4a", "audio/mp4")
}

enum class AudioQuality(val bitrate: Int, val sampleRate: Int) {
    LOW(64, 22050),
    MEDIUM(128, 44100),
    HIGH(256, 48000),
    LOSSLESS(1411, 96000)
}

enum class RecordingState {
    IDLE, RECORDING, PAUSED, PLAYING, STOPPED
}

// ============================================================================
// 🎤 VOICE RECORDER COMPONENT
// ============================================================================

@Composable
fun VoiceRecorder(
    state: VoiceRecorderState,
    onStateChange: (VoiceRecorderState) -> Unit,
    modifier: Modifier = Modifier,
    audioFormat: AudioFormat = AudioFormat.MP3,
    audioQuality: AudioQuality = AudioQuality.MEDIUM,
    maxDuration: Long = 300000L,
    showWaveform: Boolean = true,
    showRecordingsList: Boolean = true,
    onRecordingComplete: ((VoiceRecording) -> Unit)? = null,
    onRecordingDelete: ((VoiceRecording) -> Unit)? = null
) {
    // Simulate waveform data generation during recording
    LaunchedEffect(state.isRecording) {
        if (state.isRecording && !state.isPaused) {
            while (state.isRecording && !state.isPaused) {
                delay(100) // Update every 100ms
                val newAmplitude = Random.nextFloat() * 0.8f + 0.1f
                val newWaveformData = state.waveformData + newAmplitude
                val newDuration = state.duration + 100
                
                onStateChange(
                    state.copy(
                        amplitude = newAmplitude,
                        waveformData = newWaveformData.takeLast(100), // Keep last 100 samples
                        duration = newDuration,
                        estimatedFileSize = calculateEstimatedFileSize(newDuration, audioQuality, audioFormat)
                    )
                )
                
                // Auto-stop at max duration
                if (newDuration >= maxDuration) {
                    onStateChange(state.copy(isRecording = false))
                    break
                }
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recording controls
        VoiceRecorderControls(
            state = state,
            onStartRecording = {
                onStateChange(
                    state.copy(
                        isRecording = true,
                        isPaused = false,
                        duration = 0L,
                        waveformData = emptyList(),
                        amplitude = 0f
                    )
                )
            },
            onPauseRecording = {
                onStateChange(state.copy(isPaused = true))
            },
            onResumeRecording = {
                onStateChange(state.copy(isPaused = false))
            },
            onStopRecording = {
                if (state.duration > 0) {
                    val recording = VoiceRecording(
                        id = "recording_${System.currentTimeMillis()}",
                        fileName = "recording_${System.currentTimeMillis()}.${audioFormat.extension}",
                        filePath = "/recordings/recording_${System.currentTimeMillis()}.${audioFormat.extension}",
                        duration = state.duration,
                        fileSize = state.estimatedFileSize,
                        format = audioFormat,
                        quality = audioQuality,
                        waveformData = state.waveformData
                    )
                    
                    onStateChange(
                        state.copy(
                            isRecording = false,
                            isPaused = false,
                            currentRecording = recording,
                            recordings = state.recordings + recording,
                            duration = 0L,
                            waveformData = emptyList(),
                            amplitude = 0f
                        )
                    )
                    
                    onRecordingComplete?.invoke(recording)
                } else {
                    onStateChange(
                        state.copy(
                            isRecording = false,
                            isPaused = false,
                            duration = 0L,
                            waveformData = emptyList(),
                            amplitude = 0f
                        )
                    )
                }
            },
            onDeleteRecording = {
                onStateChange(
                    state.copy(
                        isRecording = false,
                        isPaused = false,
                        duration = 0L,
                        waveformData = emptyList(),
                        amplitude = 0f
                    )
                )
            }
        )
        
        // Waveform visualization
        if (showWaveform) {
            WaveformVisualization(
                waveformData = state.waveformData,
                isRecording = state.isRecording && !state.isPaused,
                currentAmplitude = state.amplitude,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
        
        // Recording info
        if (state.isRecording || state.duration > 0) {
            RecordingInfo(
                duration = state.duration,
                fileSize = state.estimatedFileSize,
                format = audioFormat,
                quality = audioQuality,
                maxDuration = maxDuration
            )
        }
        
        // Recordings list
        if (showRecordingsList && state.recordings.isNotEmpty()) {
            VoiceRecordingList(
                recordings = state.recordings,
                onPlayRecording = { recording ->
                    onStateChange(state.copy(isPlaying = true, currentRecording = recording))
                },
                onDeleteRecording = { recording ->
                    onStateChange(
                        state.copy(
                            recordings = state.recordings.filter { it.id != recording.id }
                        )
                    )
                    onRecordingDelete?.invoke(recording)
                },
                onShareRecording = { recording ->
                    println("Share recording: ${recording.fileName}")
                }
            )
        }
    }
}

// ============================================================================
// 🎛️ VOICE RECORDER CONTROLS
// ============================================================================

@Composable
fun VoiceRecorderControls(
    state: VoiceRecorderState,
    onStartRecording: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onDeleteRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main record button
            Box(contentAlignment = Alignment.Center) {
                // Pulsing animation when recording
                if (state.isRecording && !state.isPaused) {
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(scale)
                            .background(
                                MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )
                }
                
                FloatingActionButton(
                    onClick = {
                        when {
                            !state.isRecording -> onStartRecording()
                            state.isPaused -> onResumeRecording()
                            else -> onPauseRecording()
                        }
                    },
                    modifier = Modifier.size(64.dp),
                    containerColor = when {
                        state.isRecording && !state.isPaused -> MaterialTheme.colorScheme.error
                        state.isPaused -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.primary
                    }
                ) {
                    Icon(
                        when {
                            !state.isRecording -> Icons.Default.Mic
                            state.isPaused -> Icons.Default.PlayArrow
                            else -> Icons.Default.Pause
                        },
                        contentDescription = when {
                            !state.isRecording -> "Start Recording"
                            state.isPaused -> "Resume Recording"
                            else -> "Pause Recording"
                        },
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // Recording indicator
            if (state.isRecording) {
                RecordingIndicator(
                    isPaused = state.isPaused,
                    duration = state.duration
                )
            }
            
            // Control buttons
            if (state.isRecording || state.duration > 0) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Stop button
                    OutlinedButton(
                        onClick = onStopRecording,
                        enabled = state.isRecording || state.duration > 0
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Stop")
                    }
                    
                    // Delete button
                    OutlinedButton(
                        onClick = onDeleteRecording,
                        enabled = state.isRecording || state.duration > 0,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}

// ============================================================================
// 📊 WAVEFORM VISUALIZATION
// ============================================================================

@Composable
fun WaveformVisualization(
    waveformData: List<Float>,
    isRecording: Boolean,
    currentAmplitude: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val width = size.width
            val height = size.height
            val centerY = height / 2
            
            if (waveformData.isEmpty()) {
                // Draw baseline when no data
                drawLine(
                    color = Color.Gray.copy(alpha = 0.3f),
                    start = Offset(0f, centerY),
                    end = Offset(width, centerY),
                    strokeWidth = 2.dp.toPx()
                )
                
                // Draw "Ready to record" text
                drawContext.canvas.nativeCanvas.apply {
                    // Text drawing would be platform-specific
                }
            } else {
                // Draw waveform
                val barWidth = width / maxOf(waveformData.size, 50)
                
                waveformData.forEachIndexed { index, amplitude ->
                    val x = index * barWidth
                    val barHeight = amplitude * height * 0.8f
                    val top = centerY - barHeight / 2
                    val bottom = centerY + barHeight / 2
                    
                    val color = if (isRecording) {
                        Color.Red.copy(alpha = 0.8f)
                    } else {
                        Color.Blue.copy(alpha = 0.6f)
                    }
                    
                    drawLine(
                        color = color,
                        start = Offset(x, top),
                        end = Offset(x, bottom),
                        strokeWidth = (barWidth * 0.8f).coerceAtLeast(2.dp.toPx())
                    )
                }
                
                // Draw current amplitude indicator when recording
                if (isRecording && currentAmplitude > 0) {
                    val indicatorX = width - 20.dp.toPx()
                    val indicatorHeight = currentAmplitude * height * 0.8f
                    val indicatorTop = centerY - indicatorHeight / 2
                    val indicatorBottom = centerY + indicatorHeight / 2
                    
                    drawLine(
                        color = Color.Red,
                        start = Offset(indicatorX, indicatorTop),
                        end = Offset(indicatorX, indicatorBottom),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🔴 RECORDING INDICATOR
// ============================================================================

@Composable
fun RecordingIndicator(
    isPaused: Boolean,
    duration: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Blinking red dot when recording
        if (!isPaused) {
            val infiniteTransition = rememberInfiniteTransition(label = "blink")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        Color.Red.copy(alpha = alpha),
                        CircleShape
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Orange, CircleShape)
            )
        }
        
        Text(
            text = if (isPaused) "PAUSED" else "RECORDING",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPaused) Color.Orange else Color.Red
        )
        
        Text(
            text = formatDuration(duration),
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace
        )
    }
}

// ============================================================================
// ℹ️ RECORDING INFO
// ============================================================================

@Composable
fun RecordingInfo(
    duration: Long,
    fileSize: Long,
    format: AudioFormat,
    quality: AudioQuality,
    maxDuration: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Duration progress
            LinearProgressIndicator(
                progress = { (duration.toFloat() / maxDuration.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = if (duration >= maxDuration * 0.9f) Color.Red else MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = formatDuration(maxDuration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // File info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${format.extension.uppercase()} • ${quality.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatFileSize(fileSize),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 📋 VOICE RECORDING LIST
// ============================================================================

@Composable
fun VoiceRecordingList(
    recordings: List<VoiceRecording>,
    onPlayRecording: (VoiceRecording) -> Unit,
    onDeleteRecording: (VoiceRecording) -> Unit,
    onShareRecording: (VoiceRecording) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recordings (${recordings.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(recordings.reversed()) { recording ->
                    VoiceRecordingItem(
                        recording = recording,
                        onPlay = { onPlayRecording(recording) },
                        onDelete = { onDeleteRecording(recording) },
                        onShare = { onShareRecording(recording) }
                    )
                }
            }
        }
    }
}

@Composable
fun VoiceRecordingItem(
    recording: VoiceRecording,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Play button
            IconButton(
                onClick = onPlay,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            // Recording info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recording.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = formatDuration(recording.duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatFileSize(recording.fileSize),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recording.format.extension.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action buttons
            Row {
                IconButton(onClick = onShare) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private fun formatDuration(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}

private fun calculateEstimatedFileSize(
    durationMs: Long,
    quality: AudioQuality,
    format: AudioFormat
): Long {
    val durationSeconds = durationMs / 1000.0
    val bitsPerSecond = quality.bitrate * 1000
    val bytesPerSecond = bitsPerSecond / 8
    
    // Apply format-specific compression factor
    val compressionFactor = when (format) {
        AudioFormat.WAV -> 1.0 // Uncompressed
        AudioFormat.MP3 -> 0.1 // ~10:1 compression
        AudioFormat.AAC -> 0.08 // ~12:1 compression
        AudioFormat.OGG -> 0.09 // ~11:1 compression
        AudioFormat.M4A -> 0.08 // ~12:1 compression
    }
    
    return (durationSeconds * bytesPerSecond * compressionFactor).toLong()
}