package com.everybytesystems.dataflow.core.api.users

import kotlinx.serialization.Serializable

// ========================================
// USER MODELS
// ========================================

@Serializable
data class User(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
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
    val whatsApp: String? = null,
    val facebookMessenger: String? = null,
    val skype: String? = null,
    val telegram: String? = null,
    val twitter: String? = null,
    val avatar: Avatar? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val lastLogin: String? = null,
    val restoreToken: String? = null,
    val restoreCode: String? = null,
    val restoreExpiry: String? = null,
    val selfRegistered: Boolean = false,
    val invitation: Boolean = false,
    val disabled: Boolean = false,
    val accountExpiry: String? = null,
    val passwordLastUpdated: String? = null,
    val twoFA: Boolean = false,
    val externalAuth: Boolean = false,
    val openId: String? = null,
    val ldapId: String? = null,
    val userCredentials: UserCredentials? = null,
    val userRoles: List<UserRole> = emptyList(),
    val userGroups: List<UserGroup> = emptyList(),
    val organisationUnits: List<OrganisationUnitReference> = emptyList(),
    val dataViewOrganisationUnits: List<OrganisationUnitReference> = emptyList(),
    val teiSearchOrganisationUnits: List<OrganisationUnitReference> = emptyList(),
    val catDimensionConstraints: List<CategoryDimensionConstraint> = emptyList(),
    val cogsDimensionConstraints: List<CategoryOptionGroupSetDimensionConstraint> = emptyList(),
    val settings: UserSettings? = null,
    val dataApprovalLevels: List<DataApprovalLevel> = emptyList(),
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class UsersResponse(
    val users: List<User> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Avatar(
    val id: String,
    val name: String,
    val created: String? = null,
    val lastUpdated: String? = null,
    val contentType: String? = null,
    val contentLength: Long? = null,
    val hasImage: Boolean = false
)

@Serializable
data class UserReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val username: String? = null
)

// ========================================
// USER CREDENTIALS MODELS
// ========================================

@Serializable
data class UserCredentials(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val username: String,
    val password: String? = null,
    val passwordLastUpdated: String? = null,
    val twoFA: Boolean = false,
    val externalAuth: Boolean = false,
    val openId: String? = null,
    val ldapId: String? = null,
    val disabled: Boolean = false,
    val accountExpiry: String? = null,
    val restoreToken: String? = null,
    val restoreCode: String? = null,
    val restoreExpiry: String? = null,
    val selfRegistered: Boolean = false,
    val invitation: Boolean = false,
    val lastLogin: String? = null,
    val userInfo: UserReference? = null,
    val userRoles: List<UserRole> = emptyList(),
    val catDimensionConstraints: List<CategoryDimensionConstraint> = emptyList(),
    val cogsDimensionConstraints: List<CategoryOptionGroupSetDimensionConstraint> = emptyList()
)

// ========================================
// USER ROLE MODELS
// ========================================

@Serializable
data class UserRole(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val authorities: List<String> = emptyList(),
    val users: List<UserReference> = emptyList(),
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class UserRolesResponse(
    val userRoles: List<UserRole> = emptyList(),
    val pager: Pager? = null
)

// ========================================
// USER GROUP MODELS
// ========================================

@Serializable
data class UserGroup(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val users: List<UserReference> = emptyList(),
    val managedGroups: List<UserGroupReference> = emptyList(),
    val managedByGroups: List<UserGroupReference> = emptyList(),
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class UserGroupsResponse(
    val userGroups: List<UserGroup> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class UserGroupReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

// ========================================
// ORGANIZATION UNIT MODELS
// ========================================

@Serializable
data class OrganisationUnitReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val level: Int? = null,
    val path: String? = null
)

@Serializable
data class UserOrganisationUnitsResponse(
    val organisationUnits: List<OrganisationUnitReference> = emptyList(),
    val pager: Pager? = null
)

// ========================================
// CONSTRAINT MODELS
// ========================================

@Serializable
data class CategoryDimensionConstraint(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val categoryOptions: List<CategoryOptionReference> = emptyList()
)

@Serializable
data class CategoryOptionReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class CategoryOptionGroupSetDimensionConstraint(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val categoryOptionGroups: List<CategoryOptionGroupReference> = emptyList()
)

@Serializable
data class CategoryOptionGroupReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

// ========================================
// DATA APPROVAL MODELS
// ========================================

@Serializable
data class DataApprovalLevel(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val level: Int,
    val orgUnitLevel: Int? = null,
    val categoryOptionGroupSet: CategoryOptionGroupSetReference? = null
)

@Serializable
data class CategoryOptionGroupSetReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class UserDataApprovalLevelsResponse(
    val dataApprovalLevels: List<DataApprovalLevel> = emptyList()
)

// ========================================
// USER SETTINGS MODELS
// ========================================

@Serializable
data class UserSettings(
    val keyUiLocale: String? = null,
    val keyDbLocale: String? = null,
    val keyAnalysisDisplayProperty: String? = null,
    val keyCurrentDomainType: String? = null,
    val keyAutoSaveCaseEntryForm: Boolean? = null,
    val keyAutoSaveTrackedEntityForm: Boolean? = null,
    val keyAutoSaveDataEntryForm: Boolean? = null,
    val keyTrackerDashboardLayout: String? = null,
    val keyStyle: String? = null,
    val keyMessageEmailNotification: Boolean? = null,
    val keyMessageSmsNotification: Boolean? = null,
    val keyAnalysisDigitGroupSeparator: String? = null,
    val keyJobsShowNotifications: Boolean? = null,
    val keyGatherAnalyticalObjectStatisticsInDashboardViews: Boolean? = null,
    val keyRequireAddToView: Boolean? = null,
    val keyUseCustomTopMenu: Boolean? = null,
    val keyUseCustomLeftMenu: Boolean? = null,
    val keyCustomTopMenuLogo: String? = null,
    val keyCustomLeftMenuLogo: String? = null,
    val keyCustomTopMenuTitle: String? = null,
    val keyCustomLeftMenuTitle: String? = null,
    val keyCustomCss: String? = null,
    val keyCustomJs: String? = null,
    val keySkipDataTypeValidationInAnalyticsTableExport: Boolean? = null,
    val keyRespectMetaDataStartEndDatesInAnalyticsTableExport: Boolean? = null,
    val keyCustomLoginPageLogo: String? = null,
    val keyCustomLoginPageTitle: String? = null,
    val keyCustomLoginPageApplicationTitle: String? = null,
    val keyCustomLoginPageApplicationIntro: String? = null,
    val keyCustomLoginPageApplicationNotification: String? = null,
    val keyCustomLoginPageApplicationLeftSideFooter: String? = null,
    val keyCustomLoginPageApplicationRightSideFooter: String? = null,
    val keyCanGrantOwnUserAuthorityGroups: Boolean? = null,
    val keyHideDailyPeriods: Boolean? = null,
    val keyHideWeeklyPeriods: Boolean? = null,
    val keyHideMonthlyPeriods: Boolean? = null,
    val keyHideBiMonthlyPeriods: Boolean? = null
)

@Serializable
data class UserSettingsResponse(
    val settings: UserSettings? = null,
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null
)

@Serializable
data class UserSettingValue(
    val value: String
)

// ========================================
// PASSWORD MODELS
// ========================================

@Serializable
data class UserPasswordUpdateRequest(
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class UserPasswordResetRequest(
    val newPassword: String
)

@Serializable
data class UserPasswordResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

@Serializable
data class UserAccountStatusRequest(
    val disabled: Boolean
)

@Serializable
data class UserSessionResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val expiredSessions: Int = 0
)

// ========================================
// INVITATION MODELS
// ========================================

@Serializable
data class UserInvitation(
    val email: String,
    val userRoles: List<String> = emptyList(),
    val userGroups: List<String> = emptyList(),
    val organisationUnits: List<String> = emptyList(),
    val dataViewOrganisationUnits: List<String> = emptyList(),
    val firstName: String? = null,
    val surname: String? = null,
    val username: String? = null
)

@Serializable
data class UserInvitationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val invitationToken: String? = null
)

@Serializable
data class UserInvitationsResponse(
    val userInvitations: List<UserInvitationInfo> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class UserInvitationInfo(
    val id: String,
    val email: String,
    val token: String,
    val status: String,
    val created: String? = null,
    val expires: String? = null,
    val invitedBy: UserReference? = null
)

@Serializable
data class UserInvitationAcceptance(
    val firstName: String,
    val surname: String,
    val username: String,
    val password: String
)

// ========================================
// ACCOUNT RECOVERY MODELS
// ========================================

@Serializable
data class AccountRecoveryRequest(
    val usernameOrEmail: String
)

@Serializable
data class PasswordResetRequest(
    val newPassword: String
)

@Serializable
data class AccountRecoveryResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null
)

// ========================================
// TWO-FACTOR AUTHENTICATION MODELS
// ========================================

@Serializable
data class TwoFactorAuthResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val secret: String? = null,
    val qrCodeUrl: String? = null
)

@Serializable
data class TwoFactorAuthDisableRequest(
    val password: String
)

@Serializable
data class TwoFactorQRCodeResponse(
    val qrCodeUrl: String,
    val secret: String
)

// ========================================
// AUTHORITIES MODELS
// ========================================

@Serializable
data class UserAuthoritiesResponse(
    val authorities: List<String> = emptyList()
)

// ========================================
// ANALYTICS MODELS
// ========================================

@Serializable
data class UserAnalyticsResponse(
    val userAnalytics: List<UserAnalytic> = emptyList()
)

@Serializable
data class UserAnalytic(
    val date: String,
    val users: Int = 0,
    val activeUsers: Int = 0,
    val newUsers: Int = 0,
    val sessions: Int = 0,
    val averageSessionDuration: Double = 0.0
)

@Serializable
data class UserLookupResponse(
    val users: List<UserLookupResult> = emptyList()
)

@Serializable
data class UserLookupResult(
    val id: String,
    val uid: String,
    val username: String,
    val firstName: String? = null,
    val surname: String? = null,
    val displayName: String? = null,
    val avatar: Avatar? = null
)

// ========================================
// IMPORT/EXPORT MODELS
// ========================================

@Serializable
data class UserImportResponse(
    val responseType: String,
    val status: String,
    val importCount: UserImportCount,
    val conflicts: List<UserImportConflict> = emptyList(),
    val typeReports: List<UserTypeReport> = emptyList(),
    val stats: UserImportStats? = null,
    val reference: String? = null
)

@Serializable
data class UserImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0
)

@Serializable
data class UserImportConflict(
    val `object`: String,
    val value: String,
    val errorCode: String? = null,
    val property: String? = null,
    val args: List<String> = emptyList()
)

@Serializable
data class UserTypeReport(
    val klass: String,
    val stats: UserImportCount,
    val objectReports: List<UserObjectReport> = emptyList()
)

@Serializable
data class UserObjectReport(
    val klass: String,
    val index: Int,
    val uid: String? = null,
    val errorReports: List<UserErrorReport> = emptyList()
)

@Serializable
data class UserErrorReport(
    val message: String,
    val mainKlass: String? = null,
    val errorCode: String? = null,
    val errorProperty: String? = null,
    val args: List<String> = emptyList()
)

@Serializable
data class UserImportStats(
    val created: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0,
    val total: Int = 0
)

@Serializable
data class UserBulkImportRequest(
    val users: List<User>
)

// ========================================
// SHARING MODELS
// ========================================

@Serializable
data class Access(
    val read: Boolean = false,
    val update: Boolean = false,
    val externalize: Boolean = false,
    val delete: Boolean = false,
    val write: Boolean = false,
    val manage: Boolean = false
)

@Serializable
data class Sharing(
    val owner: String? = null,
    val external: Boolean = false,
    val public: String? = null,
    val users: Map<String, UserAccess> = emptyMap(),
    val userGroups: Map<String, UserGroupAccess> = emptyMap()
)

@Serializable
data class UserAccess(
    val id: String,
    val access: String,
    val displayName: String? = null,
    val username: String? = null
)

@Serializable
data class UserGroupAccess(
    val id: String,
    val access: String,
    val displayName: String? = null
)

// ========================================
// ATTRIBUTE MODELS
// ========================================

@Serializable
data class AttributeValue(
    val attribute: AttributeReference,
    val value: String
)

@Serializable
data class AttributeReference(
    val id: String,
    val uid: String? = null,
    val code: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val valueType: String? = null
)

// ========================================
// TRANSLATION MODELS
// ========================================

@Serializable
data class Translation(
    val property: String,
    val locale: String,
    val value: String
)

// ========================================
// COMMON MODELS
// ========================================

@Serializable
data class Pager(
    val page: Int = 1,
    val pageCount: Int = 1,
    val total: Int = 0,
    val pageSize: Int = 50,
    val nextPage: String? = null,
    val prevPage: String? = null
)

// ========================================
// FILTER MODELS
// ========================================

@Serializable
data class UserFilter(
    val property: String,
    val operator: UserFilterOperator,
    val value: String
)

enum class UserFilterOperator {
    EQ, NE, GT, GE, LT, LE, LIKE, ILIKE, IN, NIN, TOKEN, NEMPTY, NULL, NNULL
}

// ========================================
// BULK OPERATION MODELS
// ========================================

@Serializable
data class UserBulkOperation(
    val operation: UserBulkOperationType,
    val users: List<String>,
    val parameters: Map<String, String> = emptyMap()
)

enum class UserBulkOperationType {
    ENABLE, DISABLE, DELETE, EXPIRE_SESSIONS, RESET_PASSWORD, ASSIGN_ROLE, REMOVE_ROLE, ASSIGN_GROUP, REMOVE_GROUP
}

@Serializable
data class UserBulkOperationResponse(
    val status: String,
    val message: String? = null,
    val processed: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val errors: List<UserBulkOperationError> = emptyList()
)

@Serializable
data class UserBulkOperationError(
    val userId: String,
    val username: String? = null,
    val message: String,
    val errorCode: String? = null
)