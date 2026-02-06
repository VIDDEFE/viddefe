package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.common.exceptions.NonRetryableWhatsappException;
import com.viddefe.viddefe_api.notifications.common.exceptions.RetryableWhatsappException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Listener resiliente para mensajes de WhatsApp.
 *
 * Flujo:
 * 1. Mensaje -> whatsapp.queue -> Listener
 * 2. Error transitorio -> whatsapp.retry.queue (con TTL) -> vuelta a whatsapp.queue
 * 3. Error no recuperable o máximo reintentos -> whatsapp.dlq
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsappMessageListener {

    private static final int MAX_RETRY_COUNT = 3;

    private final WhatsappClient whatsappClient;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitQueues.WHATSAPP_QUEUE, concurrency = "1-5")
    public void handleWhatsappMessage(WhatsappMessageDto messageDto) {
        try {
            log.info("Processing WhatsApp message for: {} (attempt: {})",
                     messageDto.getPhoneNumber(), messageDto.getRetryCount() + 1);

            String message = ResolverMessage.resolveMessage(
                messageDto.getTemplate(),
                messageDto.getVariables()
            );

            whatsappClient.sendTextMessage(messageDto.getPhoneNumber(), message);

            log.info("WhatsApp message processed successfully for: {}", messageDto.getPhoneNumber());

        } catch (RetryableWhatsappException e) {
            handleRetryableError(messageDto, e);
        } catch (NonRetryableWhatsappException e) {
            handleNonRetryableError(messageDto, e);
        } catch (Exception e) {
            // Errores inesperados -> retry para ser conservadores
            log.warn("Unexpected error processing WhatsApp message, treating as retryable", e);
            handleRetryableError(messageDto, e);
        }
    }

    private void handleRetryableError(WhatsappMessageDto messageDto, Exception e) {
        if (messageDto.hasExceededMaxRetries(MAX_RETRY_COUNT)) {
            log.error("Max retries exceeded for WhatsApp message to: {}. Sending to DLQ",
                      messageDto.getPhoneNumber(), e);
            sendToDlq(messageDto, "Max retries exceeded: " + e.getMessage());
            return;
        }

        messageDto.incrementRetry();

        log.warn("Retryable error for WhatsApp message to: {}. Scheduling retry #{}",
                 messageDto.getPhoneNumber(), messageDto.getRetryCount(), e);

        // Enviar a la cola de retry (con TTL)
        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_EXCHANGE,
            RabbitQueues.WHATSAPP_RETRY_ROUTING_KEY,
            messageDto
        );
    }

    private void handleNonRetryableError(WhatsappMessageDto messageDto, Exception e) {
        log.error("Non-retryable error for WhatsApp message to: {}. Sending to DLQ",
                  messageDto.getPhoneNumber(), e);
        sendToDlq(messageDto, "Non-retryable error: " + e.getMessage());
    }

    private void sendToDlq(WhatsappMessageDto messageDto, String reason) {
        // Agregar metadatos para debugging en DLQ
        Map<String, Object> dlqMessage = Map.of(
            "originalMessage", messageDto,
            "failureReason", reason,
            "failureTime", Instant.now().toString(),
            "correlationId", messageDto.getCorrelationId()
        );

        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_DLX,
            RabbitQueues.WHATSAPP_DLQ_ROUTING_KEY,
            dlqMessage
        );

        log.warn("WhatsApp message sent to DLQ. Phone: {}, Reason: {}",
                 messageDto.getPhoneNumber(), reason);
    }
}
