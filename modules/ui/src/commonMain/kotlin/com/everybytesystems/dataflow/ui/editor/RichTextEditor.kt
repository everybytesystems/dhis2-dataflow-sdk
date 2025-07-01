package com.everybytesystems.dataflow.ui.editor

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*

/**
 * WYSIWYG Rich Text Editor
 * Advanced text editing with formatting, media embeds, and export capabilities
 */

// ============================================================================
// 📝 DATA MODELS
// ============================================================================

data class RichTextEditorState(
    val content: RichTextContent = RichTextContent(),
    val selection: TextRange = TextRange.Zero,
    val isEditing: Boolean = false,
    val currentFormat: TextFormat = TextFormat(),
    val undoStack: List<RichTextContent> = emptyList(),
    val redoStack: List<RichTextContent> = emptyList(),
    val wordCount: Int = 0,
    val characterCount: Int = 0
)

data class RichTextContent(
    val text: String = "",
    val formatting: List<FormatSpan> = emptyList(),
    val embeds: List<MediaEmbed> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)

data class FormatSpan(
    val start: Int,
    val end: Int,
    val format: TextFormat
)

data class TextFormat(
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val isStrikethrough: Boolean = false,
    val fontSize: TextUnit = 14.sp,
    val fontFamily: FontFamily? = null,
    val color: Color = Color.Unspecified,
    val backgroundColor: Color = Color.Unspecified,
    val alignment: TextAlign = TextAlign.Start,
    val heading: HeadingLevel? = null,
    val listType: ListType? = null,
    val isCode: Boolean = false,
    val isBlockquote: Boolean = false
)

enum class HeadingLevel { H1, H2, H3, H4, H5, H6 }
enum class ListType { BULLET, NUMBERED }
enum class MediaType { IMAGE, VIDEO, AUDIO, FILE, LINK, TABLE, FORMULA, DIAGRAM }
enum class ExportFormat { HTML, MARKDOWN, PLAIN_TEXT, PDF, DOCX }

data class MediaEmbed(
    val id: String,
    val type: MediaType,
    val url: String,
    val caption: String = "",
    val position: Int,
    val metadata: Map<String, Any> = emptyMap()
)

// ============================================================================
// 📝 RICH TEXT EDITOR COMPONENT
// ============================================================================

@Composable
fun RichTextEditor(
    state: RichTextEditorState,
    onStateChange: (RichTextEditorState) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Start typing...",
    readOnly: Boolean = false,
    showToolbar: Boolean = true,
    showStatusBar: Boolean = true,
    onMediaInsert: ((MediaType) -> Unit)? = null,
    onExport: ((ExportFormat) -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Toolbar
        if (showToolbar && !readOnly) {
            RichTextToolbar(
                currentFormat = state.currentFormat,
                onFormatChange = { format ->
                    onStateChange(state.copy(currentFormat = format))
                },
                onUndo = {
                    if (state.undoStack.isNotEmpty()) {
                        val previousContent = state.undoStack.last()
                        onStateChange(
                            state.copy(
                                content = previousContent,
                                undoStack = state.undoStack.dropLast(1),
                                redoStack = state.redoStack + state.content
                            )
                        )
                    }
                },
                onRedo = {
                    if (state.redoStack.isNotEmpty()) {
                        val nextContent = state.redoStack.last()
                        onStateChange(
                            state.copy(
                                content = nextContent,
                                redoStack = state.redoStack.dropLast(1),
                                undoStack = state.undoStack + state.content
                            )
                        )
                    }
                },
                onMediaInsert = onMediaInsert,
                onExport = onExport,
                canUndo = state.undoStack.isNotEmpty(),
                canRedo = state.redoStack.isNotEmpty()
            )
        }
        
        // Editor content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Text field
                BasicTextField(
                    value = state.content.text,
                    onValueChange = { newText ->
                        val newContent = state.content.copy(text = newText)
                        onStateChange(
                            state.copy(
                                content = newContent,
                                wordCount = newText.split("\\s+".toRegex()).size,
                                characterCount = newText.length,
                                undoStack = state.undoStack + state.content
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    readOnly = readOnly,
                    decorationBox = { innerTextField ->
                        if (state.content.text.isEmpty() && !readOnly) {
                            Text(
                                text = placeholder,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                
                // Media embeds overlay
                if (state.content.embeds.isNotEmpty()) {
                    MediaEmbedOverlay(
                        embeds = state.content.embeds,
                        onEmbedClick = { embed ->
                            println("Clicked embed: ${embed.type}")
                        },
                        onEmbedRemove = { embed ->
                            val newEmbeds = state.content.embeds.filter { it.id != embed.id }
                            onStateChange(
                                state.copy(
                                    content = state.content.copy(embeds = newEmbeds)
                                )
                            )
                        }
                    )
                }
            }
        }
        
        // Status bar
        if (showStatusBar) {
            RichTextStatusBar(
                wordCount = state.wordCount,
                characterCount = state.characterCount,
                currentFormat = state.currentFormat
            )
        }
    }
}

// ============================================================================
// 🛠️ RICH TEXT TOOLBAR
// ============================================================================

@Composable
fun RichTextToolbar(
    currentFormat: TextFormat,
    onFormatChange: (TextFormat) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onMediaInsert: ((MediaType) -> Unit)?,
    onExport: ((ExportFormat) -> Unit)?,
    canUndo: Boolean,
    canRedo: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            // Undo/Redo
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onUndo,
                        enabled = canUndo
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo")
                    }
                    IconButton(
                        onClick = onRedo,
                        enabled = canRedo
                    ) {
                        Icon(Icons.Default.Redo, contentDescription = "Redo")
                    }
                    
                    VerticalDivider(modifier = Modifier.height(32.dp))
                }
            }
            
            // Text formatting
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Bold
                    IconButton(
                        onClick = {
                            onFormatChange(currentFormat.copy(isBold = !currentFormat.isBold))
                        }
                    ) {
                        Icon(
                            Icons.Default.FormatBold,
                            contentDescription = "Bold",
                            tint = if (currentFormat.isBold) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    // Italic
                    IconButton(
                        onClick = {
                            onFormatChange(currentFormat.copy(isItalic = !currentFormat.isItalic))
                        }
                    ) {
                        Icon(
                            Icons.Default.FormatItalic,
                            contentDescription = "Italic",
                            tint = if (currentFormat.isItalic) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    // Underline
                    IconButton(
                        onClick = {
                            onFormatChange(currentFormat.copy(isUnderline = !currentFormat.isUnderline))
                        }
                    ) {
                        Icon(
                            Icons.Default.FormatUnderlined,
                            contentDescription = "Underline",
                            tint = if (currentFormat.isUnderline) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    VerticalDivider(modifier = Modifier.height(32.dp))
                }
            }
            
            // Headings
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    HeadingLevel.entries.take(3).forEach { level ->
                        TextButton(
                            onClick = {
                                onFormatChange(
                                    currentFormat.copy(
                                        heading = if (currentFormat.heading == level) null else level
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = level.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (currentFormat.heading == level) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                    
                    VerticalDivider(modifier = Modifier.height(32.dp))
                }
            }
            
            // Lists
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            onFormatChange(
                                currentFormat.copy(
                                    listType = if (currentFormat.listType == ListType.BULLET) {
                                        null
                                    } else {
                                        ListType.BULLET
                                    }
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.FormatListBulleted,
                            contentDescription = "Bullet List",
                            tint = if (currentFormat.listType == ListType.BULLET) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            onFormatChange(
                                currentFormat.copy(
                                    listType = if (currentFormat.listType == ListType.NUMBERED) {
                                        null
                                    } else {
                                        ListType.NUMBERED
                                    }
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.FormatListNumbered,
                            contentDescription = "Numbered List",
                            tint = if (currentFormat.listType == ListType.NUMBERED) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    VerticalDivider(modifier = Modifier.height(32.dp))
                }
            }
            
            // Media insertion
            if (onMediaInsert != null) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = { onMediaInsert(MediaType.IMAGE) }) {
                            Icon(Icons.Default.Image, contentDescription = "Insert Image")
                        }
                        IconButton(onClick = { onMediaInsert(MediaType.LINK) }) {
                            Icon(Icons.Default.Link, contentDescription = "Insert Link")
                        }
                        IconButton(onClick = { onMediaInsert(MediaType.TABLE) }) {
                            Icon(Icons.Default.TableChart, contentDescription = "Insert Table")
                        }
                        
                        VerticalDivider(modifier = Modifier.height(32.dp))
                    }
                }
            }
            
            // Export
            if (onExport != null) {
                item {
                    var showExportMenu by remember { mutableStateOf(false) }
                    
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export")
                        }
                        
                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
                            ExportFormat.entries.forEach { format ->
                                DropdownMenuItem(
                                    text = { Text(format.name) },
                                    onClick = {
                                        onExport(format)
                                        showExportMenu = false
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

// ============================================================================
// 📊 STATUS BAR
// ============================================================================

@Composable
fun RichTextStatusBar(
    wordCount: Int,
    characterCount: Int,
    currentFormat: TextFormat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Word and character count
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "$wordCount words",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$characterCount characters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Current formatting indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentFormat.isBold) {
                    Chip(
                        onClick = { },
                        label = { Text("B", style = MaterialTheme.typography.labelSmall) }
                    )
                }
                if (currentFormat.isItalic) {
                    Chip(
                        onClick = { },
                        label = { Text("I", style = MaterialTheme.typography.labelSmall) }
                    )
                }
                if (currentFormat.heading != null) {
                    Chip(
                        onClick = { },
                        label = { Text(currentFormat.heading!!.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🖼️ MEDIA EMBED OVERLAY
// ============================================================================

@Composable
fun MediaEmbedOverlay(
    embeds: List<MediaEmbed>,
    onEmbedClick: (MediaEmbed) -> Unit,
    onEmbedRemove: (MediaEmbed) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(embeds) { embed ->
            MediaEmbedItem(
                embed = embed,
                onClick = { onEmbedClick(embed) },
                onRemove = { onEmbedRemove(embed) }
            )
        }
    }
}

@Composable
fun MediaEmbedItem(
    embed: MediaEmbed,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Media type icon
            Icon(
                when (embed.type) {
                    MediaType.IMAGE -> Icons.Default.Image
                    MediaType.VIDEO -> Icons.Default.VideoFile
                    MediaType.AUDIO -> Icons.Default.AudioFile
                    MediaType.FILE -> Icons.Default.AttachFile
                    MediaType.LINK -> Icons.Default.Link
                    MediaType.TABLE -> Icons.Default.TableChart
                    MediaType.FORMULA -> Icons.Default.Functions
                    MediaType.DIAGRAM -> Icons.Default.AccountTree
                },
                contentDescription = embed.type.name,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = embed.type.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (embed.caption.isNotEmpty()) {
                    Text(
                        text = embed.caption,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = embed.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
            
            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Placeholder icons for missing Material icons
private val Icons.Default.Undo get() = Icons.Default.ArrowBack
private val Icons.Default.Redo get() = Icons.Default.ArrowForward
private val Icons.Default.FormatBold get() = Icons.Default.FormatBold
private val Icons.Default.FormatItalic get() = Icons.Default.FormatItalic
private val Icons.Default.FormatUnderlined get() = Icons.Default.FormatUnderlined
private val Icons.Default.FormatListBulleted get() = Icons.Default.List
private val Icons.Default.FormatListNumbered get() = Icons.Default.List
private val Icons.Default.TableChart get() = Icons.Default.GridView
private val Icons.Default.FileDownload get() = Icons.Default.Download
private val Icons.Default.VideoFile get() = Icons.Default.PlayArrow
private val Icons.Default.AudioFile get() = Icons.Default.MusicNote
private val Icons.Default.AttachFile get() = Icons.Default.Attachment
private val Icons.Default.Functions get() = Icons.Default.Calculate
private val Icons.Default.AccountTree get() = Icons.Default.AccountTree

@Composable
private fun VerticalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    )
}

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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            label()
        }
    }
}