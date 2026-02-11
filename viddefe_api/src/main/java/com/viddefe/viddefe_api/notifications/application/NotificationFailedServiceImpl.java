package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.contracts.NotificationFailedService;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationsFailed;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationFailedRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationFailedServiceImpl implements NotificationFailedService {

    private final NotificationFailedRepository notificationFailedRepository;
    /**
     * Retrieve a paginated list of failed notifications, allowing administrators to review and manage them effectively.
     * @param pageable
     * @return
     */
    @Override
    public Page<NotificationsFailed> getFailedNotifications(Pageable pageable) {
        NotificationStatus status = NotificationStatus.FAILED;
        return notificationFailedRepository.findByStatus(status, pageable);
    }

    /**
     * Update the status of a failed notification, allowing for tracking and management of retry attempts or resolution.
     * @param notificationId
     * @param status
     */
    @Override
    public void updateNotificationStatus(UUID notificationId, NotificationStatus status) {
        NotificationsFailed notification = notificationFailedRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + notificationId));
        notification.setStatus(status);
        notificationFailedRepository.save(notification);
    }

    /**
     * Make a record of the failed notification in the database for later analysis and potential retries.
     * @param whatsappMessageDto
     */
    @Override
    public void createFailedNotification(WhatsappMessageDto whatsappMessageDto) {
        String originalMessage = ResolverMessage.resolveMessage(
            whatsappMessageDto.getTemplate(),
            whatsappMessageDto.getVariables()
        );
        OffsetDateTime now = OffsetDateTime.now();
        NotificationsFailed notificationsFailed = NotificationsFailed.builder()
                .to(whatsappMessageDto.getPhoneNumber())
                .message(originalMessage)
                .status(NotificationStatus.FAILED)
                .createdAt(now)
                .build();
        notificationFailedRepository.save(notificationsFailed);
    }
}
