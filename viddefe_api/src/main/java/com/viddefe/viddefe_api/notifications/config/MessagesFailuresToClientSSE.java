package com.viddefe.viddefe_api.notifications.config;

import lombok.Getter;

/**
 * Enum to define failure messages to be sent to the client via Server-Sent Events (SSE) in case of errors during account creation, reminders, or ministry assignments.
 * Each enum constant represents a specific failure scenario and contains a template message that can be formatted with relevant details when sending the notification to the client.
 */
@Getter
public enum MessagesFailuresToClientSSE {

    /**
     * values for the enum constants, each with a template message for different failure scenarios:
     * - 1. Name: Name of the person who is the subject of the account creation failure.
     */
    ACCOUNT_CREATION_FAILURE(
            "Ha ocurrido un error al enviar las credenciales de %s. Estaremos trabajando en ello," +
                    "en dado caso puedes eliminar la cuenta y volver a crearla para intentar nuevamente."
    ),
    /**
     * values for the enum constants, each with a template message for different failure scenarios:
     * - 1. Name: Name of the person who is the subject of the reminder failure.
     * - 2. Ministry Function Name: Name of the ministry function for which the reminder failed to be sent.
     */
    MINISTRY_REMINDER_FAILURE(
            "Ha ocurrido un error al enviar el recordatorio de %s para su servicio como %s. Por favor, intente nuevamente."
    ),

    /**
     * values for the enum constants, each with a template message for different failure scenarios:
     * - 1. Name: Name of the person who is the subject of the ministry assignment failure.
     * - 2. Ministry Function Name: Name of the ministry function for which the assignment failed.
     */
    WHATSAPP_SERVICE_HEALTH(
            "Hola %s, hemos detectado un problema con el servicio de WhatsApp que podría afectar la entrega de tus mensajes. " +
                    "Nuestro equipo ya está trabajando para resolverlo lo antes posible. Agradecemos tu paciencia y comprensión."
    );

    private final String template;

    MessagesFailuresToClientSSE(String template) {
        this.template = template;
    }

    public String format(Object... args) {
        return String.format(template, args);
    }
}
