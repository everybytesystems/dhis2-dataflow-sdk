package com.everybytesystems.dataflow.core.api.messaging

import com.everybytesystems.dataflow.core.api.base.BaseApi
import com.everybytesystems.dataflow.core.config.DHIS2Config
import com.everybytesystems.dataflow.core.network.ApiResponse
import com.everybytesystems.dataflow.core.version.DHIS2Version
import io.ktor.client.*
import kotlinx.serialization.Serializable

/**
 * Complete Messaging API implementation for DHIS2 2.36+
 * Supports message conversations, SMS, email notifications, and push notifications
 */
class MessagingApi(
    httpClient: HttpClient,
    config: DHIS2Config,
    private val version: DHIS2Version
) : BaseApi(httpClient, config) {
    
    // ========================================
    // MESSAGE CONVERSATIONS
    // ========================================
    
    /**
     * Get message conversations
     */
    suspend fun getMessageConversations(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        messageType: MessageType? = null,
        priority: MessagePriority? = null,
        status: MessageStatus? = null
    ): ApiResponse<MessageConversationsResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            messageType?.let { put("messageType", it.name) }
            priority?.let { put("priority", it.name) }
            status?.let { put("status", it.name) }
        }
        
        return get("messageConversations", params)
    }
    
    /**
     * Get a specific message conversation
     */
    suspend fun getMessageConversation(
        id: String,
        fields: String = "*",
        markRead: Boolean = false
    ): ApiResponse<MessageConversation> {
        val params = buildMap {
            put("fields", fields)
            if (markRead) put("markRead", "true")
        }
        return get("messageConversations/$id", params)
    }
    
    /**
     * Create a new message conversation
     */
    suspend fun createMessageConversation(
        message: MessageConversationCreate
    ): ApiResponse<MessageConversationResponse> {
        return post("messageConversations", message)
    }
    
    /**
     * Reply to a message conversation
     */
    suspend fun replyToMessageConversation(
        id: String,
        reply: MessageReply
    ): ApiResponse<MessageConversationResponse> {
        return post("messageConversations/$id", reply)
    }
    
    /**
     * Update message conversation
     */
    suspend fun updateMessageConversation(
        id: String,
        update: MessageConversationUpdate
    ): ApiResponse<MessageConversationResponse> {
        return put("messageConversations/$id", update)
    }
    
    /**
     * Delete message conversation
     */
    suspend fun deleteMessageConversation(id: String): ApiResponse<MessageConversationResponse> {
        return delete("messageConversations/$id")
    }
    
    /**
     * Mark message conversation as read
     */
    suspend fun markMessageAsRead(
        id: String,
        userId: String? = null
    ): ApiResponse<MessageConversationResponse> {
        val params = buildMap {
            userId?.let { put("user", it) }
        }
        return post("messageConversations/$id/read", emptyMap<String, Any>(), params)
    }
    
    /**
     * Mark message conversation as unread
     */
    suspend fun markMessageAsUnread(
        id: String,
        userId: String? = null
    ): ApiResponse<MessageConversationResponse> {
        val params = buildMap {
            userId?.let { put("user", it) }
        }
        return post("messageConversations/$id/unread", emptyMap<String, Any>(), params)
    }
    
    /**
     * Assign message conversation
     */
    suspend fun assignMessageConversation(
        id: String,
        assigneeId: String
    ): ApiResponse<MessageConversationResponse> {
        val assignment = MessageAssignment(assigneeId)
        return post("messageConversations/$id/assign", assignment)
    }
    
    /**
     * Get message conversation feedback
     */
    suspend fun getMessageConversationFeedback(
        id: String
    ): ApiResponse<MessageFeedbackResponse> {
        return get("messageConversations/$id/feedback")
    }
    
    // ========================================
    // BULK MESSAGE OPERATIONS
    // ========================================
    
    /**
     * Bulk mark messages as read
     */
    suspend fun bulkMarkMessagesAsRead(
        messageIds: List<String>,
        userId: String? = null
    ): ApiResponse<MessageBulkOperationResponse> {
        val operation = MessageBulkOperation(
            operation = MessageBulkOperationType.MARK_READ,
            messageIds = messageIds,
            userId = userId
        )
        return post("messageConversations/bulk", operation)
    }
    
    /**
     * Bulk mark messages as unread
     */
    suspend fun bulkMarkMessagesAsUnread(
        messageIds: List<String>,
        userId: String? = null
    ): ApiResponse<MessageBulkOperationResponse> {
        val operation = MessageBulkOperation(
            operation = MessageBulkOperationType.MARK_UNREAD,
            messageIds = messageIds,
            userId = userId
        )
        return post("messageConversations/bulk", operation)
    }
    
    /**
     * Bulk delete messages
     */
    suspend fun bulkDeleteMessages(
        messageIds: List<String>
    ): ApiResponse<MessageBulkOperationResponse> {
        val operation = MessageBulkOperation(
            operation = MessageBulkOperationType.DELETE,
            messageIds = messageIds
        )
        return post("messageConversations/bulk", operation)
    }
    
    // ========================================
    // SMS MESSAGING
    // ========================================
    
    /**
     * Send SMS message
     */
    suspend fun sendSMS(
        sms: SMSMessage
    ): ApiResponse<SMSResponse> {
        return post("sms/outbound", sms)
    }
    
    /**
     * Send bulk SMS messages
     */
    suspend fun sendBulkSMS(
        messages: List<SMSMessage>
    ): ApiResponse<SMSBulkResponse> {
        val bulkSMS = SMSBulkMessage(messages)
        return post("sms/outbound/bulk", bulkSMS)
    }
    
    /**
     * Get SMS messages
     */
    suspend fun getSMSMessages(
        fields: String = "*",
        filter: List<String> = emptyList(),
        order: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        status: SMSStatus? = null,
        direction: SMSDirection? = null
    ): ApiResponse<SMSMessagesResponse> {
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            order?.let { put("order", it) }
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            status?.let { put("status", it.name) }
            direction?.let { put("direction", it.name) }
        }
        
        return get("sms", params)
    }
    
    /**
     * Get inbound SMS messages
     */
    suspend fun getInboundSMS(
        fields: String = "*",
        page: Int? = null,
        pageSize: Int? = null,
        unprocessed: Boolean? = null
    ): ApiResponse<SMSMessagesResponse> {
        
        val params = buildMap {
            put("fields", fields)
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            unprocessed?.let { put("unprocessed", it.toString()) }
        }
        
        return get("sms/inbound", params)
    }
    
    /**
     * Get outbound SMS messages
     */
    suspend fun getOutboundSMS(
        fields: String = "*",
        page: Int? = null,
        pageSize: Int? = null,
        status: SMSStatus? = null
    ): ApiResponse<SMSMessagesResponse> {
        
        val params = buildMap {
            put("fields", fields)
            page?.let { put("page", it.toString()) }
            pageSize?.let { put("pageSize", it.toString()) }
            status?.let { put("status", it.name) }
        }
        
        return get("sms/outbound", params)
    }
    
    /**
     * Delete SMS message
     */
    suspend fun deleteSMS(id: String): ApiResponse<SMSResponse> {
        return delete("sms/$id")
    }
    
    /**
     * Mark SMS as processed
     */
    suspend fun markSMSAsProcessed(id: String): ApiResponse<SMSResponse> {
        return post("sms/$id/processed", emptyMap<String, Any>())
    }
    
    // ========================================
    // SMS CONFIGURATION
    // ========================================
    
    /**
     * Get SMS configuration
     */
    suspend fun getSMSConfiguration(): ApiResponse<SMSConfigurationResponse> {
        return get("sms/config")
    }
    
    /**
     * Update SMS configuration
     */
    suspend fun updateSMSConfiguration(
        config: SMSConfiguration
    ): ApiResponse<SMSConfigurationResponse> {
        return put("sms/config", config)
    }
    
    /**
     * Test SMS configuration
     */
    suspend fun testSMSConfiguration(
        testMessage: SMSTestMessage
    ): ApiResponse<SMSTestResponse> {
        return post("sms/config/test", testMessage)
    }
    
    /**
     * Get SMS gateways
     */
    suspend fun getSMSGateways(): ApiResponse<SMSGatewaysResponse> {
        return get("sms/gateways")
    }
    
    /**
     * Create SMS gateway
     */
    suspend fun createSMSGateway(
        gateway: SMSGateway
    ): ApiResponse<SMSGatewayResponse> {
        return post("sms/gateways", gateway)
    }
    
    /**
     * Update SMS gateway
     */
    suspend fun updateSMSGateway(
        id: String,
        gateway: SMSGateway
    ): ApiResponse<SMSGatewayResponse> {
        return put("sms/gateways/$id", gateway)
    }
    
    /**
     * Delete SMS gateway
     */
    suspend fun deleteSMSGateway(id: String): ApiResponse<SMSGatewayResponse> {
        return delete("sms/gateways/$id")
    }
    
    // ========================================
    // EMAIL NOTIFICATIONS (2.37+)
    // ========================================
    
    /**
     * Send email notification (2.37+)
     */
    suspend fun sendEmailNotification(
        email: EmailNotification
    ): ApiResponse<EmailNotificationResponse> {
        if (!version.supportsEmailNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Email notifications not supported in version ${version.versionString}"))
        }
        
        return post("email/notification", email)
    }
    
    /**
     * Send bulk email notifications (2.37+)
     */
    suspend fun sendBulkEmailNotifications(
        emails: List<EmailNotification>
    ): ApiResponse<EmailBulkNotificationResponse> {
        if (!version.supportsEmailNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Email notifications not supported in version ${version.versionString}"))
        }
        
        val bulkEmail = EmailBulkNotification(emails)
        return post("email/notification/bulk", bulkEmail)
    }
    
    /**
     * Get email configuration (2.37+)
     */
    suspend fun getEmailConfiguration(): ApiResponse<EmailConfigurationResponse> {
        if (!version.supportsEmailNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Email notifications not supported in version ${version.versionString}"))
        }
        
        return get("email/config")
    }
    
    /**
     * Update email configuration (2.37+)
     */
    suspend fun updateEmailConfiguration(
        config: EmailConfiguration
    ): ApiResponse<EmailConfigurationResponse> {
        if (!version.supportsEmailNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Email notifications not supported in version ${version.versionString}"))
        }
        
        return put("email/config", config)
    }
    
    /**
     * Test email configuration (2.37+)
     */
    suspend fun testEmailConfiguration(
        testEmail: EmailTestMessage
    ): ApiResponse<EmailTestResponse> {
        if (!version.supportsEmailNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Email notifications not supported in version ${version.versionString}"))
        }
        
        return post("email/config/test", testEmail)
    }
    
    // ========================================
    // PUSH NOTIFICATIONS (2.40+)
    // ========================================
    
    /**
     * Send push notification (2.40+)
     */
    suspend fun sendPushNotification(
        notification: PushNotification
    ): ApiResponse<PushNotificationResponse> {
        if (!version.supportsPushNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Push notifications not supported in version ${version.versionString}"))
        }
        
        return post("push/notification", notification)
    }
    
    /**
     * Send bulk push notifications (2.40+)
     */
    suspend fun sendBulkPushNotifications(
        notifications: List<PushNotification>
    ): ApiResponse<PushBulkNotificationResponse> {
        if (!version.supportsPushNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Push notifications not supported in version ${version.versionString}"))
        }
        
        val bulkPush = PushBulkNotification(notifications)
        return post("push/notification/bulk", bulkPush)
    }
    
    /**
     * Register device for push notifications (2.40+)
     */
    suspend fun registerPushDevice(
        device: PushDeviceRegistration
    ): ApiResponse<PushDeviceResponse> {
        if (!version.supportsPushNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Push notifications not supported in version ${version.versionString}"))
        }
        
        return post("push/devices", device)
    }
    
    /**
     * Unregister device from push notifications (2.40+)
     */
    suspend fun unregisterPushDevice(
        deviceId: String
    ): ApiResponse<PushDeviceResponse> {
        if (!version.supportsPushNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Push notifications not supported in version ${version.versionString}"))
        }
        
        return delete("push/devices/$deviceId")
    }
    
    /**
     * Get push notification configuration (2.40+)
     */
    suspend fun getPushConfiguration(): ApiResponse<PushConfigurationResponse> {
        if (!version.supportsPushNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Push notifications not supported in version ${version.versionString}"))
        }
        
        return get("push/config")
    }
    
    /**
     * Update push notification configuration (2.40+)
     */
    suspend fun updatePushConfiguration(
        config: PushConfiguration
    ): ApiResponse<PushConfigurationResponse> {
        if (!version.supportsPushNotifications()) {
            return ApiResponse.Error(UnsupportedOperationException("Push notifications not supported in version ${version.versionString}"))
        }
        
        return put("push/config", config)
    }
    
    // ========================================
    // NOTIFICATION TEMPLATES (2.38+)
    // ========================================
    
    /**
     * Get notification templates (2.38+)
     */
    suspend fun getNotificationTemplates(
        fields: String = "*",
        filter: List<String> = emptyList(),
        templateType: NotificationTemplateType? = null
    ): ApiResponse<NotificationTemplatesResponse> {
        if (!version.supportsNotificationTemplates()) {
            return ApiResponse.Error(UnsupportedOperationException("Notification templates not supported in version ${version.versionString}"))
        }
        
        val params = buildMap {
            put("fields", fields)
            if (filter.isNotEmpty()) put("filter", filter.joinToString(","))
            templateType?.let { put("templateType", it.name) }
        }
        
        return get("notificationTemplates", params)
    }
    
    /**
     * Get a specific notification template (2.38+)
     */
    suspend fun getNotificationTemplate(
        id: String,
        fields: String = "*"
    ): ApiResponse<NotificationTemplate> {
        if (!version.supportsNotificationTemplates()) {
            return ApiResponse.Error(UnsupportedOperationException("Notification templates not supported in version ${version.versionString}"))
        }
        
        return get("notificationTemplates/$id", mapOf("fields" to fields))
    }
    
    /**
     * Create notification template (2.38+)
     */
    suspend fun createNotificationTemplate(
        template: NotificationTemplate
    ): ApiResponse<NotificationTemplateResponse> {
        if (!version.supportsNotificationTemplates()) {
            return ApiResponse.Error(UnsupportedOperationException("Notification templates not supported in version ${version.versionString}"))
        }
        
        return post("notificationTemplates", template)
    }
    
    /**
     * Update notification template (2.38+)
     */
    suspend fun updateNotificationTemplate(
        id: String,
        template: NotificationTemplate
    ): ApiResponse<NotificationTemplateResponse> {
        if (!version.supportsNotificationTemplates()) {
            return ApiResponse.Error(UnsupportedOperationException("Notification templates not supported in version ${version.versionString}"))
        }
        
        return put("notificationTemplates/$id", template)
    }
    
    /**
     * Delete notification template (2.38+)
     */
    suspend fun deleteNotificationTemplate(id: String): ApiResponse<NotificationTemplateResponse> {
        if (!version.supportsNotificationTemplates()) {
            return ApiResponse.Error(UnsupportedOperationException("Notification templates not supported in version ${version.versionString}"))
        }
        
        return delete("notificationTemplates/$id")
    }
    
    // ========================================
    // MESSAGE ANALYTICS
    // ========================================
    
    /**
     * Get message analytics
     */
    suspend fun getMessageAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        messageType: MessageType? = null,
        groupBy: List<MessageAnalyticsGroupBy> = emptyList()
    ): ApiResponse<MessageAnalyticsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            messageType?.let { put("messageType", it.name) }
            if (groupBy.isNotEmpty()) put("groupBy", groupBy.joinToString(",") { it.name })
        }
        
        return get("messageConversations/analytics", params)
    }
    
    /**
     * Get SMS analytics
     */
    suspend fun getSMSAnalytics(
        startDate: String? = null,
        endDate: String? = null,
        direction: SMSDirection? = null,
        status: SMSStatus? = null
    ): ApiResponse<SMSAnalyticsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            direction?.let { put("direction", it.name) }
            status?.let { put("status", it.name) }
        }
        
        return get("sms/analytics", params)
    }
    
    /**
     * Get notification delivery statistics
     */
    suspend fun getNotificationDeliveryStats(
        startDate: String? = null,
        endDate: String? = null,
        notificationType: NotificationType? = null
    ): ApiResponse<NotificationDeliveryStatsResponse> {
        
        val params = buildMap {
            startDate?.let { put("startDate", it) }
            endDate?.let { put("endDate", it) }
            notificationType?.let { put("notificationType", it.name) }
        }
        
        return get("notifications/deliveryStats", params)
    }
}

// ========================================
// ENUMS
// ========================================

enum class MessageType { PRIVATE, SYSTEM, VALIDATION_RESULT, TICKET }
enum class MessagePriority { LOW, MEDIUM, HIGH, URGENT }
enum class MessageStatus { OPEN, PENDING, INVALID, SOLVED }
enum class MessageBulkOperationType { MARK_READ, MARK_UNREAD, DELETE, ASSIGN }
enum class SMSStatus { OUTBOUND, SENT, RECEIVED, FAILED }
enum class SMSDirection { INBOUND, OUTBOUND }
enum class NotificationTemplateType { SMS, EMAIL, PUSH, SYSTEM }
enum class NotificationType { SMS, EMAIL, PUSH, SYSTEM, MESSAGE }
enum class MessageAnalyticsGroupBy { TYPE, PRIORITY, STATUS, USER, DATE }