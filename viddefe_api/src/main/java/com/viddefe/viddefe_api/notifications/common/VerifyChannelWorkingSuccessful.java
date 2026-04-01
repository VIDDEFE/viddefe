package com.viddefe.viddefe_api.notifications.common;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
/**
 * Class to verify if a notification channel is working successfully by checking the state of its Circuit Breaker.
 * Use the State of the circuitBraker.
 */
public class VerifyChannelWorkingSuccessful {
    private final CircuitBreakerRegistry registry;

    public boolean verify(Channels channel) {
        String channelName = switch (channel) {
            case WHATSAPP -> Channels.WHATSAPP.name();
            case EMAIL -> Channels.EMAIL.name();
            case APP -> null;
        };
        assert channelName != null;
        CircuitBreaker breaker = registry.circuitBreaker(channelName);
        return breaker.getState() != CircuitBreaker.State.CLOSED;
    }
}
