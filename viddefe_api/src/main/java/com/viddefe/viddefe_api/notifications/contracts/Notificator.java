package com.viddefe.viddefe_api.notifications.contracts;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.config.Channels;
import jakarta.validation.Valid;

import java.nio.file.Path;
import java.util.Map;

/**
 * Messenger contract to send messages via different channels
 */
public interface Notificator {

    Channels channel();

    /**
     * Send a notification
     * @param notificationDto The notification details
     */
        void send(@Valid NotificationDto notificationDto);

        void sendWithAttachment(
               NotificationDto notificationDto,
                Path attachment
        );
}