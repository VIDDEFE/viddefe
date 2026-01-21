package com.viddefe.viddefe_api.notifications.contracts;

import com.viddefe.viddefe_api.notifications.Infrastructure.dto.NotificationEvent;

public interface NotificationEventPublisher {
    /**
     * Publishes a notification event.
     *
     * @param event the notification event to be published
     */
    void publish(NotificationEvent event);
}
