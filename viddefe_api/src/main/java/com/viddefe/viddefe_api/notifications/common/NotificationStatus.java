package com.viddefe.viddefe_api.notifications.common;

/**
 * Represents the status of a notification after a delivery failure.
 *
 * FAILED:
 *  - The notification exceeded the maximum retry attempts and was persisted
 *    for later processing.
 *
 * RESOLVED:
 *  - The underlying error was fixed and the notification was successfully
 *    re-sent.
 */
public enum NotificationStatus {
    FAILED,
    RESOLVED
}
