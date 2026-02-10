package com.viddefe.viddefe_api.notifications.common.exceptions;

/**
 * Excepción para errores de WhatsApp que deben ser reintentados.
 * Incluye errores transitorios como 5xx, timeouts, 429 rate limit.
 */
public class RetryableWhatsappException extends RuntimeException {

    public RetryableWhatsappException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryableWhatsappException(String message) {
        super(message);
    }
}
