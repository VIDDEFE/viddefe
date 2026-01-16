package com.viddefe.viddefe_api.notifications.Infrastructure.whatsapp;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.application.WhatsappClient;
import com.viddefe.viddefe_api.notifications.common.ResolverMessage;
import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;

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
    public void send(@Valid NotificationDto notificationDto) {
        String message = ResolverMessage.resolveMessage(notificationDto.getTemplate(), notificationDto.getVariables());
        whatsappClient.sendTextMessage(
                notificationDto.getTo(),
                message
        );
    }

}
