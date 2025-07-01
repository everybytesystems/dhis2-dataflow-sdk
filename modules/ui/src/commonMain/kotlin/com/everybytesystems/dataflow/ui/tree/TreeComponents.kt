package com.everybytesystems.dataflow.ui.tree

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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*

/**
 * Tree View Components
 * Hierarchical navigation and data visualization with expandable nodes
 */

// ============================================================================
// 🌳 TREE DATA MODELS
// ============================================================================

data class TreeNode<T>(
    val id: String,
    val data: T,
    val label: String,
    val icon: ImageVector? = null,
    val children: MutableList<TreeNode<T>> = mutableListOf(),
    val parent: TreeNode<T>? = null,
    val isExpanded: Boolean = false,
    val isSelected: Boolean = false,
    val isEnabled: Boolean = true,
    val isVisible: Boolean = true,
    val level: Int = 0,
    val nodeType: TreeNodeType = TreeNodeType.BRANCH,
    val metadata: Map<String, Any> = emptyMap(),
    val customData: Any? = null
) {
    val hasChildren: Boolean get() = children.isNotEmpty()
    val isLeaf: Boolean get() = children.isEmpty()
    val isRoot: Boolean get() = parent == null
    val path: List<String> get() = generatePath()
    val childCount: Int get() = children.size
    val descendantCount: Int get() = children.sumOf { 1 + it.descendantCount }
    
    private fun generatePath(): List<String> {
        val path = mutableListOf<String>()
        var current: TreeNode<T>? = this
        while (current != null) {
            path.add(0, current.id)
            current = current.parent
        }
        return path
    }
    
    fun addChild(child: TreeNode<T>): TreeNode<T> {
        val childWithParent = child.copy(
            parent = this,
            level = this.level + 1
        )
        children.add(childWithParent)
        return childWithParent
    }
    
    fun removeChild(childId: String): Boolean {
        return children.removeIf { it.id == childId }
    }
    
    fun findNode(id: String): TreeNode<T>? {
        if (this.id == id) return this
        for (child in children) {
            val found = child.findNode(id)
            if (found != null) return found
        }
        return null
    }
    
    fun getAllNodes(): List<TreeNode<T>> {
        val nodes = mutableListOf<TreeNode<T>>()
        nodes.add(this)
        children.forEach { child ->
            nodes.addAll(child.getAllNodes())
        }
        return nodes
    }
    
    fun getVisibleNodes(): List<TreeNode<T>> {
        val nodes = mutableListOf<TreeNode<T>>()
        if (isVisible) {
            nodes.add(this)
            if (isExpanded) {
                children.forEach { child ->
                    nodes.addAll(child.getVisibleNodes())
                }
            }
        }
        return nodes
    }
}

data class TreeState<T>(
    val rootNodes: List<TreeNode<T>> = emptyList(),
    val selectedNodes: Set<String> = emptySet(),
    val expandedNodes: Set<String> = emptySet(),
    val searchQuery: String = "",
    val filteredNodes: List<TreeNode<T>> = emptyList(),
    val selectionMode: TreeSelectionMode = TreeSelectionMode.SINGLE,
    val showRoot: Boolean = true,
    val showLines: Boolean = true,
    val showIcons: Boolean = true,
    val allowMultiSelection: Boolean = false,
    val allowDragDrop: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

// ============================================================================
// 🌳 ENUMS
// ============================================================================

enum class TreeNodeType {
    ROOT, BRANCH, LEAF, SEPARATOR, HEADER, FOOTER
}

enum class TreeSelectionMode {
    NONE, SINGLE, MULTIPLE, CHECKBOX
}

enum class TreeViewStyle {
    STANDARD, COMPACT, DETAILED, CARD, OUTLINE
}

enum class TreeLineStyle {
    SOLID, DASHED, DOTTED, NONE
}

enum class TreeExpandMode {
    CLICK, DOUBLE_CLICK, ICON_ONLY, HOVER
}

enum class TreeSortOrder {
    NONE, ALPHABETICAL, REVERSE_ALPHABETICAL, CUSTOM
}

// ============================================================================
// 🌳 MAIN TREE VIEW COMPONENT
// ============================================================================

@Composable
fun <T> TreeView(
    rootNodes: List<TreeNode<T>>,
    modifier: Modifier = Modifier,
    style: TreeViewStyle = TreeViewStyle.STANDARD,
    selectionMode: TreeSelectionMode = TreeSelectionMode.SINGLE,
    showRoot: Boolean = true,
    showLines: Boolean = true,
    showIcons: Boolean = true,
    expandMode: TreeExpandMode = TreeExpandMode.CLICK,
    lineStyle: TreeLineStyle = TreeLineStyle.SOLID,
    sortOrder: TreeSortOrder = TreeSortOrder.NONE,
    searchable: Boolean = false,
    onNodeClick: ((TreeNode<T>) -> Unit)? = null,
    onNodeDoubleClick: ((TreeNode<T>) -> Unit)? = null,
    onNodeExpand: ((TreeNode<T>) -> Unit)? = null,
    onNodeCollapse: ((TreeNode<T>) -> Unit)? = null,
    onSelectionChange: ((Set<String>) -> Unit)? = null,
    onNodeDrop: ((TreeNode<T>, TreeNode<T>) -> Unit)? = null,
    nodeContent: @Composable ((TreeNode<T>) -> Unit)? = null
) {
    var treeState by remember {
        mutableStateOf(
            TreeState(
                rootNodes = rootNodes,
                selectionMode = selectionMode,
                showRoot = showRoot,
                showLines = showLines,
                showIcons = showIcons
            )
        )
    }
    
    var searchQuery by remember { mutableStateOf("") }
    
    // Update tree state when root nodes change
    LaunchedEffect(rootNodes) {
        treeState = treeState.copy(rootNodes = rootNodes)
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search bar
        if (searchable) {
            TreeSearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    searchQuery = query
                    treeState = treeState.copy(searchQuery = query)
                },
                onClear = {
                    searchQuery = ""
                    treeState = treeState.copy(searchQuery = "")
                }
            )
        }
        
        // Tree content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val visibleNodes = getVisibleNodes(treeState, searchQuery)
            
            items(visibleNodes, key = { it.id }) { node ->
                TreeNodeItem(
                    node = node,
                    style = style,
                    showLines = showLines,
                    showIcons = showIcons,
                    lineStyle = lineStyle,
                    isSelected = node.id in treeState.selectedNodes,
                    selectionMode = selectionMode,
                    expandMode = expandMode,
                    onClick = { clickedNode ->
                        handleNodeClick(
                            node = clickedNode,
                            treeState = treeState,
                            selectionMode = selectionMode,
                            onStateChange = { newState -> treeState = newState },
                            onNodeClick = onNodeClick,
                            onSelectionChange = onSelectionChange
                        )
                    },
                    onDoubleClick = onNodeDoubleClick,
                    onExpandToggle = { nodeToToggle ->
                        handleExpandToggle(
                            node = nodeToToggle,
                            treeState = treeState,
                            onStateChange = { newState -> treeState = newState },
                            onNodeExpand = onNodeExpand,
                            onNodeCollapse = onNodeCollapse
                        )
                    },
                    customContent = nodeContent
                )
            }
            
            if (visibleNodes.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    EmptySearchResults(query = searchQuery)
                }
            }
        }
    }
}

// ============================================================================
// 🌳 TREE NODE ITEM
// ============================================================================

@Composable
fun <T> TreeNodeItem(
    node: TreeNode<T>,
    style: TreeViewStyle,
    showLines: Boolean,
    showIcons: Boolean,
    lineStyle: TreeLineStyle,
    isSelected: Boolean,
    selectionMode: TreeSelectionMode,
    expandMode: TreeExpandMode,
    onClick: (TreeNode<T>) -> Unit,
    onDoubleClick: ((TreeNode<T>) -> Unit)?,
    onExpandToggle: (TreeNode<T>) -> Unit,
    customContent: (@Composable (TreeNode<T>) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val indentWidth = 24.dp
    val totalIndent = indentWidth * node.level
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = totalIndent)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .clickable(enabled = node.isEnabled) {
                when (expandMode) {
                    TreeExpandMode.CLICK -> {
                        if (node.hasChildren) {
                            onExpandToggle(node)
                        }
                        onClick(node)
                    }
                    TreeExpandMode.DOUBLE_CLICK -> {
                        onClick(node)
                    }
                    TreeExpandMode.ICON_ONLY -> {
                        onClick(node)
                    }
                    TreeExpandMode.HOVER -> {
                        onClick(node)
                    }
                }
            }
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Connection lines
        if (showLines && node.level > 0) {
            TreeConnectionLines(
                node = node,
                lineStyle = lineStyle,
                modifier = Modifier.size(16.dp)
            )
        }
        
        // Expand/collapse icon
        if (node.hasChildren) {
            IconButton(
                onClick = { onExpandToggle(node) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (node.isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (node.isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
        
        // Selection checkbox
        if (selectionMode == TreeSelectionMode.CHECKBOX) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick(node) },
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Node icon
        if (showIcons) {
            val nodeIcon = node.icon ?: when (node.nodeType) {
                TreeNodeType.ROOT -> Icons.Default.AccountTree
                TreeNodeType.BRANCH -> if (node.isExpanded) Icons.Default.FolderOpen else Icons.Default.Folder
                TreeNodeType.LEAF -> Icons.Default.Description
                TreeNodeType.SEPARATOR -> Icons.Default.Remove
                TreeNodeType.HEADER -> Icons.Default.Title
                TreeNodeType.FOOTER -> Icons.Default.Notes
            }
            
            Icon(
                imageVector = nodeIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = when {
                    !node.isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        
        // Node content
        if (customContent != null) {
            customContent(node)
        } else {
            when (style) {
                TreeViewStyle.STANDARD -> StandardNodeContent(node, isSelected)
                TreeViewStyle.COMPACT -> CompactNodeContent(node, isSelected)
                TreeViewStyle.DETAILED -> DetailedNodeContent(node, isSelected)
                TreeViewStyle.CARD -> CardNodeContent(node, isSelected)
                TreeViewStyle.OUTLINE -> OutlineNodeContent(node, isSelected)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Node metadata indicators
        if (node.metadata.isNotEmpty()) {
            NodeMetadataIndicators(
                metadata = node.metadata,
                modifier = Modifier.padding(end = 4.dp)
            )
        }
    }
}

// ============================================================================
// 🌳 NODE CONTENT STYLES
// ============================================================================

@Composable
fun <T> StandardNodeContent(
    node: TreeNode<T>,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = node.label,
        style = MaterialTheme.typography.bodyMedium,
        color = when {
            !node.isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        },
        fontWeight = if (node.hasChildren) FontWeight.Medium else FontWeight.Normal,
        modifier = modifier
    )
}

@Composable
fun <T> CompactNodeContent(
    node: TreeNode<T>,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = node.label,
        style = MaterialTheme.typography.bodySmall,
        color = when {
            !node.isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun <T> DetailedNodeContent(
    node: TreeNode<T>,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = node.label,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                !node.isEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (node.hasChildren) FontWeight.Medium else FontWeight.Normal
        )
        
        if (node.hasChildren) {
            Text(
                text = "${node.childCount} items",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun <T> CardNodeContent(
    node: TreeNode<T>,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = node.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (node.hasChildren) {
                Text(
                    text = "${node.childCount} items • ${node.descendantCount} total",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun <T> OutlineNodeContent(
    node: TreeNode<T>,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = node.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (node.hasChildren) FontWeight.Medium else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            if (node.hasChildren) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = node.childCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

// ============================================================================
// 🌳 HELPER COMPONENTS
// ============================================================================

@Composable
fun TreeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search tree...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun <T> TreeConnectionLines(
    node: TreeNode<T>,
    lineStyle: TreeLineStyle,
    modifier: Modifier = Modifier
) {
    if (lineStyle == TreeLineStyle.NONE) return
    
    Canvas(modifier = modifier) {
        val strokeWidth = 1.dp.toPx()
        val pathEffect = when (lineStyle) {
            TreeLineStyle.DASHED -> PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
            TreeLineStyle.DOTTED -> PathEffect.dashPathEffect(floatArrayOf(2f, 4f))
            else -> null
        }
        
        // Vertical line from parent
        if (node.parent != null) {
            drawLine(
                color = Color.Gray,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height / 2),
                strokeWidth = strokeWidth,
                pathEffect = pathEffect
            )
        }
        
        // Horizontal line to node
        drawLine(
            color = Color.Gray,
            start = Offset(size.width / 2, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth,
            pathEffect = pathEffect
        )
        
        // Vertical line to children (if expanded and has children)
        if (node.isExpanded && node.hasChildren) {
            drawLine(
                color = Color.Gray,
                start = Offset(size.width / 2, size.height / 2),
                end = Offset(size.width / 2, size.height),
                strokeWidth = strokeWidth,
                pathEffect = pathEffect
            )
        }
    }
}

@Composable
fun NodeMetadataIndicators(
    metadata: Map<String, Any>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        metadata.forEach { (key, value) ->
            when (key) {
                "badge" -> {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                "warning" -> {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Warning",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                "info" -> {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                "lock" -> {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Locked",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySearchResults(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "No results found",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "No items match \"$query\"",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ============================================================================
// 🌳 SPECIALIZED TREE COMPONENTS
// ============================================================================

@Composable
fun FileTreeView(
    rootPath: String,
    files: List<FileTreeNode>,
    modifier: Modifier = Modifier,
    onFileClick: ((FileTreeNode) -> Unit)? = null,
    onFolderClick: ((FileTreeNode) -> Unit)? = null
) {
    val treeNodes = remember(files) {
        buildFileTree(files, rootPath)
    }
    
    TreeView(
        rootNodes = treeNodes,
        modifier = modifier,
        style = TreeViewStyle.STANDARD,
        showIcons = true,
        searchable = true,
        onNodeClick = { node ->
            val fileNode = node.data as FileTreeNode
            if (fileNode.isDirectory) {
                onFolderClick?.invoke(fileNode)
            } else {
                onFileClick?.invoke(fileNode)
            }
        }
    )
}

@Composable
fun OrganizationTreeView(
    organization: OrganizationNode,
    modifier: Modifier = Modifier,
    onEmployeeClick: ((Employee) -> Unit)? = null,
    onDepartmentClick: ((Department) -> Unit)? = null
) {
    val treeNodes = remember(organization) {
        buildOrganizationTree(organization)
    }
    
    TreeView(
        rootNodes = listOf(treeNodes),
        modifier = modifier,
        style = TreeViewStyle.DETAILED,
        selectionMode = TreeSelectionMode.SINGLE,
        showIcons = true,
        onNodeClick = { node ->
            when (val data = node.data) {
                is Employee -> onEmployeeClick?.invoke(data)
                is Department -> onDepartmentClick?.invoke(data)
            }
        },
        nodeContent = { node ->
            when (val data = node.data) {
                is Employee -> EmployeeNodeContent(data)
                is Department -> DepartmentNodeContent(data)
            }
        }
    )
}

@Composable
fun NavigationTreeView(
    navigationItems: List<NavigationItem>,
    modifier: Modifier = Modifier,
    selectedItemId: String? = null,
    onItemClick: ((NavigationItem) -> Unit)? = null
) {
    val treeNodes = remember(navigationItems) {
        buildNavigationTree(navigationItems)
    }
    
    TreeView(
        rootNodes = treeNodes,
        modifier = modifier,
        style = TreeViewStyle.OUTLINE,
        selectionMode = TreeSelectionMode.SINGLE,
        showLines = false,
        onNodeClick = { node ->
            val navItem = node.data as NavigationItem
            onItemClick?.invoke(navItem)
        }
    )
}

// ============================================================================
// 🌳 DATA MODELS FOR SPECIALIZED TREES
// ============================================================================

data class FileTreeNode(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = 0,
    val extension: String = "",
    val permissions: String = ""
)

data class Employee(
    val id: String,
    val name: String,
    val title: String,
    val email: String,
    val department: String,
    val avatar: String? = null,
    val isManager: Boolean = false
)

data class Department(
    val id: String,
    val name: String,
    val description: String,
    val manager: Employee? = null,
    val employeeCount: Int = 0
)

data class OrganizationNode(
    val department: Department,
    val employees: List<Employee> = emptyList(),
    val subDepartments: List<OrganizationNode> = emptyList()
)

data class NavigationItem(
    val id: String,
    val label: String,
    val icon: ImageVector? = null,
    val route: String = "",
    val badge: String? = null,
    val isEnabled: Boolean = true,
    val children: List<NavigationItem> = emptyList()
)

// ============================================================================
// 🌳 SPECIALIZED NODE CONTENT
// ============================================================================

@Composable
fun EmployeeNodeContent(
    employee: Employee,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Avatar placeholder
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = employee.name.take(2).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.wrapContentSize(Alignment.Center)
            )
        }
        
        Column {
            Text(
                text = employee.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = employee.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (employee.isManager) {
            Icon(
                Icons.Default.Star,
                contentDescription = "Manager",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DepartmentNodeContent(
    department: Department,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = department.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (department.description.isNotEmpty()) {
                Text(
                    text = department.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = "${department.employeeCount}",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

// ============================================================================
// 🌳 UTILITY FUNCTIONS
// ============================================================================

private fun <T> getVisibleNodes(
    treeState: TreeState<T>,
    searchQuery: String
): List<TreeNode<T>> {
    val allNodes = treeState.rootNodes.flatMap { it.getVisibleNodes() }
    
    return if (searchQuery.isBlank()) {
        allNodes
    } else {
        allNodes.filter { node ->
            node.label.contains(searchQuery, ignoreCase = true)
        }
    }
}

private fun <T> handleNodeClick(
    node: TreeNode<T>,
    treeState: TreeState<T>,
    selectionMode: TreeSelectionMode,
    onStateChange: (TreeState<T>) -> Unit,
    onNodeClick: ((TreeNode<T>) -> Unit)?,
    onSelectionChange: ((Set<String>) -> Unit)?
) {
    val newSelectedNodes = when (selectionMode) {
        TreeSelectionMode.NONE -> emptySet()
        TreeSelectionMode.SINGLE -> setOf(node.id)
        TreeSelectionMode.MULTIPLE, TreeSelectionMode.CHECKBOX -> {
            if (node.id in treeState.selectedNodes) {
                treeState.selectedNodes - node.id
            } else {
                treeState.selectedNodes + node.id
            }
        }
    }
    
    onStateChange(treeState.copy(selectedNodes = newSelectedNodes))
    onSelectionChange?.invoke(newSelectedNodes)
    onNodeClick?.invoke(node)
}

private fun <T> handleExpandToggle(
    node: TreeNode<T>,
    treeState: TreeState<T>,
    onStateChange: (TreeState<T>) -> Unit,
    onNodeExpand: ((TreeNode<T>) -> Unit)?,
    onNodeCollapse: ((TreeNode<T>) -> Unit)?
) {
    val newExpandedNodes = if (node.id in treeState.expandedNodes) {
        treeState.expandedNodes - node.id
    } else {
        treeState.expandedNodes + node.id
    }
    
    onStateChange(treeState.copy(expandedNodes = newExpandedNodes))
    
    if (node.id in newExpandedNodes) {
        onNodeExpand?.invoke(node)
    } else {
        onNodeCollapse?.invoke(node)
    }
}

private fun buildFileTree(files: List<FileTreeNode>, rootPath: String): List<TreeNode<FileTreeNode>> {
    // Simplified file tree building - in a real implementation, this would be more complex
    return files.map { file ->
        TreeNode(
            id = file.path,
            data = file,
            label = file.name,
            icon = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
            nodeType = if (file.isDirectory) TreeNodeType.BRANCH else TreeNodeType.LEAF
        )
    }
}

private fun buildOrganizationTree(organization: OrganizationNode): TreeNode<Any> {
    val departmentNode = TreeNode(
        id = organization.department.id,
        data = organization.department,
        label = organization.department.name,
        icon = Icons.Default.Business,
        nodeType = TreeNodeType.BRANCH
    )
    
    // Add employees
    organization.employees.forEach { employee ->
        departmentNode.addChild(
            TreeNode(
                id = employee.id,
                data = employee,
                label = employee.name,
                icon = Icons.Default.Person,
                nodeType = TreeNodeType.LEAF
            )
        )
    }
    
    // Add sub-departments recursively
    organization.subDepartments.forEach { subDept ->
        departmentNode.addChild(buildOrganizationTree(subDept))
    }
    
    return departmentNode
}

private fun buildNavigationTree(navigationItems: List<NavigationItem>): List<TreeNode<NavigationItem>> {
    return navigationItems.map { item ->
        val node = TreeNode(
            id = item.id,
            data = item,
            label = item.label,
            icon = item.icon,
            nodeType = if (item.children.isNotEmpty()) TreeNodeType.BRANCH else TreeNodeType.LEAF,
            metadata = if (item.badge != null) mapOf("badge" to item.badge) else emptyMap()
        )
        
        // Add children recursively
        item.children.forEach { child ->
            node.addChild(buildNavigationTree(listOf(child)).first())
        }
        
        node
    }
}