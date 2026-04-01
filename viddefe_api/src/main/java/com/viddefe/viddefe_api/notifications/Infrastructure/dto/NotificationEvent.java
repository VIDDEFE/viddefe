package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import com.viddefe.viddefe_api.infrastructure.rabbit.config.RabbitPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
    @NotNull(message = "subject cannot be null")
    @NotBlank(message = "subject cannot be blank")
    protected String subject;
    @NotNull(message = "template cannot be null")
    @NotBlank(message = "template cannot be blank")
    protected String template;
    @NotNull(message = "variables cannot be null")
    protected Map<String, Object> variables;
    protected UUID remitter;

    protected List<UUID> peopleIdList;

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "meetingId=" + meetingId +
                ", personId=" + personId +
                ", channels=" + channels +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                ", subject='" + subject + '\'' +
                ", template='" + template + '\'' +
                ", variables=" + variables +
                ", remitter=" + remitter +
                ", notificationType=" + getNotificationType() +
                ", routingKey=" + getNotificationType().routingKey() +
                '}';
    }

    public abstract NotificationTypeEnum getNotificationType();
}
