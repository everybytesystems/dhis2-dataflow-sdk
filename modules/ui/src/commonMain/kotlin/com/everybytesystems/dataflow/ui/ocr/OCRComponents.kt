package com.everybytesystems.dataflow.ui.ocr

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
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

/**
 * OCR (Optical Character Recognition) Components
 * Text extraction from images with document scanning, business card reading, and text recognition
 */

// ============================================================================
// 📸 OCR DATA MODELS
// ============================================================================

data class OCRState(
    val isProcessing: Boolean = false,
    val currentImage: OCRImage? = null,
    val extractedText: String = "",
    val textBlocks: List<TextBlock> = emptyList(),
    val recognitionLanguage: OCRLanguage = OCRLanguage.ENGLISH,
    val processingMode: OCRMode = OCRMode.DOCUMENT,
    val confidence: Float = 0.0f,
    val processingTime: Long = 0L,
    val error: String? = null,
    val history: List<OCRResult> = emptyList()
)

data class OCRImage(
    val id: String,
    val uri: String,
    val width: Int,
    val height: Int,
    val size: Long,
    val format: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class TextBlock(
    val id: String,
    val text: String,
    val boundingBox: BoundingRect,
    val confidence: Float,
    val language: String? = null,
    val fontSize: Float? = null,
    val fontFamily: String? = null,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val textAngle: Float = 0f,
    val wordCount: Int = 0,
    val lineHeight: Float = 0f
)

data class BoundingRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val rotation: Float = 0f
)

data class OCRResult(
    val id: String,
    val image: OCRImage,
    val extractedText: String,
    val textBlocks: List<TextBlock>,
    val confidence: Float,
    val language: OCRLanguage,
    val mode: OCRMode,
    val processingTime: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, Any> = emptyMap()
)

enum class OCRLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    SPANISH("Spanish", "es"),
    FRENCH("French", "fr"),
    GERMAN("German", "de"),
    ITALIAN("Italian", "it"),
    PORTUGUESE("Portuguese", "pt"),
    RUSSIAN("Russian", "ru"),
    CHINESE_SIMPLIFIED("Chinese (Simplified)", "zh-CN"),
    CHINESE_TRADITIONAL("Chinese (Traditional)", "zh-TW"),
    JAPANESE("Japanese", "ja"),
    KOREAN("Korean", "ko"),
    ARABIC("Arabic", "ar"),
    HINDI("Hindi", "hi"),
    DUTCH("Dutch", "nl"),
    SWEDISH("Swedish", "sv"),
    AUTO_DETECT("Auto Detect", "auto")
}

enum class OCRMode(val displayName: String, val description: String) {
    DOCUMENT("Document", "Optimized for documents and printed text"),
    HANDWRITING("Handwriting", "Specialized for handwritten text"),
    BUSINESS_CARD("Business Card", "Extract contact information"),
    LICENSE_PLATE("License Plate", "Vehicle license plate recognition"),
    RECEIPT("Receipt", "Receipt and invoice processing"),
    BOOK("Book", "Book and magazine text"),
    SIGN("Sign", "Street signs and signage"),
    GENERAL("General", "General purpose text recognition")
}

enum class ImageSource {
    CAMERA, GALLERY, FILE_PICKER, CLIPBOARD, URL
}

// ============================================================================
// 📸 MAIN OCR COMPONENT
// ============================================================================

@Composable
fun OCRTextExtractor(
    state: OCRState,
    onStateChange: (OCRState) -> Unit,
    onImageSelected: (OCRImage) -> Unit,
    onTextExtracted: (OCRResult) -> Unit,
    modifier: Modifier = Modifier,
    supportedLanguages: List<OCRLanguage> = OCRLanguage.values().toList(),
    supportedModes: List<OCRMode> = OCRMode.values().toList(),
    showHistory: Boolean = true,
    showPreview: Boolean = true
) {
    Column(modifier = modifier.fillMaxSize()) {
        // OCR Controls
        OCRControls(
            state = state,
            onStateChange = onStateChange,
            onImageSelected = onImageSelected,
            supportedLanguages = supportedLanguages,
            supportedModes = supportedModes
        )
        
        // Main content area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image preview and processing
            if (showPreview) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    OCRImagePreview(
                        state = state,
                        onProcessImage = {
                            // Simulate OCR processing
                            onStateChange(state.copy(isProcessing = true))
                            
                            // Simulate processing delay
                            LaunchedEffect(Unit) {
                                delay(2000)
                                
                                val mockResult = OCRResult(
                                    id = "result_${System.currentTimeMillis()}",
                                    image = state.currentImage!!,
                                    extractedText = generateMockText(state.processingMode),
                                    textBlocks = generateMockTextBlocks(state.processingMode),
                                    confidence = 0.85f + (0..15).random() / 100f,
                                    language = state.recognitionLanguage,
                                    mode = state.processingMode,
                                    processingTime = 1500 + (0..1000).random()
                                )
                                
                                onStateChange(
                                    state.copy(
                                        isProcessing = false,
                                        extractedText = mockResult.extractedText,
                                        textBlocks = mockResult.textBlocks,
                                        confidence = mockResult.confidence,
                                        processingTime = mockResult.processingTime,
                                        history = state.history + mockResult
                                    )
                                )
                                
                                onTextExtracted(mockResult)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Results panel
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                OCRResultsPanel(
                    state = state,
                    onCopyText = {
                        // Copy to clipboard
                        println("Copied text: ${state.extractedText}")
                    },
                    onExportText = { format ->
                        println("Export as: $format")
                    },
                    onEditText = { newText ->
                        onStateChange(state.copy(extractedText = newText))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        // History panel
        if (showHistory && state.history.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                OCRHistoryPanel(
                    history = state.history,
                    onResultClick = { result ->
                        onStateChange(
                            state.copy(
                                currentImage = result.image,
                                extractedText = result.extractedText,
                                textBlocks = result.textBlocks,
                                confidence = result.confidence,
                                processingTime = result.processingTime
                            )
                        )
                    },
                    onClearHistory = {
                        onStateChange(state.copy(history = emptyList()))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ============================================================================
// 🎛️ OCR CONTROLS
// ============================================================================

@Composable
fun OCRControls(
    state: OCRState,
    onStateChange: (OCRState) -> Unit,
    onImageSelected: (OCRImage) -> Unit,
    supportedLanguages: List<OCRLanguage>,
    supportedModes: List<OCRMode>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OCR Text Extraction",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (state.isProcessing) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Processing...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Image source buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ImageSourceButton(
                    source = ImageSource.CAMERA,
                    onClick = {
                        // Simulate camera capture
                        val mockImage = OCRImage(
                            id = "img_${System.currentTimeMillis()}",
                            uri = "camera://captured_image",
                            width = 1920,
                            height = 1080,
                            size = 2048576,
                            format = "JPEG"
                        )
                        onStateChange(state.copy(currentImage = mockImage))
                        onImageSelected(mockImage)
                    },
                    enabled = !state.isProcessing
                )
                
                ImageSourceButton(
                    source = ImageSource.GALLERY,
                    onClick = {
                        // Simulate gallery selection
                        val mockImage = OCRImage(
                            id = "img_${System.currentTimeMillis()}",
                            uri = "gallery://selected_image",
                            width = 1600,
                            height = 1200,
                            size = 1536000,
                            format = "PNG"
                        )
                        onStateChange(state.copy(currentImage = mockImage))
                        onImageSelected(mockImage)
                    },
                    enabled = !state.isProcessing
                )
                
                ImageSourceButton(
                    source = ImageSource.FILE_PICKER,
                    onClick = {
                        // Simulate file picker
                        val mockImage = OCRImage(
                            id = "img_${System.currentTimeMillis()}",
                            uri = "file://document.pdf",
                            width = 2480,
                            height = 3508,
                            size = 4096000,
                            format = "PDF"
                        )
                        onStateChange(state.copy(currentImage = mockImage))
                        onImageSelected(mockImage)
                    },
                    enabled = !state.isProcessing
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Settings row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language selection
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    var languageExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = languageExpanded,
                        onExpandedChange = { languageExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.recognitionLanguage.displayName,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            singleLine = true
                        )
                        
                        ExposedDropdownMenu(
                            expanded = languageExpanded,
                            onDismissRequest = { languageExpanded = false }
                        ) {
                            supportedLanguages.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(language.displayName) },
                                    onClick = {
                                        onStateChange(state.copy(recognitionLanguage = language))
                                        languageExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Mode selection
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Processing Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    var modeExpanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = modeExpanded,
                        onExpandedChange = { modeExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.processingMode.displayName,
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = modeExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            singleLine = true
                        )
                        
                        ExposedDropdownMenu(
                            expanded = modeExpanded,
                            onDismissRequest = { modeExpanded = false }
                        ) {
                            supportedModes.forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(mode.displayName)
                                            Text(
                                                text = mode.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onStateChange(state.copy(processingMode = mode))
                                        modeExpanded = false
                                    }
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
fun ImageSourceButton(
    source: ImageSource,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val (icon, label) = when (source) {
        ImageSource.CAMERA -> Icons.Default.CameraAlt to "Camera"
        ImageSource.GALLERY -> Icons.Default.PhotoLibrary to "Gallery"
        ImageSource.FILE_PICKER -> Icons.Default.FolderOpen to "Files"
        ImageSource.CLIPBOARD -> Icons.Default.ContentPaste to "Clipboard"
        ImageSource.URL -> Icons.Default.Link to "URL"
    }
    
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label)
    }
}

// ============================================================================
// 🖼️ IMAGE PREVIEW
// ============================================================================

@Composable
fun OCRImagePreview(
    state: OCRState,
    onProcessImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Image Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (state.currentImage != null) {
            // Image preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Simulate image preview
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "📸 ${state.currentImage!!.format}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text(
                        text = "${state.currentImage!!.width} × ${state.currentImage!!.height}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = formatFileSize(state.currentImage!!.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Text block overlays
                if (state.textBlocks.isNotEmpty()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        state.textBlocks.forEach { block ->
                            val rect = androidx.compose.ui.geometry.Rect(
                                left = block.boundingBox.left * size.width,
                                top = block.boundingBox.top * size.height,
                                right = block.boundingBox.right * size.width,
                                bottom = block.boundingBox.bottom * size.height
                            )
                            
                            // Draw bounding box
                            drawRect(
                                color = Color.Blue.copy(alpha = 0.3f),
                                topLeft = rect.topLeft,
                                size = rect.size
                            )
                            
                            drawRect(
                                color = Color.Blue,
                                topLeft = rect.topLeft,
                                size = rect.size,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Process button
            Button(
                onClick = onProcessImage,
                enabled = !state.isProcessing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...")
                } else {
                    Icon(
                        Icons.Default.TextFields,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Extract Text")
                }
            }
        } else {
            // No image selected state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "No Image Selected",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Select an image to extract text",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ============================================================================
// 📄 RESULTS PANEL
// ============================================================================

@Composable
fun OCRResultsPanel(
    state: OCRState,
    onCopyText: () -> Unit,
    onExportText: (String) -> Unit,
    onEditText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Header with stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Extracted Text",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (state.extractedText.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Confidence indicator
                    ConfidenceIndicator(confidence = state.confidence)
                    
                    // Processing time
                    Text(
                        text = "${state.processingTime}ms",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (state.extractedText.isNotEmpty()) {
            // Text content
            var editableText by remember(state.extractedText) { mutableStateOf(state.extractedText) }
            
            OutlinedTextField(
                value = editableText,
                onValueChange = { newText ->
                    editableText = newText
                    onEditText(newText)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Recognized Text") },
                placeholder = { Text("Extracted text will appear here...") },
                maxLines = Int.MAX_VALUE
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Text statistics
            TextStatistics(text = editableText)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopyText,
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
                    onClick = { onExportText("TXT") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export")
                }
                
                OutlinedButton(
                    onClick = { onExportText("PDF") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
            }
        } else {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.TextFields,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "No Text Extracted",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "Process an image to see extracted text",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ConfidenceIndicator(
    confidence: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        confidence >= 0.9f -> Color(0xFF4CAF50) // Green
        confidence >= 0.7f -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            when {
                confidence >= 0.9f -> Icons.Default.CheckCircle
                confidence >= 0.7f -> Icons.Default.Warning
                else -> Icons.Default.Error
            },
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = color
        )
        
        Text(
            text = "${(confidence * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
fun TextStatistics(
    text: String,
    modifier: Modifier = Modifier
) {
    val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
    val lines = text.lines().size
    val characters = text.length
    val charactersNoSpaces = text.replace(Regex("\\s"), "").length
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Words", words.size.toString())
            StatItem("Lines", lines.toString())
            StatItem("Characters", characters.toString())
            StatItem("No Spaces", charactersNoSpaces.toString())
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// 📜 HISTORY PANEL
// ============================================================================

@Composable
fun OCRHistoryPanel(
    history: List<OCRResult>,
    onResultClick: (OCRResult) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OCR History (${history.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            TextButton(onClick = onClearHistory) {
                Text("Clear All")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // History list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history.reversed()) { result ->
                OCRHistoryItem(
                    result = result,
                    onClick = { onResultClick(result) }
                )
            }
        }
    }
}

@Composable
fun OCRHistoryItem(
    result: OCRResult,
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
            // Mode icon
            Icon(
                when (result.mode) {
                    OCRMode.DOCUMENT -> Icons.Default.Description
                    OCRMode.BUSINESS_CARD -> Icons.Default.ContactPage
                    OCRMode.RECEIPT -> Icons.Default.Receipt
                    OCRMode.HANDWRITING -> Icons.Default.Edit
                    else -> Icons.Default.TextFields
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.extractedText.take(50) + if (result.extractedText.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.mode.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    ConfidenceIndicator(confidence = result.confidence)
                    
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = formatTimestamp(result.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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

private fun generateMockText(mode: OCRMode): String {
    return when (mode) {
        OCRMode.DOCUMENT -> """
            DataFlow UI SDK Documentation
            
            Welcome to the comprehensive UI component library for modern applications. This SDK provides a wide range of components including charts, forms, authentication, and advanced visualizations.
            
            Key Features:
            • Rich text editing capabilities
            • Voice recording and playback
            • QR code and barcode scanning
            • Advanced authentication flows
            • Comprehensive notification system
            
            Getting Started:
            1. Install the SDK dependencies
            2. Configure your application theme
            3. Import the required components
            4. Start building amazing user interfaces
        """.trimIndent()
        
        OCRMode.BUSINESS_CARD -> """
            John Smith
            Senior Software Engineer
            
            DataFlow Technologies Inc.
            123 Innovation Drive
            San Francisco, CA 94105
            
            Phone: (555) 123-4567
            Email: john.smith@dataflow.com
            Website: www.dataflow.com
        """.trimIndent()
        
        OCRMode.RECEIPT -> """
            DataFlow Store
            123 Main Street
            San Francisco, CA 94105
            
            Date: 2024-01-15
            Time: 14:30:25
            
            Items:
            UI SDK License      $299.00
            Support Package     $99.00
            Documentation       $49.00
            
            Subtotal:          $447.00
            Tax (8.5%):        $37.99
            Total:             $484.99
            
            Payment: Credit Card
            Thank you for your purchase!
        """.trimIndent()
        
        OCRMode.HANDWRITING -> """
            Meeting Notes - January 15, 2024
            
            Discussed new UI components:
            - OCR text extraction
            - Voice recording features
            - Enhanced authentication
            
            Action items:
            1. Complete OCR implementation
            2. Test voice recording on mobile
            3. Review authentication flows
            
            Next meeting: January 22, 2024
        """.trimIndent()
        
        else -> """
            Sample extracted text from image processing. This demonstrates the OCR capabilities of the DataFlow UI SDK. The system can recognize various types of text content with high accuracy.
        """.trimIndent()
    }
}

private fun generateMockTextBlocks(mode: OCRMode): List<TextBlock> {
    return when (mode) {
        OCRMode.DOCUMENT -> listOf(
            TextBlock(
                id = "block1",
                text = "DataFlow UI SDK Documentation",
                boundingBox = BoundingRect(0.1f, 0.1f, 0.9f, 0.2f),
                confidence = 0.95f,
                fontSize = 24f,
                isBold = true
            ),
            TextBlock(
                id = "block2",
                text = "Welcome to the comprehensive UI component library...",
                boundingBox = BoundingRect(0.1f, 0.25f, 0.9f, 0.4f),
                confidence = 0.88f,
                fontSize = 14f
            ),
            TextBlock(
                id = "block3",
                text = "Key Features:",
                boundingBox = BoundingRect(0.1f, 0.45f, 0.3f, 0.5f),
                confidence = 0.92f,
                fontSize = 16f,
                isBold = true
            )
        )
        
        OCRMode.BUSINESS_CARD -> listOf(
            TextBlock(
                id = "name",
                text = "John Smith",
                boundingBox = BoundingRect(0.1f, 0.1f, 0.6f, 0.2f),
                confidence = 0.96f,
                fontSize = 18f,
                isBold = true
            ),
            TextBlock(
                id = "title",
                text = "Senior Software Engineer",
                boundingBox = BoundingRect(0.1f, 0.22f, 0.7f, 0.3f),
                confidence = 0.89f,
                fontSize = 12f
            ),
            TextBlock(
                id = "company",
                text = "DataFlow Technologies Inc.",
                boundingBox = BoundingRect(0.1f, 0.35f, 0.8f, 0.42f),
                confidence = 0.93f,
                fontSize = 14f
            )
        )
        
        else -> listOf(
            TextBlock(
                id = "general",
                text = "Sample extracted text",
                boundingBox = BoundingRect(0.1f, 0.1f, 0.9f, 0.9f),
                confidence = 0.85f,
                fontSize = 14f
            )
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    
    return when {
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}

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