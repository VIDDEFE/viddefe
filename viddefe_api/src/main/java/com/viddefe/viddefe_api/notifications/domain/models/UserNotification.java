package com.viddefe.viddefe_api.notifications.domain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * UserNotification represents the relationship between a Notification and a User.
 * This is a per-user tracking entity that allows one Notification to be sent to multiple users
 * with individual read status and delivery tracking.
 *
 * Table: user_notifications
 * Constraints:
 *   - Primary Key: id (UUID)
 *   - Unique: (notification_id, people_id) - ensures one record per user per notification
 *   - Foreign Keys: notification_id → notifications.id, people_id → people.id
 */
@Entity
@Table(name = "user_notifications", 
        uniqueConstraints = @UniqueConstraint(columnNames = {"notification_id", "people_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Reference to the Notification entity
     * ON DELETE CASCADE ensures cleanup when notification is deleted
     */
    @Column(name = "notification_id", nullable = false)
    private UUID notificationId;

    /**
     * Reference to the user (People) entity
     * ON DELETE CASCADE ensures cleanup when user is deleted
     */
    @Column(name = "people_id", nullable = false)
    private UUID peopleId;

    /**
     * Timestamp when this notification was marked as read
     * Null if unread
     */
    @Column(name = "read_at")
    private Instant readAt;

    /**
     * Status of the notification delivery to this user
     * PENDING: Just created, not yet processed
     * SENT: Successfully sent to user
     * READ: User has read the notification
     * FAILED: Delivery failed, may be retried
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserNotificationStatus status;

    /**
     * Timestamp when this record was created
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when this record was last updated
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    /**
     * Helper method to mark this notification as read
     */
    public void markAsRead() {
        this.readAt = Instant.now();
        this.status = UserNotificationStatus.READ;
        this.updatedAt = Instant.now();
    }

    /**
     * Helper method to check if notification is unread
     */
    public boolean isUnread() {
        return this.readAt == null;
    }

    /**
     * Helper method to check if notification was read
     */
    public boolean isRead() {
        return this.readAt != null;
    }

    /**
     * Helper method to mark as sent
     */
    public void markAsSent() {
        this.status = UserNotificationStatus.SENT;
        this.updatedAt = Instant.now();
    }

    /**
     * Helper method to mark as failed
     */
    public void markAsFailed() {
        this.status = UserNotificationStatus.FAILED;
        this.updatedAt = Instant.now();
    }
}
