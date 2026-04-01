package com.viddefe.viddefe_api.notifications.domain.models.enums;

/**
 * ContextEntityType enum represents the entity type that a notification is associated with.
 * Used primarily for EVENT type notifications to maintain context about what triggered the notification.
 */
public enum ContextEntityType {

    /**
     * Context refers to a Church entity
     */
    CHURCH,

    /**
     * Context refers to a HomeGroup entity
     */
    GROUP
}
