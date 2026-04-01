package com.viddefe.viddefe_api.notifications.application;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.UserNotificationResponseDto;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DTO Mapper for UserNotification entities.
 * Converts UserNotification entities to UserNotificationResponseDto.
 * Encapsulates all mapping logic including notification enrichment.
 * 
 * Optimized to avoid N+1 queries by pre-loading notifications when mapping collections.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserNotificationDtoMapper {

    private final NotificationRepository notificationRepository;

    /**
     * Convert a UserNotification entity to UserNotificationResponseDto with pre-fetched notification.
     * Uses provided notification to build the complete DTO.
     * 
     * IMPORTANT: This mapper should NOT access the database.
     * The service is responsible for pre-loading all required data.
     * If notification is null, returns minimal DTO without database queries.
     * 
     * @param userNotification The user notification entity
     * @param notification Optional notification (cannot be null if full details needed)
     * @return Mapped response DTO with notification details if available
     */
    public UserNotificationResponseDto toResponseDto(
            UserNotification userNotification, 
            Notification notification) {
        
        // Build minimal DTO if notification not provided
        if (notification == null) {
            throw new EntityNotFoundException("Notification not found for ID: " + userNotification.getNotificationId());
        }
        
        // Build full DTO with notification details
        return buildFullDto(userNotification, notification);
    }

    /**
     * Convert a list of UserNotification entities to DTOs with bulk notification fetching.
     * Optimized to fetch all related notifications in a single query (1 + 1 pattern).
     * 
     * @param userNotifications List of user notification entities
     * @return List of mapped response DTOs
     */
    public List<UserNotificationResponseDto> toResponseDtoList(List<UserNotification> userNotifications) {
        if (userNotifications.isEmpty() || userNotifications == null) {
            return Collections.emptyList();
        }
        
        // Extract all unique notification IDs
        List<UUID> notificationIds = userNotifications.stream()
                .map(UserNotification::getNotificationId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        
        // Fetch all notifications in a single query
        final Map<UUID, Notification> notificationsMap;
        if (notificationIds.isEmpty()) {
            throw new EntityNotFoundException("No notifications found for user notifications");
        } 
        List<Notification> notifications = notificationRepository.findByIdIn(notificationIds);
        notificationsMap = notifications.stream()
                .collect(Collectors.toMap(Notification::getId, n -> n));
        
        // Map user notifications to DTOs using pre-fetched notifications
        return userNotifications.stream()
                .map(userNotif -> {
                    UUID notificationId = userNotif.getNotificationId();
                    Notification notification = notificationId != null 
                        ? notificationsMap.get(notificationId)
                        : null;
                    return toResponseDto(userNotif, notification);
                })
                .collect(Collectors.toList());
    }

    /**
     * Build a full DTO with all notification and user notification details.
     * 
     * @param userNotification The user notification tracking entity
     * @param notification The notification entity with details
     * @return Full response DTO
     */
    private UserNotificationResponseDto buildFullDto(
            UserNotification userNotification,
            Notification notification) {
        log.debug("Building full DTO for UserNotification ID: {} with Notification ID: {}", 
                userNotification.getId(), notification.getId());
                log.debug("variables {}, body {}", notification.getVariables(), notification.getBody());

        String template = notification.getTemplate() != null ? notification.getTemplate() : notification.getBody();
        log.debug("Using template: {}", template);
        String message = ResolverMessage.resolveMessage(template, notification.getVariables());
        log.debug("Resolved message: {}", message);
        return UserNotificationResponseDto.builder()
                .id(userNotification.getId())
                .notificationId(userNotification.getNotificationId())
                .peopleId(userNotification.getUserId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType() != null ? notification.getType().name() : null)
                .status(userNotification.getStatus())
                .readAt(userNotification.getReadAt())
                .createdAt(userNotification.getCreatedAt())
                .updatedAt(userNotification.getUpdatedAt())
                .message(message)
                .build();
    }
}
