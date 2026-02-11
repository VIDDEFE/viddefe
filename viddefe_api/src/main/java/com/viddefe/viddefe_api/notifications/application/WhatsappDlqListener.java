package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.contracts.NotificationFailedService;
import com.viddefe.viddefe_api.notifications.domain.models.NotificationsFailed;
import com.viddefe.viddefe_api.notifications.common.NotificationStatus;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

/**
 * Listener para la Dead Letter Queue (DLQ) de WhatsApp.
 * Registra mensajes que no pudieron ser procesados para análisis posterior.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsappDlqListener {

    private final NotificationFailedService notificationFailedService;

    @RabbitListener(queues = RabbitQueues.WHATSAPP_DLQ)
    public void handleDlqMessage(WhatsappMessageDto messageDto) {

        String correlationId = messageDto.getCorrelationId();
        String failureReason = "Exceeded max retries or non-retryable error"; // Esto se podría mejorar si se envía el motivo exacto desde el listener principal
        String failureTime = messageDto.getLastRetryAt() != null ? messageDto.getLastRetryAt().toString() : "Unknown";
        String originalMessage = ResolverMessage.resolveMessage(
            messageDto.getTemplate(),
            messageDto.getVariables()
        );


        //Here send a message to the sender of the message to notify that the message has failed and will be stored in the database for later analysis and potential retries.
        //sendNotificationToSenderSSE(messageDto.getTo(), originalMessage, failureReason);

        log.error("WhatsApp message sent to DLQ - CorrelationId: {}, Reason: {}, Time: {}, Message: {}",
                  correlationId, failureReason, failureTime, originalMessage);
        notificationFailedService.createFailedNotification(messageDto);
    }
}
