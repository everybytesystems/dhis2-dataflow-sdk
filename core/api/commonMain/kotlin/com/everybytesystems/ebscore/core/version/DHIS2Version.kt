package com.everybytesystems.ebscore.core.version

import kotlinx.serialization.Serializable

/**
 * DHIS2 Version representation with feature support detection
 */
@Serializable
data class DHIS2Version(
    val major: Int,
    val minor: Int,
    val patch: Int = 0,
    val build: String? = null,
    val revision: String? = null
) : Comparable<DHIS2Version> {
    
    val versionString: String
        get() = buildString {
            append("$major.$minor")
            if (patch > 0) append(".$patch")
            build?.let { append("-$it") }
            revision?.let { append(".$it") }
        }
    
    override fun compareTo(other: DHIS2Version): Int {
        return when {
            major != other.major -> major.compareTo(other.major)
            minor != other.minor -> minor.compareTo(other.minor)
            else -> patch.compareTo(other.patch)
        }
    }
    
    // ========================================
    // COMPREHENSIVE FEATURE SUPPORT DETECTION
    // ========================================
    
    // Core API Features
    fun supportsTrackerApi(): Boolean = this >= V2_36
    fun supportsNewAnalytics(): Boolean = this >= V2_37
    fun supportsEnhancedMetadata(): Boolean = this >= V2_36
    fun supportsAdvancedSync(): Boolean = this >= V2_39
    fun supportsModernAuth(): Boolean = this >= V2_40
    
    // Metadata Features
    fun supportsMetadataGist(): Boolean = this >= V2_37
    fun supportsMetadataVersioning(): Boolean = this >= V2_38
    fun supportsMetadataTranslations(): Boolean = this >= V2_36
    fun supportsMetadataSharing(): Boolean = this >= V2_36
    fun supportsMetadataDependencies(): Boolean = this >= V2_37
    fun supportsMetadataSearch(): Boolean = this >= V2_36
    fun supportsMetadataAnalytics(): Boolean = this >= V2_37
    fun supportsMetadataBulkOperations(): Boolean = this >= V2_37
    
    // Tracker Features
    fun supportsTrackerImportStrategy(): Boolean = this >= V2_37
    fun supportsTrackerWorkingLists(): Boolean = this >= V2_37
    fun supportsTrackerPotentialDuplicates(): Boolean = this >= V2_39
    fun supportsTrackerOwnership(): Boolean = this >= V2_38
    fun supportsTrackerCSVImport(): Boolean = this >= V2_40
    
    // Analytics Features
    fun supportsAnalyticsOutlierDetection(): Boolean = this >= V2_36
    fun supportsAnalyticsValidationResults(): Boolean = this >= V2_36
    fun supportsAnalyticsDataStatistics(): Boolean = this >= V2_37
    fun supportsAnalyticsGeoFeatures(): Boolean = this >= V2_38
    fun supportsAnalyticsEnrollments(): Boolean = this >= V2_37
    fun supportsAnalyticsEventClusters(): Boolean = this >= V2_39
    
    // Data Features
    fun supportsDataValueAudit(): Boolean = this >= V2_36
    fun supportsDataValueFollowUp(): Boolean = this >= V2_36
    fun supportsDataValueCompleteRegistration(): Boolean = this >= V2_36
    fun supportsDataValueSetCompleteRegistration(): Boolean = this >= V2_37
    fun supportsDataValueSetAsync(): Boolean = this >= V2_38
    
    // User Management Features
    fun supportsUserInvitations(): Boolean = this >= V2_36
    fun supportsUserAccountRecovery(): Boolean = this >= V2_37
    fun supportsUserTwoFactorAuth(): Boolean = this >= V2_38
    fun supportsUserSettings(): Boolean = this >= V2_36
    fun supportsUserDataApproval(): Boolean = this >= V2_36
    
    // System Features
    fun supportsSystemSettings(): Boolean = this >= V2_36
    fun supportsSystemMaintenance(): Boolean = this >= V2_36
    fun supportsMaintenanceMode(): Boolean = this >= V2_37
    fun supportsSystemStatistics(): Boolean = this >= V2_37
    fun supportsSystemInfo(): Boolean = this >= V2_36
    fun supportsSystemHealth(): Boolean = this >= V2_38
    fun supportsSystemPerformance(): Boolean = this >= V2_38
    fun supportsSystemFlags(): Boolean = this >= V2_38
    fun supportsSecuritySettings(): Boolean = this >= V2_37
    fun supportsSystemBackup(): Boolean = this >= V2_38
    fun supportsSystemUpdates(): Boolean = this >= V2_39
    fun supportsClusterManagement(): Boolean = this >= V2_40
    
    // Messaging Features
    fun supportsMessageConversations(): Boolean = this >= V2_36
    fun supportsMessageAttachments(): Boolean = this >= V2_37
    fun supportsSMSCommands(): Boolean = this >= V2_36
    fun supportsSMSGateways(): Boolean = this >= V2_36
    fun supportsEmailNotifications(): Boolean = this >= V2_37
    fun supportsPushNotifications(): Boolean = this >= V2_40
    fun supportsNotificationTemplates(): Boolean = this >= V2_38
    
    // File Management Features
    fun supportsFileResources(): Boolean = this >= V2_36
    fun supportsFileResourceDomains(): Boolean = this >= V2_37
    fun supportsFileResourceExternalStorage(): Boolean = this >= V2_38
    fun supportsDocumentManagement(): Boolean = this >= V2_36
    
    // Data Store Features
    fun supportsDataStore(): Boolean = this >= V2_36
    fun supportsUserDataStore(): Boolean = this >= V2_36
    fun supportsDataStoreSharing(): Boolean = this >= V2_37
    fun supportsDataStoreNamespaces(): Boolean = this >= V2_36
    fun supportsAppDataStore(): Boolean = this >= V2_38
    fun supportsDataStoreBulkOperations(): Boolean = this >= V2_39
    fun supportsDataStoreQuery(): Boolean = this >= V2_40
    fun supportsDataStoreBackup(): Boolean = this >= V2_41
    fun supportsDataStoreVersioning(): Boolean = this >= V2_42
    
    // App Management Features
    fun supportsAppManagement(): Boolean = this >= V2_36
    fun supportsAppMarketplace(): Boolean = this >= V2_37
    fun supportsAppStore(): Boolean = this >= V2_38
    fun supportsAppInstallation(): Boolean = this >= V2_36
    fun supportsAppBundling(): Boolean = this >= V2_38
    fun supportsAppDevelopmentTools(): Boolean = this >= V2_39
    fun supportsAppSecurity(): Boolean = this >= V2_40
    
    // Data Approval Features
    fun supportsDataApprovalWorkflows(): Boolean = this >= V2_36
    fun supportsDataApprovalLevels(): Boolean = this >= V2_36
    fun supportsDataApprovalAudit(): Boolean = this >= V2_37
    fun supportsDataApprovalMultiple(): Boolean = this >= V2_38
    
    // Validation Features
    fun supportsValidationRules(): Boolean = this >= V2_36
    fun supportsValidationRuleGroups(): Boolean = this >= V2_36
    fun supportsValidationNotifications(): Boolean = this >= V2_37
    fun supportsValidationAnalysis(): Boolean = this >= V2_36
    
    // Synchronization Features
    fun supportsDataSynchronization(): Boolean = this >= V2_36
    fun supportsMetadataSynchronization(): Boolean = this >= V2_36
    fun supportsRemoteInstances(): Boolean = this >= V2_37
    fun supportsDataExchange(): Boolean = this >= V2_38
    fun supportsSyncConflictResolution(): Boolean = this >= V2_39
    
    // Event Hooks (2.39+)
    fun supportsEventHooks(): Boolean = this >= V2_39
    fun supportsWebHooks(): Boolean = this >= V2_39
    fun supportsEventFilters(): Boolean = this >= V2_39
    
    // Advanced Features (2.40+)
    fun supportsOfflineSync(): Boolean = this >= V2_40
    fun supportsAdvancedCaching(): Boolean = this >= V2_40
    fun supportsPerformanceMonitoring(): Boolean = this >= V2_40
    fun supportsAdvancedSecurity(): Boolean = this >= V2_40
    
    // Latest Features (2.41+)
    fun supportsEnhancedReporting(): Boolean = this >= V2_41
    fun supportsAdvancedAnalytics(): Boolean = this >= V2_41
    fun supportsImprovedUI(): Boolean = this >= V2_41
    
    companion object {
        val V2_35 = DHIS2Version(2, 35)
        val V2_36 = DHIS2Version(2, 36)
        val V2_37 = DHIS2Version(2, 37)
        val V2_38 = DHIS2Version(2, 38)
        val V2_39 = DHIS2Version(2, 39)
        val V2_40 = DHIS2Version(2, 40)
        val V2_41 = DHIS2Version(2, 41)
        val V2_42 = DHIS2Version(2, 42)
        
        fun parse(versionString: String): DHIS2Version? {
            return try {
                val cleanVersion = versionString.trim()
                val parts = cleanVersion.split(".", "-")
                
                if (parts.size < 2) return null
                
                val major = parts[0].toIntOrNull() ?: return null
                val minor = parts[1].toIntOrNull() ?: return null
                val patch = if (parts.size > 2) parts[2].toIntOrNull() ?: 0 else 0
                val build = if (parts.size > 3) parts[3] else null
                
                DHIS2Version(major, minor, patch, build)
            } catch (e: Exception) {
                null
            }
        }
    }
}