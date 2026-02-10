package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitQueues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Listener para la Dead Letter Queue (DLQ) de WhatsApp.
 * Registra mensajes que no pudieron ser procesados para análisis posterior.
 */
@Slf4j
@Component
public class WhatsappDlqListener {

    @RabbitListener(queues = RabbitQueues.WHATSAPP_DLQ)
    public void handleDlqMessage(Map<String, Object> dlqMessage) {

        Object originalMessage = dlqMessage.get("originalMessage");
        String failureReason = (String) dlqMessage.get("failureReason");
        String failureTime = (String) dlqMessage.get("failureTime");
        String correlationId = (String) dlqMessage.get("correlationId");

        log.error("WhatsApp message sent to DLQ - CorrelationId: {}, Reason: {}, Time: {}, Message: {}",
                  correlationId, failureReason, failureTime, originalMessage);

        // Aquí se podría integrar con un sistema de alertas
        // Por ejemplo, enviar notificación a Slack, email, etc.

        // También se podría guardar en base de datos para análisis posterior
        logFailedMessageForAnalysis(correlationId, failureReason, originalMessage);
    }

    private void logFailedMessageForAnalysis(String correlationId, String failureReason, Object originalMessage) {
        // TODO: Implementar persistencia de mensajes fallidos para análisis
        // Esto podría incluir:
        // - Guardar en tabla de mensajes fallidos
        // - Generar métricas para monitoreo
        // - Alertar al equipo de operaciones

        log.warn("Failed WhatsApp message logged for analysis: correlationId={}, reason={}",
                 correlationId, failureReason);
    }
}
