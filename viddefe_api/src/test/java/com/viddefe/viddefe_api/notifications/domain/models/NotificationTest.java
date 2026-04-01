package com.viddefe.viddefe_api.notifications.domain.models;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.domain.models.enums.ContextEntityType;
import com.viddefe.viddefe_api.notifications.domain.models.enums.NotificationTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Notification Entity Tests")
class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
    }

    @Nested
    @DisplayName("Notification Creation and Basic Properties")
    class NotificationCreation {

        @Test
        @DisplayName("Should create notification with all required fields")
        void shouldCreateNotificationWithRequiredFields() {
            // Arrange
            String title = "Reunión de Ministerio";
            String body = "Una nueva reunión ha sido programada";
            NotificationTypeEnum type = NotificationTypeEnum.MINISTRY;
            Channels channel = Channels.EMAIL;

            // Act
            notification.setTitle(title);
            notification.setBody(body);
            notification.setType(type);
            notification.setChannel(channel);

            // Assert
            assertThat(notification.getTitle()).isEqualTo(title);
            assertThat(notification.getBody()).isEqualTo(body);
            assertThat(notification.getType()).isEqualTo(type);
            assertThat(notification.getChannel()).isEqualTo(channel);
        }

        @Test
        @DisplayName("Should create notification with template and variables")
        void shouldCreateNotificationWithTemplateAndVariables() {
            // Arrange
            String template = "welcome_email_template";
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipient_name", "Juan Pérez");
            variables.put("church_name", "Iglesia de la Fe");
            variables.put("event_date", "2026-03-15T10:30:00Z");

            // Act
            notification.setTemplate(template);
            notification.setVariables(variables);
            notification.setTitle("Bienvenida al ministerio");
            notification.setBody("Te damos la bienvenida");
            notification.setType(NotificationTypeEnum.MINISTRY);
            notification.setChannel(Channels.EMAIL);

            // Assert
            assertThat(notification.getTemplate()).isEqualTo(template);
            assertThat(notification.getVariables()).isNotNull();
            assertThat(notification.getVariables())
                    .containsEntry("recipient_name", "Juan Pérez")
                    .containsEntry("church_name", "Iglesia de la Fe")
                    .containsEntry("event_date", "2026-03-15T10:30:00Z")
                    .hasSize(3);
        }

        @Test
        @DisplayName("Should create notification with metadata in data field")
        void shouldCreateNotificationWithMetadata() {
            // Arrange
            Map<String, Object> data = new HashMap<>();
            data.put("priority", "HIGH");
            data.put("tracking_id", UUID.randomUUID().toString());
            data.put("retry_count", 0);

            // Act
            notification.setTitle("Notification");
            notification.setBody("Body");
            notification.setType(NotificationTypeEnum.ADMINISTRATIVE);
            notification.setChannel(Channels.APP);
            notification.setData(data);

            // Assert
            assertThat(notification.getData()).isNotNull();
            assertThat(notification.getData())
                    .containsEntry("priority", "HIGH")
                    .containsEntry("retry_count", 0);
            assertThat(notification.getData().get("tracking_id")).isNotNull();
        }

        @Test
        @DisplayName("Should support different notification types")
        void shouldSupportDifferentNotificationTypes() {
            // Arrange & Act & Assert
            for (NotificationTypeEnum type : NotificationTypeEnum.values()) {
                notification.setTitle("Test");
                notification.setBody("Test Body");
                notification.setType(type);
                notification.setChannel(Channels.APP);

                assertThat(notification.getType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should support different channels")
        void shouldSupportDifferentChannels() {
            // Arrange & Act & Assert
            notification.setTitle("Test");
            notification.setBody("Test Body");
            notification.setType(NotificationTypeEnum.EVENT);

            for (Channels channel : Channels.values()) {
                notification.setChannel(channel);
                assertThat(notification.getChannel()).isEqualTo(channel);
            }
        }
    }

    @Nested
    @DisplayName("Notification Context and Event References")
    class NotificationContext {

        @Test
        @DisplayName("Should store context ID and entity type for EVENT notifications")
        void shouldStoreEventContextInformation() {
            // Arrange
            UUID contextId = UUID.randomUUID();
            ContextEntityType contextEntityType = ContextEntityType.CHURCH;

            // Act
            notification.setTitle("Event Notification");
            notification.setBody("An event has occurred");
            notification.setType(NotificationTypeEnum.EVENT);
            notification.setChannel(Channels.APP);
            notification.setContextId(contextId);
            notification.setContextEntityType(contextEntityType);

            // Assert
            assertThat(notification.getContextId()).isEqualTo(contextId);
            assertThat(notification.getContextEntityType()).isEqualTo(contextEntityType);
        }

        @Test
        @DisplayName("Should allow context ID and entity type for non-EVENT notifications")
        void shouldAllowContextForAllNotificationTypes() {
            // Arrange
            UUID contextId = UUID.randomUUID();
            ContextEntityType contextEntityType = ContextEntityType.GROUP;

            // Act
            notification.setTitle("Ministry Event");
            notification.setBody("Ministry notification with context");
            notification.setType(NotificationTypeEnum.MINISTRY);
            notification.setChannel(Channels.EMAIL);
            notification.setContextId(contextId);
            notification.setContextEntityType(contextEntityType);

            // Assert
            assertThat(notification.getContextId()).isEqualTo(contextId);
            assertThat(notification.getContextEntityType()).isEqualTo(contextEntityType);
        }
    }

    @Nested
    @DisplayName("Notification Timestamps")
    class NotificationTimestamps {

        @Test
        @DisplayName("Should have createdAt and updatedAt timestamps after creation")
        void shouldHaveTimestampsAfterCreation() {
            // Arrange
            notification.setTitle("Test");
            notification.setBody("Test Body");
            notification.setType(NotificationTypeEnum.ADMINISTRATIVE);
            notification.setChannel(Channels.APP);

            Instant now = Instant.now();

            // Act - Note: In real persistence, Hibernate handles this
            notification.setCreatedAt(now);
            notification.setUpdatedAt(now);

            // Assert
            assertThat(notification.getCreatedAt()).isNotNull();
            assertThat(notification.getUpdatedAt()).isNotNull();
            assertThat(notification.getUpdatedAt()).isAfterOrEqualTo(notification.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Notification Personalization")
    class NotificationPersonalization {

        @Test
        @DisplayName("Should store complex personalized variables")
        void shouldStoreComplexPersonalizedVariables() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("first_name", "Carlos");
            variables.put("last_name", "García");
            variables.put("email", "carlos.garcia@iglesia.com");
            variables.put("phone", "+34612345678");
            variables.put("role", "Líder del ministerio");
            variables.put("church", "Iglesia Central");

            // Act
            notification.setTitle("Invitación personalizada");
            notification.setBody("Estás invitado a una reunión especial");
            notification.setType(NotificationTypeEnum.MINISTRY);
            notification.setChannel(Channels.EMAIL);
            notification.setTemplate("ministry_invitation");
            notification.setVariables(variables);

            // Assert
            assertThat(notification.getVariables())
                    .hasSize(6)
                    .containsEntry("first_name", "Carlos")
                    .containsEntry("last_name", "García")
                    .containsEntry("role", "Líder del ministerio");
        }

        @Test
        @DisplayName("Should handle nested variables for complex templates")
        void shouldHandleNestedVariables() {
            // Arrange
            Map<String, Object> nestedData = new HashMap<>();
            nestedData.put("time", "10:30 AM");
            nestedData.put("location", "Salón principal");

            Map<String, Object> variables = new HashMap<>();
            variables.put("user_name", "María López");
            variables.put("event_details", nestedData);

            // Act
            notification.setTitle("Recordatorio de evento");
            notification.setBody("Tienes un evento programado");
            notification.setType(NotificationTypeEnum.EVENT);
            notification.setChannel(Channels.APP);
            notification.setVariables(variables);

            // Assert
            assertThat(notification.getVariables())
                    .isNotNull()
                    .containsKeys("user_name", "event_details");
            assertThat(notification.getVariables().get("event_details")).isInstanceOf(Map.class);
        }

        @Test
        @DisplayName("Should preserve null values in variables")
        void shouldPreserveNullValuesInVariables() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "Juan");
            variables.put("optional_field", null);

            // Act
            notification.setTitle("Test");
            notification.setBody("Test Body");
            notification.setType(NotificationTypeEnum.ADMINISTRATIVE);
            notification.setChannel(Channels.EMAIL);
            notification.setVariables(variables);

            // Assert
            assertThat(notification.getVariables()).containsKey("optional_field");
            assertThat(notification.getVariables().get("optional_field")).isNull();
        }
    }

    @Nested
    @DisplayName("Notification Builder Pattern")
    class NotificationBuilder {

        @Test
        @DisplayName("Should build a complete notification with fluent pattern")
        void shouldBuildCompleteNotification() {
            // Arrange & Act
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "Pedro");
            variables.put("church", "Iglesia de Gracia");

            Map<String, Object> data = new HashMap<>();
            data.put("priority", "HIGH");

            UUID contextId = UUID.randomUUID();
            Notification notification = new Notification(
                    UUID.randomUUID(),
                    NotificationTypeEnum.MINISTRY,
                    "Invitación al ministerio",
                    "Te invitamos a participar",
                    Channels.EMAIL,
                    "ministry_template",
                    variables,
                    data,
                    contextId,
                    ContextEntityType.GROUP,
                    Instant.now(),
                    Instant.now()
            );

            // Assert
            assertThat(notification.getId()).isNotNull();
            assertThat(notification.getTitle()).isEqualTo("Invitación al ministerio");
            assertThat(notification.getChannel()).isEqualTo(Channels.EMAIL);
            assertThat(notification.getVariables().get("name")).isEqualTo("Pedro");
            assertThat(notification.getData().get("priority")).isEqualTo("HIGH");
            assertThat(notification.getContextId()).isEqualTo(contextId);
        }
    }
}
