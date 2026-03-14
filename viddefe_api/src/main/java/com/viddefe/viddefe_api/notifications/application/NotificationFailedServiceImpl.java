package com.viddefe.viddefe_api.notifications.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.contracts.NotificationFailedService;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotifications;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;

import io.github.resilience4j.core.lang.NonNull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationFailedServiceImpl implements NotificationFailedService {

    private final UserNotificationRepository userNotificationRepository;
    /**
     * Retrieve a paginated list of failed notifications, allowing administrators to review and manage them effectively.
     * @param pageable
     * @return
     */
    @Override
    public Page<UserNotifications> getFailedNotifications(Pageable pageable) {
        NotificationStatus status = NotificationStatus.FAILED;
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
    public void createFailedNotification(WhatsappMessageDto whatsappMessageDto) {
        NotificationTypeEnum type = whatsappMessageDto.getNotificationType();
        Instant now = Instant.now();
        UserNotifications notificationsFailed = UserNotifications.builder()
                .to(whatsappMessageDto.getPhoneNumber())
                .template(whatsappMessageDto.getTemplate())
                .variables(whatsappMessageDto.getVariables())
                .status(NotificationStatus.FAILED)
                .createdAt(now)
                .type(type)
                .build();
        userNotificationRepository.save(notificationsFailed);
    }
}
