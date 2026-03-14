package com.viddefe.viddefe_api.notifications.application;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.auth.contracts.AccountService;
import com.viddefe.viddefe_api.auth.contracts.AuthMeService;
import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.factory.NotificatorFactory;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReminderSentWriter;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * NotificationConsumer processes notification events from RabbitMQ.
 * 
 * Responsibilities:
 * - Consume notification events from various queues (MINISTRY, ACCOUNT, SSE)
 * - Create decoupled Notification entities (independent of users)
 * - Create UserNotification records for each recipient
 * - Attempt to send notifications via appropriate channels
 * - Track delivery status: success → markAsSent(), failure → recordFailedNotification()
 * - Handle ministry-specific logic (reminder tracking, etc.)
 * 
 * The new decoupled model allows one Notification to be distributed to multiple users
 * with per-user delivery tracking and retry logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final PeopleReader peopleReader;
    private final AuthMeService authMeService;
    private final NotificatorFactory notificatorFactory;
    private final MinistryFunctionReader ministryFunctionReader;
    private final MinistryFunctionReminderSentWriter ministryFunctionReminderSentWriter;
    private final AccountService accountService;
    private final NotificationApplicationService notificationApplicationService;

    /**
     * Consume MINISTRY queue notifications
     * Sends to multiple channels: SMS (phone), APP (client ID)
     */
    @RabbitListener(
            queues = RabbitQueues.MINISTRY_QUEUE,
            concurrency = "1-5"
    )
    @Transactional
    public void consumeMinistry(NotificationEvent event) {
        log.info("Starting MINISTRY notification processing");
        printDataEvent(event);

        try {
            PeopleResDto person = peopleReader.getPeopleById(event.getPersonId()).toDto();
            UUID clientId = accountService.getAccountIdByPeopleId(person.getId());

            // Create a decoupled Notification entity
            Notification notification = notificationApplicationService.createNotification(
                    event.getSubject() != null ? event.getSubject() : "Notificación de Ministerio",
                    event.getTemplate() != null ? event.getTemplate() : "",
                    "MINISTRY",
                    event.getChannels(),
                    event.getTemplate(),
                    event.getVariables(),
                    null
            );

            // Create UserNotification for the recipient
            List<UserNotification> userNotifications = 
                    notificationApplicationService.createUserNotifications(
                            notification.getId(),
                            Arrays.asList(event.getPersonId())
                    );

            // Send via SMS (phone)
            UserNotification smNotif = userNotifications.get(0);
            NotificationDto dtoPhone = resolveNotificationDto(person.getPhone(), event);
            boolean smsSent = sendNotificationWithTracking(dtoPhone, event.getChannels(), smNotif.getId());

            // Send via APP (client ID)
            event.setChannels(Channels.APP);
            NotificationDto dtoApp = resolveNotificationDto(clientId.toString(), event);
            boolean appSent = sendNotificationWithTracking(dtoApp, Channels.APP, smNotif.getId());

            // Handle ministry-specific logic (reminder tracking)
            handleMinistryFunctionReminder(event);

            log.info("MINISTRY notification sent successfully at {}", Instant.now());
        } catch (Exception e) {
            log.error("Error processing MINISTRY notification", e);
        }
    }

    /**
     * Consume ACCOUNT queue notifications
     * Used for account-related notifications (registration, password reset, etc.)
     */
    @RabbitListener(
            queues = RabbitQueues.ACCOUNT_QUEUE,
            concurrency = "1-5"
    )
    @Transactional
    public void consumeAccount(NotificationEvent event) {
        log.info("Starting ACCOUNT notification processing");
        printDataEvent(event);

        try {
            String contact = authMeService.getContactByPersonId(event.getPersonId());

            // Create a decoupled Notification entity
            Notification notification = notificationApplicationService.createNotification(
                    event.getSubject() != null ? event.getSubject() : "Notificación de Cuenta",
                    event.getTemplate() != null ? event.getTemplate() : "",
                    "ADMINISTRATIVE",
                    event.getChannels(),
                    event.getTemplate(),
                    event.getVariables(),
                    null
            );

            // Create UserNotification for the recipient
            List<UserNotification> userNotifications = 
                    notificationApplicationService.createUserNotifications(
                            notification.getId(),
                            Arrays.asList(event.getPersonId())
                    );

            // Send notification
            UserNotification userNotif = userNotifications.get(0);
            NotificationDto dto = resolveNotificationDto(contact, event);
            sendNotificationWithTracking(dto, event.getChannels(), userNotif.getId());

            log.info("ACCOUNT notification sent successfully at {}", Instant.now());
        } catch (Exception e) {
            log.error("Error processing ACCOUNT notification", e);
        }
    }

    /**
     * Consume SSE (Server-Sent Events) queue notifications
     * Used for in-app real-time notifications
     */
    @RabbitListener(
            queues = RabbitQueues.NOTIFICATION_SSE_QUEUE,
            concurrency = "1-5"
    )
    @Transactional
    public void consumeSse(NotificationEvent event) {
        log.info("Starting SSE notification processing");
        printDataEvent(event);

        try {
            String clientId = "client-" + event.getPersonId().toString();

            // Create a decoupled Notification entity
            Notification notification = notificationApplicationService.createNotification(
                    event.getSubject() != null ? event.getSubject() : "Notificación",
                    event.getTemplate() != null ? event.getTemplate() : "",
                    "EVENT",
                    event.getChannels(),
                    null,
                    event.getVariables(),
                    null
            );

            // Create UserNotification for the recipient
            List<UserNotification> userNotifications = 
                    notificationApplicationService.createUserNotifications(
                            notification.getId(),
                            Arrays.asList(event.getPersonId())
                    );

            // Send notification
            UserNotification userNotif = userNotifications.get(0);
            NotificationDto dto = resolveNotificationDto(clientId, event);
            sendNotificationWithTracking(dto, event.getChannels(), userNotif.getId());

            log.info("SSE notification sent successfully at {}", Instant.now());
        } catch (Exception e) {
            log.error("Error processing SSE notification", e);
        }
    }

    /**
     * Send notification with delivery tracking
     * @param dto The notification DTO to send
     * @param channel The channel to send through
     * @param userNotificationId The ID of the UserNotification to track
     * @return true if sent successfully, false otherwise
     */
    private boolean sendNotificationWithTracking(NotificationDto dto, Channels channel, UUID userNotificationId) {
        try {
            Notificator notificator = notificatorFactory.get(channel);
            notificator.send(dto);
            
            // Mark as sent on success
            notificationApplicationService.markAsSent(userNotificationId);
            log.debug("Notification {} sent successfully via {}", userNotificationId, channel);
            return true;
        } catch (Exception e) {
            log.error("Failed to send notification {} via {}", userNotificationId, channel, e);
            
            // Record failed notification for retry
            notificationApplicationService.recordFailedNotification(
                    dto.getPersonId(),
                    userNotificationId,
                    "EVENT",
                    channel,
                    dto.getVariables()
            );
            return false;
        }
    }

    /**
     * Handle ministry-specific logic
     * Updates ministry function reminder status when notification is sent
     */
    private void handleMinistryFunctionReminder(NotificationEvent event) {
        try {
            MinistryFunction ministryFunction =
                    ministryFunctionReader.getByPeopleIdAndMeetingId(
                            event.getPersonId(),
                            event.getMeetingId()
                    );

            ministryFunctionReminderSentWriter
                    .writeMinistryFunctionReminderSent(ministryFunction);
        } catch (Exception e) {
            log.warn("Could not update ministry function reminder status", e);
        }
    }

    /**
     * Resolve NotificationDTO from NotificationEvent
     */
    private NotificationDto resolveNotificationDto(@NonNull String to, NotificationEvent event) {
        NotificationDto dto = NotificationDto.builder()
                .to(to)
                .personId(event.getPersonId())
                .template(event.getTemplate())
                .variables(event.getVariables())
                .channels(event.getChannels())
                .notificationType(event.getNotificationType())
                .remitter(event.getRemitter())
                .build();
        
        if (event.getChannels() == Channels.EMAIL) {
            dto.setSubject(event.getSubject());
        }

        return dto;
    }

    /**
     * Log notification event details for debugging
     */
    private void printDataEvent(NotificationEvent event) {
        log.debug("Notification Event Data:");
        log.debug("Person ID: {}", event.getPersonId());
        log.debug("Channels: {}", event.getChannels());
        log.debug("Template: {}", event.getTemplate());
        log.debug("Subject: {}", event.getSubject());
        log.debug("Variables: {}", event.getVariables());
    }
}
