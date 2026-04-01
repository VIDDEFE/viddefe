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
        
        // Get paginated user notifications as DTOs
        Page<UserNotificationResponseDto> responsePage = notificationApplicationService
                .getUserNotificationsAsDto(userId, pageable);
        
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
        
        // Get the notification with validation
        UserNotificationResponseDto response = notificationApplicationService
                .getUserNotificationByIdAsDto(notificationId, userId);
        
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
        
        // Get the notification to verify it belongs to the user
        UserNotificationResponseDto userNotification = notificationApplicationService
                .getUserNotificationByIdAsDto(notificationId, userId);
        
        // Mark as read using the internal user notification ID
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
}
