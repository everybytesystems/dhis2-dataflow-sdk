package com.everybytesystems.dataflow.ui.dhis2

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

/**
 * DHIS2 User Management Components
 * Comprehensive user administration and management
 */

// ============================================================================
// 👤 USER MODELS
// ============================================================================

data class User(
    val id: String,
    val username: String,
    val firstName: String,
    val surname: String,
    val displayName: String = "$firstName $surname",
    val email: String? = null,
    val phoneNumber: String? = null,
    val jobTitle: String? = null,
    val introduction: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val nationality: String? = null,
    val employer: String? = null,
    val education: String? = null,
    val interests: String? = null,
    val languages: String? = null,
    val avatar: String? = null,
    val whatsApp: String? = null,
    val facebookMessenger: String? = null,
    val skype: String? = null,
    val telegram: String? = null,
    val twitter: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val lastLogin: String? = null,
    val disabled: Boolean = false,
    val accountExpiry: String? = null,
    val passwordLastUpdated: String? = null,
    val twoFA: Boolean = false,
    val externalAuth: Boolean = false,
    val openId: String? = null,
    val ldapId: String? = null,
    val userRoles: List<String> = emptyList(),
    val userGroups: List<String> = emptyList(),
    val organisationUnits: List<String> = emptyList(),
    val dataViewOrganisationUnits: List<String> = emptyList(),
    val teiSearchOrganisationUnits: List<String> = emptyList(),
    val programs: List<String> = emptyList(),
    val dataSets: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap()
)

data class UserRole(
    val id: String,
    val name: String,
    val displayName: String = name,
    val description: String? = null,
    val authorities: List<String> = emptyList(),
    val dataSets: List<String> = emptyList(),
    val programs: List<String> = emptyList(),
    val users: List<String> = emptyList()
)

data class UserGroup(
    val id: String,
    val name: String,
    val displayName: String = name,
    val code: String? = null,
    val description: String? = null,
    val users: List<String> = emptyList(),
    val managedGroups: List<String> = emptyList(),
    val managedByGroups: List<String> = emptyList()
)

enum class UserStatus {
    ACTIVE,
    DISABLED,
    EXPIRED,
    LOCKED,
    PENDING
}

// ============================================================================
// 👥 USER LIST COMPONENT
// ============================================================================

@Composable
fun UserList(
    users: List<User>,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    onUserEdit: ((User) -> Unit)? = null,
    onUserDelete: ((User) -> Unit)? = null,
    onUserEnable: ((User) -> Unit)? = null,
    onUserDisable: ((User) -> Unit)? = null,
    showSearch: Boolean = true,
    showFilters: Boolean = true,
    showActions: Boolean = true
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<UserStatus?>(null) }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf("displayName") }
    var sortAscending by remember { mutableStateOf(true) }
    
    val filteredUsers = remember(users, searchQuery, selectedStatus, selectedRole) {
        users.filter { user ->
            val matchesSearch = searchQuery.isEmpty() || 
                user.displayName.contains(searchQuery, ignoreCase = true) ||
                user.username.contains(searchQuery, ignoreCase = true) ||
                user.email?.contains(searchQuery, ignoreCase = true) == true
            
            val matchesStatus = selectedStatus == null || getUserStatus(user) == selectedStatus
            val matchesRole = selectedRole == null || user.userRoles.contains(selectedRole)
            
            matchesSearch && matchesStatus && matchesRole
        }.let { filtered ->
            when (sortBy) {
                "displayName" -> if (sortAscending) filtered.sortedBy { it.displayName } else filtered.sortedByDescending { it.displayName }
                "username" -> if (sortAscending) filtered.sortedBy { it.username } else filtered.sortedByDescending { it.username }
                "lastLogin" -> if (sortAscending) filtered.sortedBy { it.lastLogin } else filtered.sortedByDescending { it.lastLogin }
                "created" -> if (sortAscending) filtered.sortedBy { it.created } else filtered.sortedByDescending { it.created }
                else -> filtered
            }
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Search and filters
        if (showSearch || showFilters) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search bar
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search users...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = if (searchQuery.isNotEmpty()) {
                                {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear"
                                        )
                                    }
                                }
                            } else null,
                            singleLine = true
                        )
                    }
                    
                    // Filters and sorting
                    if (showFilters) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Status filter
                            item {
                                var statusExpanded by remember { mutableStateOf(false) }
                                
                                FilterChip(
                                    onClick = { statusExpanded = true },
                                    label = { 
                                        Text(selectedStatus?.name ?: "All Status") 
                                    },
                                    selected = selectedStatus != null,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                                
                                DropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Status") },
                                        onClick = {
                                            selectedStatus = null
                                            statusExpanded = false
                                        }
                                    )
                                    UserStatus.values().forEach { status ->
                                        DropdownMenuItem(
                                            text = { Text(status.name) },
                                            onClick = {
                                                selectedStatus = status
                                                statusExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            
                            // Sort options
                            item {
                                var sortExpanded by remember { mutableStateOf(false) }
                                
                                FilterChip(
                                    onClick = { sortExpanded = true },
                                    label = { 
                                        Text("Sort: $sortBy ${if (sortAscending) "↑" else "↓"}") 
                                    },
                                    selected = true
                                )
                                
                                DropdownMenu(
                                    expanded = sortExpanded,
                                    onDismissRequest = { sortExpanded = false }
                                ) {
                                    listOf("displayName", "username", "lastLogin", "created").forEach { option ->
                                        DropdownMenuItem(
                                            text = { 
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(option)
                                                    if (sortBy == option) {
                                                        Icon(
                                                            imageVector = if (sortAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                if (sortBy == option) {
                                                    sortAscending = !sortAscending
                                                } else {
                                                    sortBy = option
                                                    sortAscending = true
                                                }
                                                sortExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Results summary
                        Text(
                            text = "${filteredUsers.size} users found",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // User list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(filteredUsers) { user ->
                UserListItem(
                    user = user,
                    onClick = { onUserClick(user) },
                    onEdit = if (onUserEdit != null) { { onUserEdit(user) } } else null,
                    onDelete = if (onUserDelete != null) { { onUserDelete(user) } } else null,
                    onEnable = if (onUserEnable != null && user.disabled) { { onUserEnable(user) } } else null,
                    onDisable = if (onUserDisable != null && !user.disabled) { { onUserDisable(user) } } else null,
                    showActions = showActions
                )
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onClick: () -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    onEnable: (() -> Unit)?,
    onDisable: (() -> Unit)?,
    showActions: Boolean
) {
    var showActionsMenu by remember { mutableStateOf(false) }
    val userStatus = getUserStatus(user)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (userStatus) {
                UserStatus.DISABLED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                UserStatus.EXPIRED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                UserStatus.PENDING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User avatar
            UserAvatar(
                user = user,
                size = 48.dp
            )
            
            // User info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = user.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "@${user.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (user.email != null) {
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status chip
                    AssistChip(
                        onClick = { },
                        label = { 
                            Text(
                                text = userStatus.name,
                                style = MaterialTheme.typography.labelSmall
                            ) 
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getUserStatusColor(userStatus).copy(alpha = 0.2f),
                            labelColor = getUserStatusColor(userStatus)
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                    
                    // Last login
                    if (user.lastLogin != null) {
                        Text(
                            text = "Last login: ${user.lastLogin}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Actions menu
            if (showActions) {
                Box {
                    IconButton(
                        onClick = { showActionsMenu = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More actions"
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showActionsMenu,
                        onDismissRequest = { showActionsMenu = false }
                    ) {
                        if (onEdit != null) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    onEdit()
                                    showActionsMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        
                        if (onEnable != null) {
                            DropdownMenuItem(
                                text = { Text("Enable") },
                                onClick = {
                                    onEnable()
                                    showActionsMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        
                        if (onDisable != null) {
                            DropdownMenuItem(
                                text = { Text("Disable") },
                                onClick = {
                                    onDisable()
                                    showActionsMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        
                        if (onDelete != null) {
                            Divider()
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        text = "Delete",
                                        color = MaterialTheme.colorScheme.error
                                    ) 
                                },
                                onClick = {
                                    onDelete()
                                    showActionsMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// 👤 USER DETAILS COMPONENT
// ============================================================================

@Composable
fun UserDetails(
    user: User,
    userRoles: List<UserRole>,
    userGroups: List<UserGroup>,
    modifier: Modifier = Modifier,
    onEditClick: (() -> Unit)? = null,
    showRoles: Boolean = true,
    showGroups: Boolean = true,
    showOrganisationUnits: Boolean = true,
    showPrograms: Boolean = true,
    showDataSets: Boolean = true
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UserAvatar(
                                user = user,
                                size = 80.dp
                            )
                            
                            Column {
                                Text(
                                    text = user.displayName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                if (user.jobTitle != null) {
                                    Text(
                                        text = user.jobTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                // Status
                                AssistChip(
                                    onClick = { },
                                    label = { 
                                        Text(getUserStatus(user).name) 
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = getUserStatusColor(getUserStatus(user)).copy(alpha = 0.2f),
                                        labelColor = getUserStatusColor(getUserStatus(user))
                                    )
                                )
                            }
                        }
                        
                        if (onEditClick != null) {
                            IconButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit user"
                                )
                            }
                        }
                    }
                    
                    if (user.introduction != null) {
                        Text(
                            text = user.introduction,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
        
        // Basic Information
        item {
            UserInfoSection(
                title = "Basic Information",
                icon = Icons.Default.Person
            ) {
                UserInfoItem("ID", user.id)
                UserInfoItem("Username", user.username)
                UserInfoItem("First Name", user.firstName)
                UserInfoItem("Surname", user.surname)
                user.email?.let { UserInfoItem("Email", it) }
                user.phoneNumber?.let { UserInfoItem("Phone", it) }
                user.gender?.let { UserInfoItem("Gender", it) }
                user.birthday?.let { UserInfoItem("Birthday", it) }
                user.nationality?.let { UserInfoItem("Nationality", it) }
                user.employer?.let { UserInfoItem("Employer", it) }
                user.education?.let { UserInfoItem("Education", it) }
                user.interests?.let { UserInfoItem("Interests", it) }
                user.languages?.let { UserInfoItem("Languages", it) }
            }
        }
        
        // Contact Information
        if (user.whatsApp != null || user.facebookMessenger != null || 
            user.skype != null || user.telegram != null || user.twitter != null) {
            item {
                UserInfoSection(
                    title = "Social & Messaging",
                    icon = Icons.Default.Chat
                ) {
                    user.whatsApp?.let { UserInfoItem("WhatsApp", it) }
                    user.facebookMessenger?.let { UserInfoItem("Facebook Messenger", it) }
                    user.skype?.let { UserInfoItem("Skype", it) }
                    user.telegram?.let { UserInfoItem("Telegram", it) }
                    user.twitter?.let { UserInfoItem("Twitter", it) }
                }
            }
        }
        
        // Account Information
        item {
            UserInfoSection(
                title = "Account Information",
                icon = Icons.Default.AccountCircle
            ) {
                user.created?.let { UserInfoItem("Created", it) }
                user.lastUpdated?.let { UserInfoItem("Last Updated", it) }
                user.lastLogin?.let { UserInfoItem("Last Login", it) }
                user.passwordLastUpdated?.let { UserInfoItem("Password Last Updated", it) }
                user.accountExpiry?.let { UserInfoItem("Account Expiry", it) }
                UserInfoItem("Disabled", if (user.disabled) "Yes" else "No")
                UserInfoItem("Two-Factor Authentication", if (user.twoFA) "Enabled" else "Disabled")
                UserInfoItem("External Authentication", if (user.externalAuth) "Yes" else "No")
                user.openId?.let { UserInfoItem("OpenID", it) }
                user.ldapId?.let { UserInfoItem("LDAP ID", it) }
            }
        }
        
        // User Roles
        if (showRoles && user.userRoles.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "User Roles (${user.userRoles.size})",
                    icon = Icons.Default.Security
                ) {
                    user.userRoles.forEach { roleId ->
                        val role = userRoles.find { it.id == roleId }
                        UserRoleItem(
                            roleId = roleId,
                            roleName = role?.displayName ?: roleId,
                            roleDescription = role?.description
                        )
                    }
                }
            }
        }
        
        // User Groups
        if (showGroups && user.userGroups.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "User Groups (${user.userGroups.size})",
                    icon = Icons.Default.Group
                ) {
                    user.userGroups.forEach { groupId ->
                        val group = userGroups.find { it.id == groupId }
                        UserGroupItem(
                            groupId = groupId,
                            groupName = group?.displayName ?: groupId,
                            groupDescription = group?.description
                        )
                    }
                }
            }
        }
        
        // Organisation Units
        if (showOrganisationUnits && user.organisationUnits.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "Organisation Units (${user.organisationUnits.size})",
                    icon = Icons.Default.Business
                ) {
                    user.organisationUnits.forEach { orgUnitId ->
                        UserInfoItem("Organisation Unit", orgUnitId)
                    }
                }
            }
        }
        
        // Data View Organisation Units
        if (showOrganisationUnits && user.dataViewOrganisationUnits.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "Data View Organisation Units (${user.dataViewOrganisationUnits.size})",
                    icon = Icons.Default.Visibility
                ) {
                    user.dataViewOrganisationUnits.forEach { orgUnitId ->
                        UserInfoItem("Data View Org Unit", orgUnitId)
                    }
                }
            }
        }
        
        // Programs
        if (showPrograms && user.programs.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "Programs (${user.programs.size})",
                    icon = Icons.Default.Assignment
                ) {
                    user.programs.forEach { programId ->
                        UserInfoItem("Program", programId)
                    }
                }
            }
        }
        
        // Data Sets
        if (showDataSets && user.dataSets.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "Data Sets (${user.dataSets.size})",
                    icon = Icons.Default.Dataset
                ) {
                    user.dataSets.forEach { dataSetId ->
                        UserInfoItem("Data Set", dataSetId)
                    }
                }
            }
        }
        
        // Metadata
        if (user.metadata.isNotEmpty()) {
            item {
                UserInfoSection(
                    title = "Additional Metadata",
                    icon = Icons.Default.MoreHoriz
                ) {
                    user.metadata.forEach { (key, value) ->
                        UserInfoItem(key, value.toString())
                    }
                }
            }
        }
    }
}

// ============================================================================
// 👤 USER AVATAR COMPONENT
// ============================================================================

@Composable
fun UserAvatar(
    user: User,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null
) {
    val initials = remember(user) {
        "${user.firstName.firstOrNull()?.uppercase() ?: ""}${user.surname.firstOrNull()?.uppercase() ?: ""}"
    }
    
    val backgroundColor = remember(user.id) {
        val colors = listOf(
            Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFFF9800),
            Color(0xFF9C27B0), Color(0xFFF44336), Color(0xFF607D8B),
            Color(0xFF795548), Color(0xFF009688), Color(0xFFE91E63)
        )
        colors[user.id.hashCode().absoluteValue % colors.size]
    }
    
    Card(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (user.avatar != null) {
                // In a real implementation, this would load the avatar image
                // For now, show initials
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ============================================================================
// 🔧 HELPER COMPONENTS
// ============================================================================

@Composable
private fun UserInfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun UserInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
private fun UserRoleItem(
    roleId: String,
    roleName: String,
    roleDescription: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = roleName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (roleDescription != null) {
                Text(
                    text = roleDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "ID: $roleId",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UserGroupItem(
    groupId: String,
    groupName: String,
    groupDescription: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = groupName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            if (groupDescription != null) {
                Text(
                    text = groupDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "ID: $groupId",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 🔧 UTILITY FUNCTIONS
// ============================================================================

private fun getUserStatus(user: User): UserStatus {
    return when {
        user.disabled -> UserStatus.DISABLED
        user.accountExpiry != null -> {
            // In real implementation, would check if account is expired
            UserStatus.EXPIRED
        }
        user.lastLogin == null -> UserStatus.PENDING
        else -> UserStatus.ACTIVE
    }
}

private fun getUserStatusColor(status: UserStatus): Color {
    return when (status) {
        UserStatus.ACTIVE -> Color(0xFF4CAF50)
        UserStatus.DISABLED -> Color(0xFFF44336)
        UserStatus.EXPIRED -> Color(0xFFFF9800)
        UserStatus.LOCKED -> Color(0xFF9C27B0)
        UserStatus.PENDING -> Color(0xFF2196F3)
    }
}

private val Int.absoluteValue: Int
    get() = if (this < 0) -this else this