package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.contracts.NotificationFailedService;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

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

        log.error("WhatsApp message sent to DLQ - CorrelationId: {}, Reason: {}, Time: {}, Message: {}",
                  correlationId, failureReason, failureTime, originalMessage);
        notificationFailedService.createFailedNotification(messageDto);
    }
}
