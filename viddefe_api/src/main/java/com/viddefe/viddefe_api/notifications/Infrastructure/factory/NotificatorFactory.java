package com.viddefe.viddefe_api.notifications.Infrastructure.factory;

import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificatorFactory {

    @Qualifier("EMAIL")
    private final Notificator emailNotificator;

    public Notificator get(Channels channels) {
        switch (channels) {
            case EMAIL:
                return emailNotificator;
            default:
                throw new IllegalArgumentException("Channel not supported: " + channels);
        }
    }

}