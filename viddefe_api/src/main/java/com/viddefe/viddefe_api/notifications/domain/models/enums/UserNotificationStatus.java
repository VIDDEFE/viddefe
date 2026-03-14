package com.viddefe.viddefe_api.notifications.domain.models.enums;

/**
 * Status enum for UserNotification entity
 * Represents the delivery and read state of a notification for a specific user
 */
public enum UserNotificationStatus {

    /**
     * Notification has been created but not yet processed for delivery
     */
    PENDING,

    /**
     * Notification has been successfully sent to the user (via email, SMS, push, etc.)
     */
    SENT,

    /**
     * User has read the notification
     */
    READ,

    /**
     * Delivery of notification failed
     * The notification may be retried using the NotificationFailed mechanism
     */
    FAILED
}
