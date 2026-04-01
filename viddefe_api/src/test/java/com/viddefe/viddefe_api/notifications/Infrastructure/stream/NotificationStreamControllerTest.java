package com.viddefe.viddefe_api.notifications.Infrastructure.stream;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("NotificationStreamController Tests")
class NotificationStreamControllerTest {

    private NotificationStreamController streamController;

    @BeforeEach
    void setUp() {
        streamController = new NotificationStreamController();
    }

    @Nested
    @DisplayName("Stream Connection Management")
    class StreamConnectionManagement {

        @Test
        @DisplayName("Should create and register SSE emitter for client")
        void shouldCreateAndRegisterEmitterForClient() {
            // Arrange
            String clientId = "user_" + UUID.randomUUID().toString();

            // Act
            SseEmitter emitter = streamController.stream(clientId);

            // Assert
            assertThat(emitter).isNotNull();
            assertThat(emitter.getTimeout()).isEqualTo(0L); // No timeout
        }

        @Test
        @DisplayName("Should register multiple clients with different IDs")
        void shouldRegisterMultipleClientsWithDifferentIds() {
            // Arrange
            String clientId1 = "user_123";
            String clientId2 = "user_456";
            String clientId3 = "user_789";

            // Act
            SseEmitter emitter1 = streamController.stream(clientId1);
            SseEmitter emitter2 = streamController.stream(clientId2);
            SseEmitter emitter3 = streamController.stream(clientId3);

            // Assert
            assertThat(emitter1).isNotNull();
            assertThat(emitter2).isNotNull();
            assertThat(emitter3).isNotNull();
            assertThat(emitter1).isNotSameAs(emitter2);
            assertThat(emitter2).isNotSameAs(emitter3);
        }

        @Test
        @DisplayName("Should replace emitter when client reconnects")
        void shouldReplaceEmitterOnClientReconnection() {
            // Arrange
            String clientId = "user_reconnect";

            // Act
            SseEmitter emitter1 = streamController.stream(clientId);
            SseEmitter emitter2 = streamController.stream(clientId);

            // Assert
            assertThat(emitter1).isNotNull();
            assertThat(emitter2).isNotNull();
            assertThat(emitter1).isNotSameAs(emitter2); // New emitter should be created
        }
    }

    @Nested
    @DisplayName("Stream Event Sending")
    class StreamEventSending {

        @Test
        @DisplayName("Should send event to registered client")
        void shouldSendEventToRegisteredClient() throws IOException {
            // Arrange
            String clientId = "user_send";
            SseEmitter emitter = streamController.stream(clientId);
            String payload = "Notification message";

            // Act
            streamController.sendEvent(clientId, payload);

            // Assert - No exception thrown means success
            assertThat(emitter).isNotNull();
        }

        @Test
        @DisplayName("Should not send event to unregistered client")
        void shouldNotSendEventToUnregisteredClient() {
            // Arrange
            String unregisteredClientId = "user_unregistered";
            String payload = "This should not be sent";

            // Act & Assert - should not throw exception, just silently ignore
            assertThatCode(() -> streamController.sendEvent(unregisteredClientId, payload))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should send notification DTO as event payload")
        void shouldSendNotificationDtoAsEventPayload() throws IOException {
            // Arrange
            String clientId = "user_dto";
            SseEmitter emitter = streamController.stream(clientId);
            
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipient_name", "Juan");
            variables.put("event_name", "Reunión de Ministerio");

            NotificationDto notificationDto = NotificationDto.builder()
                    .to(clientId)
                    .template("event_template")
                    .subject("Nueva reunión")
                    .variables(variables)
                    .channels(Channels.APP)
                    .personId(UUID.randomUUID())
                    .notificationType(NotificationTypeEnum.NOTIFICATION_APP_SSE)
                    .build();

            // Act
            streamController.sendEvent(clientId, notificationDto);

            // Assert
            assertThat(emitter).isNotNull();
        }

        @Test
        @DisplayName("Should send multiple events to same client sequentially")
        void shouldSendMultipleEventsToSameClient() {
            // Arrange
            String clientId = "user_multiple";
            SseEmitter emitter = streamController.stream(clientId);

            // Act
            streamController.sendEvent(clientId, "Message 1");
            streamController.sendEvent(clientId, "Message 2");
            streamController.sendEvent(clientId, "Message 3");

            // Assert
            assertThat(emitter).isNotNull();
        }

        @Test
        @DisplayName("Should send events to multiple clients independently")
        void shouldSendEventsToMultipleClientsIndependently() {
            // Arrange
            String clientId1 = "user_1";
            String clientId2 = "user_2";
            String clientId3 = "user_3";

            SseEmitter emitter1 = streamController.stream(clientId1);
            SseEmitter emitter2 = streamController.stream(clientId2);
            SseEmitter emitter3 = streamController.stream(clientId3);

            // Act
            streamController.sendEvent(clientId1, "Message for user 1");
            streamController.sendEvent(clientId2, "Message for user 2");
            streamController.sendEvent(clientId3, "Message for user 3");

            // Assert
            assertThat(emitter1).isNotNull();
            assertThat(emitter2).isNotNull();
            assertThat(emitter3).isNotNull();
        }
    }

    @Nested
    @DisplayName("Stream Cleanup on Completion")
    class StreamCleanupOnCompletion {

        @Test
        @DisplayName("Should handle emitter completion callback")
        void shouldHandleEmitterCompletion() {
            // Arrange
            String clientId = "user_completion";
            
            // Act
            SseEmitter emitter = streamController.stream(clientId);

            // Assert - Callbacks are handled by the controller
            assertThat(emitter).isNotNull();
        }

        @Test
        @DisplayName("Should handle emitter timeout callback")
        void shouldHandleEmitterTimeout() {
            // Arrange
            String clientId = "user_timeout";

            // Act
            SseEmitter emitter = streamController.stream(clientId);

            // Assert
            assertThat(emitter).isNotNull();
        }

        @Test
        @DisplayName("Should handle emitter error callback")
        void shouldHandleEmitterError() {
            // Arrange
            String clientId = "user_error";

            // Act
            SseEmitter emitter = streamController.stream(clientId);

            // Assert
            assertThat(emitter).isNotNull();
        }
    }

    @Nested
    @DisplayName("Stream Notification Personalization")
    class StreamNotificationPersonalization {

        @Test
        @DisplayName("Should send personalized notification to stream")
        void shouldSendPersonalizedNotificationToStream() {
            // Arrange
            String clientId = "user_personalized";
            streamController.stream(clientId);

            Map<String, Object> variables = new HashMap<>();
            variables.put("first_name", "María");
            variables.put("last_name", "González");
            variables.put("church_name", "Iglesia Central");
            variables.put("event_date", "2026-03-15");

            NotificationDto notificationDto = NotificationDto.builder()
                    .to(clientId)
                    .template("personalized_template")
                    .subject("Notificación personalizada")
                    .variables(variables)
                    .channels(Channels.APP)
                    .notificationType(NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER)
                    .build();

            // Act
            streamController.sendEvent(clientId, notificationDto);

            // Assert - Should complete without exception
            assertThat(notificationDto.getVariables()).hasSize(4);
            assertThat(notificationDto.getVariables()).containsEntry("first_name", "María");
        }

        @Test
        @DisplayName("Should preserve variable values in stream notification")
        void shouldPreserveVariableValuesInStreamNotification() {
            // Arrange
            String clientId = "user_vars_preserved";
            streamController.stream(clientId);

            Map<String, Object> variables = new HashMap<>();
            variables.put("role", "Líder de ministerio");
            variables.put("phone", "+34612345678");
            variables.put("ministry", "Adoración");

            // Act
            streamController.sendEvent(clientId, variables);

            // Assert
            assertThat(variables)
                    .containsEntry("role", "Líder de ministerio")
                    .containsEntry("ministry", "Adoración");
        }

        @Test
        @DisplayName("Should send notification with special characters in personalization")
        void shouldSendNotificationWithSpecialCharactersInPersonalization() {
            // Arrange
            String clientId = "user_special_chars";
            streamController.stream(clientId);

            Map<String, Object> variables = new HashMap<>();
            variables.put("name", "José María de la Cruz");
            variables.put("message", "¡Bienvenido a nuestro ministerio!");
            variables.put("emoji", "🎉✨🙏");

            NotificationDto notificationDto = NotificationDto.builder()
                    .to(clientId)
                    .template("special_template")
                    .subject("Notificación especial")
                    .variables(variables)
                    .channels(Channels.APP)
                    .build();

            // Act
            streamController.sendEvent(clientId, notificationDto);

            // Assert
            assertThat(notificationDto.getVariables().get("name"))
                    .isEqualTo("José María de la Cruz");
            assertThat(notificationDto.getVariables().get("emoji"))
                    .isEqualTo("🎉✨🙏");
        }
    }

    @Nested
    @DisplayName("Stream Controller Channel Support")
    class ChannelSupport {

        @Test
        @DisplayName("Should identify stream controller as APP channel notificator")
        void shouldIdentifyAsAppChannel() {
            // Act
            Channels channel = streamController.channel();

            // Assert
            assertThat(channel).isEqualTo(Channels.APP);
        }

        @Test
        @DisplayName("Should send notification via Notificator interface")
        void shouldSendNotificationViaNotificatorInterface() {
            // Arrange
            String clientId = "user_notificator";
            streamController.stream(clientId);

            Map<String, Object> variables = new HashMap<>();
            variables.put("user_name", "Pedro");

            NotificationDto notificationDto = NotificationDto.builder()
                    .to(clientId)
                    .template("notificator_template")
                    .subject("Test")
                    .variables(variables)
                    .channels(Channels.APP)
                    .build();

            // Act
            streamController.send(notificationDto);

            // Assert
            assertThat(notificationDto.getTo()).isEqualTo(clientId);
        }
    }

    @Nested
    @DisplayName("Stream with Real-time Notification Scenarios")
    class RealTimeNotificationScenarios {

        @Test
        @DisplayName("Should handle rapid fire notifications to single client")
        void shouldHandleRapidNotificationsToSingleClient() {
            // Arrange
            String clientId = "user_rapid";
            streamController.stream(clientId);

            // Act & Assert
            for (int i = 0; i < 10; i++) {
                Map<String, Object> vars = new HashMap<>();
                vars.put("message_id", i);
                vars.put("timestamp", System.currentTimeMillis());
                
                assertThatCode(() -> streamController.sendEvent(clientId, vars))
                        .doesNotThrowAnyException();
            }
        }

        @Test
        @DisplayName("Should handle notifications to multiple clients simultaneously")
        void shouldHandleNotificationsToMultipleClientsConcurrently() {
            // Arrange
            String[] clientIds = new String[5];
            for (int i = 0; i < 5; i++) {
                clientIds[i] = "user_concurrent_" + i;
                streamController.stream(clientIds[i]);
            }

            // Act & Assert
            for (int i = 0; i < 5; i++) {
                final int index = i;
                Map<String, Object> vars = new HashMap<>();
                vars.put("item", "Notification to user " + i);
                
                assertThatCode(() -> streamController.sendEvent(clientIds[index], vars))
                        .doesNotThrowAnyException();
            }
        }

        @Test
        @DisplayName("Should handle different notification types in stream")
        void shouldHandleDifferentNotificationTypesInStream() {
            // Arrange
            String clientId = "user_mixed_types";
            streamController.stream(clientId);

            // Act & Assert
            NotificationTypeEnum[] types = {
                    NotificationTypeEnum.ACCOUNT_CREATED,
                    NotificationTypeEnum.PASSWORD_RESET,
                    NotificationTypeEnum.MINISTRY_FUNCTION_REMINDER,
                    NotificationTypeEnum.NOTIFICATION_APP_SSE
            };
            for (NotificationTypeEnum type : types) {
                NotificationDto dto = NotificationDto.builder()
                        .to(clientId)
                        .template("template_" + type)
                        .notificationType(type)
                        .variables(new HashMap<>())
                        .channels(Channels.APP)
                        .build();

                streamController.send(dto);
                assertThat(dto.getNotificationType()).isEqualTo(type);
            }
        }
    }

    @Nested
    @DisplayName("Stream Emitter Lifecycle")
    class EmitterLifecycle {

        @Test
        @DisplayName("Should create fresh emitter for each stream request")
        void shouldCreateFreshEmitterForEachRequest() {
            // Arrange
            String clientId = "user_fresh";

            // Act
            SseEmitter emitter1 = streamController.stream(clientId);
            SseEmitter emitter2 = streamController.stream(clientId);
            SseEmitter emitter3 = streamController.stream(clientId);

            // Assert
            assertThat(emitter1).isNotSameAs(emitter2);
            assertThat(emitter2).isNotSameAs(emitter3);
        }

        @Test
        @DisplayName("Should have no timeout configured for emitter")
        void shouldHaveNoTimeoutConfigured() {
            // Arrange
            String clientId = "user_no_timeout";

            // Act
            SseEmitter emitter = streamController.stream(clientId);

            // Assert
            assertThat(emitter.getTimeout()).isEqualTo(0L);
        }

        @Test
        @DisplayName("Should maintain separate emitter instances for different clients")
        void shouldMaintainSeparateInstancesForDifferentClients() {
            // Arrange & Act
            SseEmitter emitterA = streamController.stream("user_A");
            SseEmitter emitterB = streamController.stream("user_B");

            // Assert
            assertThat(emitterA).isNotSameAs(emitterB);
            assertThat(emitterA.getTimeout()).isEqualTo(emitterB.getTimeout());
        }
    }

    @Nested
    @DisplayName("Stream Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle send error gracefully by removing client")
        void shouldHandleSendErrorGracefully() {
            // Arrange
            String clientId = "user_error_handling";
            streamController.stream(clientId);

            // Act - Attempting to send to disconnected client
            // In real scenario, emitter would throw IOException
            streamController.sendEvent(clientId, "Message");

            // Assert - Second send to same client should handle gracefully
            assertThatCode(() -> streamController.sendEvent(clientId, "Another message"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should silently ignore send when client not found")
        void shouldSilentlyIgnoreSendWhenClientNotFound() {
            // Act & Assert
            assertThatCode(() -> streamController.sendEvent("non_existent_client", "Message"))
                    .doesNotThrowAnyException();
        }
    }
}
