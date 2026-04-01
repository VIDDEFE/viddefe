package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for user notification response.
 * Contains notification details along with user-specific tracking information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNotificationResponseDto {

    /**
     * User notification ID (tracking record)
     */
    private UUID id;

    /**
     * ID of the actual notification
     */
    private UUID notificationId;

    /**
     * User (people) ID who received the notification
     */
    private UUID peopleId;

    /**
     * Notification title
     */
    private String title;

    /**
     * Notification body/content
     */
    private String body;

    /**
     * Notification type (EVENT, MINISTRY, ADMINISTRATIVE)
     */
    private String type;

    /**
     * Current status of the notification for this user
     * (PENDING, SENT, READ, FAILED)
     */
    private UserNotificationStatus status;

    /**
     * When the notification was read by the user
     * Null if unread
     */
    private Instant readAt;

    /**
     * When the notification was created
     */
    private Instant createdAt;

    /**
     * When the notification was last updated
     */
    private Instant updatedAt;

    private String message;

    /**
     * Derived property indicating if notification is unread
     */
    public boolean isUnread() {
        return readAt == null;
    }

    /**
     * Derived property indicating if notification has been read
     */
    public boolean isRead() {
        return readAt != null;
    }
}
