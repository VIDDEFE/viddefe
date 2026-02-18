package com.viddefe.viddefe_api.notifications.Infrastructure.whatsapp;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio de notificaciones WhatsApp que usa el sistema de colas resilientes.
 * Ya no hace llamadas directas sino que envía mensajes a RabbitMQ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsappNotifierService implements Notificator {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Channels channel() {
        return Channels.WHATSAPP;
    }

    @Async
    public void send(@Valid NotificationDto notificationDto) {
        log.info("Queuing WhatsApp notification for: {}", notificationDto.toString());

        // Crear DTO con información de retry
        WhatsappMessageDto messageDto = WhatsappMessageDto.builder()
            .phoneNumber(notificationDto.getTo())
            .template(notificationDto.getTemplate())
            .variables(notificationDto.getVariables())
            .notificationType(notificationDto.getNotificationType())
            .build();

        // Enviar a la cola principal de WhatsApp
        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_EXCHANGE,
            RabbitQueues.WHATSAPP_ROUTING_KEY,
            messageDto
        );

        log.info("WhatsApp notification queued successfully for: {}", notificationDto.getTo());
    }
}
