package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.common.RabbitQueues;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;

/**
 * EventNotification is responsible for handling event notifications.
 * it´s recibes events {@link NotificationDto} and process store the notifications in RabbitMQ
 *
 */
@Component
@RequiredArgsConstructor
public class NotificationEventPublisherImpl implements NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(NotificationEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitQueues.NOTIFICATIONS_EXCHANGE,
                event.getNotificationType().routingKey(),   // Decide the routing key based on notification type
                event,
                message -> {
                    message.getMessageProperties()
                            .setPriority(event.getPriority().value());
                    return message;
                }
        );
    }
}