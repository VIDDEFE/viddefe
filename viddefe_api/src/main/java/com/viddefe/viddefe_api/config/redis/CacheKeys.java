package com.viddefe.viddefe_api.config.redis;

import java.util.UUID;

/**
 * Generador de claves para el sistema de caché.
 */
public final class CacheKeys {

    private static final String NAMESPACE = "viddefe";
    private static final String SEPARATOR = ":";

    private CacheKeys() {
        // Utility class
    }

    public static String person(UUID id) {
        return build("people", id);
    }

    public static String church(UUID id) {
        return build("churches", id);
    }

    public static String user(UUID id) {
        return build("users", id);
    }

    public static String session(String sessionId) {
        return build("sessions", sessionId);
    }

    public static String userSessions(UUID userId) {
        return build("sessions", "user", userId.toString());
    }

    public static String peoplePattern() {
        return pattern("people");
    }

    public static String churchesPattern() {
        return pattern("churches");
    }

    public static String usersPattern() {
        return pattern("users");
    }

    public static String sessionsPattern() {
        return pattern("sessions");
    }

    private static String build(String entity, UUID id) {
        return String.join(SEPARATOR, NAMESPACE, entity, id.toString());
    }

    private static String build(String entity, String... segments) {
        StringBuilder key = new StringBuilder(NAMESPACE)
                .append(SEPARATOR)
                .append(entity);
        for (String segment : segments) {
            key.append(SEPARATOR).append(segment);
        }
        return key.toString();
    }

    private static String pattern(String entity) {
        return String.join(SEPARATOR, NAMESPACE, entity, "*");
    }
}

