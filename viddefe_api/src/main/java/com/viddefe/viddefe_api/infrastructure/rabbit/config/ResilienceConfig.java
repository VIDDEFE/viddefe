package com.viddefe.viddefe_api.infrastructure.rabbit.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuración de Resilience4j para WhatsApp API.
 * Circuit Breaker para proteger contra cascadas de fallos.
 */
@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreaker whatsappCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50.0f)
                .slowCallRateThreshold(50.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        return CircuitBreaker.of("whatsapp-api", config);
    }
}
