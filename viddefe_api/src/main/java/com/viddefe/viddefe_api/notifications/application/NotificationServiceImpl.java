package com.viddefe.viddefe_api.notifications.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.contracts.NotificationService;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotifications;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    /**
     * Retrieve a paginated list of notifications by their status, allowing administrators to review and manage them effectively.
     * @param pageable
     * @param status
     * @return
     */
    @Override
    public Page<UserNotifications> getNotificationsByStatus(Pageable pageable, NotificationStatus status) {
        return userNotificationRepository.findByStatus(status, pageable);
    }

    /**
     * Update the status of a failed notification, allowing for tracking and management of retry attempts or resolution.
     * @param notificationId
     * @param status
     */
    @Override
    public void updateNotificationStatus(@lombok.NonNull UUID notificationId, NotificationStatus status) {
        UserNotifications notification = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + notificationId));
        notification.setStatus(status);
        userNotificationRepository.save(notification);
    }

    /**
     * Make a record of the failed notification in the database for later analysis and potential retries.
     * @param whatsappMessageDto
     */
    @Override
    public void createFailedNotification(@NonNull WhatsappMessageDto whatsappMessageDto) {
        NotificationTypeEnum type = whatsappMessageDto.getNotificationType();
        Instant now = Instant.now();
        UserNotifications userNotification = UserNotifications.builder()
                .to(whatsappMessageDto.getPhoneNumber())
                .template(whatsappMessageDto.getTemplate())
                .variables(whatsappMessageDto.getVariables())
                .status(NotificationStatus.FAILED)
                .createdAt(now)
                .type(type)
                .build();
        if (userNotification == null) return;
        userNotificationRepository.save(userNotification);
    }
}
