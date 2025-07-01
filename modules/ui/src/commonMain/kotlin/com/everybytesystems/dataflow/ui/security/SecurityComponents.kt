package com.everybytesystems.dataflow.ui.security

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import kotlinx.datetime.*
import kotlin.random.*

/**
 * Security & Privacy Components
 * Comprehensive security tools and privacy management
 */

// ============================================================================
// 📊 DATA MODELS
// ============================================================================

enum class SecurityLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class EncryptionType {
    NONE,
    AES_128,
    AES_256,
    RSA_2048,
    RSA_4096
}

enum class BiometricType {
    FINGERPRINT,
    FACE_ID,
    VOICE,
    IRIS,
    PALM
}

enum class PrivacyLevel {
    PUBLIC,
    INTERNAL,
    CONFIDENTIAL,
    RESTRICTED,
    TOP_SECRET
}

enum class DataCategory {
    PERSONAL_INFO,
    FINANCIAL,
    HEALTH,
    LOCATION,
    COMMUNICATIONS,
    DEVICE_INFO,
    USAGE_DATA,
    PREFERENCES
}

enum class ConsentStatus {
    NOT_REQUESTED,
    PENDING,
    GRANTED,
    DENIED,
    REVOKED,
    EXPIRED
}

data class SecuritySettings(
    val enableEncryption: Boolean = true,
    val encryptionType: EncryptionType = EncryptionType.AES_256,
    val enableBiometric: Boolean = false,
    val biometricTypes: Set<BiometricType> = emptySet(),
    val sessionTimeout: kotlin.time.Duration = kotlin.time.Duration.parse("PT30M"),
    val enableTwoFactor: Boolean = false,
    val requirePasswordChange: Boolean = false,
    val passwordExpiryDays: Int = 90,
    val enableAuditLog: Boolean = true,
    val enableSecureStorage: Boolean = true,
    val enableDataMasking: Boolean = false
)

data class PrivacyConsent(
    val id: String,
    val category: DataCategory,
    val purpose: String,
    val description: String,
    val status: ConsentStatus,
    val grantedAt: Instant? = null,
    val expiresAt: Instant? = null,
    val isRequired: Boolean = false,
    val dataRetentionDays: Int? = null
)

data class SecurityAuditEntry(
    val id: String,
    val timestamp: Instant,
    val userId: String,
    val action: String,
    val resource: String,
    val ipAddress: String,
    val userAgent: String,
    val success: Boolean,
    val riskLevel: SecurityLevel,
    val details: Map<String, Any> = emptyMap()
)

data class DataClassification(
    val level: PrivacyLevel,
    val categories: Set<DataCategory>,
    val retentionPeriod: kotlin.time.Duration?,
    val encryptionRequired: Boolean,
    val accessRestrictions: List<String>
)

data class SecurityThreat(
    val id: String,
    val type: String,
    val severity: SecurityLevel,
    val description: String,
    val detectedAt: Instant,
    val source: String,
    val isResolved: Boolean = false,
    val resolvedAt: Instant? = null,
    val mitigation: String? = null
)

// ============================================================================
// 🔐 SECURE AUTHENTICATION
// ============================================================================

@Composable
fun SecureLoginForm(
    onLogin: (String, String) -> Unit,
    onBiometricLogin: () -> Unit,
    modifier: Modifier = Modifier,
    enableBiometric: Boolean = true,
    enableTwoFactor: Boolean = false,
    securityLevel: SecurityLevel = SecurityLevel.MEDIUM
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var loginAttempts by remember { mutableStateOf(0) }
    var isLocked by remember { mutableStateOf(false) }
    var lockoutTime by remember { mutableStateOf<Instant?>(null) }
    
    // Auto-unlock after lockout period
    LaunchedEffect(lockoutTime) {
        lockoutTime?.let { lockTime ->
            val unlockTime = lockTime.plus(5.minutes)
            val now = Clock.System.now()
            if (now < unlockTime) {
                delay((unlockTime - now).inWholeMilliseconds)
                isLocked = false
                loginAttempts = 0
                lockoutTime = null
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Secure Login",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                SecurityLevelIndicator(level = securityLevel)
            }
            
            if (isLocked) {
                // Lockout message
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Account Temporarily Locked",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Too many failed attempts. Please try again in 5 minutes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Login form
                SecureTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    leadingIcon = Icons.Default.Person,
                    securityLevel = securityLevel,
                    modifier = Modifier.fillMaxWidth()
                )
                
                SecureTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    showPassword = showPassword,
                    onTogglePasswordVisibility = { showPassword = !showPassword },
                    securityLevel = securityLevel,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Login attempts warning
                if (loginAttempts > 0) {
                    Text(
                        text = "Failed attempts: $loginAttempts/5",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (loginAttempts >= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Login button
                Button(
                    onClick = {
                        if (username.isNotBlank() && password.isNotBlank()) {
                            isLoading = true
                            // Simulate login attempt
                            if (username == "admin" && password == "password") {
                                onLogin(username, password)
                                loginAttempts = 0
                            } else {
                                loginAttempts++
                                if (loginAttempts >= 5) {
                                    isLocked = true
                                    lockoutTime = Clock.System.now()
                                }
                            }
                            isLoading = false
                        }
                    },
                    enabled = !isLoading && username.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Login")
                    }
                }
                
                // Biometric login
                if (enableBiometric) {
                    OutlinedButton(
                        onClick = onBiometricLogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use Biometric")
                    }
                }
                
                // Two-factor authentication
                if (enableTwoFactor) {
                    TextButton(
                        onClick = { /* Navigate to 2FA */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Setup Two-Factor Authentication")
                    }
                }
            }
        }
    }
}

@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    securityLevel: SecurityLevel = SecurityLevel.MEDIUM,
    enableMasking: Boolean = false
) {
    val visualTransformation = when {
        isPassword && !showPassword -> PasswordVisualTransformation()
        enableMasking -> MaskingVisualTransformation()
        else -> VisualTransformation.None
    }
    
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = when (securityLevel) {
                        SecurityLevel.CRITICAL -> MaterialTheme.colorScheme.error
                        SecurityLevel.HIGH -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
            {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Hide password" else "Show password"
                    )
                }
            }
        } else null,
        visualTransformation = visualTransformation,
        keyboardOptions = if (isPassword) {
            KeyboardOptions(keyboardType = KeyboardType.Password)
        } else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = when (securityLevel) {
                SecurityLevel.CRITICAL -> MaterialTheme.colorScheme.error
                SecurityLevel.HIGH -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
    )
}

// ============================================================================
// 🔒 DATA ENCRYPTION MANAGER
// ============================================================================

@Composable
fun DataEncryptionManager(
    settings: SecuritySettings,
    onSettingsChange: (SecuritySettings) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Data Encryption",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Encryption Status
        item {
            EncryptionStatusCard(
                enabled = settings.enableEncryption,
                type = settings.encryptionType,
                onToggle = {
                    onSettingsChange(settings.copy(enableEncryption = it))
                }
            )
        }
        
        // Encryption Type Selector
        item {
            EncryptionTypeSelector(
                currentType = settings.encryptionType,
                enabled = settings.enableEncryption,
                onTypeChange = {
                    onSettingsChange(settings.copy(encryptionType = it))
                }
            )
        }
        
        // Secure Storage
        item {
            SecurityToggleCard(
                title = "Secure Storage",
                description = "Encrypt data stored on device",
                icon = Icons.Default.Storage,
                enabled = settings.enableSecureStorage,
                onToggle = {
                    onSettingsChange(settings.copy(enableSecureStorage = it))
                }
            )
        }
        
        // Data Masking
        item {
            SecurityToggleCard(
                title = "Data Masking",
                description = "Hide sensitive data in UI",
                icon = Icons.Default.VisibilityOff,
                enabled = settings.enableDataMasking,
                onToggle = {
                    onSettingsChange(settings.copy(enableDataMasking = it))
                }
            )
        }
        
        // Encryption Tools
        item {
            EncryptionToolsCard()
        }
    }
}

@Composable
private fun EncryptionStatusCard(
    enabled: Boolean,
    type: EncryptionType,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (enabled) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (enabled) "Encryption Enabled" else "Encryption Disabled",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (enabled) {
                        "Data protected with ${type.name} encryption"
                    } else {
                        "Data is not encrypted"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun EncryptionTypeSelector(
    currentType: EncryptionType,
    enabled: Boolean,
    onTypeChange: (EncryptionType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Encryption Algorithm",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            EncryptionType.values().forEach { type ->
                if (type != EncryptionType.NONE) {
                    EncryptionTypeOption(
                        type = type,
                        selected = currentType == type,
                        enabled = enabled,
                        onClick = { onTypeChange(type) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EncryptionTypeOption(
    type: EncryptionType,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val (strength, description) = when (type) {
        EncryptionType.AES_128 -> "Medium" to "Fast encryption, good for most data"
        EncryptionType.AES_256 -> "High" to "Strong encryption, recommended for sensitive data"
        EncryptionType.RSA_2048 -> "High" to "Asymmetric encryption, good for key exchange"
        EncryptionType.RSA_4096 -> "Maximum" to "Strongest encryption, slower performance"
        else -> "None" to ""
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                } else {
                    Color.Transparent
                }
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            enabled = enabled
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type.name.replace('_', '-'),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                SecurityLevelChip(
                    level = when (strength) {
                        "Medium" -> SecurityLevel.MEDIUM
                        "High" -> SecurityLevel.HIGH
                        "Maximum" -> SecurityLevel.CRITICAL
                        else -> SecurityLevel.LOW
                    }
                )
            }
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// 🛡️ PRIVACY CONSENT MANAGER
// ============================================================================

@Composable
fun PrivacyConsentManager(
    consents: List<PrivacyConsent>,
    onConsentChange: (PrivacyConsent, ConsentStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Privacy & Consent",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage your data privacy preferences",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        items(consents) { consent ->
            PrivacyConsentCard(
                consent = consent,
                onStatusChange = { status ->
                    onConsentChange(consent, status)
                }
            )
        }
        
        item {
            PrivacyPolicyCard()
        }
    }
}

@Composable
private fun PrivacyConsentCard(
    consent: PrivacyConsent,
    onStatusChange: (ConsentStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = consent.purpose,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = consent.category.name.replace('_', ' '),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                ConsentStatusChip(status = consent.status)
            }
            
            // Description
            Text(
                text = consent.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Details
            if (consent.dataRetentionDays != null || consent.expiresAt != null) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    consent.dataRetentionDays?.let { days ->
                        Text(
                            text = "Data retention: $days days",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    consent.expiresAt?.let { expiry ->
                        Text(
                            text = "Expires: ${formatDate(expiry)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Actions
            if (!consent.isRequired) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (consent.status != ConsentStatus.GRANTED) {
                        Button(
                            onClick = { onStatusChange(ConsentStatus.GRANTED) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Grant")
                        }
                    }
                    
                    if (consent.status == ConsentStatus.GRANTED) {
                        OutlinedButton(
                            onClick = { onStatusChange(ConsentStatus.REVOKED) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Revoke")
                        }
                    } else if (consent.status != ConsentStatus.DENIED) {
                        OutlinedButton(
                            onClick = { onStatusChange(ConsentStatus.DENIED) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Deny")
                        }
                    }
                }
            } else {
                Text(
                    text = "Required for app functionality",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ============================================================================
// 📊 SECURITY AUDIT LOG
// ============================================================================

@Composable
fun SecurityAuditLog(
    auditEntries: List<SecurityAuditEntry>,
    onExportLog: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Security Audit Log",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${auditEntries.size} entries",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                OutlinedButton(onClick = onExportLog) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export")
                }
            }
        }
        
        items(auditEntries) { entry ->
            SecurityAuditEntryCard(entry = entry)
        }
    }
}

@Composable
private fun SecurityAuditEntryCard(
    entry: SecurityAuditEntry
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (entry.riskLevel) {
                SecurityLevel.CRITICAL -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                SecurityLevel.HIGH -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (entry.success) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (entry.success) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                    
                    Text(
                        text = entry.action,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                SecurityLevelChip(level = entry.riskLevel)
            }
            
            // Details
            Text(
                text = "User: ${entry.userId} • Resource: ${entry.resource}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "IP: ${entry.ipAddress} • ${formatDateTime(entry.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (entry.userAgent.isNotEmpty()) {
                Text(
                    text = "User Agent: ${entry.userAgent}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============================================================================
// ⚠️ SECURITY THREAT MONITOR
// ============================================================================

@Composable
fun SecurityThreatMonitor(
    threats: List<SecurityThreat>,
    onResolveThreat: (SecurityThreat) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeThreatCount = threats.count { !it.isResolved }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SecurityThreatSummary(
                totalThreats = threats.size,
                activeThreats = activeThreatCount
            )
        }
        
        items(threats) { threat ->
            SecurityThreatCard(
                threat = threat,
                onResolve = { onResolveThreat(threat) }
            )
        }
    }
}

@Composable
private fun SecurityThreatSummary(
    totalThreats: Int,
    activeThreats: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (activeThreats > 0) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (activeThreats > 0) Icons.Default.Warning else Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (activeThreats > 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (activeThreats > 0) {
                        "Security Threats Detected"
                    } else {
                        "System Secure"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$activeThreats active threats • $totalThreats total",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SecurityThreatCard(
    threat: SecurityThreat,
    onResolve: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (threat.isResolved) {
                MaterialTheme.colorScheme.surface
            } else {
                when (threat.severity) {
                    SecurityLevel.CRITICAL -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    SecurityLevel.HIGH -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.surface
                }
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = threat.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Source: ${threat.source}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                SecurityLevelChip(level = threat.severity)
            }
            
            // Description
            Text(
                text = threat.description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Timestamps
            Text(
                text = "Detected: ${formatDateTime(threat.detectedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (threat.isResolved && threat.resolvedAt != null) {
                Text(
                    text = "Resolved: ${formatDateTime(threat.resolvedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
                
                threat.mitigation?.let { mitigation ->
                    Text(
                        text = "Mitigation: $mitigation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Actions
            if (!threat.isResolved) {
                Button(
                    onClick = onResolve,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mark as Resolved")
                }
            }
        }
    }
}

// ============================================================================
// 🎨 HELPER COMPONENTS
// ============================================================================

@Composable
private fun SecurityLevelIndicator(level: SecurityLevel) {
    val (color, text) = when (level) {
        SecurityLevel.LOW -> Color(0xFF4CAF50) to "Low Security"
        SecurityLevel.MEDIUM -> Color(0xFFFF9800) to "Medium Security"
        SecurityLevel.HIGH -> Color(0xFFF44336) to "High Security"
        SecurityLevel.CRITICAL -> Color(0xFF9C27B0) to "Critical Security"
    }
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (index < level.ordinal + 1) color else Color.Gray.copy(alpha = 0.3f),
                        CircleShape
                    )
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun SecurityLevelChip(level: SecurityLevel) {
    val (color, text) = when (level) {
        SecurityLevel.LOW -> Color(0xFF4CAF50) to "LOW"
        SecurityLevel.MEDIUM -> Color(0xFFFF9800) to "MED"
        SecurityLevel.HIGH -> Color(0xFFF44336) to "HIGH"
        SecurityLevel.CRITICAL -> Color(0xFF9C27B0) to "CRIT"
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        modifier = Modifier.height(24.dp)
    )
}

@Composable
private fun ConsentStatusChip(status: ConsentStatus) {
    val (color, text) = when (status) {
        ConsentStatus.GRANTED -> Color(0xFF4CAF50) to "Granted"
        ConsentStatus.DENIED -> Color(0xFFF44336) to "Denied"
        ConsentStatus.REVOKED -> Color(0xFFFF9800) to "Revoked"
        ConsentStatus.EXPIRED -> Color(0xFF9C27B0) to "Expired"
        ConsentStatus.PENDING -> Color(0xFF2196F3) to "Pending"
        ConsentStatus.NOT_REQUESTED -> Color(0xFF757575) to "Not Requested"
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        modifier = Modifier.height(24.dp)
    )
}

@Composable
private fun SecurityToggleCard(
    title: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun EncryptionToolsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Encryption Tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Generate key */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Generate Key")
                }
                
                OutlinedButton(
                    onClick = { /* Test encryption */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Test Encryption")
                }
            }
        }
    }
}

@Composable
private fun PrivacyPolicyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Privacy Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Learn more about how we protect your privacy and handle your data.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = { /* Open privacy policy */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Privacy Policy")
                }
                
                TextButton(
                    onClick = { /* Open data policy */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Data Usage")
                }
            }
        }
    }
}

// ============================================================================
// 🛠️ UTILITY FUNCTIONS
// ============================================================================

private class MaskingVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val maskedText = "*".repeat(text.text.length)
        return TransformedText(
            AnnotatedString(maskedText),
            OffsetMapping.Identity
        )
    }
}

private fun formatDate(instant: Instant): String {
    return instant.toString().substringBefore('T')
}

private fun formatDateTime(instant: Instant): String {
    return instant.toString().replace('T', ' ').substringBefore('.')
}

// Sample data generators
fun generateSamplePrivacyConsents(): List<PrivacyConsent> {
    return listOf(
        PrivacyConsent(
            id = "consent_1",
            category = DataCategory.PERSONAL_INFO,
            purpose = "Account Management",
            description = "We collect your name, email, and profile information to manage your account and provide personalized services.",
            status = ConsentStatus.GRANTED,
            grantedAt = Clock.System.now().minus(30.days),
            isRequired = true,
            dataRetentionDays = 365
        ),
        PrivacyConsent(
            id = "consent_2",
            category = DataCategory.LOCATION,
            purpose = "Location Services",
            description = "Access your location to provide location-based features and services.",
            status = ConsentStatus.PENDING,
            isRequired = false,
            dataRetentionDays = 90
        ),
        PrivacyConsent(
            id = "consent_3",
            category = DataCategory.USAGE_DATA,
            purpose = "Analytics & Improvement",
            description = "Collect usage data to improve our services and user experience.",
            status = ConsentStatus.DENIED,
            isRequired = false,
            dataRetentionDays = 180
        )
    )
}

fun generateSampleSecurityAuditEntries(): List<SecurityAuditEntry> {
    return (1..20).map { index ->
        SecurityAuditEntry(
            id = "audit_$index",
            timestamp = Clock.System.now().minus((index * 3600).seconds),
            userId = "user_${(1..5).random()}",
            action = listOf("LOGIN", "LOGOUT", "DATA_ACCESS", "SETTINGS_CHANGE", "FILE_UPLOAD").random(),
            resource = listOf("USER_PROFILE", "DOCUMENTS", "SETTINGS", "API_ENDPOINT").random(),
            ipAddress = "192.168.1.${(1..255).random()}",
            userAgent = "Mozilla/5.0 (compatible; DataFlowApp/1.0)",
            success = Random.nextBoolean(),
            riskLevel = SecurityLevel.values().random()
        )
    }
}

fun generateSampleSecurityThreats(): List<SecurityThreat> {
    return listOf(
        SecurityThreat(
            id = "threat_1",
            type = "Brute Force Attack",
            severity = SecurityLevel.HIGH,
            description = "Multiple failed login attempts detected from IP 192.168.1.100",
            detectedAt = Clock.System.now().minus(2.hours),
            source = "192.168.1.100",
            isResolved = false
        ),
        SecurityThreat(
            id = "threat_2",
            type = "Suspicious Data Access",
            severity = SecurityLevel.MEDIUM,
            description = "Unusual data access pattern detected for user account",
            detectedAt = Clock.System.now().minus(1.days),
            source = "Internal System",
            isResolved = true,
            resolvedAt = Clock.System.now().minus(20.hours),
            mitigation = "User account temporarily locked and password reset required"
        )
    )
}