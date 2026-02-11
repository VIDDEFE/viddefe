package com.viddefe.viddefe_api.notifications.contracts;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationsFailed;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationFailedService {
    Page<NotificationsFailed> getFailedNotifications(Pageable pageable);
    void updateNotificationStatus(UUID notificationId, NotificationStatus status);
    void createFailedNotification(WhatsappMessageDto whatsappMessageDto);
}
