package com.viddefe.viddefe_api.notifications.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.auth.contracts.AccountService;
import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitPriority;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationAccountEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationMeetingEvent;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.common.VerifyChannelWorkingSuccessful;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.notifications.contracts.NotificationFailedService;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotifications;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationFailedScheduled {
    private final NotificationFailedService notificationFailedService;
    private final static Integer PAGE_SIZE = 10;
    private final AccountService accountService;
    private final NotificationEventPublisher eventPublisher;
    private final VerifyChannelWorkingSuccessful verifyChannelWorkingSuccessful;

    @Scheduled(fixedRate = 6000 * 10) // Execute every 10 minutes (60000 ms * 10 = 10 minutes)
    public void sendNotificationFailed() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        Page<UserNotifications> failedNotificationsPage;
        do{
            failedNotificationsPage = notificationFailedService.getFailedNotifications(pageable);
            failedNotificationsPage.getContent()
                    .forEach(this::retrySendingNotification);
            pageable = pageable.next();
        }while (failedNotificationsPage.hasNext());
    }

    private void retrySendingNotification(UserNotifications notification) {
        if(verifyChannelWorkingSuccessful.verify(notification.getChannel())) return;
        NotificationEvent event = resolveType(notification);
        eventPublisher.publish(event);
        notificationFailedService.updateNotificationStatus(notification.getId(), NotificationStatus.RESOLVED);

    }

    private NotificationEvent resolveType(UserNotifications notification) {
        return switch (notification.getType()) {
            case ACCOUNT_CREATED -> {
                UUID accountId = accountService.getAccountIdByPeopleId(notification.getPeopleId());
                yield NotificationAccountEvent.builder()
                        .peopleId(notification.getPeopleId())
                        .accountId(accountId)
                        .channels(notification.getChannel())
                        .variables(notification.getVariables())
                        .template(notification.getTemplate())
                        .createdAt(notification.getCreatedAt())
                        .priority(RabbitPriority.HIGH)
                        .build();
            }
            case MINISTRY_FUNCTION_REMINDER -> NotificationMeetingEvent.builder()
                    .channels(notification.getChannel())
                    .variables(notification.getVariables())
                    .template(notification.getTemplate())
                    .createdAt(notification.getCreatedAt())
                    .priority(RabbitPriority.LOW)
                    .build();
            default -> throw new IllegalArgumentException("Unsupported notification type: " + notification.getType());
        };
    }

}
