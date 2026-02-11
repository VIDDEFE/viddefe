package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.contracts.NotificationFailedService;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationsFailed;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationFailedScheduled {
    private final NotificationFailedService notificationFailedService;
    private final static Integer PAGE_SIZE = 10;
    private final ApplicationEventPublisher publisher;

    @Scheduled(fixedRate = 6000 * 60) // Execute every hour (60000 ms * 60 = 1 hour)
    public void sendNotificationFailed() {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);
        Page<NotificationsFailed> failedNotificationsPage;
        do{
            failedNotificationsPage = notificationFailedService.getFailedNotifications(pageable);
            failedNotificationsPage.getContent()
                    .forEach(this::retrySendingNotification);
            pageable = pageable.next();
        }while (failedNotificationsPage.hasNext());
    }

    private void retrySendingNotification(NotificationsFailed notification) {
        //NotificationEvent
    }

}
