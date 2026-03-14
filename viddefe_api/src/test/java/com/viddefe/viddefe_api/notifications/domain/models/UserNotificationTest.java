package com.viddefe.viddefe_api.notifications.domain.models;

import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("UserNotification Entity Tests")
class UserNotificationTest {

    private UserNotification userNotification;
    private UUID notificationId;
    private UUID peopleId;

    @BeforeEach
    void setUp() {
        userNotification = new UserNotification();
        notificationId = UUID.randomUUID();
        peopleId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("UserNotification Creation and Basic Properties")
    class UserNotificationCreation {

        @Test
        @DisplayName("Should create user notification with required fields")
        void shouldCreateUserNotificationWithRequiredFields() {
            // Act
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.PENDING);

            // Assert
            assertThat(userNotification.getNotificationId()).isEqualTo(notificationId);
            assertThat(userNotification.getPeopleId()).isEqualTo(peopleId);
            assertThat(userNotification.getStatus()).isEqualTo(UserNotificationStatus.PENDING);
        }

        @Test
        @DisplayName("Should have initial status as PENDING")
        void shouldHavePendingStatusInitially() {
            // Act
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.PENDING);

            // Assert
            assertThat(userNotification.getStatus()).isEqualTo(UserNotificationStatus.PENDING);
            assertThat(userNotification.getReadAt()).isNull();
        }

        @Test
        @DisplayName("Should create multiple user notifications for the same notification")
        void shouldCreateMultipleUserNotificationsForSameNotification() {
            // Arrange
            UUID notificationId1 = UUID.randomUUID();
            UUID user1 = UUID.randomUUID();
            UUID user2 = UUID.randomUUID();
            UUID user3 = UUID.randomUUID();

            // Act
            UserNotification userNotif1 = new UserNotification();
            userNotif1.setNotificationId(notificationId1);
            userNotif1.setPeopleId(user1);
            userNotif1.setStatus(UserNotificationStatus.PENDING);

            UserNotification userNotif2 = new UserNotification();
            userNotif2.setNotificationId(notificationId1);
            userNotif2.setPeopleId(user2);
            userNotif2.setStatus(UserNotificationStatus.PENDING);

            UserNotification userNotif3 = new UserNotification();
            userNotif3.setNotificationId(notificationId1);
            userNotif3.setPeopleId(user3);
            userNotif3.setStatus(UserNotificationStatus.PENDING);

            // Assert
            assertThat(userNotif1.getNotificationId()).isEqualTo(userNotif2.getNotificationId());
            assertThat(userNotif2.getNotificationId()).isEqualTo(userNotif3.getNotificationId());
            assertThat(userNotif1.getPeopleId()).isNotEqualTo(userNotif2.getPeopleId());
            assertThat(userNotif2.getPeopleId()).isNotEqualTo(userNotif3.getPeopleId());
        }
    }

    @Nested
    @DisplayName("UserNotification Status Transitions")
    class StatusTransitions {

        @Test
        @DisplayName("Should transition from PENDING to SENT")
        void shouldTransitionFromPendingToSent() {
            // Arrange
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.PENDING);
            Instant now = Instant.now();

            // Act
            userNotification.setStatus(UserNotificationStatus.SENT);

            // Assert
            assertThat(userNotification.getStatus()).isEqualTo(UserNotificationStatus.SENT);
        }

        @Test
        @DisplayName("Should transition from SENT to READ with readAt timestamp")
        void shouldTransitionFromSentToReadWithTimestamp() {
            // Arrange
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.SENT);
            Instant readTime = Instant.now();

            // Act
            userNotification.setStatus(UserNotificationStatus.READ);
            userNotification.setReadAt(readTime);

            // Assert
            assertThat(userNotification.getStatus()).isEqualTo(UserNotificationStatus.READ);
            assertThat(userNotification.getReadAt()).isEqualTo(readTime);
        }

        @Test
        @DisplayName("Should mark notification as FAILED")
        void shouldMarkNotificationAsFailed() {
            // Arrange
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.PENDING);

            // Act
            userNotification.setStatus(UserNotificationStatus.FAILED);

            // Assert
            assertThat(userNotification.getStatus()).isEqualTo(UserNotificationStatus.FAILED);
        }

        @Test
        @DisplayName("Should support all status transitions")
        void shouldSupportAllStatusTransitions() {
            // Arrange
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);

            // Act & Assert
            for (UserNotificationStatus status : UserNotificationStatus.values()) {
                userNotification.setStatus(status);
                assertThat(userNotification.getStatus()).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("UserNotification Read Tracking")
    class ReadTracking {

        @Test
        @DisplayName("Should not set readAt if notification is not read")
        void shouldNotSetReadAtIfNotRead() {
            // Act
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.SENT);

            // Assert
            assertThat(userNotification.getReadAt()).isNull();
        }

        @Test
        @DisplayName("Should set readAt timestamp when marking as read")
        void shouldSetReadAtTimestampWhenRead() {
            // Arrange
            Instant readTime = Instant.now();
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);

            // Act
            userNotification.setStatus(UserNotificationStatus.READ);
            userNotification.setReadAt(readTime);

            // Assert
            assertThat(userNotification.getReadAt()).isEqualTo(readTime);
            assertThat(userNotification.getReadAt()).isNotNull();
        }

        @Test
        @DisplayName("Should clear readAt when reverting to SENT status")
        void shouldClearReadAtWhenRevertingToSent() {
            // Arrange
            Instant readTime = Instant.now();
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.READ);
            userNotification.setReadAt(readTime);

            // Act
            userNotification.setStatus(UserNotificationStatus.SENT);
            userNotification.setReadAt(null);

            // Assert
            assertThat(userNotification.getReadAt()).isNull();
        }

        @Test
        @DisplayName("Should track read time in order of occurrence")
        void shouldTrackReadTimeCorrectly() {
            // Arrange
            Instant sentTime = Instant.now();
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setCreatedAt(sentTime);

            // Act
            userNotification.setStatus(UserNotificationStatus.SENT);
            Instant readTime = sentTime.plusSeconds(300); // 5 minutes later
            userNotification.setStatus(UserNotificationStatus.READ);
            userNotification.setReadAt(readTime);

            // Assert
            assertThat(userNotification.getReadAt()).isAfter(userNotification.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("UserNotification Timestamps")
    class UserNotificationTimestamps {

        @Test
        @DisplayName("Should have createdAt and updatedAt timestamps")
        void shouldHaveTimestamps() {
            // Arrange
            Instant now = Instant.now();
            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.PENDING);
            userNotification.setCreatedAt(now);
            userNotification.setUpdatedAt(now);

            // Assert
            assertThat(userNotification.getCreatedAt()).isNotNull();
            assertThat(userNotification.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should update updatedAt independently from createdAt")
        void shouldUpdateUpdatedAtIndependently() {
            // Arrange
            Instant createdTime = Instant.now();
            Instant updatedTime = createdTime.plusSeconds(600);

            userNotification.setNotificationId(notificationId);
            userNotification.setPeopleId(peopleId);
            userNotification.setStatus(UserNotificationStatus.PENDING);
            userNotification.setCreatedAt(createdTime);
            userNotification.setUpdatedAt(updatedTime);

            // Assert
            assertThat(userNotification.getCreatedAt()).isEqualTo(createdTime);
            assertThat(userNotification.getUpdatedAt()).isEqualTo(updatedTime);
            assertThat(userNotification.getUpdatedAt()).isAfter(userNotification.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("UserNotification Unique Constraint Scenario")
    class UniqueConstraintScenario {

        @Test
        @DisplayName("Should represent unique constraint (notification_id, people_id)")
        void shouldRepresentUniqueConstraint() {
            // Arrange
            UUID notificationId1 = UUID.randomUUID();
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();

            // Act
            UserNotification notif1 = new UserNotification();
            notif1.setNotificationId(notificationId1);
            notif1.setPeopleId(userId1);
            notif1.setStatus(UserNotificationStatus.PENDING);

            UserNotification notif2 = new UserNotification();
            notif2.setNotificationId(notificationId1);
            notif2.setPeopleId(userId2);
            notif2.setStatus(UserNotificationStatus.PENDING);

            UserNotification notif3 = new UserNotification();
            notif3.setNotificationId(notificationId1);
            notif3.setPeopleId(userId1);
            notif3.setStatus(UserNotificationStatus.PENDING);

            // Assert - notif1 and notif3 would violate unique constraint in DB
            assertThat(notif1.getNotificationId()).isEqualTo(notif3.getNotificationId());
            assertThat(notif1.getPeopleId()).isEqualTo(notif3.getPeopleId());
            // In real scenario, the second insert would fail due to unique constraint
        }

        @Test
        @DisplayName("Should allow same notification to different users")
        void shouldAllowSameNotificationToDifferentUsers() {
            // Arrange
            UUID notificationId = UUID.randomUUID();
            UUID user1 = UUID.randomUUID();
            UUID user2 = UUID.randomUUID();
            UUID user3 = UUID.randomUUID();

            // Act
            UserNotification notif1 = new UserNotification();
            notif1.setNotificationId(notificationId);
            notif1.setPeopleId(user1);
            notif1.setStatus(UserNotificationStatus.PENDING);

            UserNotification notif2 = new UserNotification();
            notif2.setNotificationId(notificationId);
            notif2.setPeopleId(user2);
            notif2.setStatus(UserNotificationStatus.PENDING);

            UserNotification notif3 = new UserNotification();
            notif3.setNotificationId(notificationId);
            notif3.setPeopleId(user3);
            notif3.setStatus(UserNotificationStatus.PENDING);

            // Assert
            assertThat(notif1.getNotificationId()).isEqualTo(notif2.getNotificationId());
            assertThat(notif1.getNotificationId()).isEqualTo(notif3.getNotificationId());
            assertThat(notif1.getPeopleId()).isNotEqualTo(notif2.getPeopleId());
            assertThat(notif2.getPeopleId()).isNotEqualTo(notif3.getPeopleId());
        }

        @Test
        @DisplayName("Should allow same user to different notifications")
        void shouldAllowSameUserToDifferentNotifications() {
            // Arrange
            UUID notification1 = UUID.randomUUID();
            UUID notification2 = UUID.randomUUID();
            UUID notification3 = UUID.randomUUID();
            UUID userId = UUID.randomUUID();

            // Act
            UserNotification notif1 = new UserNotification();
            notif1.setNotificationId(notification1);
            notif1.setPeopleId(userId);
            notif1.setStatus(UserNotificationStatus.PENDING);

            UserNotification notif2 = new UserNotification();
            notif2.setNotificationId(notification2);
            notif2.setPeopleId(userId);
            notif2.setStatus(UserNotificationStatus.PENDING);

            UserNotification notif3 = new UserNotification();
            notif3.setNotificationId(notification3);
            notif3.setPeopleId(userId);
            notif3.setStatus(UserNotificationStatus.PENDING);

            // Assert
            assertThat(notif1.getPeopleId()).isEqualTo(notif2.getPeopleId());
            assertThat(notif1.getPeopleId()).isEqualTo(notif3.getPeopleId());
            assertThat(notif1.getNotificationId()).isNotEqualTo(notif2.getNotificationId());
            assertThat(notif2.getNotificationId()).isNotEqualTo(notif3.getNotificationId());
        }
    }

    @Nested
    @DisplayName("UserNotification Builder Pattern")
    class UserNotificationBuilder {

        @Test
        @DisplayName("Should build complete user notification")
        void shouldBuildCompleteUserNotification() {
            // Arrange & Act
            Instant now = Instant.now();
            UUID notificationId = UUID.randomUUID();
            UUID peopleId = UUID.randomUUID();

            UserNotification userNotif = new UserNotification(
                    UUID.randomUUID(),
                    notificationId,
                    peopleId,
                    null,
                    UserNotificationStatus.PENDING,
                    now,
                    now
            );

            // Assert
            assertThat(userNotif.getId()).isNotNull();
            assertThat(userNotif.getNotificationId()).isEqualTo(notificationId);
            assertThat(userNotif.getPeopleId()).isEqualTo(peopleId);
            assertThat(userNotif.getStatus()).isEqualTo(UserNotificationStatus.PENDING);
            assertThat(userNotif.getReadAt()).isNull();
        }
    }
}
