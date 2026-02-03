package com.viddefe.viddefe_api.config.redis.application;

import com.viddefe.viddefe_api.config.redis.CacheKeys;
import com.viddefe.viddefe_api.config.redis.contracts.CacheService;
import com.viddefe.viddefe_api.config.redis.contracts.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Implementación de SessionService usando Redis.
 *
 * Gestiona las sesiones de usuario con las siguientes características:
 * - Sesiones con TTL configurable (por defecto 8 horas)
 * - Soporte para múltiples sesiones por usuario
 * - Invalidación individual y masiva de sesiones
 * - Refresh de sesiones para mantenerlas activas
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSessionServiceImpl implements SessionService {

    private final CacheService cacheService;

    @Value("${viddefe.session.ttl-hours:8}")
    private int sessionTtlHours;

    @Override
    public String createSession(UUID userId, String token, SessionMetadata metadata) {
        String sessionId = UUID.randomUUID().toString();

        long now = System.currentTimeMillis();
        Duration ttl = Duration.ofHours(sessionTtlHours);
        long expiresAt = now + ttl.toMillis();

        UserSession session = new UserSession(
                sessionId,
                userId,
                token,
                metadata,
                now,
                expiresAt
        );

        // Almacenar la sesión
        String sessionKey = CacheKeys.session(sessionId);
        cacheService.put(sessionKey, session, ttl);

        // Registrar el session ID en el índice del usuario
        String userSessionsKey = CacheKeys.userSessions(userId);
        Set<String> existingSessions = cacheService.get(userSessionsKey, Set.class)
                .orElse(new java.util.HashSet<>());
        existingSessions.add(sessionId);
        cacheService.put(userSessionsKey, existingSessions, ttl);

        log.info("Session created: sessionId={}, userId={}", sessionId, userId);
        return sessionId;
    }

    @Override
    public boolean isSessionValid(String sessionId) {
        return cacheService.exists(CacheKeys.session(sessionId));
    }

    @Override
    public Optional<UserSession> getSession(String sessionId) {
        return cacheService.get(CacheKeys.session(sessionId), UserSession.class);
    }

    @Override
    public void invalidateSession(String sessionId) {
        Optional<UserSession> session = getSession(sessionId);

        // Eliminar la sesión
        cacheService.delete(CacheKeys.session(sessionId));

        // Actualizar el índice del usuario
        session.ifPresent(s -> removeSessionFromUserIndex(s.userId(), sessionId));

        log.info("Session invalidated: sessionId={}", sessionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public int invalidateAllUserSessions(UUID userId) {
        String userSessionsKey = CacheKeys.userSessions(userId);

        Optional<Set> rawOpt = cacheService.get(userSessionsKey, Set.class);
        if (rawOpt.isEmpty()) {
            return 0;
        }

        Set<String> sessionIds = (Set<String>) rawOpt.get();
        int count = 0;

        for (String sessionId : sessionIds) {
            if (cacheService.delete(CacheKeys.session(sessionId))) {
                count++;
            }
        }

        // Eliminar el índice
        cacheService.delete(userSessionsKey);

        log.info("All sessions invalidated for user: userId={}, count={}", userId, count);
        return count;
    }

    @Override
    public boolean refreshSession(String sessionId) {
        Optional<UserSession> session = getSession(sessionId);
        if (session.isEmpty()) {
            return false;
        }

        // Actualizar TTL de la sesión
        Duration ttl = Duration.ofHours(sessionTtlHours);
        boolean refreshed = cacheService.expire(CacheKeys.session(sessionId), ttl);

        if (refreshed) {
            log.debug("Session refreshed: sessionId={}", sessionId);
        }

        return refreshed;
    }

    @SuppressWarnings("unchecked")
    private void removeSessionFromUserIndex(UUID userId, String sessionId) {
        String userSessionsKey = CacheKeys.userSessions(userId);

        Optional<Set> rawOpt = cacheService.get(userSessionsKey, Set.class);
        if (rawOpt.isPresent()) {
            Set<String> sessions = (Set<String>) rawOpt.get();
            sessions.remove(sessionId);

            if (sessions.isEmpty()) {
                cacheService.delete(userSessionsKey);
            } else {
                cacheService.put(userSessionsKey, sessions, Duration.ofHours(sessionTtlHours));
            }
        }
    }
}

