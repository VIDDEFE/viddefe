package com.viddefe.viddefe_api.notifications.config;

public class MessagesFailuresToClientSSE {

    /**
     * Account creation failure message.
     * - {{name}}: Name of the person who is the remitter of the account creation failure.
     */
    public static final String ACCOUNT_CREATION_FAILURE =
            "Ha ocurrido un error al enviar las credenciales de {{name}}. Estaremos trabajando en ello, " +
                    "en dado caso puedes eliminar la cuenta y volver a crearla para intentar nuevamente.";

    /**
     * Ministry reminder failure message.
     * - {{name}}: Name of the person who is the subject of the reminder failure.
     * - {{ministryFunction}}: Name of the ministry function for which the reminder failed.
     */
    public static final String MINISTRY_REMINDER_FAILURE =
            "Ha ocurrido un error al enviar el recordatorio de {{name}} para su servicio como {{ministryFunction}}. Por favor, intente nuevamente.";

    /**
     * WhatsApp service health issue message.
     * - {{remmiterName}}: Name of the person being notified.
     */
    public static final String WHATSAPP_SERVICE_HEALTH =
            "Hola {{remmiterName}}, hemos detectado un problema con el servicio de WhatsApp que podría afectar la entrega de tus mensajes. " +
                    "Nuestro equipo ya está trabajando para resolverlo lo antes posible. Agradecemos tu paciencia y comprensión.";

    /**
     * Invalid phone number message.
     * - {{remmiterName}}: Name of the admin/user being notified.
     * - {{personName}}: Name of the person whose phone number is invalid.
     */
    public static final String INVALID_PHONE_NUMBER =
            "Hola {{remmiterName}}, el número de teléfono proporcionado para la persona {{personName}} es inválido. " +
                    "Por favor, verifica el número y actualízalo para asegurarte de recibir las notificaciones correctamente.";

    /**
     * Unknown error message.
     * - {{remmiterName}}: Name of the person being notified.
     */
    public static final String UNKNOWN_ERROR =
            "Hola {{remmiterName}}, hemos detectado un problema inesperado que podría afectar la entrega de tus mensajes. " +
                    "Nuestro equipo ya está investigando el problema para resolverlo lo antes posible. Agradecemos tu paciencia y comprensión.";

    private MessagesFailuresToClientSSE() {}
}