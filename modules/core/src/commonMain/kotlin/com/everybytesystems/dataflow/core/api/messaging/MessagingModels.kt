package com.everybytesystems.dataflow.core.api.messaging

import kotlinx.serialization.Serializable

// ========================================
// MESSAGE CONVERSATION MODELS
// ========================================

@Serializable
data class MessageConversation(
    val id: String? = null,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val created: String? = null,
    val lastUpdated: String? = null,
    val lastMessage: String? = null,
    val messageType: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val subject: String? = null,
    val text: String? = null,
    val read: Boolean = false,
    val followUp: Boolean = false,
    val user: UserReference? = null,
    val assignee: UserReference? = null,
    val lastSender: UserReference? = null,
    val userMessages: List<UserMessage> = emptyList(),
    val messages: List<Message> = emptyList(),
    val users: List<UserReference> = emptyList(),
    val userGroups: List<UserGroupReference> = emptyList(),
    val organisationUnits: List<OrganisationUnitReference> = emptyList(),
    val messageCount: Int = 0,
    val extendedData: Map<String, String> = emptyMap()
)

@Serializable
data class MessageConversationsResponse(
    val messageConversations: List<MessageConversation> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class Message(
    val id: String? = null,
    val uid: String? = null,
    val text: String,
    val metaData: String? = null,
    val created: String? = null,
    val sender: UserReference? = null,
    val internal: Boolean = false,
    val attachments: List<MessageAttachment> = emptyList()
)

@Serializable
data class UserMessage(
    val id: String? = null,
    val user: UserReference,
    val read: Boolean = false,
    val followUp: Boolean = false,
    val lastUpdated: String? = null
)

@Serializable
data class MessageAttachment(
    val id: String,
    val name: String,
    val contentType: String? = null,
    val contentLength: Long? = null,
    val fileResource: FileResourceReference? = null
)

@Serializable
data class UserReference(
    val id: String,
    val uid: String? = null,
    val username: String? = null,
    val firstName: String? = null,
    val surname: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
)

@Serializable
data class UserGroupReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class OrganisationUnitReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val path: String? = null
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
// MESSAGE CONVERSATION OPERATIONS
// ========================================

@Serializable
data class MessageConversationCreate(
    val subject: String,
    val text: String,
    val messageType: String = "PRIVATE",
    val priority: String = "MEDIUM",
    val users: List<String> = emptyList(),
    val userGroups: List<String> = emptyList(),
    val organisationUnits: List<String> = emptyList(),
    val attachments: List<String> = emptyList(),
    val extendedData: Map<String, String> = emptyMap()
)

@Serializable
data class MessageReply(
    val text: String,
    val internal: Boolean = false,
    val attachments: List<String> = emptyList()
)

@Serializable
data class MessageConversationUpdate(
    val subject: String? = null,
    val messageType: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val followUp: Boolean? = null,
    val assignee: String? = null
)

@Serializable
data class MessageAssignment(
    val assignee: String
)

@Serializable
data class MessageConversationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: MessageConversationResponseDetails? = null
)

@Serializable
data class MessageConversationResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val importCount: MessageImportCount? = null
)

@Serializable
data class MessageImportCount(
    val imported: Int = 0,
    val updated: Int = 0,
    val deleted: Int = 0,
    val ignored: Int = 0
)

// ========================================
// MESSAGE FEEDBACK MODELS
// ========================================

@Serializable
data class MessageFeedbackResponse(
    val feedback: List<MessageFeedback> = emptyList()
)

@Serializable
data class MessageFeedback(
    val id: String,
    val user: UserReference,
    val rating: Int, // 1-5
    val comment: String? = null,
    val created: String? = null
)

// ========================================
// BULK MESSAGE OPERATIONS
// ========================================

@Serializable
data class MessageBulkOperation(
    val operation: MessageBulkOperationType,
    val messageIds: List<String>,
    val userId: String? = null,
    val assignee: String? = null,
    val status: String? = null
)

@Serializable
data class MessageBulkOperationResponse(
    val status: String,
    val message: String? = null,
    val processed: Int = 0,
    val successful: Int = 0,
    val failed: Int = 0,
    val errors: List<MessageBulkOperationError> = emptyList()
)

@Serializable
data class MessageBulkOperationError(
    val messageId: String,
    val subject: String? = null,
    val message: String,
    val errorCode: String? = null
)

// ========================================
// SMS MODELS
// ========================================

@Serializable
data class SMSMessage(
    val id: String? = null,
    val uid: String? = null,
    val text: String,
    val originator: String? = null,
    val recipients: List<String> = emptyList(),
    val phoneNumbers: List<String> = emptyList(),
    val gateway: String? = null,
    val status: String? = null,
    val date: String? = null,
    val sentDate: String? = null,
    val receivedDate: String? = null,
    val encoding: String = "PLAIN_TEXT",
    val submitDate: String? = null,
    val user: UserReference? = null,
    val parsedMessage: ParsedSMSMessage? = null
)

@Serializable
data class SMSBulkMessage(
    val messages: List<SMSMessage>
)

@Serializable
data class SMSMessagesResponse(
    val inboundSms: List<SMSMessage> = emptyList(),
    val outboundSms: List<SMSMessage> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class SMSResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: SMSResponseDetails? = null
)

@Serializable
data class SMSBulkResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val responses: List<SMSResponse> = emptyList(),
    val summary: SMSBulkSummary? = null
)

@Serializable
data class SMSResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val sent: Int = 0,
    val failed: Int = 0
)

@Serializable
data class SMSBulkSummary(
    val total: Int = 0,
    val sent: Int = 0,
    val failed: Int = 0,
    val pending: Int = 0
)

@Serializable
data class ParsedSMSMessage(
    val commandType: String? = null,
    val dataValues: List<SMSDataValue> = emptyList(),
    val completionDate: String? = null,
    val eventDate: String? = null,
    val orgUnit: String? = null,
    val program: String? = null,
    val programStage: String? = null
)

@Serializable
data class SMSDataValue(
    val dataElement: String,
    val categoryOptionCombo: String? = null,
    val value: String
)

// ========================================
// SMS CONFIGURATION MODELS
// ========================================

@Serializable
data class SMSConfiguration(
    val enabled: Boolean = false,
    val defaultGateway: String? = null,
    val gateways: List<SMSGateway> = emptyList(),
    val commands: List<SMSCommand> = emptyList(),
    val settings: SMSSettings? = null
)

@Serializable
data class SMSConfigurationResponse(
    val configuration: SMSConfiguration,
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null
)

@Serializable
data class SMSGateway(
    val id: String? = null,
    val uid: String? = null,
    val name: String,
    val type: String, // GENERIC, BULK_SMS, CLICKATELL, etc.
    val username: String? = null,
    val password: String? = null,
    val urlTemplate: String? = null,
    val parameters: List<SMSGatewayParameter> = emptyList(),
    val isDefault: Boolean = false,
    val sendUrlParameters: Boolean = true,
    val contentType: String = "APPLICATION_FORM_URLENCODED",
    val useGet: Boolean = false
)

@Serializable
data class SMSGatewayParameter(
    val key: String,
    val value: String,
    val header: Boolean = false,
    val encode: Boolean = false,
    val confidential: Boolean = false
)

@Serializable
data class SMSGatewaysResponse(
    val gateways: List<SMSGateway> = emptyList()
)

@Serializable
data class SMSGatewayResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val gateway: SMSGateway? = null
)

@Serializable
data class SMSCommand(
    val id: String? = null,
    val uid: String? = null,
    val name: String,
    val parser: String, // KEY_VALUE_PARSER, ALERT_PARSER, etc.
    val parserType: String,
    val separator: String = "=",
    val defaultMessage: String? = null,
    val receivedMessage: String? = null,
    val wrongFormatMessage: String? = null,
    val noUserMessage: String? = null,
    val moreThanOneOrgUnitMessage: String? = null,
    val successMessage: String? = null,
    val currentPeriodUsedForReporting: Boolean = false,
    val completenessMethod: String = "AT_LEAST_ONE_DATAVALUE",
    val dataset: DataSetReference? = null,
    val program: ProgramReference? = null,
    val programStage: ProgramStageReference? = null,
    val codes: List<SMSCode> = emptyList(),
    val specialCharacters: List<SMSSpecialCharacter> = emptyList()
)

@Serializable
data class SMSCode(
    val id: String? = null,
    val code: String,
    val dataElement: DataElementReference? = null,
    val optionSet: OptionSetReference? = null,
    val trackedEntityAttribute: TrackedEntityAttributeReference? = null,
    val formula: String? = null,
    val compulsory: Boolean = false
)

@Serializable
data class SMSSpecialCharacter(
    val name: String,
    val value: String
)

@Serializable
data class SMSSettings(
    val maxMessageLength: Int = 160,
    val concatenatedMessageSupport: Boolean = false,
    val deliveryReportEnabled: Boolean = false,
    val retryAttempts: Int = 3,
    val retryInterval: Int = 5000,
    val batchSize: Int = 100
)

@Serializable
data class SMSTestMessage(
    val phoneNumber: String,
    val message: String,
    val gateway: String? = null
)

@Serializable
data class SMSTestResponse(
    val success: Boolean,
    val message: String? = null,
    val deliveryTime: Long? = null,
    val gatewayResponse: String? = null
)

// ========================================
// EMAIL NOTIFICATION MODELS
// ========================================

@Serializable
data class EmailNotification(
    val id: String? = null,
    val subject: String,
    val message: String,
    val recipients: List<String> = emptyList(),
    val ccRecipients: List<String> = emptyList(),
    val bccRecipients: List<String> = emptyList(),
    val sender: String? = null,
    val replyTo: String? = null,
    val priority: String = "NORMAL",
    val contentType: String = "TEXT_HTML",
    val attachments: List<EmailAttachment> = emptyList(),
    val template: String? = null,
    val templateVariables: Map<String, String> = emptyMap(),
    val scheduledDate: String? = null
)

@Serializable
data class EmailBulkNotification(
    val notifications: List<EmailNotification>
)

@Serializable
data class EmailAttachment(
    val name: String,
    val contentType: String,
    val content: String, // Base64 encoded
    val fileResource: String? = null
)

@Serializable
data class EmailNotificationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: EmailNotificationResponseDetails? = null
)

@Serializable
data class EmailBulkNotificationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val responses: List<EmailNotificationResponse> = emptyList(),
    val summary: EmailBulkSummary? = null
)

@Serializable
data class EmailNotificationResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val sent: Int = 0,
    val failed: Int = 0
)

@Serializable
data class EmailBulkSummary(
    val total: Int = 0,
    val sent: Int = 0,
    val failed: Int = 0,
    val pending: Int = 0
)

@Serializable
data class EmailConfiguration(
    val enabled: Boolean = false,
    val smtpHost: String? = null,
    val smtpPort: Int = 587,
    val smtpUsername: String? = null,
    val smtpPassword: String? = null,
    val smtpTls: Boolean = true,
    val smtpSsl: Boolean = false,
    val fromAddress: String? = null,
    val fromName: String? = null,
    val replyToAddress: String? = null,
    val maxAttachmentSize: Long = 10485760, // 10MB
    val connectionTimeout: Int = 30000,
    val readTimeout: Int = 30000
)

@Serializable
data class EmailConfigurationResponse(
    val configuration: EmailConfiguration,
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null
)

@Serializable
data class EmailTestMessage(
    val recipient: String,
    val subject: String = "DHIS2 Email Test",
    val message: String = "This is a test email from DHIS2."
)

@Serializable
data class EmailTestResponse(
    val success: Boolean,
    val message: String? = null,
    val deliveryTime: Long? = null,
    val smtpResponse: String? = null
)

// ========================================
// PUSH NOTIFICATION MODELS
// ========================================

@Serializable
data class PushNotification(
    val id: String? = null,
    val title: String,
    val message: String,
    val recipients: List<String> = emptyList(),
    val deviceTokens: List<String> = emptyList(),
    val data: Map<String, String> = emptyMap(),
    val priority: String = "NORMAL",
    val sound: String? = null,
    val badge: Int? = null,
    val icon: String? = null,
    val color: String? = null,
    val clickAction: String? = null,
    val timeToLive: Int? = null,
    val scheduledDate: String? = null,
    val collapseKey: String? = null
)

@Serializable
data class PushBulkNotification(
    val notifications: List<PushNotification>
)

@Serializable
data class PushNotificationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: PushNotificationResponseDetails? = null
)

@Serializable
data class PushBulkNotificationResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val responses: List<PushNotificationResponse> = emptyList(),
    val summary: PushBulkSummary? = null
)

@Serializable
data class PushNotificationResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val sent: Int = 0,
    val failed: Int = 0,
    val messageId: String? = null
)

@Serializable
data class PushBulkSummary(
    val total: Int = 0,
    val sent: Int = 0,
    val failed: Int = 0,
    val pending: Int = 0
)

@Serializable
data class PushDeviceRegistration(
    val deviceToken: String,
    val platform: String, // ANDROID, IOS, WEB
    val appVersion: String? = null,
    val osVersion: String? = null,
    val deviceModel: String? = null,
    val user: String? = null,
    val topics: List<String> = emptyList()
)

@Serializable
data class PushDeviceResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val device: PushDeviceInfo? = null
)

@Serializable
data class PushDeviceInfo(
    val id: String,
    val deviceToken: String,
    val platform: String,
    val registered: String? = null,
    val lastActive: String? = null,
    val user: UserReference? = null
)

@Serializable
data class PushConfiguration(
    val enabled: Boolean = false,
    val fcmServerKey: String? = null,
    val fcmSenderId: String? = null,
    val apnsKeyId: String? = null,
    val apnsTeamId: String? = null,
    val apnsBundleId: String? = null,
    val apnsPrivateKey: String? = null,
    val apnsProduction: Boolean = false,
    val webPushVapidPublicKey: String? = null,
    val webPushVapidPrivateKey: String? = null,
    val webPushVapidSubject: String? = null
)

@Serializable
data class PushConfigurationResponse(
    val configuration: PushConfiguration,
    val httpStatus: String? = null,
    val httpStatusCode: Int? = null,
    val status: String? = null,
    val message: String? = null
)

// ========================================
// NOTIFICATION TEMPLATE MODELS
// ========================================

@Serializable
data class NotificationTemplate(
    val id: String? = null,
    val uid: String? = null,
    val name: String,
    val displayName: String? = null,
    val description: String? = null,
    val created: String? = null,
    val createdBy: UserReference? = null,
    val lastUpdated: String? = null,
    val lastUpdatedBy: UserReference? = null,
    val templateType: String, // SMS, EMAIL, PUSH, SYSTEM
    val subject: String? = null,
    val message: String,
    val variables: List<TemplateVariable> = emptyList(),
    val defaultLanguage: String = "en",
    val translations: List<TemplateTranslation> = emptyList(),
    val active: Boolean = true
)

@Serializable
data class NotificationTemplatesResponse(
    val notificationTemplates: List<NotificationTemplate> = emptyList(),
    val pager: Pager? = null
)

@Serializable
data class NotificationTemplateResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String? = null,
    val response: NotificationTemplateResponseDetails? = null
)

@Serializable
data class NotificationTemplateResponseDetails(
    val responseType: String? = null,
    val uid: String? = null,
    val importCount: MessageImportCount? = null
)

@Serializable
data class TemplateVariable(
    val name: String,
    val description: String? = null,
    val required: Boolean = false,
    val defaultValue: String? = null,
    val type: String = "STRING" // STRING, NUMBER, DATE, BOOLEAN
)

@Serializable
data class TemplateTranslation(
    val locale: String,
    val subject: String? = null,
    val message: String
)

// ========================================
// MESSAGE ANALYTICS MODELS
// ========================================

@Serializable
data class MessageAnalyticsResponse(
    val analytics: List<MessageAnalytic> = emptyList(),
    val summary: MessageAnalyticsSummary? = null
)

@Serializable
data class MessageAnalytic(
    val messageType: String? = null,
    val priority: String? = null,
    val status: String? = null,
    val user: String? = null,
    val date: String? = null,
    val count: Int = 0,
    val averageResponseTime: Double = 0.0
)

@Serializable
data class MessageAnalyticsSummary(
    val totalMessages: Int = 0,
    val openMessages: Int = 0,
    val solvedMessages: Int = 0,
    val averageResponseTime: Double = 0.0,
    val byType: Map<String, MessageTypeStats> = emptyMap(),
    val byPriority: Map<String, MessagePriorityStats> = emptyMap()
)

@Serializable
data class MessageTypeStats(
    val count: Int = 0,
    val percentage: Double = 0.0,
    val averageResponseTime: Double = 0.0
)

@Serializable
data class MessagePriorityStats(
    val count: Int = 0,
    val percentage: Double = 0.0,
    val averageResponseTime: Double = 0.0
)

@Serializable
data class SMSAnalyticsResponse(
    val analytics: List<SMSAnalytic> = emptyList(),
    val summary: SMSAnalyticsSummary? = null
)

@Serializable
data class SMSAnalytic(
    val direction: String? = null,
    val status: String? = null,
    val gateway: String? = null,
    val date: String? = null,
    val count: Int = 0,
    val cost: Double = 0.0
)

@Serializable
data class SMSAnalyticsSummary(
    val totalSMS: Int = 0,
    val sentSMS: Int = 0,
    val receivedSMS: Int = 0,
    val failedSMS: Int = 0,
    val totalCost: Double = 0.0,
    val deliveryRate: Double = 0.0,
    val byGateway: Map<String, SMSGatewayStats> = emptyMap()
)

@Serializable
data class SMSGatewayStats(
    val sent: Int = 0,
    val failed: Int = 0,
    val cost: Double = 0.0,
    val deliveryRate: Double = 0.0
)

@Serializable
data class NotificationDeliveryStatsResponse(
    val stats: NotificationDeliveryStats
)

@Serializable
data class NotificationDeliveryStats(
    val totalNotifications: Int = 0,
    val deliveredNotifications: Int = 0,
    val failedNotifications: Int = 0,
    val pendingNotifications: Int = 0,
    val deliveryRate: Double = 0.0,
    val byType: Map<String, NotificationTypeStats> = emptyMap(),
    val byDate: Map<String, NotificationDateStats> = emptyMap()
)

@Serializable
data class NotificationTypeStats(
    val sent: Int = 0,
    val delivered: Int = 0,
    val failed: Int = 0,
    val deliveryRate: Double = 0.0
)

@Serializable
data class NotificationDateStats(
    val sent: Int = 0,
    val delivered: Int = 0,
    val failed: Int = 0
)

// ========================================
// REFERENCE MODELS
// ========================================

@Serializable
data class DataSetReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class ProgramReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class ProgramStageReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class DataElementReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class OptionSetReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
)

@Serializable
data class TrackedEntityAttributeReference(
    val id: String,
    val uid: String? = null,
    val name: String? = null,
    val displayName: String? = null
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