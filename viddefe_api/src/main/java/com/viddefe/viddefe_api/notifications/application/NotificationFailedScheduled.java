package com.viddefe.viddefe_api.notifications.application;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.common.VerifyChannelWorkingSuccessful;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationFailed;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationFailedRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * NotificationFailedScheduled processes failed notifications and manages retry logic.
 * 
 * Responsibilities:
 * - Execute every 10 minutes to find notifications ready for retry
 * - Verify channel availability before retrying
 * - Republish notificationfailed events to RabbitMQ
 * - Increment retry count with exponential backoff
 * - Daily cleanup of exhausted records (older than 30 days)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationFailedScheduled {

    private final NotificationFailedRepository notificationFailedRepository;
    private final NotificationEventPublisher eventPublisher;
    private final VerifyChannelWorkingSuccessful verifyChannelWorkingSuccessful;
    private final NotificationApplicationService notificationApplicationService;

    /**
     * Process failed notifications every 10 minutes
     * Executes at fixed rate: 600000 milliseconds (10 minutes)
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void processFailedNotifications() {
        log.debug("Starting failed notification processing...");

        List<NotificationFailed> readyForRetry = notificationFailedRepository.findReadyForRetry();
        
        if (readyForRetry.isEmpty()) {
            log.debug("No notifications ready for retry");
            return;
        }

        log.info("Found {} notifications ready for retry", readyForRetry.size());

        for (NotificationFailed failed : readyForRetry) {
            try {
                // Verify if the channel is currently working/available
                if (!verifyChannelWorkingSuccessful.verify(failed.getChannel())) {
                    log.warn("Channel {} is not available for notification {}", 
                             failed.getChannel(), failed.getId());
                    continue;
                }

                // Create and publish notification event for retry
                NotificationEvent event = createRetryEvent(failed);
                eventPublisher.publish(event);

                // Increment retry count and schedule next retry
                notificationApplicationService.retryFailedNotification(failed.getId());
                
                log.info("Republished notification {} for retry", failed.getId());
            } catch (Exception e) {
                log.error("Error processing failed notification {}", failed.getId(), e);
            }
        }
    }

    /**
     * Cleanup exhausted notifications older than 30 days
     * Executes daily at 2 AM (02:00:00)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExhaustedNotifications() {
        log.debug("Starting cleanup of exhausted notifications...");
        
        try {
            notificationApplicationService.cleanupExhaustedNotifications();
            log.info("Cleanup of exhausted notifications completed");
        } catch (Exception e) {
            log.error("Error during cleanup of exhausted notifications", e);
        }
    }

    /**
     * Create a NotificationEvent from a failed notification for retry
     * @param failed The NotificationFailed entity
     * @return NotificationEvent ready to be published to RabbitMQ
     */
    private NotificationEvent createRetryEvent(NotificationFailed failed) {
        // Since NotificationEvent is abstract, we need to return null or handle this differently
        // In a real scenario, you would reconstruct the full event based on the notification type
        // This is a placeholder that will need to be implemented based on your actual NotificationEvent hierarchy
        log.warn("createRetryEvent not fully implemented - NotificationEvent is abstract");
        return null;
    }
}
