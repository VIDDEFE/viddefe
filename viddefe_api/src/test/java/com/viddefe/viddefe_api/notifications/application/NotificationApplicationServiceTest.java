package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.domain.models.Notification;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationFailed;
import com.viddefe.viddefe_api.notifications.domain.models.UserNotification;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationFailedStatus;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.domain.models.enums.UserNotificationStatus;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationFailedRepository;
import com.viddefe.viddefe_api.notifications.domain.repository.NotificationRepository;
import com.viddefe.viddefe_api.notifications.domain.repository.UserNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationApplicationService Tests")
class NotificationApplicationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserNotificationRepository userNotificationRepository;

    @Mock
    private NotificationFailedRepository notificationFailedRepository;

    @InjectMocks
    private NotificationApplicationService notificationApplicationService;

    @Captor
    private ArgumentCaptor<Notification> notificationCaptor;

    @Captor
    private ArgumentCaptor<List<UserNotification>> userNotificationsCaptor;

    private Map<String, Object> testVariables;
    private Map<String, Object> testData;

    @BeforeEach
    void setUp() {
        testVariables = new HashMap<>();
        testVariables.put("recipient_name", "Juan García");
        testVariables.put("church_name", "Iglesia Central");
        testVariables.put("event_date", "2026-03-15");
        testVariables.put("event_time", "10:30 AM");

        testData = new HashMap<>();
        testData.put("priority", "HIGH");
        testData.put("retry_count", 0);
    }

    @Nested
    @DisplayName("Notification Creation and Persistence")
    class NotificationCreationAndPersistence {

        @Test
        @DisplayName("Should create and persist notification with all fields")
        void shouldCreateAndPersistNotification() {
            // Arrange
            String title = "Invitación al ministerio";
            String body = "Estás invitado a participar en nuestro ministerio";
            String type = "MINISTRY";
            Channels channel = Channels.EMAIL;
            String template = "ministry_invitation.html";
            UUID notificationId = UUID.randomUUID();
            Notification savedNotification = new Notification();
            savedNotification.setId(notificationId);
            savedNotification.setTitle(title);
            savedNotification.setBody(body);
            savedNotification.setType(NotificationTypeEnum.MINISTRY);
            savedNotification.setChannel(channel);
            savedNotification.setTemplate(template);
            savedNotification.setVariables(testVariables);
            savedNotification.setData(testData);

            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

            // Act
            Notification result = notificationApplicationService.createNotification(
                    title, body, type, channel, template, testVariables, testData);

            // Assert
            verify(notificationRepository).save(notificationCaptor.capture());
            Notification capturedNotification = notificationCaptor.getValue();

            assertThat(result.getId()).isEqualTo(notificationId);
            assertThat(capturedNotification.getTitle()).isEqualTo(title);
            assertThat(capturedNotification.getBody()).isEqualTo(body);
            assertThat(capturedNotification.getType()).isEqualTo(NotificationTypeEnum.MINISTRY);
            assertThat(capturedNotification.getChannel()).isEqualTo(channel);
            assertThat(capturedNotification.getTemplate()).isEqualTo(template);
            assertThat(capturedNotification.getVariables()).isEqualTo(testVariables);
            assertThat(capturedNotification.getData()).isEqualTo(testData);
        }

        @Test
        @DisplayName("Should create notification with EVENT type")
        void shouldCreateEventTypeNotification() {
            // Arrange
            String title = "Nueva reunión programada";
            String body = "Una nueva reunión de adoración ha sido añadida";
            String type = "EVENT";
            Channels channel = Channels.APP;
            UUID notificationId = UUID.randomUUID();
            Notification savedNotification = new Notification();
            savedNotification.setId(notificationId);
            savedNotification.setType(NotificationTypeEnum.EVENT);

            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

            // Act
            Notification result = notificationApplicationService.createNotification(
                    title, body, type, channel, null, testVariables, null);

            // Assert
            verify(notificationRepository).save(any(Notification.class));
            assertThat(result.getType()).isEqualTo(NotificationTypeEnum.EVENT);
        }

        @Test
        @DisplayName("Should persist personalized variables in notification")
        void shouldPersistPersonalizedVariables() {
            // Arrange
            Map<String, Object> personalizedVars = new HashMap<>();
            personalizedVars.put("first_name", "Carlos");
            personalizedVars.put("last_name", "López");
            personalizedVars.put("email", "carlos@iglesia.com");
            personalizedVars.put("phone", "+34612345678");
            personalizedVars.put("role", "Diácono");

            UUID notificationId = UUID.randomUUID();
            Notification savedNotification = new Notification();
            savedNotification.setId(notificationId);
            savedNotification.setVariables(personalizedVars);

            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

            // Act
            Notification result = notificationApplicationService.createNotification(
                    "Test", "Test Body", "MINISTRY", Channels.EMAIL, 
                    "template", personalizedVars, null);

            // Assert
            verify(notificationRepository).save(notificationCaptor.capture());
            assertThat(notificationCaptor.getValue().getVariables())
                    .containsEntry("first_name", "Carlos")
                    .containsEntry("role", "Diácono")
                    .hasSize(5);
        }

        @Test
        @DisplayName("Should throw exception for invalid notification type")
        void shouldThrowExceptionForInvalidType() {
            // Arrange
            String invalidType = "INVALID_TYPE";

            // Act & Assert
            assertThatThrownBy(() -> 
                notificationApplicationService.createNotification(
                    "Title", "Body", invalidType, Channels.EMAIL, 
                    "template", null, null)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid notification type");

            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create notification with metadata")
        void shouldCreateNotificationWithMetadata() {
            // Arrange
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("tracking_id", UUID.randomUUID().toString());
            metadata.put("priority", "HIGH");
            metadata.put("retry_enabled", true);

            UUID notificationId = UUID.randomUUID();
            Notification savedNotification = new Notification();
            savedNotification.setId(notificationId);
            savedNotification.setData(metadata);

            when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

            // Act
            Notification result = notificationApplicationService.createNotification(
                    "Test", "Test Body", "ADMINISTRATIVE", Channels.APP, 
                    null, null, metadata);

            // Assert
            verify(notificationRepository).save(notificationCaptor.capture());
            assertThat(notificationCaptor.getValue().getData())
                    .isNotNull()
                    .containsEntry("priority", "HIGH")
                    .containsEntry("retry_enabled", true);
        }
    }

    @Nested
    @DisplayName("User Notification Distribution")
    class UserNotificationDistribution {

        @Test
        @DisplayName("Should distribute notification to single user")
        void shouldDistributeNotificationToSingleUser() {
            // Arrange
            UUID notificationId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            List<UUID> userIds = List.of(userId);

            UserNotification userNotif = new UserNotification();
            userNotif.setId(UUID.randomUUID());
            userNotif.setNotificationId(notificationId);
            userNotif.setUserId(userId);
            userNotif.setStatus(UserNotificationStatus.PENDING);

            when(userNotificationRepository.saveAll(anyList()))
                    .thenReturn(List.of(userNotif));

            // Act
            List<UserNotification> result = notificationApplicationService
                    .createUserNotifications(notificationId, userIds);

            // Assert
            verify(userNotificationRepository).saveAll(userNotificationsCaptor.capture());
            List<UserNotification> capturedNotifications = userNotificationsCaptor.getValue();

            assertThat(result).hasSize(1);
            assertThat(capturedNotifications).hasSize(1);
            assertThat(capturedNotifications.get(0).getNotificationId()).isEqualTo(notificationId);
            assertThat(capturedNotifications.get(0).getUserId()).isEqualTo(userId);
            assertThat(capturedNotifications.get(0).getStatus()).isEqualTo(UserNotificationStatus.PENDING);
        }

        @Test
        @DisplayName("Should distribute notification to multiple users in batch")
        void shouldDistributeNotificationToMultipleUsers() {
            // Arrange
            UUID notificationId = UUID.randomUUID();
            UUID user1 = UUID.randomUUID();
            UUID user2 = UUID.randomUUID();
            UUID user3 = UUID.randomUUID();
            List<UUID> userIds = List.of(user1, user2, user3);

            List<UserNotification> savedNotifications = new ArrayList<>();
            for (UUID userId : userIds) {
                UserNotification userNotif = new UserNotification();
                userNotif.setId(UUID.randomUUID());
                userNotif.setNotificationId(notificationId);
                userNotif.setUserId(userId);
                userNotif.setStatus(UserNotificationStatus.PENDING);
                savedNotifications.add(userNotif);
            }

            when(userNotificationRepository.saveAll(anyList()))
                    .thenReturn(savedNotifications);

            // Act
            List<UserNotification> result = notificationApplicationService
                    .createUserNotifications(notificationId, userIds);

            // Assert
            verify(userNotificationRepository).saveAll(userNotificationsCaptor.capture());
            List<UserNotification> capturedNotifications = userNotificationsCaptor.getValue();

            assertThat(result).hasSize(3);
            assertThat(capturedNotifications).hasSize(3);
            assertThat(capturedNotifications)
                    .allMatch(un -> un.getNotificationId().equals(notificationId))
                    .allMatch(un -> un.getStatus().equals(UserNotificationStatus.PENDING));

            Set<UUID> capturedUserIds = capturedNotifications.stream()
                    .map(UserNotification::getUserId)
                    .collect(java.util.stream.Collectors.toSet());
            assertThat(capturedUserIds).containsExactlyInAnyOrder(user1, user2, user3);
        }

        @Test
        @DisplayName("Should distribute notification to large group of users")
        void shouldDistributeNotificationToLargeGroup() {
            // Arrange
            UUID notificationId = UUID.randomUUID();
            List<UUID> userIds = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                userIds.add(UUID.randomUUID());
            }

            List<UserNotification> savedNotifications = new ArrayList<>();
            for (UUID userId : userIds) {
                UserNotification userNotif = new UserNotification();
                userNotif.setId(UUID.randomUUID());
                userNotif.setNotificationId(notificationId);
                userNotif.setUserId(userId);
                userNotif.setStatus(UserNotificationStatus.PENDING);
                savedNotifications.add(userNotif);
            }

            when(userNotificationRepository.saveAll(anyList()))
                    .thenReturn(savedNotifications);

            // Act
            List<UserNotification> result = notificationApplicationService
                    .createUserNotifications(notificationId, userIds);

            // Assert
            assertThat(result).hasSize(100);
            verify(userNotificationRepository).saveAll(any());
        }

        @Test
        @DisplayName("Should create user notifications with PENDING status")
        void shouldCreateUserNotificationsWithPendingStatus() {
            // Arrange
            UUID notificationId = UUID.randomUUID();
            List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());

            List<UserNotification> savedNotifications = userIds.stream()
                    .map(userId -> {
                        UserNotification un = new UserNotification();
                        un.setId(UUID.randomUUID());
                        un.setNotificationId(notificationId);
                        un.setUserId(userId);
                        un.setStatus(UserNotificationStatus.PENDING);
                        return un;
                    })
                    .toList();

            when(userNotificationRepository.saveAll(anyList()))
                    .thenReturn(savedNotifications);

            // Act
            List<UserNotification> result = notificationApplicationService
                    .createUserNotifications(notificationId, userIds);

            // Assert
            assertThat(result)
                    .allMatch(un -> un.getStatus().equals(UserNotificationStatus.PENDING));
        }
    }

    @Nested
    @DisplayName("Notification Status Management")
    class NotificationStatusManagement {

        @Test
        @DisplayName("Should mark user notification as SENT")
        void shouldMarkUserNotificationAsSent() {
            // Arrange
            UUID userNotificationId = UUID.randomUUID();
            UserNotification userNotif = new UserNotification();
            userNotif.setId(userNotificationId);
            userNotif.setStatus(UserNotificationStatus.PENDING);

            when(userNotificationRepository.findById(userNotificationId))
                    .thenReturn(Optional.of(userNotif));
            when(userNotificationRepository.save(any()))
                    .thenReturn(userNotif);

            // Act
            notificationApplicationService.markAsSent(userNotificationId);

            // Assert
            verify(userNotificationRepository).save(any(UserNotification.class));
        }

        @Test
        @DisplayName("Should mark user notification as READ with timestamp")
        void shouldMarkUserNotificationAsRead() {
            // Arrange
            UUID userNotificationId = UUID.randomUUID();
            UserNotification userNotif = new UserNotification();
            userNotif.setId(userNotificationId);
            userNotif.setStatus(UserNotificationStatus.SENT);
            userNotif.setReadAt(null);

            when(userNotificationRepository.findById(userNotificationId))
                    .thenReturn(Optional.of(userNotif));
            when(userNotificationRepository.save(any()))
                    .thenReturn(userNotif);

            // Act
            notificationApplicationService.markAsRead(userNotificationId);

            // Assert
            verify(userNotificationRepository).save(any(UserNotification.class));
        }

        @Test
        @DisplayName("Should throw exception if user notification not found")
        void shouldThrowExceptionIfUserNotificationNotFound() {
            // Arrange
            UUID userNotificationId = UUID.randomUUID();
            when(userNotificationRepository.findById(userNotificationId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> 
                notificationApplicationService.markAsSent(userNotificationId)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("UserNotification not found");

            verify(userNotificationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Failed Notification Recording and Retry")
    class FailedNotificationManagement {

        @Test
        @DisplayName("Should record failed notification for retry")
        void shouldRecordFailedNotificationForRetry() {
            // Arrange
            UUID peopleId = UUID.randomUUID();
            UUID userNotificationId = UUID.randomUUID();
            String type = "MINISTRY";
            Channels channel = Channels.EMAIL;

            when(notificationFailedRepository.save(any(NotificationFailed.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            notificationApplicationService.recordFailedNotification(
                    peopleId, userNotificationId, type, channel, testVariables);

            // Assert
            verify(notificationFailedRepository).save(any(NotificationFailed.class));
        }

        @Test
        @DisplayName("Should retry failed notification with incremented count")
        void shouldRetryFailedNotificationWithIncrementedCount() {
            // Arrange
            UUID failedNotificationId = UUID.randomUUID();
            NotificationFailed notificationFailed = new NotificationFailed();
            notificationFailed.setId(failedNotificationId);
            notificationFailed.setStatus(NotificationFailedStatus.PENDING_RETRY);
            notificationFailed.setRetryCount(0);
            notificationFailed.setMaxRetries(3);

            when(notificationFailedRepository.findById(failedNotificationId))
                    .thenReturn(Optional.of(notificationFailed));
            when(notificationFailedRepository.save(any()))
                    .thenReturn(notificationFailed);

            // Act
            notificationApplicationService.retryFailedNotification(failedNotificationId);

            // Assert
            verify(notificationFailedRepository).save(any(NotificationFailed.class));
        }

        @Test
        @DisplayName("Should initialize retry with max retries and scheduled time")
        void shouldInitializeRetryWithMaxRetriesAndScheduledTime() {
            // Arrange
            UUID peopleId = UUID.randomUUID();
            UUID userNotificationId = UUID.randomUUID();

            NotificationFailed notificationFailed = new NotificationFailed();
            notificationFailed.setPeopleId(peopleId);
            notificationFailed.setUserNotificationId(userNotificationId);
            notificationFailed.setStatus(NotificationFailedStatus.PENDING_RETRY);
            notificationFailed.setRetryCount(0);
            notificationFailed.setMaxRetries(3);

            when(notificationFailedRepository.save(any(NotificationFailed.class)))
                    .thenReturn(notificationFailed);

            // Act
            notificationApplicationService.recordFailedNotification(
                    peopleId, userNotificationId, "EVENT", Channels.APP, testVariables);

            // Assert
            verify(notificationFailedRepository).save(any(NotificationFailed.class));
        }
    }

    @Nested
    @DisplayName("Notification Personalization in Service")
    class PersonalizationInService {

        @Test
        @DisplayName("Should persist complex personalization variables")
        void shouldPersistComplexPersonalizationVariables() {
            // Arrange
            Map<String, Object> personalizationVars = new HashMap<>();
            personalizationVars.put("first_name", "María");
            personalizationVars.put("last_name", "Rodríguez");
            personalizationVars.put("email", "maria@iglesia.com");
            personalizationVars.put("phone", "+34687654321");
            personalizationVars.put("church", "Iglesia de la Gracia");
            personalizationVars.put("ministry", "Adoración");
            personalizationVars.put("role", "Coordinadora");

            UUID notificationId = UUID.randomUUID();
            Notification savedNotification = new Notification();
            savedNotification.setId(notificationId);
            savedNotification.setVariables(personalizationVars);

            when(notificationRepository.save(any(Notification.class)))
                    .thenReturn(savedNotification);

            // Act
            Notification result = notificationApplicationService.createNotification(
                    "Bienvenida personalizada", "Te damos la bienvenida",
                    "MINISTRY", Channels.EMAIL, "welcome_template",
                    personalizationVars, null);

            // Assert
            verify(notificationRepository).save(notificationCaptor.capture());
            Map<String, Object> savedVars = notificationCaptor.getValue().getVariables();
            assertThat(savedVars)
                    .hasSize(7)
                    .containsEntry("first_name", "María")
                    .containsEntry("email", "maria@iglesia.com")
                    .containsEntry("ministry", "Adoración");
        }

        @Test
        @DisplayName("Should handle special characters in personalization variables")
        void shouldHandleSpecialCharactersInVariables() {
            // Arrange
            Map<String, Object> vars = new HashMap<>();
            vars.put("name", "José María de la Cruz");
            vars.put("special_chars", "¿Cómo estás? ¡Bien!");
            vars.put("symbols", "@#$%&*");
            vars.put("unicode", "😊✓🔔");

            UUID notificationId = UUID.randomUUID();
            Notification savedNotification = new Notification();
            savedNotification.setId(notificationId);
            savedNotification.setVariables(vars);

            when(notificationRepository.save(any(Notification.class)))
                    .thenReturn(savedNotification);

            // Act
            Notification result = notificationApplicationService.createNotification(
                    "Notificación especial", "Contenido especial",
                    "ADMINISTRATIVE", Channels.APP, null, vars, null);

            // Assert
            verify(notificationRepository).save(notificationCaptor.capture());
            Map<String, Object> savedVars = notificationCaptor.getValue().getVariables();
            assertThat(savedVars.get("name")).isEqualTo("José María de la Cruz");
            assertThat(savedVars.get("unicode")).isEqualTo("😊✓🔔");
        }
    }
}
