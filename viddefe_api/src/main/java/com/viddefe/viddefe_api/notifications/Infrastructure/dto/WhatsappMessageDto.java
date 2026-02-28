package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO para mensajes de WhatsApp que incluye información de reintentos
 * y metadatos para el manejo resiliente de mensajes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WhatsappMessageDto {

    private String phoneNumber;
    private String template;
    private Map<String, Object> variables;

    @JsonProperty("retry_count")
    private Integer retryCount = 0;

    @JsonProperty("correlation_id")
    private String correlationId;

    @JsonProperty("original_event_id")
    private String originalEventId;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("last_retry_at")
    private Instant lastRetryAt;

    private UUID remitter;
    private UUID toId;

    private NotificationTypeEnum notificationType;

    public void incrementRetry() {
        this.retryCount++;
        this.lastRetryAt = Instant.now();
    }

    public boolean hasExceededMaxRetries(int maxRetries) {
        return this.retryCount >= maxRetries;
    }
}
