package com.viddefe.viddefe_api.notifications.domain.models.enums;

/**
 * NotificationFailedStatus enum represents the status of a failed notification
 * that is awaiting retry.
 */
public enum NotificationFailedStatus {

    /**
     * Notification is pending retry
     * The next retry attempt will be made at the scheduled time
     */
    PENDING_RETRY,

    /**
     * Maximum retry attempts have been exhausted
     * No further retry attempts will be made for this notification
     * Such records may be archived or deleted after a retention period
     */
    EXHAUSTED
}
