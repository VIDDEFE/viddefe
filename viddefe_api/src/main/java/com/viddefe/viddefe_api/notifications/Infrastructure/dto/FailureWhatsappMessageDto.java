package com.viddefe.viddefe_api.notifications.Infrastructure.dto;

import com.viddefe.viddefe_api.notifications.config.MessagesFailuresToClientSSE;
import com.viddefe.viddefe_api.notifications.config.SseFailureType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
public class FailureWhatsappMessageDto extends WhatsappMessageDto{
    private SseFailureType sseFailureType;
}
