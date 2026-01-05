package com.viddefe.viddefe_api.notifications.Infrastructure.factory;

import com.viddefe.viddefe_api.notifications.config.Channels;
import com.viddefe.viddefe_api.notifications.contracts.Notificator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificatorFactory {

    private final Map<Channels, Notificator> notificators;

    public NotificatorFactory(List<Notificator> notificators) {
        this.notificators = notificators.stream()
                .collect(Collectors.toMap(
                        Notificator::channel,
                        Function.identity()
                ));
    }

    public Notificator get(Channels channel) {
        return Optional.ofNullable(notificators.get(channel))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Channel not supported: " + channel
                ));
    }
}