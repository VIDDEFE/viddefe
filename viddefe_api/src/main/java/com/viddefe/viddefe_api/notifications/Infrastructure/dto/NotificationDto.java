package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.NotificationTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class NotificationDto{
    @NotBlank
    private String to;

    @NotBlank
    private String template;

    private String subject;

    @NotNull
    private Map<String, Object> variables;

    @NotNull
    private Channels channels;

    private UUID remitter;

    private UUID personId;
    private NotificationTypeEnum notificationType;
}
