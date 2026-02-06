package com.viddefe.viddefe_api.notifications.common.exceptions;

/**
 * Excepción para errores de WhatsApp que NO deben ser reintentados.
 * Incluye errores como 4xx de autenticación, payload inválido, etc.
 */
public class NonRetryableWhatsappException extends RuntimeException {

    public NonRetryableWhatsappException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonRetryableWhatsappException(String message) {
        super(message);
    }
}
