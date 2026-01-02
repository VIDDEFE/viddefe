package com.viddefe.viddefe_api.notifications.config;

/**
 * Enum representing the different notification channels..
 * @APP notifications are stored in the database and can be retrieved by the user within the application.
 * @EMAIL notifications are sent to the user's registered email address.
 */
public enum Channels {
    EMAIL,
    WHATSAPP,
    APP;

    public static String getName(Channels channel) {
        return channel.name();
    }
}
