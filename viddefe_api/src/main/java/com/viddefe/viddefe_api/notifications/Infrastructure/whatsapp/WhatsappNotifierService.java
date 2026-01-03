package com.viddefe.viddefe_api.notifications.Infrastructure.whatsapp;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.application.WhatsappClient;
import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class WhatsappNotifierService implements Notificator {
    private final WhatsappClient whatsappClient;

    private static final String TEMPLATE_INVITATION_CREDENTIALS = "" +
            "Hola {{name}}, bienvenido a VidDefe! Tus credenciales son:\n" +
            "Usuario: {{username}}\n" +
            "Contraseña: {{password}}\n" +
            "Por favor, cambia tu contraseña después de iniciar sesión.";

    @Override
    public Channels channel() {
        return Channels.WHATSAPP;
    }

    @Async
    @Override
    public void send(NotificationDto notificationDto) {
        String message = TEMPLATE_INVITATION_CREDENTIALS
                .replace("{{name}}", (CharSequence) notificationDto.getVariables().get("name"))
                .replace("{{username}}", (CharSequence) notificationDto.getVariables().get("username"))
                .replace("{{password}}", (CharSequence) notificationDto.getVariables().get("password"));
        whatsappClient.sendTextMessage(
                notificationDto.getTo(),
                message
        );
    }

    @Override
    public void sendWithAttachment(NotificationDto notificationDto, Path attachment) {

    }
}
