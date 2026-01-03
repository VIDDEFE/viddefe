package com.viddefe.viddefe_api.notifications.config;

import com.fasterxml.jackson.annotation.JsonCreator;

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
    @JsonCreator
    public static Channels from(String value) {
        return Channels.valueOf(value.toUpperCase());
    }
}
