package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.notifications.common.RabbitPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public abstract class NotificationEvent {
    protected UUID meetingId;
    @NotNull(message = "personId cannot be null")
    protected UUID personId;
    @NotNull(message = "channels cannot be null")
    protected Channels channels;
    @NotNull(message = "priority cannot be null")
    protected RabbitPriority priority;
    @NotNull(message = "createdAt cannot be null")
    protected Instant createdAt;
    @NotNull(message = "to cannot be null")
    @NotBlank(message = "to cannot be blank")
    protected String subject;
    @NotNull(message = "template cannot be null")
    @NotBlank(message = "template cannot be blank")
    protected String template;
    @NotNull(message = "variables cannot be null")
    protected Map<String, Object> variables;

    public abstract NotificationTypeEnum getNotificationType();
}
