package com.viddefe.viddefe_api.notifications.application;

import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viddefe.viddefe_api.notifications.common.Channels;
import com.viddefe.viddefe_api.notifications.common.exceptions.NonRetryableWhatsappException;
import com.viddefe.viddefe_api.notifications.common.exceptions.RetryableWhatsappException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WhatsappClient {

    private final RestClient restClient;

    @Value("${whatsapp.api.url}")
    private String graphBaseUrl;

    @Value("${whatsapp.api.phone.number.id}")
    private String phoneNumberId;
    private final CircuitBreaker circuitBreaker;

    public WhatsappClient(RestClient restClient, CircuitBreakerRegistry registry) {
        this.restClient = restClient;
        String channelName = Channels.WHATSAPP.name();
        this.circuitBreaker = registry.circuitBreaker(channelName);
    }
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
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            int errorCode = extractErrorCode(body); // parseas el JSON de Meta

            if (isInvalidPhoneNumber(errorCode)) {
                throw new NonRetryableWhatsappException("Invalid phone number: " + to, e);
            }
            throw new RetryableWhatsappException("Transient error", e);
        } catch (Exception e) {
            throw new RetryableWhatsappException("Unexpected error", e);
        }
    }
    private int extractErrorCode(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode errorCode = root.path("error").path("code");

            if (errorCode.isMissingNode()) {
                log.warn("No error code found in WhatsApp response body: {}", responseBody);
                return -1;
            }

            return errorCode.asInt();
        } catch (JsonProcessingException e) {
            log.warn("Could not parse WhatsApp error response body: {}", responseBody);
            return -1;
        }
    }

    private boolean isInvalidPhoneNumber(int errorCode) {
        // Códigos de error de Meta para número inválido
        return errorCode == 131026 // Recipient phone number not in allowed list
                || errorCode == 131047 // Non-existent number
                || errorCode == 100;   // Invalid parameter (número mal formado)
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
            log.info("WhatsApp message sended");
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
               statusCode.value() == 400 ||
               statusCode.value() == 408 ||  // Request timeout
               statusCode.value() == 503;    // Service unavailable
    }
}
