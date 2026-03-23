package com.viddefe.viddefe_api.notifications.application;

import java.time.Instant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.FailureWhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.common.exceptions.NonRetryableWhatsappException;
import com.viddefe.viddefe_api.notifications.common.exceptions.RetryableWhatsappException;
import com.viddefe.viddefe_api.notifications.config.SseFailureType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
            Integer retryCount = messageDto.getRetryCount() != null ? messageDto.getRetryCount() : 0;
            log.info("Processing WhatsApp message for: {} (attempt: {})",
                     messageDto.getPhoneNumber(), retryCount + 1);

            String message = ResolverMessage.resolveMessage(
                messageDto.getTemplate(),
                messageDto.getVariables()
            );

            whatsappClient.sendTextMessage(messageDto.getPhoneNumber(), message);

            log.info("WhatsApp message processed successfully for: {}", messageDto.getPhoneNumber());

        } catch (RetryableWhatsappException e) {
            SseFailureType failureReason = resolveFailureReason(messageDto.getNotificationType(), e);
            handleRetryableError(messageDto, e, failureReason);
        } catch (NonRetryableWhatsappException e) {
            SseFailureType failureReason = resolveFailureReason(messageDto.getNotificationType(), e);
            handleNonRetryableError(messageDto, failureReason);
        } catch (Exception e) {
            // Errores inesperados -> retry para ser conservadores
            log.warn("Unexpected error processing WhatsApp message, treating as retryable", e);
            SseFailureType failureReason = resolveFailureReason(messageDto.getNotificationType(), e);
            handleRetryableError(messageDto, e, failureReason);
            sendToDlq(messageDto, SseFailureType.WHATSAPP_SERVICE_HEALTH);
        }
    }

    private void handleRetryableError(WhatsappMessageDto messageDto, Exception e, SseFailureType reason) {
        if (messageDto.hasExceededMaxRetries(MAX_RETRY_COUNT)) {
            sendToDlq(messageDto, reason);
            return;
        }

        messageDto.incrementRetry();
        Integer retryCount = messageDto.getRetryCount();

        log.warn("Retryable error for WhatsApp message to: {}. Scheduling retry #{}",
                 messageDto.getPhoneNumber(), retryCount, e);

        // Enviar a la cola de retry (con TTL)
        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_EXCHANGE,
            RabbitQueues.WHATSAPP_RETRY_ROUTING_KEY,
            messageDto
        );
    }

    private SseFailureType resolveFailureReason(NotificationTypeEnum notificationType, Exception e) {
        if (e instanceof NonRetryableWhatsappException && e.getMessage().contains("Invalid phone number")) {
            return SseFailureType.INVALID_PHONE_NUMBER;
        }

        return switch (notificationType) {
            case ACCOUNT_CREATED -> SseFailureType.ACCOUNT_CREATION_FAILURE;
            case MINISTRY_FUNCTION_REMINDER -> SseFailureType.MINISTRY_REMINDER_FAILURE;
            default -> SseFailureType.WHATSAPP_SERVICE_HEALTH;
        };
    }

    private void handleNonRetryableError(WhatsappMessageDto messageDto, SseFailureType reason) {
        sendToDlq(messageDto, reason);
    }

    private void sendToDlq(WhatsappMessageDto messageDto, SseFailureType reason) {
        // Agregar metadatos para debugging en DLQ
        FailureWhatsappMessageDto failureMessageDto = FailureWhatsappMessageDto.builder()
                .toId(messageDto.getToId())
                .remitter(messageDto.getRemitter())
                .createdAt(Instant.now())
                .correlationId(messageDto.getCorrelationId())
                .lastRetryAt(messageDto.getLastRetryAt())
                .originalEventId(messageDto.getOriginalEventId())
                .notificationType(messageDto.getNotificationType())
                .variables(messageDto.getVariables())
                .sseFailureType(reason)
                .build();

        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_DLX,
            RabbitQueues.WHATSAPP_DLQ_ROUTING_KEY,
                failureMessageDto
        );

        log.warn("WhatsApp message sent to DLQ. Phone: {}, Reason: {}",
                 messageDto.getPhoneNumber(), reason);
    }
}
