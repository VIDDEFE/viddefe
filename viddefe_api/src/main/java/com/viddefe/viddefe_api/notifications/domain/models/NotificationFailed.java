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

import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationFailedStatus;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.Channels;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * NotificationFailed tracks failed notifications and manages retry logic with exponential backoff.
 * 
 * When a notification delivery fails, a NotificationFailed record is created.
 * The system processes these periodically (every 10 minutes) and attempts to retry delivery.
 * 
 * Retry strategy:
 * - Exponential backoff: delays = 5, 10, 20 minutes
 * - Default max retries: 3
 * - Records older than 30 days with status EXHAUSTED are cleaned up automatically
 *
 * Table: notifications_failed
 * Primary Key: id (UUID)
 */
@Entity
@Table(name = "notifications_failed")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFailed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Reference to the user (People) who failed to receive the notification
     * ON DELETE CASCADE ensures cleanup when user is deleted
     */
    @Column(name = "people_id", nullable = false)
    private UUID peopleId;

    /**
     * Optional reference to the UserNotification that failed
     * May be null if the failure occurred before UserNotification was created
     */
    @Column(name = "user_notification_id")
    private UUID userNotificationId;

    /**
     * Notification type (EVENT, MINISTRY, ADMINISTRATIVE)
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    /**
     * Channel through which delivery was attempted
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Channels channel;

    /**
     * Template variables in JSONB format
     * Stored to allow retry with same data
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> variables;

    /**
     * Current status of this failed notification record
     * PENDING_RETRY: Ready for next retry attempt
     * EXHAUSTED: Max retries reached, no more attempts will be made
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationFailedStatus status;

    /**
     * Number of retry attempts already made
     */
    @Column(nullable = false)
    private Integer retryCount = 0;

    /**
     * Maximum number of retries allowed (default: 3)
     */
    @Column(nullable = false)
    private Integer maxRetries = 3;

    /**
     * Timestamp for when the next retry should be attempted
     * Initially set when record is created, updated after each retry
     */
    @Column(name = "next_retry_at", nullable = false)
    private Instant nextRetryAt;

    /**
     * Timestamp when this failed record was created
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
     * Check if maximum retries have been exhausted
     */
    public boolean isExhausted() {
        return retryCount >= maxRetries;
    }

    /**
     * Increment retry count and schedule next retry with exponential backoff
     * Backoff formula: delay = 5 * 2^retryCount minutes
     * Examples: 5 min (0 retries), 10 min (1 retry), 20 min (2 retries)
     */
    public void incrementRetry() {
        this.retryCount++;
        if (this.retryCount >= this.maxRetries) {
            this.status = NotificationFailedStatus.EXHAUSTED;
        } else {
            scheduleNextRetry();
        }
        this.updatedAt = Instant.now();
    }

    /**
     * Schedule next retry with exponential backoff
     * Base delay: 5 minutes (300 seconds)
     * Multiplier: 2^retryCount
     */
    public void scheduleNextRetry() {
        long baseDelaySeconds = 5L * 60L; // 5 minutes
        long delayMultiplier = (long) Math.pow(2, this.retryCount);
        long delaySeconds = baseDelaySeconds * delayMultiplier;
        this.nextRetryAt = Instant.now().plusSeconds(delaySeconds);
        this.status = NotificationFailedStatus.PENDING_RETRY;
    }

    /**
     * Check if this notification is ready for retry
     */
    public boolean isReadyForRetry() {
        return this.status == NotificationFailedStatus.PENDING_RETRY 
               && this.nextRetryAt.isBefore(Instant.now());
    }
}
