package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.RabbitPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class NotificationEvent {
    @NotNull(message = "personId cannot be null")
    private UUID personId;
    @NotNull(message = "channels cannot be null")
    private Channels channels;
    @NotNull(message = "priority cannot be null")
    private RabbitPriority priority;
    @NotNull(message = "createdAt cannot be null")
    private Instant createdAt;
    @NotNull(message = "to cannot be null")
    @NotBlank(message = "to cannot be blank")
    private String subject;
    @NotNull(message = "template cannot be null")
    @NotBlank(message = "template cannot be blank")
    private String template;
    @NotNull(message = "variables cannot be null")
    private Map<String, Object> variables;
}
