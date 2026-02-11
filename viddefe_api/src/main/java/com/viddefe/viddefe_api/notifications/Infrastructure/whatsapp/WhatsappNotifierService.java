package com.viddefe.viddefe_api.notifications.Infrastructure.whatsapp;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitPriority;
import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationAccountEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
    @Override
    public void send(@Valid NotificationEvent notificationEvent) {
        //log.info("Queuing WhatsApp notification for: {}", notificationEvent.());

        /*NotificationAccountEvent event = new NotificationAccountEvent();
        event.setPriority(RabbitPriority.HIGH);
        event.setSubject("Bienvenido a VidDefe!");
        event.setChannels(channel);
        event.setPersonId(person.getId());
        event.setCreatedAt(Instant.now());
        event.setVariables(resolveVariables(event, person, userModel, temporaryPassword));
        String template = resolveTemplate(dtp.getChannel());
        event.setTemplate(template);
        notificationEventPublisher.publish(event);


        // Crear DTO con información de retry
        WhatsappMessageDto messageDto = WhatsappMessageDto.builder()
            .phoneNumber()
            .template(notificationDto.getTemplate())
            .variables(notificationDto.getVariables())
            //.notificationType(notificationDto.getNotificationType())
            .build();

        // Enviar a la cola principal de WhatsApp
        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_EXCHANGE,
            RabbitQueues.WHATSAPP_ROUTING_KEY,
            messageDto
        );

        log.info("WhatsApp notification queued successfully for: {}", notificationDto.getTo());
        */
    }
}
