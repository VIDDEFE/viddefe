package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("NotificationDto and Personalization Tests")
class NotificationDtoTest {

    private NotificationDto notificationDto;
    private Map<String, Object> testVariables;

    @BeforeEach
    void setUp() {
        notificationDto = new NotificationDto();
        testVariables = new HashMap<>();
        testVariables.put("recipient_name", "Juan García");
        testVariables.put("church_name", "Iglesia Central");
        testVariables.put("event_date", "2026-03-15");
    }

    @Nested
    @DisplayName("NotificationDto Creation and Basic Properties")
    class NotificationDtoCreation {

        @Test
        @DisplayName("Should create notification DTO with all fields")
        void shouldCreateNotificationDtoWithAllFields() {
            // Arrange
            String to = "user_123";
            String template = "welcome_template";
            String subject = "Bienvenido a nuestro ministerio";
            Channels channels = Channels.EMAIL;
            UUID personId = UUID.randomUUID();
            NotificationTypeEnum type = NotificationTypeEnum.ACCOUNT_CREATED;

            // Act
            notificationDto.setTo(to);
            notificationDto.setTemplate(template);
            notificationDto.setSubject(subject);
            notificationDto.setVariables(testVariables);
            notificationDto.setChannels(channels);
            notificationDto.setPersonId(personId);
            notificationDto.setNotificationType(type);

            // Assert
            assertThat(notificationDto.getTo()).isEqualTo(to);
            assertThat(notificationDto.getTemplate()).isEqualTo(template);
            assertThat(notificationDto.getSubject()).isEqualTo(subject);
            assertThat(notificationDto.getVariables()).isEqualTo(testVariables);
            assertThat(notificationDto.getChannels()).isEqualTo(channels);
            assertThat(notificationDto.getPersonId()).isEqualTo(personId);
            assertThat(notificationDto.getNotificationType()).isEqualTo(type);
        }

        @Test
        @DisplayName("Should create notification DTO using builder pattern")
        void shouldCreateNotificationDtoUsingBuilder() {
            // Act
            NotificationDto dto = NotificationDto.builder()
                    .to("user_456")
                    .template("ministry_template")
                    .subject("Invitación al ministerio")
                    .variables(testVariables)
                    .channels(Channels.EMAIL)
                    .personId(UUID.randomUUID())
                    .notificationType(NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER)
                    .build();

            // Assert
            assertThat(dto.getTo()).isEqualTo("user_456");
            assertThat(dto.getTemplate()).isEqualTo("ministry_template");
            assertThat(dto.getChannels()).isEqualTo(Channels.EMAIL);
        }

        @Test
        @DisplayName("Should support all notification types in DTO")
        void shouldSupportAllNotificationTypes() {
            // Act & Assert
            for (NotificationTypeEnum type : NotificationTypeEnum.values()) {
                notificationDto.setNotificationType(type);
                assertThat(notificationDto.getNotificationType()).isEqualTo(type);
            }
        }

        @Test
        @DisplayName("Should support all channels in DTO")
        void shouldSupportAllChannelsInDto() {
            // Act & Assert
            for (Channels channel : Channels.values()) {
                notificationDto.setChannels(channel);
                assertThat(notificationDto.getChannels()).isEqualTo(channel);
            }
        }
    }

    @Nested
    @DisplayName("Notification Recipient Information")
    class RecipientInformation {

        @Test
        @DisplayName("Should store recipient identifier (to field)")
        void shouldStoreRecipientIdentifier() {
            // Arrange
            String recipientId = "user_" + UUID.randomUUID().toString();

            // Act
            notificationDto.setTo(recipientId);

            // Assert
            assertThat(notificationDto.getTo()).isEqualTo(recipientId);
        }

        @Test
        @DisplayName("Should store person UUID separately from recipient ID")
        void shouldStorePersonUUIDSeparatelyFromRecipientId() {
            // Arrange
            String recipientId = "stream_client_123";
            UUID personId = UUID.randomUUID();

            // Act
            notificationDto.setTo(recipientId);
            notificationDto.setPersonId(personId);

            // Assert
            assertThat(notificationDto.getTo()).isEqualTo(recipientId);
            assertThat(notificationDto.getPersonId()).isEqualTo(personId);
            assertThat(notificationDto.getTo()).isNotEqualTo(personId.toString());
        }

        @Test
        @DisplayName("Should support optional remitter field")
        void shouldSupportOptionalRemitterField() {
            // Arrange
            UUID remitterId = UUID.randomUUID();

            // Act
            notificationDto.setRemitter(remitterId);

            // Assert
            assertThat(notificationDto.getRemitter()).isEqualTo(remitterId);
        }
    }

    @Nested
    @DisplayName("Notification Personalization Variables")
    class PersonalizationVariables {

        @Test
        @DisplayName("Should store personalization variables for template interpolation")
        void shouldStorePersonalizationVariables() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("first_name", "María");
            variables.put("last_name", "López");
            variables.put("email", "maria@iglesia.com");

            // Act
            notificationDto.setVariables(variables);

            // Assert
            assertThat(notificationDto.getVariables())
                    .containsEntry("first_name", "María")
                    .containsEntry("last_name", "López")
                    .containsEntry("email", "maria@iglesia.com")
                    .hasSize(3);
        }

        @Test
        @DisplayName("Should support complex personalization with all details")
        void shouldSupportComplexPersonalization() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("first_name", "Carlos");
            variables.put("last_name", "Rodríguez");
            variables.put("username", "carlos.rodriguez");
            variables.put("email", "carlos@iglesia.com");
            variables.put("phone", "+34612345678");
            variables.put("church", "Iglesia de la Fe");
            variables.put("ministry", "Adoración");
            variables.put("role", "Coordinador");
            variables.put("join_date", "2025-01-15");

            // Act
            notificationDto.setVariables(variables);

            // Assert
            assertThat(notificationDto.getVariables())
                    .hasSize(9)
                    .containsKeys("first_name", "email", "ministry", "role");
        }

        @Test
        @DisplayName("Should preserve variable values exactly")
        void shouldPreserveVariableValuesExactly() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("exact_string", "Exact Value");
            variables.put("number_value", 42);
            variables.put("decimal_value", 3.14159);
            variables.put("boolean_value", true);

            // Act
            notificationDto.setVariables(variables);

            // Assert
            assertThat(notificationDto.getVariables().get("exact_string"))
                    .isEqualTo("Exact Value");
            assertThat(notificationDto.getVariables().get("number_value"))
                    .isEqualTo(42);
            assertThat(notificationDto.getVariables().get("decimal_value"))
                    .isEqualTo(3.14159);
            assertThat(notificationDto.getVariables().get("boolean_value"))
                    .isEqualTo(true);
        }

        @Test
        @DisplayName("Should support variables with special characters")
        void shouldSupportVariablesWithSpecialCharacters() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "José María de la Cruz");
            variables.put("message", "¡Bienvenido! ¿Cómo estás?");
            variables.put("emoji", "🎉✨🙏💖");
            variables.put("symbols", "@#$%&*()");

            // Act
            notificationDto.setVariables(variables);

            // Assert
            assertThat(notificationDto.getVariables().get("name"))
                    .isEqualTo("José María de la Cruz");
            assertThat(notificationDto.getVariables().get("emoji"))
                    .isEqualTo("🎉✨🙏💖");
            assertThat(notificationDto.getVariables().get("symbols"))
                    .isEqualTo("@#$%&*()");
        }

        @Test
        @DisplayName("Should handle null values in variables")
        void shouldHandleNullValuesInVariables() {
            // Arrange
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "Juan");
            variables.put("optional_field", null);
            variables.put("another_field", "Value");

            // Act
            notificationDto.setVariables(variables);

            // Assert
            assertThat(notificationDto.getVariables()).containsKey("optional_field");
            assertThat(notificationDto.getVariables().get("optional_field")).isNull();
            assertThat(notificationDto.getVariables().get("name")).isNotNull();
        }

        @Test
        @DisplayName("Should support empty variables map")
        void shouldSupportEmptyVariablesMap() {
            // Arrange
            Map<String, Object> emptyVariables = new HashMap<>();

            // Act
            notificationDto.setVariables(emptyVariables);

            // Assert
            assertThat(notificationDto.getVariables()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Notification Template and Subject")
    class TemplateAndSubject {

        @Test
        @DisplayName("Should store template reference for HTML rendering")
        void shouldStoreTemplateReferenceForRendering() {
            // Arrange
            String template = "welcome_email_template";

            // Act
            notificationDto.setTemplate(template);

            // Assert
            assertThat(notificationDto.getTemplate()).isEqualTo(template);
        }

        @Test
        @DisplayName("Should store subject for email notifications")
        void shouldStoreSubjectForEmailNotifications() {
            // Arrange
            String template = "email_template";
            String subject = "Bienvenido a nuestro ministerio";

            // Act
            notificationDto.setTemplate(template);
            notificationDto.setSubject(subject);

            // Assert
            assertThat(notificationDto.getTemplate()).isEqualTo(template);
            assertThat(notificationDto.getSubject()).isEqualTo(subject);
        }

        @Test
        @DisplayName("Should support template names with various patterns")
        void shouldSupportVariousTemplateNamePatterns() {
            // Arrange & Act & Assert
            String[] templates = {
                    "simple_template",
                    "ministry_invitation_template",
                    "event_notification_v2",
                    "template-with-hyphens",
                    "template_with_underscores",
                    "CamelCaseTemplate"
            };

            for (String template : templates) {
                notificationDto.setTemplate(template);
                assertThat(notificationDto.getTemplate()).isEqualTo(template);
            }
        }

        @Test
        @DisplayName("Should support subject with locale-specific characters")
        void shouldSupportSubjectWithLocaleSpecificCharacters() {
            // Arrange
            String[] subjects = {
                    "¡Bienvenido a nuestro ministerio!",
                    "Réunion d'adoration - 15 mars",
                    "Приглашение на служение",
                    "欢迎加入我们的事工"
            };

            // Act & Assert
            for (String subject : subjects) {
                notificationDto.setSubject(subject);
                assertThat(notificationDto.getSubject()).isEqualTo(subject);
            }
        }
    }

    @Nested
    @DisplayName("Notification Channel Configuration")
    class ChannelConfiguration {

        @Test
        @DisplayName("Should specify delivery channel for notification")
        void shouldSpecifyDeliveryChannel() {
            // Arrange
            Channels channel = Channels.EMAIL;

            // Act
            notificationDto.setChannels(channel);

            // Assert
            assertThat(notificationDto.getChannels()).isEqualTo(channel);
        }

        @Test
        @DisplayName("Should support multiple channel types")
        void shouldSupportMultipleChannelTypes() {
            // Act & Assert
            notificationDto.setChannels(Channels.EMAIL);
            assertThat(notificationDto.getChannels()).isEqualTo(Channels.EMAIL);

            notificationDto.setChannels(Channels.WHATSAPP);
            assertThat(notificationDto.getChannels()).isEqualTo(Channels.WHATSAPP);

            notificationDto.setChannels(Channels.APP);
            assertThat(notificationDto.getChannels()).isEqualTo(Channels.APP);
        }

        @Test
        @DisplayName("Should change channel for same notification")
        void shouldChangeChannelForSameNotification() {
            // Arrange
            notificationDto.setTo("user_123");
            notificationDto.setTemplate("template");

            // Act
            notificationDto.setChannels(Channels.EMAIL);
            assertThat(notificationDto.getChannels()).isEqualTo(Channels.EMAIL);

            notificationDto.setChannels(Channels.WHATSAPP);
            assertThat(notificationDto.getChannels()).isEqualTo(Channels.WHATSAPP);

            // Assert
            assertThat(notificationDto.getTo()).isEqualTo("user_123");
            assertThat(notificationDto.getTemplate()).isEqualTo("template");
        }
    }

    @Nested
    @DisplayName("Notification Type Information")
    class NotificationTypeInformation {

        @Test
        @DisplayName("Should store notification type")
        void shouldStoreNotificationType() {
            // Arrange
            NotificationTypeEnum type = NotificationTypeEnum.ACCOUNT_CREATED;

            // Act
            notificationDto.setNotificationType(type);

            // Assert
            assertThat(notificationDto.getNotificationType()).isEqualTo(type);
            assertThat(notificationDto.getNotificationType()).isEqualTo(type);
        }

        @Test
        @DisplayName("Should support PASSWORD_RESET type with context reference")
        void shouldSupportEventTypeWithContext() {
            // Arrange
            UUID remitterId = UUID.randomUUID();

            // Act
            notificationDto.setNotificationType(NotificationTypeEnum.PASSWORD_RESET);
            notificationDto.setRemitter(remitterId);

            // Assert
            assertThat(notificationDto.getNotificationType()).isEqualTo(NotificationTypeEnum.PASSWORD_RESET);
            assertThat(notificationDto.getRemitter()).isEqualTo(remitterId);
        }

        @Test
        @DisplayName("Should support NOTIFICATION_APP_SSE type")
        void shouldSupportAdministrativeType() {
            // Act
            notificationDto.setNotificationType(NotificationTypeEnum.NOTIFICATION_APP_SSE);

            // Assert
            assertThat(notificationDto.getNotificationType())
                    .isEqualTo(NotificationTypeEnum.NOTIFICATION_APP_SSE);
        }
    }

    @Nested
    @DisplayName("NotificationDto Fluent Builder")
    class FluentBuilder {

        @Test
        @DisplayName("Should build complete notification DTO with fluent API")
        void shouldBuildCompleteNotificationWithFluentAPI() {
            // Arrange & Act
            NotificationDto dto = NotificationDto.builder()
                    .to("user_stream_123")
                    .template("ministry_invitation_template")
                    .subject("Invitación especial a nuestro ministerio")
                    .variables(testVariables)
                    .channels(Channels.EMAIL)
                    .personId(UUID.randomUUID())
                    .remitter(UUID.randomUUID())
                    .notificationType(NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER)
                    .build();

            // Assert
            assertThat(dto.getTo()).isEqualTo("user_stream_123");
            assertThat(dto.getTemplate()).isEqualTo("ministry_invitation_template");
            assertThat(dto.getSubject()).isEqualTo("Invitación especial a nuestro ministerio");
            assertThat(dto.getVariables()).isEqualTo(testVariables);
            assertThat(dto.getChannels()).isEqualTo(Channels.EMAIL);
            assertThat(dto.getPersonId()).isNotNull();
            assertThat(dto.getRemitter()).isNotNull();
            assertThat(dto.getNotificationType()).isEqualTo(NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER);
        }

        @Test
        @DisplayName("Should allow partial construction with builder")
        void shouldAllowPartialConstructionWithBuilder() {
            // Act
            NotificationDto dto = NotificationDto.builder()
                    .to("user_123")
                    .template("basic_template")
                    .channels(Channels.APP)
                    .build();

            // Assert
            assertThat(dto.getTo()).isEqualTo("user_123");
            assertThat(dto.getTemplate()).isEqualTo("basic_template");
            assertThat(dto.getChannels()).isEqualTo(Channels.APP);
            assertThat(dto.getVariables()).isNull();
            assertThat(dto.getSubject()).isNull();
        }
    }

    @Nested
    @DisplayName("NotificationDto Validation Scenarios")
    class ValidationScenarios {

        @Test
        @DisplayName("Should allow setting required fields independently")
        void shouldAllowSettingRequiredFieldsIndependently() {
            // Act
            notificationDto.setTo("user_required");
            assertThat(notificationDto.getTo()).isEqualTo("user_required");

            notificationDto.setTemplate("template_required");
            assertThat(notificationDto.getTemplate()).isEqualTo("template_required");

            notificationDto.setVariables(testVariables);
            assertThat(notificationDto.getVariables()).isEqualTo(testVariables);

            notificationDto.setChannels(Channels.EMAIL);
            assertThat(notificationDto.getChannels()).isEqualTo(Channels.EMAIL);

            // Assert - All fields are set and accessible
            assertThat(notificationDto.getTo()).isNotNull();
            assertThat(notificationDto.getTemplate()).isNotNull();
            assertThat(notificationDto.getVariables()).isNotNull();
            assertThat(notificationDto.getChannels()).isNotNull();
        }

        @Test
        @DisplayName("Should allow optional fields to be null")
        void shouldAllowOptionalFieldsToBeNull() {
            // Arrange
            NotificationDto dto = new NotificationDto();

            // Assert
            assertThat(dto.getSubject()).isNull();
            assertThat(dto.getRemitter()).isNull();
            assertThat(dto.getPersonId()).isNull();
            assertThat(dto.getNotificationType()).isNull();
        }
    }
}
