package com.viddefe.viddefe_api.notifications.domain.models.enums;

/**
 * NotificationTypeEnum represents the category/type of notification.
 * Each type may have different handling, templates, and delivery mechanisms.
 */
public enum NotificationTypeEnum {

    /**
     * Event notifications are triggered by specific events in the system
     * Examples: Meeting reminders, attendance tracking, group updates
     * May have context_id and context_entity_type
     */
    EVENT,

    /**
     * Ministry notifications are related to ministry activities
     * Examples: Ministry announcements, team assignments, prayer requests
     */
    MINISTRY,

    /**
     * Administrative notifications are system/administrative in nature
     * Examples: Password reset, account verification, system maintenance notices
     */
    ADMINISTRATIVE
}
