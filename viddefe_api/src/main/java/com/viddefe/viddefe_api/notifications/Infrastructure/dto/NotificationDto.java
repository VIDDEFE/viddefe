package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.common.Channels;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
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
}
