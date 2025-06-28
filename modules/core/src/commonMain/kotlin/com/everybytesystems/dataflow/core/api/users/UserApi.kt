package com.everybytesystems.dataflow.core.api.users

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete User Management API implementation for DHIS2 2.36+
 * Supports user CRUD operations, roles, permissions, and advanced features
 */
class UserApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // USER OPERATIONS
    // ========================================
    
    /**
     * Get users with comprehensive filtering
     */
    suspend fun getUsers(
        fields: String = "*",
        filter: List<String> = emptyList(),
        rootJunction: String = "AND",
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        totalPages: Boolean = false,
        skipPaging: Boolean = false,
        paging: Boolean = true,
        includeChildren: Boolean = false,
        includeDescendants: Boolean = false,
        query: String? = null,
        queryFields: List<String> = emptyList()
    ): ApiResponse<UsersResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            put("rootJunction", rootJunction)
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            put("totalPages", totalPages.toString())
            put("skipPaging", skipPaging.toString())
            put("paging", paging.toString())
            put("includeChildren", includeChildren.toString())
            put("includeDescendants", includeDescendants.toString())
            query?.let { put("query", it) }
            if (queryFields.isNotEmpty()) put("queryFields", queryFields.joinToString(","))
        }
        
        return get("users", params)
    }
    
    /**
     * Get a specific user
     */
    suspend fun getUser(
        id: String,
        fields: String = "*"
    ): ApiResponse<User> {
        return get("users/$id", mapOf("fields" to fields))
    }
    
    /**
     * Get current user information
     */
    suspend fun getCurrentUser(
        fields: String = "*"
    ): ApiResponse<User> {
        return get("me", mapOf("fields" to fields))
    }
    
    /**
     * Create a new user
     */
    suspend fun createUser(
        user: User,
        skipSharing: Boolean = false,
        skipTranslation: Boolean = false,
        skipValidation: Boolean = false,
        async: Boolean = false,
        importReportMode: ImportReportMode = ImportReportMode.ERRORS,
        importStrategy: ImportStrategy = ImportStrategy.CREATE,
        mergeMode: MergeMode = MergeMode.REPLACE,
        atomicMode: AtomicMode = AtomicMode.ALL,
        flushMode: FlushMode = FlushMode.AUTO
    ): ApiResponse<UserImportResponse> {
        
        val params = buildMap {
            put("skipSharing", skipSharing.toString())
            put("skipTranslation", skipTranslation.toString())
            put("skipValidation", skipValidation.toString())
            put("async", async.toString())
            put("importReportMode", importReportMode.name)
            put("importStrategy", importStrategy.name)
            put("mergeMode", mergeMode.name)
            put("atomicMode", atomicMode.name)
            put("flushMode", flushMode.name)
        }
        
        return post("users", user, params)
    }
    
    /**
     * Update an existing user
     */
    suspend fun updateUser(
        id: String,
        user: User,
        skipSharing: Boolean = false,
        skipTranslation: Boolean = false,
        skipValidation: Boolean = false,
        mergeMode: MergeMode = MergeMode.REPLACE
    ): ApiResponse<UserImportResponse> {
        
        val params = buildMap {
            put("skipSharing", skipSharing.toString())
            put("skipTranslation", skipTranslation.toString())
            put("skipValidation", skipValidation.toString())
            put("mergeMode", mergeMode.name)
        }
        
        return put("users/$id", user, params)
    }
    
    /**
     * Delete a user
     */
    suspend fun deleteUser(id: String): ApiResponse<UserImportResponse> {
        return delete("users/$id")
    }
    
    /**
     * Bulk import users
     */
    suspend fun bulkImportUsers(
        users: UserBulkImportRequest,
        dryRun: Boolean = false,
        skipSharing: Boolean = false,
        skipTranslation: Boolean = false,
        skipValidation: Boolean = false,
        async: Boolean = false,
        importReportMode: ImportReportMode = ImportReportMode.ERRORS,
        importStrategy: ImportStrategy = ImportStrategy.CREATE_AND_UPDATE,
        mergeMode: MergeMode = MergeMode.REPLACE,
        atomicMode: AtomicMode = AtomicMode.ALL,
        flushMode: FlushMode = FlushMode.AUTO,
        inclusionStrategy: InclusionStrategy = InclusionStrategy.NON_NULL,
        userOverrideMode: UserOverrideMode = UserOverrideMode.NONE
    ): ApiResponse<UserImportResponse> {
        
        val params = buildMap {
            put("dryRun", dryRun.toString())
            put("skipSharing", skipSharing.toString())
            put("skipTranslation", skipTranslation.toString())
            put("skipValidation", skipValidation.toString())
            put("async", async.toString())
            put("importReportMode", importReportMode.name)
            put("importStrategy", importStrategy.name)
            put("mergeMode", mergeMode.name)
            put("atomicMode", atomicMode.name)
            put("flushMode", flushMode.name)
            put("inclusionStrategy", inclusionStrategy.name)
            put("userOverrideMode", userOverrideMode.name)
        }
        
        return post("users", users, params)
    }
    
    // ========================================
    // USER ROLES
    // ========================================
    
    /**
     * Get user roles
     */
    suspend fun getUserRoles(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<UserRolesResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("userRoles", params)
    }
    
    /**
     * Get a specific user role
     */
    suspend fun getUserRole(
        id: String,
        fields: String = "*"
    ): ApiResponse<UserRole> {
        return get("userRoles/$id", mapOf("fields" to fields))
    }
    
    /**
     * Create a new user role
     */
    suspend fun createUserRole(userRole: UserRole): ApiResponse<UserImportResponse> {
        return post("userRoles", userRole)
    }
    
    /**
     * Update an existing user role
     */
    suspend fun updateUserRole(id: String, userRole: UserRole): ApiResponse<UserImportResponse> {
        return put("userRoles/$id", userRole)
    }
    
    /**
     * Delete a user role
     */
    suspend fun deleteUserRole(id: String): ApiResponse<UserImportResponse> {
        return delete("userRoles/$id")
    }
    
    // ========================================
    // USER GROUPS
    // ========================================
    
    /**
     * Get user groups
     */
    suspend fun getUserGroups(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<UserGroupsResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("userGroups", params)
    }
    
    /**
     * Get a specific user group
     */
    suspend fun getUserGroup(
        id: String,
        fields: String = "*"
    ): ApiResponse<UserGroup> {
        return get("userGroups/$id", mapOf("fields" to fields))
    }
    
    /**
     * Create a new user group
     */
    suspend fun createUserGroup(userGroup: UserGroup): ApiResponse<UserImportResponse> {
        return post("userGroups", userGroup)
    }
    
    /**
     * Update an existing user group
     */
    suspend fun updateUserGroup(id: String, userGroup: UserGroup): ApiResponse<UserImportResponse> {
        return put("userGroups/$id", userGroup)
    }
    
    /**
     * Delete a user group
     */
    suspend fun deleteUserGroup(id: String): ApiResponse<UserImportResponse> {
        return delete("userGroups/$id")
    }
    
    // ========================================
    // USER CREDENTIALS
    // ========================================
    
    /**
     * Update user password
     */
    suspend fun updateUserPassword(
        userId: String,
        oldPassword: String,
        newPassword: String
    ): ApiResponse<UserPasswordResponse> {
        val payload = UserPasswordUpdateRequest(oldPassword, newPassword)
        return post("users/$userId/changePassword", payload)
    }
    
    /**
     * Reset user password (admin only)
     */
    suspend fun resetUserPassword(
        userId: String,
        newPassword: String
    ): ApiResponse<UserPasswordResponse> {
        val payload = UserPasswordResetRequest(newPassword)
        return post("users/$userId/resetPassword", payload)
    }
    
    /**
     * Enable/disable user account
     */
    suspend fun setUserAccountStatus(
        userId: String,
        disabled: Boolean
    ): ApiResponse<UserImportResponse> {
        val payload = UserAccountStatusRequest(disabled)
        return post("users/$userId/accountStatus", payload)
    }
    
    /**
     * Expire user sessions
     */
    suspend fun expireUserSessions(userId: String): ApiResponse<UserSessionResponse> {
        return post("users/$userId/expireSessions", emptyMap<String, Any>())
    }
    
    // ========================================
    // USER INVITATIONS (2.36+)
    // ========================================
    
    /**
     * Send user invitation (2.36+)
     */
    suspend fun sendUserInvitation(
        invitation: UserInvitation
    ): ApiResponse<UserInvitationResponse> {
        if (!version.supportsUserInvitations()) {
            return ApiResponse.Error(UnsupportedOperationException("User invitations not supported in version ${version.versionString}"))
        }
        
        return post("users/invite", invitation)
    }
    
    /**
     * Get user invitations (2.36+)
     */
    suspend fun getUserInvitations(
        fields: String = "*",
        page: Int? = null,
        pageSize: Int? = null
    ): ApiResponse<UserInvitationsResponse> {
        if (!version.supportsUserInvitations()) {
            return ApiResponse.Error(UnsupportedOperationException("User invitations not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("fields", fields)
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
        }
        
        return get("users/invitations", params)
    }
    
    /**
     * Accept user invitation (2.36+)
     */
    suspend fun acceptUserInvitation(
        token: String,
        acceptance: UserInvitationAcceptance
    ): ApiResponse<UserInvitationResponse> {
        if (!version.supportsUserInvitations()) {
            return ApiResponse.Error(UnsupportedOperationException("User invitations not supported in version ${version.versionString}"))
        }
        
        return post("users/invitations/$token/accept", acceptance)
    }
    
    // ========================================
    // ACCOUNT RECOVERY (2.37+)
    // ========================================
    
    /**
     * Request account recovery (2.37+)
     */
    suspend fun requestAccountRecovery(
        usernameOrEmail: String
    ): ApiResponse<AccountRecoveryResponse> {
        if (!version.supportsUserAccountRecovery()) {
            return ApiResponse.Error(UnsupportedOperationException("Account recovery not supported in version ${version.versionString}"))
        }
        
        val payload = AccountRecoveryRequest(usernameOrEmail)
        return post("account/recovery", payload)
    }
    
    /**
     * Reset password with recovery token (2.37+)
     */
    suspend fun resetPasswordWithToken(
        token: String,
        newPassword: String
    ): ApiResponse<AccountRecoveryResponse> {
        if (!version.supportsUserAccountRecovery()) {
            return ApiResponse.Error(UnsupportedOperationException("Account recovery not supported in version ${version.versionString}"))
        }
        
        val payload = PasswordResetRequest(newPassword)
        return post("account/recovery/$token/reset", payload)
    }
    
    // ========================================
    // TWO-FACTOR AUTHENTICATION (2.38+)
    // ========================================
    
    /**
     * Enable two-factor authentication (2.38+)
     */
    suspend fun enableTwoFactorAuth(
        userId: String
    ): ApiResponse<TwoFactorAuthResponse> {
        if (!version.supportsUserTwoFactorAuth()) {
            return ApiResponse.Error(UnsupportedOperationException("Two-factor authentication not supported in version ${version.versionString}"))
        }
        
        return post("users/$userId/2fa/enable", emptyMap<String, Any>())
    }
    
    /**
     * Disable two-factor authentication (2.38+)
     */
    suspend fun disableTwoFactorAuth(
        userId: String,
        password: String
    ): ApiResponse<TwoFactorAuthResponse> {
        if (!version.supportsUserTwoFactorAuth()) {
            return ApiResponse.Error(UnsupportedOperationException("Two-factor authentication not supported in version ${version.versionString}"))
        }
        
        val payload = TwoFactorAuthDisableRequest(password)
        return post("users/$userId/2fa/disable", payload)
    }
    
    /**
     * Generate QR code for two-factor authentication (2.38+)
     */
    suspend fun generateTwoFactorQRCode(
        userId: String
    ): ApiResponse<TwoFactorQRCodeResponse> {
        if (!version.supportsUserTwoFactorAuth()) {
            return ApiResponse.Error(UnsupportedOperationException("Two-factor authentication not supported in version ${version.versionString}"))
        }
        
        return get("users/$userId/2fa/qrcode")
    }
    
    // ========================================
    // USER SETTINGS (2.36+)
    // ========================================
    
    /**
     * Get user settings (2.36+)
     */
    suspend fun getUserSettings(
        userId: String? = null
    ): ApiResponse<UserSettingsResponse> {
        if (!version.supportsUserSettings()) {
            return ApiResponse.Error(UnsupportedOperationException("User settings not supported in version ${version.versionString}"))
        }
        
        val endpoint = if (userId != null) "users/$userId/settings" else "userSettings"
        return get(endpoint)
    }
    
    /**
     * Update user settings (2.36+)
     */
    suspend fun updateUserSettings(
        settings: UserSettings,
        userId: String? = null
    ): ApiResponse<UserSettingsResponse> {
        if (!version.supportsUserSettings()) {
            return ApiResponse.Error(UnsupportedOperationException("User settings not supported in version ${version.versionString}"))
        }
        
        val endpoint = if (userId != null) "users/$userId/settings" else "userSettings"
        return post(endpoint, settings)
    }
    
    /**
     * Get specific user setting (2.36+)
     */
    suspend fun getUserSetting(
        key: String,
        userId: String? = null
    ): ApiResponse<UserSettingValue> {
        if (!version.supportsUserSettings()) {
            return ApiResponse.Error(UnsupportedOperationException("User settings not supported in version ${version.versionString}"))
        }
        
        val endpoint = if (userId != null) "users/$userId/settings/$key" else "userSettings/$key"
        return get(endpoint)
    }
    
    /**
     * Set specific user setting (2.36+)
     */
    suspend fun setUserSetting(
        key: String,
        value: String,
        userId: String? = null
    ): ApiResponse<UserSettingsResponse> {
        if (!version.supportsUserSettings()) {
            return ApiResponse.Error(UnsupportedOperationException("User settings not supported in version ${version.versionString}"))
        }
        
        val endpoint = if (userId != null) "users/$userId/settings/$key" else "userSettings/$key"
        return post(endpoint, UserSettingValue(value))
    }
    
    // ========================================
    // USER AUTHORITIES & PERMISSIONS
    // ========================================
    
    /**
     * Get user authorities
     */
    suspend fun getUserAuthorities(userId: String): ApiResponse<UserAuthoritiesResponse> {
        return get("users/$userId/authorities")
    }
    
    /**
     * Get user data approval levels
     */
    suspend fun getUserDataApprovalLevels(userId: String): ApiResponse<UserDataApprovalLevelsResponse> {
        return get("users/$userId/dataApprovalLevels")
    }
    
    /**
     * Get user organization units
     */
    suspend fun getUserOrganisationUnits(
        userId: String,
        fields: String = "*"
    ): ApiResponse<UserOrganisationUnitsResponse> {
        return get("users/$userId/organisationUnits", mapOf("fields" to fields))
    }
    
    /**
     * Get user data view organization units
     */
    suspend fun getUserDataViewOrganisationUnits(
        userId: String,
        fields: String = "*"
    ): ApiResponse<UserOrganisationUnitsResponse> {
        return get("users/$userId/dataViewOrganisationUnits", mapOf("fields" to fields))
    }
    
    /**
     * Get user tei search organization units
     */
    suspend fun getUserTeiSearchOrganisationUnits(
        userId: String,
        fields: String = "*"
    ): ApiResponse<UserOrganisationUnitsResponse> {
        return get("users/$userId/teiSearchOrganisationUnits", mapOf("fields" to fields))
    }
    
    // ========================================
    // USER ANALYTICS
    // ========================================
    
    /**
     * Get user analytics
     */
    suspend fun getUserAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        interval: String = "DAY"
    ): ApiResponse<UserAnalyticsResponse> {
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            put("interval", interval)
        }
        
        return get("users/analytics", params)
    }
    
    /**
     * Get user lookup
     */
    suspend fun getUserLookup(
        query: String,
        pageSize: Int = 10
    ): ApiResponse<UserLookupResponse> {
        val params = mapOf(
            "query" to query,
            "pageSize" to pageSize.toString()
        )
        
        return get("users/lookup", params)
    }
}

// ========================================
// ENUMS
// ========================================

enum class ImportReportMode { FULL, ERRORS, WARNINGS, DEBUG }
enum class ImportStrategy { CREATE, UPDATE, CREATE_AND_UPDATE, DELETE }
enum class MergeMode { REPLACE, MERGE }
enum class AtomicMode { ALL, OBJECT }
enum class FlushMode { AUTO, OBJECT }
enum class InclusionStrategy { NON_NULL, ALWAYS, NON_EMPTY }
enum class UserOverrideMode { NONE, CURRENT, SELECTED }