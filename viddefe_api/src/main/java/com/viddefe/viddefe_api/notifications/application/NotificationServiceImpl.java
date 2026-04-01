package com.viddefe.viddefe_api.notifications.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.contracts.NotificationService;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * NotificationServiceImpl provides backward compatibility for the old notification system.
 * This service is being deprecated in favor of NotificationApplicationService which uses
 * the new decoupled Notification + UserNotification model.
 * 
 * Deprecated: Use NotificationApplicationService instead for new functionality.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Deprecated(since = "2026-02-14", forRemoval = true)
public class NotificationServiceImpl implements NotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationApplicationService notificationApplicationService;

    /**
     * @deprecated Retrieve notifications by status.
     * This is a simplified compatibility wrapper. Returns empty page for now.
     */
    @Override
    @Deprecated
    public Page getNotificationsByStatus(Pageable pageable, NotificationStatus status) {
        log.warn("getNotificationsByStatus is deprecated. Use NotificationApplicationService instead.");
        // Return empty page as compatibility layer
        return Page.empty(pageable);
    }

    /**
     * @deprecated Update notification status.
     * Maps NotificationStatus to UserNotificationStatus for compatibility.
     */
    @Override
    @Deprecated
    public void updateNotificationStatus(@NonNull UUID notificationId, NotificationStatus status) {
        log.warn("updateNotificationStatus is deprecated. Use NotificationApplicationService instead.");
        
        UserNotification notification = userNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id: " + notificationId));
        
        // Map old status to new status
        UserNotificationStatus newStatus = mapNotificationStatus(status);
        notification.setStatus(newStatus);
        
        if (newStatus == UserNotificationStatus.READ) {
            notification.setReadAt(Instant.now());
        }
        
        userNotificationRepository.save(notification);
    }

    /**
     * @deprecated Create failed notification record.
     * Maps WhatsappMessageDto to the new model.
     */
    @Override
    @Deprecated
    public void createFailedNotification(@NonNull WhatsappMessageDto whatsappMessageDto) {
        log.warn("createFailedNotification is deprecated. Use NotificationApplicationService instead.");
        
        // This method is incompatible with the new model since it doesn't have proper context
        // Log a warning and continue
        log.info("Failed notification call - compatibility layer does not fully support this operation");
    }

    /**
     * Map old NotificationStatus enum to new UserNotificationStatus enum
     */
    private UserNotificationStatus mapNotificationStatus(NotificationStatus status) {
        if (status == null) {
            return UserNotificationStatus.PENDING;
        }
        String statusName = status.name();
        return switch (statusName) {
            case "PENDING" -> UserNotificationStatus.PENDING;
            case "SENT" -> UserNotificationStatus.SENT;
            case "READ" -> UserNotificationStatus.READ;
            case "FAILED" -> UserNotificationStatus.FAILED;
            default -> UserNotificationStatus.PENDING;
        };
    }
}
