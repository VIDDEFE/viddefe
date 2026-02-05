package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.config.rabbit.RabbitQueues;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * EventNotification is responsible for handling event notifications.
 * it´s recibes events {@link NotificationDto} and process store the notifications in RabbitMQ
 *
 */
@Component
@RequiredArgsConstructor
public class NotificationEventPublisherImpl implements NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
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