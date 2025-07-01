package com.everybytesystems.dataflow.ui.scanner

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
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

/**
 * QR Code & Barcode Scanner Components
 * Camera-based scanning with multiple format support and QR code generation
 */

// ============================================================================
// 📱 DATA MODELS
// ============================================================================

data class BarcodeScannerState(
    val isScanning: Boolean = false,
    val isCameraActive: Boolean = false,
    val scanMode: ScanMode = ScanMode.SINGLE,
    val supportedFormats: Set<BarcodeFormat> = setOf(BarcodeFormat.QR_CODE),
    val lastScanResult: ScanResult? = null,
    val scanHistory: List<ScanResult> = emptyList(),
    val isFlashOn: Boolean = false,
    val zoomLevel: Float = 1.0f,
    val scanningArea: ScanningArea = ScanningArea.CENTER,
    val autoFocus: Boolean = true,
    val beepOnScan: Boolean = true,
    val vibrateOnScan: Boolean = true
)

data class ScanResult(
    val content: String,
    val format: BarcodeFormat,
    val timestamp: Long = System.currentTimeMillis(),
    val confidence: Float = 1.0f,
    val boundingBox: BoundingBox? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

enum class BarcodeFormat(val displayName: String) {
    QR_CODE("QR Code"),
    CODE_128("Code 128"),
    CODE_39("Code 39"),
    CODE_93("Code 93"),
    EAN_13("EAN-13"),
    EAN_8("EAN-8"),
    UPC_A("UPC-A"),
    UPC_E("UPC-E"),
    ITF("ITF"),
    CODABAR("Codabar"),
    DATA_MATRIX("Data Matrix"),
    PDF_417("PDF417"),
    AZTEC("Aztec"),
    MAXICODE("MaxiCode"),
    RSS_14("RSS-14"),
    RSS_EXPANDED("RSS Expanded")
}

enum class ScanMode {
    SINGLE,      // Scan one code and stop
    CONTINUOUS,  // Keep scanning
    BATCH        // Scan multiple codes
}

enum class ScanningArea {
    FULL_SCREEN,
    CENTER,
    CUSTOM
}

data class QRCodeGeneratorState(
    val content: String = "",
    val size: Int = 200,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.MEDIUM,
    val foregroundColor: Color = Color.Black,
    val backgroundColor: Color = Color.White,
    val logo: String? = null,
    val generatedCode: String? = null
)

enum class ErrorCorrectionLevel(val displayName: String) {
    LOW("Low (~7%)"),
    MEDIUM("Medium (~15%)"),
    QUARTILE("Quartile (~25%)"),
    HIGH("High (~30%)")
}

// ============================================================================
// 📱 BARCODE SCANNER COMPONENT
// ============================================================================

@Composable
fun BarcodeScanner(
    state: BarcodeScannerState,
    onStateChange: (BarcodeScannerState) -> Unit,
    onScanResult: (ScanResult) -> Unit,
    modifier: Modifier = Modifier,
    supportedFormats: Set<BarcodeFormat> = setOf(BarcodeFormat.QR_CODE),
    scanMode: ScanMode = ScanMode.SINGLE,
    showHistory: Boolean = true,
    showControls: Boolean = true
) {
    // Simulate scanning process
    LaunchedEffect(state.isScanning) {
        if (state.isScanning) {
            delay(2000) // Simulate scan delay
            val mockResult = ScanResult(
                content = "https://example.com/scanned-content",
                format = supportedFormats.first(),
                confidence = 0.95f
            )
            
            onScanResult(mockResult)
            onStateChange(
                state.copy(
                    lastScanResult = mockResult,
                    scanHistory = state.scanHistory + mockResult,
                    isScanning = if (scanMode == ScanMode.SINGLE) false else state.isScanning
                )
            )
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Scanner controls
        if (showControls) {
            ScannerControls(
                state = state,
                onStateChange = onStateChange,
                supportedFormats = supportedFormats,
                scanMode = scanMode
            )
        }
        
        // Camera preview with overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Camera preview placeholder
            CameraPreview(
                isActive = state.isCameraActive,
                modifier = Modifier.fillMaxSize()
            )
            
            // Scanning overlay
            ScanningOverlay(
                isScanning = state.isScanning,
                scanningArea = state.scanningArea,
                lastScanResult = state.lastScanResult,
                modifier = Modifier.fillMaxSize()
            )
            
            // Camera controls overlay
            CameraControlsOverlay(
                state = state,
                onStateChange = onStateChange,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        
        // Scan results
        if (state.lastScanResult != null) {
            ScanResultDisplay(
                result = state.lastScanResult!!,
                onCopy = {
                    // Copy to clipboard
                    println("Copied: ${state.lastScanResult!!.content}")
                },
                onShare = {
                    // Share content
                    println("Shared: ${state.lastScanResult!!.content}")
                },
                onOpen = {
                    // Open URL or handle content
                    println("Opened: ${state.lastScanResult!!.content}")
                }
            )
        }
        
        // Scan history
        if (showHistory && state.scanHistory.isNotEmpty()) {
            ScanHistoryList(
                history = state.scanHistory,
                onResultClick = { result ->
                    onStateChange(state.copy(lastScanResult = result))
                },
                onClearHistory = {
                    onStateChange(state.copy(scanHistory = emptyList()))
                }
            )
        }
    }
}

// ============================================================================
// 🎛️ SCANNER CONTROLS
// ============================================================================

@Composable
fun ScannerControls(
    state: BarcodeScannerState,
    onStateChange: (BarcodeScannerState) -> Unit,
    supportedFormats: Set<BarcodeFormat>,
    scanMode: ScanMode,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Start/Stop scanning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        if (state.isScanning) {
                            onStateChange(
                                state.copy(
                                    isScanning = false,
                                    isCameraActive = false
                                )
                            )
                        } else {
                            onStateChange(
                                state.copy(
                                    isScanning = true,
                                    isCameraActive = true,
                                    supportedFormats = supportedFormats,
                                    scanMode = scanMode
                                )
                            )
                        }
                    }
                ) {
                    Icon(
                        if (state.isScanning) Icons.Default.Stop else Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (state.isScanning) "Stop" else "Start Scanning")
                }
            }
            
            // Supported formats
            if (supportedFormats.size > 1) {
                Text(
                    text = "Formats: ${supportedFormats.joinToString(", ") { it.displayName }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            // Scan mode indicator
            Text(
                text = "Mode: ${scanMode.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 📷 CAMERA PREVIEW
// ============================================================================

@Composable
fun CameraPreview(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) Color.Black else MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            // Simulate camera preview with animated pattern
            val infiniteTransition = rememberInfiniteTransition(label = "camera")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.7f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = alpha),
                                Color.Black
                            )
                        )
                    )
            )
            
            Text(
                text = "📷 Camera Preview",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Camera Inactive",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Start scanning to activate camera",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 🎯 SCANNING OVERLAY
// ============================================================================

@Composable
fun ScanningOverlay(
    isScanning: Boolean,
    scanningArea: ScanningArea,
    lastScanResult: ScanResult?,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (isScanning) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val scanSize = minOf(size.width, size.height) * 0.6f
            
            // Draw scanning frame
            val frameRect = androidx.compose.ui.geometry.Rect(
                left = centerX - scanSize / 2,
                top = centerY - scanSize / 2,
                right = centerX + scanSize / 2,
                bottom = centerY + scanSize / 2
            )
            
            // Draw corner indicators
            val cornerLength = 30.dp.toPx()
            val cornerWidth = 4.dp.toPx()
            val cornerColor = Color.White
            
            // Top-left corner
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.left, frameRect.top),
                end = androidx.compose.ui.geometry.Offset(frameRect.left + cornerLength, frameRect.top),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.left, frameRect.top),
                end = androidx.compose.ui.geometry.Offset(frameRect.left, frameRect.top + cornerLength),
                strokeWidth = cornerWidth
            )
            
            // Top-right corner
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.right, frameRect.top),
                end = androidx.compose.ui.geometry.Offset(frameRect.right - cornerLength, frameRect.top),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.right, frameRect.top),
                end = androidx.compose.ui.geometry.Offset(frameRect.right, frameRect.top + cornerLength),
                strokeWidth = cornerWidth
            )
            
            // Bottom-left corner
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.left, frameRect.bottom),
                end = androidx.compose.ui.geometry.Offset(frameRect.left + cornerLength, frameRect.bottom),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.left, frameRect.bottom),
                end = androidx.compose.ui.geometry.Offset(frameRect.left, frameRect.bottom - cornerLength),
                strokeWidth = cornerWidth
            )
            
            // Bottom-right corner
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.right, frameRect.bottom),
                end = androidx.compose.ui.geometry.Offset(frameRect.right - cornerLength, frameRect.bottom),
                strokeWidth = cornerWidth
            )
            drawLine(
                color = cornerColor,
                start = androidx.compose.ui.geometry.Offset(frameRect.right, frameRect.bottom),
                end = androidx.compose.ui.geometry.Offset(frameRect.right, frameRect.bottom - cornerLength),
                strokeWidth = cornerWidth
            )
            
            // Draw scanning line animation
            // This would be animated in a real implementation
            val scanLineY = centerY
            drawLine(
                color = Color.Red.copy(alpha = 0.8f),
                start = androidx.compose.ui.geometry.Offset(frameRect.left, scanLineY),
                end = androidx.compose.ui.geometry.Offset(frameRect.right, scanLineY),
                strokeWidth = 2.dp.toPx()
            )
        }
        
        // Highlight detected barcode area
        lastScanResult?.boundingBox?.let { box ->
            val highlightRect = androidx.compose.ui.geometry.Rect(
                left = box.left * size.width,
                top = box.top * size.height,
                right = box.right * size.width,
                bottom = box.bottom * size.height
            )
            
            drawRect(
                color = Color.Green.copy(alpha = 0.3f),
                topLeft = highlightRect.topLeft,
                size = highlightRect.size
            )
            
            drawRect(
                color = Color.Green,
                topLeft = highlightRect.topLeft,
                size = highlightRect.size,
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

// ============================================================================
// 🎛️ CAMERA CONTROLS OVERLAY
// ============================================================================

@Composable
fun CameraControlsOverlay(
    state: BarcodeScannerState,
    onStateChange: (BarcodeScannerState) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isCameraActive) {
        Card(
            modifier = modifier.padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Flash toggle
                IconButton(
                    onClick = {
                        onStateChange(state.copy(isFlashOn = !state.isFlashOn))
                    }
                ) {
                    Icon(
                        if (state.isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash",
                        tint = if (state.isFlashOn) Color.Yellow else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Auto focus toggle
                IconButton(
                    onClick = {
                        onStateChange(state.copy(autoFocus = !state.autoFocus))
                    }
                ) {
                    Icon(
                        if (state.autoFocus) Icons.Default.CenterFocusStrong else Icons.Default.CenterFocusWeak,
                        contentDescription = "Auto Focus",
                        tint = if (state.autoFocus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Gallery button
                IconButton(
                    onClick = {
                        // Open gallery to scan from image
                        println("Open gallery")
                    }
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = "Gallery"
                    )
                }
            }
        }
    }
}

// ============================================================================
// 📋 SCAN RESULT DISPLAY
// ============================================================================

@Composable
fun ScanResultDisplay(
    result: ScanResult,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scan Result",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Chip(
                    onClick = { },
                    label = { Text(result.format.displayName) }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = result.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
                
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
                
                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open")
                }
            }
        }
    }
}

// ============================================================================
// 📜 SCAN HISTORY LIST
// ============================================================================

@Composable
fun ScanHistoryList(
    history: List<ScanResult>,
    onResultClick: (ScanResult) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scan History (${history.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onClearHistory) {
                    Text("Clear")
                }
            }
            
            LazyColumn(
                modifier = Modifier.heightIn(max = 200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history.reversed()) { result ->
                    ScanHistoryItem(
                        result = result,
                        onClick = { onResultClick(result) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScanHistoryItem(
    result: ScanResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.QrCode,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = "${result.format.displayName} • ${formatTimestamp(result.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
}

// Placeholder icons for missing Material icons
private val Icons.Default.QrCodeScanner get() = Icons.Default.QrCode
private val Icons.Default.FlashOn get() = Icons.Default.FlashOn
private val Icons.Default.FlashOff get() = Icons.Default.FlashOff
private val Icons.Default.CenterFocusStrong get() = Icons.Default.CenterFocusStrong
private val Icons.Default.CenterFocusWeak get() = Icons.Default.CenterFocusWeak
private val Icons.Default.PhotoLibrary get() = Icons.Default.Photo
private val Icons.Default.ContentCopy get() = Icons.Default.ContentCopy
private val Icons.Default.OpenInNew get() = Icons.Default.OpenInNew

@Composable
private fun Chip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            label()
        }
    }
}