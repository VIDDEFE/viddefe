package com.viddefe.viddefe_api.notifications.application;

import com.viddefe.viddefe_api.notifications.common.exceptions.NonRetryableWhatsappException;
import com.viddefe.viddefe_api.notifications.common.exceptions.RetryableWhatsappException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsappClient {

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    @Value("${whatsapp.api.url}")
    private String graphBaseUrl;

    @Value("${whatsapp.api.phone.number.id}")
    private String phoneNumberId;

    /**
     * Envía un mensaje de texto por WhatsApp con Circuit Breaker.
     * Distingue entre errores retryables y no retryables.
     */
    public void sendTextMessage(String to, String message) {
        Supplier<Void> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, () -> {
                    executeWhatsappCall(to, message);
                    return null;
                });

        try {
            decoratedSupplier.get();
            log.info("WhatsApp message sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to: {}. Error: {}", to, e.getMessage());
            throw e; // Re-throw para que sea manejado por el listener
        }
    }

    private void executeWhatsappCall(String to, String message) {
        String url = graphBaseUrl + "/" + phoneNumberId + "/messages";
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", message)
        );

        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

        } catch (HttpClientErrorException e) {
            if (isRetryableError(e.getStatusCode())) {
                throw new RetryableWhatsappException("Transient WhatsApp error: " + e.getStatusCode(), e);
            }
            throw new NonRetryableWhatsappException("Non-retryable WhatsApp error: " + e.getStatusCode(), e);

        } catch (HttpServerErrorException e) {
            throw new RetryableWhatsappException("WhatsApp server error: " + e.getStatusCode(), e);

        } catch (ResourceAccessException e) {
            // Timeouts, connection issues
            throw new RetryableWhatsappException("Network or timeout error", e);

        } catch (Exception e) {
            // Cualquier otro error inesperado -> retry para ser conservadores
            throw new RetryableWhatsappException("Unexpected error during WhatsApp call", e);
        }
    }

    private boolean isRetryableError(HttpStatusCode statusCode) {
        return statusCode.value() == 429 ||  // Rate limit
               statusCode.value() == 408 ||  // Request timeout
               statusCode.value() == 503;    // Service unavailable
    }
}
