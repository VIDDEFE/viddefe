package com.viddefe.viddefe_api.config.redis.contracts;

import java.util.Optional;
import java.util.UUID;

/**
 * Contrato para gestión de sesiones de usuario en Redis.
 */
public interface SessionService {

    String createSession(UUID userId, String token, SessionMetadata metadata);

    boolean isSessionValid(String sessionId);

    Optional<UserSession> getSession(String sessionId);

    void invalidateSession(String sessionId);

    int invalidateAllUserSessions(UUID userId);

    boolean refreshSession(String sessionId);

    record SessionMetadata(
            String ipAddress,
            String userAgent,
            String deviceType
    ) {
        public static SessionMetadata empty() {
            return new SessionMetadata(null, null, null);
        }
    }

    record UserSession(
            String sessionId,
            UUID userId,
            String token,
            SessionMetadata metadata,
            long createdAt,
            long expiresAt
    ) {}
}

