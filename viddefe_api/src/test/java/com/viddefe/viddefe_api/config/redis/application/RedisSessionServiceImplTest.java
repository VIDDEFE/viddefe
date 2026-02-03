package com.viddefe.viddefe_api.config.redis.application;

import com.viddefe.viddefe_api.config.redis.CacheKeys;
import com.viddefe.viddefe_api.config.redis.contracts.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisSessionServiceImpl Tests")
class RedisSessionServiceImplTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private RedisSessionServiceImpl sessionService;

    private UUID userId;
    private String token;
    private RedisSessionServiceImpl.SessionMetadata metadata;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        token = "jwt-token-12345";
        metadata = new RedisSessionServiceImpl.SessionMetadata(
                "192.168.1.1",
                "Chrome",
                "Windows"
        );
        ReflectionTestUtils.setField(sessionService, "sessionTtlHours", 8);
    }

    @Nested
    @DisplayName("createSession Tests")
    class CreateSessionTests {

        @Test
        @DisplayName("Should create session and return session ID")
        void shouldCreateSessionAndReturnSessionId() {
            when(cacheService.get(anyString(), eq(Set.class))).thenReturn(Optional.empty());
            doNothing().when(cacheService).put(anyString(), any(), any(Duration.class));

            String sessionId = sessionService.createSession(userId, token, metadata);

            assertThat(sessionId).isNotNull();
            verify(cacheService, times(2)).put(anyString(), any(), any(Duration.class));
        }

        @Test
        @DisplayName("Should add session to existing user sessions")
        void shouldAddSessionToExistingUserSessions() {
            Set<String> existingSessions = new HashSet<>();
            existingSessions.add("existing-session-id");
            when(cacheService.get(anyString(), eq(Set.class))).thenReturn(Optional.of(existingSessions));
            doNothing().when(cacheService).put(anyString(), any(), any(Duration.class));

            String sessionId = sessionService.createSession(userId, token, metadata);

            assertThat(sessionId).isNotNull();
            assertThat(existingSessions).contains(sessionId);
        }
    }

    @Nested
    @DisplayName("isSessionValid Tests")
    class IsSessionValidTests {

        @Test
        @DisplayName("Should return true when session exists")
        void shouldReturnTrueWhenSessionExists() {
            String sessionId = "session-123";
            when(cacheService.exists(CacheKeys.session(sessionId))).thenReturn(true);

            boolean result = sessionService.isSessionValid(sessionId);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when session does not exist")
        void shouldReturnFalseWhenSessionDoesNotExist() {
            String sessionId = "session-123";
            when(cacheService.exists(CacheKeys.session(sessionId))).thenReturn(false);

            boolean result = sessionService.isSessionValid(sessionId);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getSession Tests")
    class GetSessionTests {

        @Test
        @DisplayName("Should return session when exists")
        void shouldReturnSessionWhenExists() {
            String sessionId = "session-123";
            RedisSessionServiceImpl.UserSession userSession = new RedisSessionServiceImpl.UserSession(
                    sessionId, userId, token, metadata, System.currentTimeMillis(), System.currentTimeMillis() + 3600000
            );
            when(cacheService.get(CacheKeys.session(sessionId), RedisSessionServiceImpl.UserSession.class))
                    .thenReturn(Optional.of(userSession));

            Optional<RedisSessionServiceImpl.UserSession> result = sessionService.getSession(sessionId);

            assertThat(result).isPresent();
            assertThat(result.get().sessionId()).isEqualTo(sessionId);
        }

        @Test
        @DisplayName("Should return empty when session not found")
        void shouldReturnEmptyWhenSessionNotFound() {
            String sessionId = "session-123";
            when(cacheService.get(CacheKeys.session(sessionId), RedisSessionServiceImpl.UserSession.class))
                    .thenReturn(Optional.empty());

            Optional<RedisSessionServiceImpl.UserSession> result = sessionService.getSession(sessionId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("invalidateSession Tests")
    class InvalidateSessionTests {

        @Test
        @DisplayName("Should delete session from cache")
        void shouldDeleteSessionFromCache() {
            String sessionId = "session-123";
            when(cacheService.get(CacheKeys.session(sessionId), RedisSessionServiceImpl.UserSession.class))
                    .thenReturn(Optional.empty());
            when(cacheService.delete(CacheKeys.session(sessionId))).thenReturn(true);

            sessionService.invalidateSession(sessionId);

            verify(cacheService).delete(CacheKeys.session(sessionId));
        }

        @Test
        @DisplayName("Should remove session from user index if exists")
        void shouldRemoveSessionFromUserIndexIfExists() {
            String sessionId = "session-123";
            RedisSessionServiceImpl.UserSession userSession = new RedisSessionServiceImpl.UserSession(
                    sessionId, userId, token, metadata, System.currentTimeMillis(), System.currentTimeMillis() + 3600000
            );
            Set<String> userSessions = new HashSet<>();
            userSessions.add(sessionId);

            when(cacheService.get(CacheKeys.session(sessionId), RedisSessionServiceImpl.UserSession.class))
                    .thenReturn(Optional.of(userSession));
            when(cacheService.delete(CacheKeys.session(sessionId))).thenReturn(true);
            when(cacheService.get(CacheKeys.userSessions(userId), Set.class))
                    .thenReturn(Optional.of(userSessions));

            sessionService.invalidateSession(sessionId);

            verify(cacheService).delete(CacheKeys.session(sessionId));
        }
    }

    @Nested
    @DisplayName("invalidateAllUserSessions Tests")
    class InvalidateAllUserSessionsTests {

        @Test
        @DisplayName("Should return 0 when no sessions exist")
        void shouldReturnZeroWhenNoSessionsExist() {
            when(cacheService.get(CacheKeys.userSessions(userId), Set.class))
                    .thenReturn(Optional.empty());

            int result = sessionService.invalidateAllUserSessions(userId);

            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Should invalidate all user sessions")
        void shouldInvalidateAllUserSessions() {
            Set<String> sessionIds = new HashSet<>();
            sessionIds.add("session-1");
            sessionIds.add("session-2");

            when(cacheService.get(CacheKeys.userSessions(userId), Set.class))
                    .thenReturn(Optional.of(sessionIds));
            when(cacheService.delete(anyString())).thenReturn(true);

            int result = sessionService.invalidateAllUserSessions(userId);

            assertThat(result).isEqualTo(2);
            verify(cacheService, times(3)).delete(anyString()); // 2 sessions + 1 user sessions key
        }
    }
}

