package com.viddefe.viddefe_api.notifications.Infrastructure.web;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viddefe.viddefe_api.common.components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationListResponseDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.UserNotificationResponseDto;
import com.viddefe.viddefe_api.notifications.application.NotificationApplicationService;
import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationRepository;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for managing user notifications.
 * Provides endpoints to list, read, and mark notifications for the authenticated user.
 * 
 * Base path: /notifications
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationsController {

    private final NotificationApplicationService notificationApplicationService;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final JwtUtil jwtUtil;

    /**
     * Get all notifications for the authenticated user with pagination.
     * Lists notifications in reverse chronological order (newest first).
     * 
     * @param jwt Authorization token from cookie
     * @param pageable Pagination parameters (page, size, sort)
     * @return Paginated list of user notifications with metadata
     * 
     * Example: GET /notifications?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationListResponseDto>> getMyNotifications(
            @CookieValue("access_token") String jwt,
            Pageable pageable) {
        
        UUID userId = jwtUtil.getUserId(jwt);
        log.info("Fetching notifications for user: {}", userId);
        
        // Get paginated user notifications ordered by newest first
        Page<UserNotification> userNotificationsPage = userNotificationRepository
                .findByUserId(userId, pageable);
        
        // Map to response DTOs with full notification details
        Page<UserNotificationResponseDto> responsePage = userNotificationsPage
                .map(this::mapToResponseDto);
        
        // Build response with pagination metadata
        NotificationListResponseDto response = NotificationListResponseDto.from(responsePage);
        
        log.debug("Retrieved {} notifications for user: {} (page {}/{})", 
                response.getNotifications().size(), userId, response.getCurrentPage(), 
                response.getTotalPages());
        
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

    /**
     * Get a specific notification by ID for the authenticated user.
     * Only returns the notification if it belongs to the authenticated user.
     * 
     * @param jwt Authorization token from cookie
     * @param notificationId The ID of the notification to retrieve
     * @return Single notification with full details
     * 
     * Example: GET /notifications/{notificationId}
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<UserNotificationResponseDto>> getNotificationById(
            @CookieValue("access_token") String jwt,
            @PathVariable UUID notificationId) {
        
        UUID userId = jwtUtil.getUserId(jwt);
        log.info("Fetching notification {} for user: {}", notificationId, userId);
        
        // Get the user notification, ensuring it belongs to the authenticated user
        UserNotification userNotification = userNotificationRepository
                .findByNotificationIdAndUserId(notificationId, userId)
                .orElseThrow(() -> {
                    log.warn("Notification {} not found for user {}", notificationId, userId);
                    return new IllegalArgumentException("Notification not found");
                });
        
        UserNotificationResponseDto response = mapToResponseDto(userNotification);
        
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

    /**
     * Get unread notification count for the authenticated user.
     * 
     * @param jwt Authorization token from cookie
     * @return Count of unread notifications
     * 
     * Example: GET /notifications/unread/count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @CookieValue("access_token") String jwt) {
        
        UUID userId = jwtUtil.getUserId(jwt);
        
        long unreadCount = notificationApplicationService.countUnread(userId);
        
        log.debug("User {} has {} unread notifications", userId, unreadCount);
        
        return new ResponseEntity<>(ApiResponse.ok(unreadCount), HttpStatus.OK);
    }

    /**
     * Mark a specific notification as read.
     * Updates the read timestamp and status to READ for the given notification.
     * 
     * @param jwt Authorization token from cookie
     * @param notificationId The ID of the notification to mark as read
     * @return Empty response with OK status
     * 
     * Example: PATCH /notifications/{notificationId}/read
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @CookieValue("access_token") String jwt,
            @PathVariable UUID notificationId) {
        
        UUID userId = jwtUtil.getUserId(jwt);
        log.info("Marking notification {} as read for user: {}", notificationId, userId);
        
        // Verify the notification belongs to the user
        UserNotification userNotification = userNotificationRepository
                .findByNotificationIdAndUserId(notificationId, userId)
                .orElseThrow(() -> {
                    log.warn("Notification {} not found for user {}", notificationId, userId);
                    return new IllegalArgumentException("Notification not found");
                });
        
        // Mark as read
        notificationApplicationService.markAsRead(userNotification.getId());
        
        log.info("Marked notification {} as read for user: {}", notificationId, userId);
        
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.OK);
    }

    /**
     * Mark all notifications as read for the authenticated user.
     * Updates all unread notifications to READ status with current timestamp.
     * 
     * @param jwt Authorization token from cookie
     * @return Number of notifications marked as read
     * 
     * Example: PATCH /notifications/mark-all-read
     */
    @PatchMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Integer>> markAllAsRead(
            @CookieValue("access_token") String jwt) {
        
        UUID userId = jwtUtil.getUserId(jwt);
        log.info("Marking all notifications as read for user: {}", userId);
        
        int markedCount = notificationApplicationService.markAllAsRead(userId);
        
        log.info("Marked {} notifications as read for user: {}", markedCount, userId);
        
        return new ResponseEntity<>(ApiResponse.ok(markedCount), HttpStatus.OK);
    }

    /**
     * Map a UserNotification entity to a response DTO.
     * Includes full notification details along with user-specific tracking info.
     * 
     * @param userNotification The user notification entity
     * @return Mapped response DTO
     */
    private UserNotificationResponseDto mapToResponseDto(UserNotification userNotification) {
        // Get the associated notification entity
        UUID notificationId = userNotification.getNotificationId();
        Notification notification = notificationId != null 
            ? notificationRepository.findById(notificationId).orElse(null)
            : null;
        
        if (notification == null) {
            log.warn("Associated notification not found for user notification: {}", 
                    userNotification.getId());
            // Return minimal DTO if notification not found
            return UserNotificationResponseDto.builder()
                    .id(userNotification.getId())
                    .notificationId(userNotification.getNotificationId())
                    .peopleId(userNotification.getUserId())
                    .status(userNotification.getStatus())
                    .readAt(userNotification.getReadAt())
                    .createdAt(userNotification.getCreatedAt())
                    .updatedAt(userNotification.getUpdatedAt())
                    .build();
        }
        
        // Map full notification details
        return UserNotificationResponseDto.builder()
                .id(userNotification.getId())
                .notificationId(userNotification.getNotificationId())
                .peopleId(userNotification.getUserId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType() != null ? notification.getType().name() : null)
                .channel(notification.getChannel() != null ? notification.getChannel().name() : null)
                .template(notification.getTemplate())
                .variables(notification.getVariables())
                .status(userNotification.getStatus())
                .readAt(userNotification.getReadAt())
                .createdAt(userNotification.getCreatedAt())
                .updatedAt(userNotification.getUpdatedAt())
                .build();
    }
}
