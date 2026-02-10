package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.WhatsappMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio para envío de mensajes WhatsApp a través del sistema resiliente.
 * Este servicio actúa como una fachada para simplificar el envío de mensajes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsappMessagingService {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Envía un mensaje WhatsApp a través del sistema resiliente.
     *
     * @param phoneNumber Número de teléfono destino
     * @param template Template del mensaje
     * @param variables Variables para el template
     */
    public void sendMessage(String phoneNumber, String template, Map<String, Object> variables) {
        log.info("Queuing WhatsApp message for: {}", phoneNumber);

        WhatsappMessageDto messageDto = new WhatsappMessageDto(phoneNumber, template, variables);

        rabbitTemplate.convertAndSend(
            RabbitQueues.WHATSAPP_EXCHANGE,
            RabbitQueues.WHATSAPP_ROUTING_KEY,
            messageDto
        );

        log.info("WhatsApp message queued successfully. CorrelationId: {}", messageDto.getCorrelationId());
    }

    /**
     * Envía un mensaje WhatsApp simple con texto plano.
     *
     * @param phoneNumber Número de teléfono destino
     * @param message Mensaje de texto
     */
    public void sendSimpleMessage(String phoneNumber, String message) {
        Map<String, Object> variables = Map.of("message", message);
        sendMessage(phoneNumber, "simple_text", variables);
    }
}
