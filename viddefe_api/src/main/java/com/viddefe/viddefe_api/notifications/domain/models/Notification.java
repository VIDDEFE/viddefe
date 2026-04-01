package com.viddefe.viddefe_api.notifications.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.viddefe.viddefe_api.notifications.domain.models.enums.ContextEntityType;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.Channels;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Notification is the core entity for the notification refactoring.
 * It represents a notification that is decoupled from user delivery.
 * 
 * One Notification can be sent to multiple users via UserNotification entities.
 * This allows for efficient notification management and per-user tracking.
 *
 * Table: notifications
 * Primary Key: id (UUID)
 * 
 * Features:
 * - Supports different notification types (EVENT, MINISTRY, ADMINISTRATIVE)
 * - Stores template and variable information for rendering
 * - Optional context reference for EVENT type notifications
 * - JSONB support for flexible variable storage
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Type of notification: EVENT, MINISTRY, ADMINISTRATIVE
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    /**
     * Title of the notification (short text)
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Body/content of the notification
     */
    @Column(nullable = false, columnDefinition = "text")
    private String body;

    /**
     * Channel through which this notification is delivered
     * Examples: EMAIL, SMS, PUSH, IN_APP
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Channels channel;

    /**
     * Email template name (if applicable)
     * Used for HTML email rendering
     */
    @Column
    private String template;

    /**
     * Variables map in JSONB format
     * Used for template interpolation and dynamic content
     * Examples: {"recipient_name": "John", "event_name": "Sunday Service"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> variables;

    /**
     * Additional data map in JSONB format
     * Can store metadata, tracking info, etc.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> data;

    /**
     * Context ID for EVENT type notifications
     * Reference to the entity that triggered this notification
     * Examples: Meeting ID, Group ID, etc.
     */
    @Column(name = "context_id")
    private UUID contextId;

    /**
     * Context entity type for EVENT type notifications
     * Indicates what kind of entity contextId refers to
     */
    @Column(name = "context_entity_type")
    @Enumerated(EnumType.STRING)
    private ContextEntityType contextEntityType;

    /**
     * Timestamp when this notification was created
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when this notification was last updated
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
