package com.viddefe.viddefe_api.notifications.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationFailed;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationFailedStatus;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationFailedRepository;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationRepository;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * NotificationApplicationService provides high-level orchestration for notification operations.
 * Handles the creation, distribution, tracking, and retry of notifications.
 * 
 * Key responsibilities:
 * - Create new notifications
 * - Distribute notifications to multiple users
 * - Track notification status (sent, read, failed)
 * - Manage retry logic for failed notifications
 * - Cleanup of exhausted retry records
 */
@Service
@Transactional
@Slf4j
public class NotificationApplicationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationFailedRepository notificationFailedRepository;

    public NotificationApplicationService(
            NotificationRepository notificationRepository,
            UserNotificationRepository userNotificationRepository,
            NotificationFailedRepository notificationFailedRepository) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.notificationFailedRepository = notificationFailedRepository;
    }

    /**
     * Create a new notification
     * @return The created Notification entity
     */
    public Notification createNotification(
            String title,
            String body,
            String type,
            Channels channel,
            String template,
            Map<String, Object> variables,
            Map<String, Object> data) {
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setChannel(channel);
        notification.setTemplate(template);
        notification.setVariables(variables);
        notification.setData(data);
        
        // Parse the type enum
        try {
            notification.setType(NotificationTypeEnum.valueOf(type));
        } catch (IllegalArgumentException e) {
            log.error("Invalid notification type: {}", type, e);
            throw new IllegalArgumentException("Invalid notification type: " + type);
        }
        log.debug("Creating notification with title: {}, type: {}, channel: {}", title, type, channel);
        return notificationRepository.save(notification);
    }

    /**
     * Create user notifications for multiple users
     * Distributes a single notification to multiple users
     * @param notificationId The ID of the notification to distribute
     * @param userIds List of user IDs to send the notification to
     * @return List of created UserNotification entities
     */
    public List<UserNotification> createUserNotifications(UUID notificationId, List<UUID> userIds) {
        List<UserNotification> userNotifications = new ArrayList<>();
        
        for (UUID userId : userIds) {
            UserNotification userNotif = new UserNotification();
            userNotif.setNotificationId(notificationId);
            userNotif.setPeopleId(userId);
            userNotif.setStatus(UserNotificationStatus.PENDING);
            userNotifications.add(userNotif);
        }
        
        return userNotificationRepository.saveAll(userNotifications);
    }

    /**
     * Mark a user notification as sent
     * @param userNotificationId The ID of the user notification to mark
     */
    public void markAsSent(@NonNull UUID userNotificationId) {
        UserNotification userNotif = userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotification not found: " + userNotificationId));
        
        userNotif.markAsSent();
        userNotificationRepository.save(userNotif);
        
        log.debug("Marked UserNotification {} as SENT", userNotificationId);
    }

    /**
     * Mark a user notification as read
     * @param userNotificationId The ID of the user notification to mark
     */
    public void markAsRead(@NonNull UUID userNotificationId) {
        UserNotification userNotif = userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new IllegalArgumentException("UserNotification not found: " + userNotificationId));
        
        userNotif.markAsRead();
        userNotificationRepository.save(userNotif);
        
        log.debug("Marked UserNotification {} as READ", userNotificationId);
    }

    /**
     * Record a failed notification for retry
     * @param peopleId The ID of the user who failed to receive the notification
     * @param userNotificationId The ID of the user notification (optional)
     * @param type The notification type
     * @param channel The delivery channel
     * @param variables The template variables
     */
    public void recordFailedNotification(
            @NonNull UUID peopleId,
            UUID userNotificationId,
            String type,
            Channels channel,
            Map<String, Object> variables) {
        
        NotificationFailed failed = new NotificationFailed();
        failed.setPeopleId(peopleId);
        failed.setUserNotificationId(userNotificationId);
        failed.setChannel(channel);
        failed.setVariables(variables);
        
        // Set the type
        try {
            failed.setType(NotificationTypeEnum.valueOf(type));
        } catch (IllegalArgumentException e) {
            log.error("Invalid notification type: {}", type, e);
            throw new IllegalArgumentException("Invalid notification type: " + type);
        }
        
        failed.setStatus(NotificationFailedStatus.PENDING_RETRY);
        failed.setRetryCount(0);
        failed.setMaxRetries(3);
        failed.scheduleNextRetry();
        
        notificationFailedRepository.save(failed);
        
        log.info("Recorded failed notification for user {} with retry scheduled at {}", 
                 peopleId, failed.getNextRetryAt());
    }

    /**
     * Retry a failed notification
     * Increments the retry count and updates the next retry time
     * @param failedNotificationId The ID of the failed notification to retry
     */
    @Transactional
    public void retryFailedNotification(@NonNull UUID failedNotificationId) {
        NotificationFailed failed = notificationFailedRepository.findById(failedNotificationId)
                .orElseThrow(() -> new IllegalArgumentException("NotificationFailed not found: " + failedNotificationId));
        
        if (!failed.isExhausted()) {
            failed.incrementRetry();
            notificationFailedRepository.save(failed);
            Integer retryCount = failed.getRetryCount();
            
            log.info("Retrying failed notification {}. Retry count: {}/{}. Next retry at: {}",
                     failedNotificationId, retryCount, failed.getMaxRetries(), failed.getNextRetryAt());
        } else {
            log.warn("Failed notification {} is exhausted, no more retries", failedNotificationId);
        }
    }

    /**
     * Get all failed notifications ready for retry
     * @return List of NotificationFailed entities ready for retry
     */
    public List<NotificationFailed> getReadyForRetry() {
        return notificationFailedRepository.findReadyForRetry();
    }

    /**
     * Cleanup exhausted notifications older than 30 days
     */
    @Transactional
    public void cleanupExhaustedNotifications() {
        Instant thirtyDaysAgo = Instant.now().minusSeconds(30L * 24 * 60 * 60);
        int deletedCount = notificationFailedRepository.deleteExhaustedBefore(thirtyDaysAgo);
        
        if (deletedCount > 0) {
            log.info("Cleaned up {} exhausted notification records", deletedCount);
        }
    }

    /**
     * Mark all notifications as read for a specific user
     * @param peopleId The ID of the user
     * @return The number of notifications marked as read
     */
    @Transactional
    public int markAllAsRead(@NonNull UUID peopleId) {
        return userNotificationRepository.markAllAsRead(peopleId);
    }

    /**
     * Count unread notifications for a specific user
     * @param peopleId The ID of the user
     * @return The count of unread notifications
     */
    public long countUnread(@NonNull UUID peopleId) {
        return userNotificationRepository.countUnreadByPeopleId(peopleId);
    }
}
