package com.viddefe.viddefe_api.notifications.Infrastructure.whatsapp;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.application.WhatsappClient;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WhatsappNotifierService implements Notificator {
    private final WhatsappClient whatsappClient;

    @Override
    public Channels channel() {
        return Channels.WHATSAPP;
    }

    @Async
    @Override
    @CircuitBreaker(label = "whatsapp")
    @Retryable(label = "whatsapp")
    public void send(@Valid NotificationDto notificationDto) {
        String message = ResolverMessage.resolveMessage(notificationDto.getTemplate(), notificationDto.getVariables());
        whatsappClient.sendTextMessage(
                notificationDto.getTo(),
                message
        );
    }

}
