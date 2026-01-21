package com.viddefe.viddefe_api.notifications.contracts;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationDto;
import com.viddefe.viddefe_api.notifications.common.Channels;
import jakarta.validation.Valid;

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

}