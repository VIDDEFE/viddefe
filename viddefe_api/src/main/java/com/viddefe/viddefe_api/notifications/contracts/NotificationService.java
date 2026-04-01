package com.viddefe.viddefe_api.notifications.contracts;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;

public interface NotificationService {
    Page<UserNotification> getNotificationsByStatus(Pageable pageable, NotificationStatus status);
    void updateNotificationStatus(UUID notificationId, NotificationStatus status);
    void createFailedNotification(WhatsappMessageDto whatsappMessageDto);
}
