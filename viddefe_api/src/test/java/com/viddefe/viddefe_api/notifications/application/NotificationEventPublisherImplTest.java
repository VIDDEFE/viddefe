package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitPriority;
import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationEventPublisherImpl Tests")
class NotificationEventPublisherImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificationEventPublisherImpl notificationEventPublisher;

    @Captor
    private ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor;

    private TestNotificationEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new TestNotificationEvent();
        testEvent.setPersonId(UUID.randomUUID());
        testEvent.setPriority(RabbitPriority.HIGH);
        testEvent.setCreatedAt(Instant.now());
        testEvent.setSubject("Test Subject");
        testEvent.setTemplate("test_template");
        testEvent.setVariables(Map.of("key", "value"));
    }

    @Nested
    @DisplayName("publish Tests")
    class PublishTests {

        @Test
        @DisplayName("Should send notification to RabbitMQ")
        void shouldSendNotificationToRabbitMQ() {
            notificationEventPublisher.publish(testEvent);

            verify(rabbitTemplate).convertAndSend(
                    eq(RabbitQueues.NOTIFICATIONS_EXCHANGE),
                    eq(NotificationTypeEnum.ACCOUNT_CREATED.routingKey()),
                    eq(testEvent),
                    any(MessagePostProcessor.class)
            );
        }

        @Test
        @DisplayName("Should use correct exchange")
        void shouldUseCorrectExchange() {
            notificationEventPublisher.publish(testEvent);

            verify(rabbitTemplate).convertAndSend(
                    eq(RabbitQueues.NOTIFICATIONS_EXCHANGE),
                    any(String.class),
                    any(),
                    any(MessagePostProcessor.class)
            );
        }

        @Test
        @DisplayName("Should use routing key from notification type")
        void shouldUseRoutingKeyFromNotificationType() {
            notificationEventPublisher.publish(testEvent);

            verify(rabbitTemplate).convertAndSend(
                    any(String.class),
                    eq(testEvent.getNotificationType().routingKey()),
                    any(),
                    any(MessagePostProcessor.class)
            );
        }

        @Test
        @DisplayName("Should set message priority")
        void shouldSetMessagePriority() {
            // Capture the MessagePostProcessor
            doAnswer(invocation -> {
                MessagePostProcessor processor = invocation.getArgument(3);
                Message message = mock(Message.class);
                MessageProperties props = new MessageProperties();
                when(message.getMessageProperties()).thenReturn(props);
                processor.postProcessMessage(message);
                assertThat(props.getPriority()).isEqualTo(RabbitPriority.HIGH.value());
                return null;
            }).when(rabbitTemplate).convertAndSend(
                    any(String.class),
                    any(String.class),
                    any(),
                    any(MessagePostProcessor.class)
            );

            notificationEventPublisher.publish(testEvent);

            verify(rabbitTemplate).convertAndSend(
                    any(String.class),
                    any(String.class),
                    any(),
                    any(MessagePostProcessor.class)
            );
        }
    }

    // Test implementation of NotificationEvent
    private static class TestNotificationEvent extends NotificationEvent {
        @Override
        public NotificationTypeEnum getNotificationType() {
            return NotificationTypeEnum.ACCOUNT_CREATED;
        }
    }
}

