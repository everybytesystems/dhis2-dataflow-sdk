package com.everybytesystems.dataflow.core.api.files

import kotlinx.serialization.Serializable

// ========================================
// FILE RESOURCE MODELS
// ========================================

@Serializable
data class FileResource(
    val id: String? = null,
    val uid: String? = null,
    val code: String? = null,
    val name: String,
    val displayName: String? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val contentType: String,
    val contentLength: Long,
    val contentMd5: String? = null,
    val storageStatus: String? = null,
    val domain: String? = null,
    val assigned: Boolean = false,
    val hasImage: Boolean = false,
    val storageKey: String? = null,
    val externalStorageLocation: String? = null,
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class FileResourcesResponse(
    val fileResources: List<FileResource> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class UserReference(
    val id: String,
    val uid: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
    val displayName: String? = null
)

// ========================================
// FILE UPLOAD MODELS
// ========================================

@Serializable
data class FileUploadRequest(
    val name: String,
    val contentType: String,
    val contentLength: Long,
    val data: String // In real implementation, this would be handled differently
)

@Serializable
data class FileUrlUploadRequest(
    val url: String
)

@Serializable
data class FileUploadResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: FileUploadResponseDetails? = null
)

@Serializable
data class FileUploadResponseDetails(
    val responseType: String? = null,
    val fileResource: FileResource? = null,
    val uid: String? = null
)

// ========================================
// FILE DOWNLOAD MODELS
// ========================================

@Serializable
data class FileDownloadResponse(
    val content: ByteArray,
    val contentType: String,
    val contentLength: Long,
    val fileName: String? = null,
    val lastModified: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FileDownloadResponse

        if (!content.contentEquals(other.content)) return false
        if (contentType != other.contentType) return false
        if (contentLength != other.contentLength) return false
        if (fileName != other.fileName) return false
        if (lastModified != other.lastModified) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + contentType.hashCode()
        result = 31 * result + contentLength.hashCode()
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (lastModified?.hashCode() ?: 0)
        return result
    }
}

// ========================================
// FILE RESOURCE RESPONSE MODELS
// ========================================

@Serializable
data class FileResourceResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: FileResourceResponseDetails? = null
)

@Serializable
data class FileResourceResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val importCount: FileResourceImportCount? = null
)

@Serializable
data class FileResourceImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0
)

// ========================================
// FILE RESOURCE DOMAINS MODELS
// ========================================

@Serializable
data class FileResourceDomainsResponse(
    val domains: List<FileResourceDomainInfo> = emptyList()
)

@Serializable
data class FileResourceDomainInfo(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val maxFileSize: Long? = null,
    val allowedContentTypes: List<String> = emptyList(),
    val storageLocation: String? = null
)

// ========================================
// EXTERNAL STORAGE MODELS
// ========================================

@Serializable
data class ExternalStorageConfig(
    val provider: String, // AWS_S3, GOOGLE_CLOUD, AZURE_BLOB, MINIO
    val enabled: Boolean = false,
    val bucketName: String? = null,
    val region: String? = null,
    val accessKey: String? = null,
    val secretKey: String? = null,
    val endpoint: String? = null,
    val pathPrefix: String? = null,
    val encryption: ExternalStorageEncryption? = null,
    val credentials: ExternalStorageCredentials? = null
)

@Serializable
data class ExternalStorageEncryption(
    val enabled: Boolean = false,
    val algorithm: String? = null,
    val keyId: String? = null
)

@Serializable
data class ExternalStorageCredentials(
    val type: String, // ACCESS_KEY, IAM_ROLE, SERVICE_ACCOUNT
    val accessKey: String? = null,
    val secretKey: String? = null,
    val roleArn: String? = null,
    val serviceAccountKey: String? = null
)

@Serializable
data class ExternalStorageResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val configuration: ExternalStorageConfig? = null
)

@Serializable
data class ExternalStorageTestResponse(
    val success: Boolean,
    val message: String? = null,
    val connectionTime: Long? = null,
    val errors: List<String> = emptyList()
)

@Serializable
data class FileMigrationResponse(
    val status: String,
    val message: String? = null,
    val totalFiles: Int = 0,
    val migratedFiles: Int = 0,
    val failedFiles: Int = 0,
    val estimatedTime: String? = null,
    val jobId: String? = null
)

// ========================================
// DOCUMENT MODELS
// ========================================

@Serializable
data class Document(
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
    val url: String? = null,
    val external: Boolean = false,
    val contentType: String? = null,
    val attachment: Boolean = false,
    val fileResource: FileResourceReference? = null,
    val access: Access? = null,
    val sharing: Sharing? = null,
    val userAccesses: List<UserAccess> = emptyList(),
    val userGroupAccesses: List<UserGroupAccess> = emptyList(),
    val attributeValues: List<AttributeValue> = emptyList(),
    val translations: List<Translation> = emptyList()
)

@Serializable
data class DocumentsResponse(
    val documents: List<Document> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class DocumentResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: DocumentResponseDetails? = null
)

@Serializable
data class DocumentResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val importCount: FileResourceImportCount? = null
)

@Serializable
data class FileResourceReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val contentType: String? = null,
    val contentLength: Long? = null
)

// ========================================
// FILE ANALYTICS MODELS
// ========================================

@Serializable
data class FileResourceAnalyticsResponse(
    val analytics: List<FileResourceAnalytic> = emptyList(),
    val summary: FileResourceAnalyticsSummary? = null
)

@Serializable
data class FileResourceAnalytic(
    val domain: String? = null,
    val contentType: String? = null,
    val user: String? = null,
    val date: String? = null,
    val count: Int = 0,
    val totalSize: Long = 0,
    val averageSize: Long = 0
)

@Serializable
data class FileResourceAnalyticsSummary(
    val totalFiles: Int = 0,
    val totalSize: Long = 0,
    val averageFileSize: Long = 0,
    val byDomain: Map<String, FileResourceDomainSummary> = emptyMap(),
    val byContentType: Map<String, FileResourceContentTypeSummary> = emptyMap()
)

@Serializable
data class FileResourceDomainSummary(
    val count: Int = 0,
    val totalSize: Long = 0,
    val averageSize: Long = 0
)

@Serializable
data class FileResourceContentTypeSummary(
    val count: Int = 0,
    val totalSize: Long = 0,
    val averageSize: Long = 0
)

@Serializable
data class FileStorageStatisticsResponse(
    val statistics: FileStorageStatistics
)

@Serializable
data class FileStorageStatistics(
    val totalFiles: Int = 0,
    val totalSize: Long = 0,
    val availableSpace: Long? = null,
    val usedSpace: Long = 0,
    val usagePercentage: Double = 0.0,
    val orphanedFiles: Int = 0,
    val orphanedSize: Long = 0,
    val byDomain: Map<String, FileStorageDomainStats> = emptyMap(),
    val externalStorage: ExternalStorageStats? = null
)

@Serializable
data class FileStorageDomainStats(
    val files: Int = 0,
    val size: Long = 0,
    val percentage: Double = 0.0
)

@Serializable
data class ExternalStorageStats(
    val enabled: Boolean = false,
    val provider: String? = null,
    val files: Int = 0,
    val size: Long = 0,
    val connectionStatus: String? = null
)

// ========================================
// BULK OPERATION MODELS
// ========================================

@Serializable
data class FileBulkDeleteRequest(
    val fileIds: List<String>
)

@Serializable
data class FileBulkMoveRequest(
    val fileIds: List<String>,
    val targetDomain: FileResourceDomain
)

@Serializable
data class FileBulkOperationResponse(
    val status: String,
    val message: String? = null,
    val processed: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val errors: List<FileBulkOperationError> = emptyList()
)

@Serializable
data class FileBulkOperationError(
    val fileId: String,
    val fileName: String? = null,
    val message: String,
    val errorCode: String? = null
)

@Serializable
data class FileCleanupResponse(
    val status: String,
    val message: String? = null,
    val orphanedFiles: Int = 0,
    val cleanedFiles: Int = 0,
    val reclaimedSpace: Long = 0,
    val errors: List<String> = emptyList()
)

// ========================================
// FILE VALIDATION MODELS
// ========================================

@Serializable
data class FileValidationRequest(
    val fileName: String,
    val contentType: String,
    val fileSize: Long,
    val domain: FileResourceDomain
)

@Serializable
data class FileValidationResponse(
    val valid: Boolean,
    val errors: List<FileValidationError> = emptyList(),
    val warnings: List<FileValidationWarning> = emptyList()
)

@Serializable
data class FileValidationError(
    val code: String,
    val message: String,
    val field: String? = null
)

@Serializable
data class FileValidationWarning(
    val code: String,
    val message: String,
    val field: String? = null
)

@Serializable
data class FileUploadConstraintsResponse(
    val constraints: FileUploadConstraints
)

@Serializable
data class FileUploadConstraints(
    val maxFileSize: Long,
    val allowedContentTypes: List<String> = emptyList(),
    val blockedContentTypes: List<String> = emptyList(),
    val allowedExtensions: List<String> = emptyList(),
    val blockedExtensions: List<String> = emptyList(),
    val virusScanEnabled: Boolean = false,
    val domainConstraints: Map<String, FileUploadDomainConstraints> = emptyMap()
)

@Serializable
data class FileUploadDomainConstraints(
    val maxFileSize: Long? = null,
    val allowedContentTypes: List<String> = emptyList(),
    val allowedExtensions: List<String> = emptyList(),
    val requiresApproval: Boolean = false
)

// ========================================
// FILE SHARING MODELS
// ========================================

@Serializable
data class FileResourceSharing(
    val owner: String? = null,
    val external: Boolean = false,
    val public: String? = null,
    val users: Map<String, UserAccess> = emptyMap(),
    val userGroups: Map<String, UserGroupAccess> = emptyMap(),
    val publicLink: FilePublicLink? = null
)

@Serializable
data class FilePublicLink(
    val url: String,
    val token: String,
    val expiresAt: String? = null,
    val accessCount: Int = 0,
    val maxAccess: Int? = null,
    val passwordProtected: Boolean = false
)

@Serializable
data class FilePublicLinkResponse(
    val publicLink: FilePublicLink,
    val message: String? = null
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
// FILE PROCESSING MODELS
// ========================================

@Serializable
data class FileProcessingJob(
    val id: String,
    val type: String, // VIRUS_SCAN, THUMBNAIL_GENERATION, METADATA_EXTRACTION
    val status: String, // PENDING, PROCESSING, COMPLETED, FAILED
    val fileResourceId: String,
    val created: String? = null,
    val started: String? = null,
    val completed: String? = null,
    val progress: Int = 0,
    val result: FileProcessingResult? = null,
    val error: String? = null
)

@Serializable
data class FileProcessingResult(
    val virusScanResult: VirusScanResult? = null,
    val thumbnailGenerated: Boolean = false,
    val metadataExtracted: FileMetadata? = null
)

@Serializable
data class VirusScanResult(
    val clean: Boolean,
    val threats: List<String> = emptyList(),
    val scanEngine: String? = null,
    val scanTime: String? = null
)

@Serializable
data class FileMetadata(
    val width: Int? = null,
    val height: Int? = null,
    val duration: Double? = null,
    val bitrate: Int? = null,
    val format: String? = null,
    val codec: String? = null,
    val colorSpace: String? = null,
    val exifData: Map<String, String> = emptyMap()
)

// ========================================
// FILE VERSIONING MODELS
// ========================================

@Serializable
data class FileVersion(
    val id: String,
    val version: Int,
    val fileResourceId: String,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val comment: String? = null,
    val contentLength: Long,
    val contentMd5: String? = null,
    val current: Boolean = false
)

@Serializable
data class FileVersionsResponse(
    val versions: List<FileVersion> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class FileVersionCreateRequest(
    val comment: String? = null,
    val makeCurrent: Boolean = true
)

// ========================================
// FILE SEARCH MODELS
// ========================================

@Serializable
data class FileSearchRequest(
    val query: String? = null,
    val contentType: List<String> = emptyList(),
    val domain: List<String> = emptyList(),
    val sizeMin: Long? = null,
    val sizeMax: Long? = null,
    val createdAfter: String? = null,
    val createdBefore: String? = null,
    val createdBy: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val sortBy: String = "created",
    val sortOrder: String = "desc",
    val page: Int = 1,
    val pageSize: Int = 20
)

@Serializable
data class FileSearchResponse(
    val files: List<FileResource> = emptyList(),
    val pager: Pager? = null,
    val facets: FileSearchFacets? = null
)

@Serializable
data class FileSearchFacets(
    val contentTypes: Map<String, Int> = emptyMap(),
    val domains: Map<String, Int> = emptyMap(),
    val sizes: Map<String, Int> = emptyMap(),
    val creators: Map<String, Int> = emptyMap()
)