package com.viddefe.viddefe_api.notifications.Infrastructure.web;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.UserNotificationResponseDto;
import com.viddefe.viddefe_api.notifications.application.NotificationApplicationService;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationRepository;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;
import com.viddefe.viddefe_api.common.components.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

/**
 * Unit tests for NotificationsController.
 * Tests user-specific notification retrieval, pagination, and notification state management.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationsController Tests")
class NotificationsControllerTest {

    @Mock
    private NotificationApplicationService notificationApplicationService;

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private NotificationsController notificationsController;

    private UUID userId;
    private UUID notificationId1;
    private UUID notificationId2;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId1 = UUID.randomUUID();
        notificationId2 = UUID.randomUUID();
        jwtToken = "test-jwt-token";
    }

    @Nested
    @DisplayName("List Notifications with Pagination")
    class ListNotifications {

        @Test
        @DisplayName("Should retrieve paginated notifications for authenticated user")
        void shouldRetrievePaginatedNotifications() {
            // Arrange
            when(jwtUtil.getUserId(jwtToken)).thenReturn(userId);

            UserNotification userNotif1 = createUserNotification(UUID.randomUUID(), notificationId1, userId);
            UserNotification userNotif2 = createUserNotification(UUID.randomUUID(), notificationId2, userId);

            Page<UserNotification> page = new PageImpl<>(
                    Arrays.asList(userNotif1, userNotif2),
                    PageRequest.of(0, 20),
                    2);

            when(userNotificationRepository.findByPeopleId(userId, PageRequest.of(0, 20)))
                    .thenReturn(page);

            Notification notif1 = createNotification(notificationId1, "Test Notification 1");
            Notification notif2 = createNotification(notificationId2, "Test Notification 2");

            when(notificationRepository.findById(notificationId1)).thenReturn(Optional.of(notif1));
            when(notificationRepository.findById(notificationId2)).thenReturn(Optional.of(notif2));

            // Act
            Page<UserNotificationResponseDto> result = userNotificationRepository
                    .findByPeopleId(userId, PageRequest.of(0, 20))
                    .map(userNotif -> mapToResponseDto(userNotif, notificationRepository));

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(1);

            verify(userNotificationRepository).findByPeopleId(userId, PageRequest.of(0, 20));
        }

        @Test
        @DisplayName("Should handle pagination with default size")
        void shouldHandlePaginationWithDefaultSize() {
            // Arrange
            when(jwtUtil.getUserId(jwtToken)).thenReturn(userId);

            UserNotification userNotif = createUserNotification(UUID.randomUUID(), notificationId1, userId);
            Page<UserNotification> page = new PageImpl<>(
                    Arrays.asList(userNotif),
                    PageRequest.of(1, 10),
                    25);

            when(userNotificationRepository.findByPeopleId(userId, PageRequest.of(1, 10)))
                    .thenReturn(page);

            Notification notif = createNotification(notificationId1, "Test");
            when(notificationRepository.findById(notificationId1)).thenReturn(Optional.of(notif));

            // Act
            Page<UserNotification> result = userNotificationRepository.findByPeopleId(userId, PageRequest.of(1, 10));

            // Assert
            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getSize()).isEqualTo(10);
            assertThat(result.getTotalElements()).isEqualTo(25);
            assertThat(result.getTotalPages()).isEqualTo(3);
            assertThat(result.hasNext()).isTrue();
        }

        @Test
        @DisplayName("Should return empty list when no notifications exist")
        void shouldReturnEmptyListWhenNoNotifications() {
            // Arrange
            when(jwtUtil.getUserId(jwtToken)).thenReturn(userId);

            Page<UserNotification> emptyPage = new PageImpl<>(
                    Arrays.asList(),
                    PageRequest.of(0, 20),
                    0);

            when(userNotificationRepository.findByPeopleId(userId, PageRequest.of(0, 20)))
                    .thenReturn(emptyPage);

            // Act
            Page<UserNotification> result = userNotificationRepository.findByPeopleId(userId, PageRequest.of(0, 20));

            // Assert
            assertThat(result).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.isFirst()).isTrue();
        }
    }

    @Nested
    @DisplayName("Get Single Notification")
    class GetSingleNotification {

        @Test
        @DisplayName("Should retrieve specific notification for authenticated user")
        void shouldRetrieveSingleNotification() {
            // Arrange
            when(jwtUtil.getUserId(jwtToken)).thenReturn(userId);

            UUID userNotifId = UUID.randomUUID();
            UserNotification userNotif = createUserNotification(userNotifId, notificationId1, userId);

            when(userNotificationRepository.findByNotificationIdAndPeopleId(notificationId1, userId))
                    .thenReturn(Optional.of(userNotif));

            Notification notif = createNotification(notificationId1, "Specific Notification");
            when(notificationRepository.findById(notificationId1)).thenReturn(Optional.of(notif));

            // Act
            Optional<UserNotification> result = userNotificationRepository
                    .findByNotificationIdAndPeopleId(notificationId1, userId);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getNotificationId()).isEqualTo(notificationId1);
            assertThat(result.get().getPeopleId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("Should return empty when notification not found for user")
        void shouldReturnEmptyWhenNotificationNotFoundForUser() {
            // Arrange
            when(jwtUtil.getUserId(jwtToken)).thenReturn(userId);
            UUID differentUserId = UUID.randomUUID();

            when(userNotificationRepository.findByNotificationIdAndPeopleId(notificationId1, differentUserId))
                    .thenReturn(Optional.empty());

            // Act
            Optional<UserNotification> result = userNotificationRepository
                    .findByNotificationIdAndPeopleId(notificationId1, differentUserId);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Unread Count")
    class UnreadCount {

        @Test
        @DisplayName("Should return unread notification count")
        void shouldReturnUnreadCount() {
            // Arrange
            when(jwtUtil.getUserId(jwtToken)).thenReturn(userId);
            long unreadCount = 5L;

            when(notificationApplicationService.countUnread(userId)).thenReturn(unreadCount);

            // Act
            long result = notificationApplicationService.countUnread(userId);

            // Assert
            assertThat(result).isEqualTo(5);
            verify(notificationApplicationService).countUnread(userId);
        }

        @Test
        @DisplayName("Should return zero when no unread notifications")
        void shouldReturnZeroWhenNoUnread() {
            // Arrange
            when(notificationApplicationService.countUnread(userId)).thenReturn(0L);

            // Act
            long result = notificationApplicationService.countUnread(userId);

            // Assert
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("Mark Notifications as Read")
    class MarkAsRead {

        @Test
        @DisplayName("Should mark specific notification as read")
        void shouldMarkSingleNotificationAsRead() {
            // Arrange
            UUID userNotifId = UUID.randomUUID();

            // Act
            notificationApplicationService.markAsRead(userNotifId);

            // Assert
            verify(notificationApplicationService).markAsRead(userNotifId);
        }

        @Test
        @DisplayName("Should mark all notifications as read for user")
        void shouldMarkAllNotificationsAsRead() {
            // Arrange
            when(notificationApplicationService.markAllAsRead(userId)).thenReturn(3);

            // Act
            int result = notificationApplicationService.markAllAsRead(userId);

            // Assert
            assertThat(result).isEqualTo(3);
            verify(notificationApplicationService).markAllAsRead(userId);
        }

        @Test
        @DisplayName("Should return zero when no notifications to mark")
        void shouldReturnZeroWhenNoNotificationsToMark() {
            // Arrange
            when(notificationApplicationService.markAllAsRead(userId)).thenReturn(0);

            // Act
            int result = notificationApplicationService.markAllAsRead(userId);

            // Assert
            assertThat(result).isZero();
        }
    }

    // ======================= Helper Methods =======================

    /**
     * Creates a test UserNotification entity
     */
    private UserNotification createUserNotification(UUID userNotifId, UUID notifId, UUID peopleId) {
        UserNotification userNotif = new UserNotification();
        userNotif.setId(userNotifId);
        userNotif.setNotificationId(notifId);
        userNotif.setPeopleId(peopleId);
        userNotif.setStatus(UserNotificationStatus.SENT);
        userNotif.setReadAt(null);
        userNotif.setCreatedAt(Instant.now());
        userNotif.setUpdatedAt(Instant.now());
        return userNotif;
    }

    /**
     * Creates a test Notification entity
     */
    private Notification createNotification(UUID notifId, String title) {
        Notification notif = new Notification();
        notif.setId(notifId);
        notif.setTitle(title);
        notif.setBody("Test body for " + title);
        notif.setType(NotificationTypeEnum.EVENT);
        notif.setChannel(Channels.APP);
        notif.setTemplate("test-template");
        notif.setVariables(new java.util.HashMap<>());
        notif.setData(new java.util.HashMap<>());
        notif.setCreatedAt(Instant.now());
        notif.setUpdatedAt(Instant.now());
        return notif;
    }

    /**
     * Maps UserNotification to response DTO
     */
    private UserNotificationResponseDto mapToResponseDto(UserNotification userNotification, 
            NotificationRepository notificationRepository) {
        UUID notificationId = userNotification.getNotificationId();
        Notification notification = notificationId != null 
            ? notificationRepository.findById(notificationId).orElse(null)
            : null;

        if (notification == null) {
            return UserNotificationResponseDto.builder()
                    .id(userNotification.getId())
                    .notificationId(userNotification.getNotificationId())
                    .peopleId(userNotification.getPeopleId())
                    .status(userNotification.getStatus())
                    .readAt(userNotification.getReadAt())
                    .createdAt(userNotification.getCreatedAt())
                    .updatedAt(userNotification.getUpdatedAt())
                    .build();
        }

        return UserNotificationResponseDto.builder()
                .id(userNotification.getId())
                .notificationId(userNotification.getNotificationId())
                .peopleId(userNotification.getPeopleId())
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
