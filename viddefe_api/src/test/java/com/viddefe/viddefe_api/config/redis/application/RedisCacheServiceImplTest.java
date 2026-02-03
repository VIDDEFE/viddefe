package com.viddefe.viddefe_api.config.redis.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisCacheServiceImpl Tests")
class RedisCacheServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisCacheServiceImpl cacheService;

    private String testKey;
    private String testValue;

    @BeforeEach
    void setUp() {
        testKey = "test:key:123";
        testValue = "testValue";
    }

    @Nested
    @DisplayName("put Tests")
    class PutTests {

        @Test
        @DisplayName("Should put value with default TTL")
        void shouldPutValueWithDefaultTTL() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            cacheService.put(testKey, testValue);

            verify(valueOperations).set(eq(testKey), eq(testValue), any(Duration.class));
        }

        @Test
        @DisplayName("Should put value with custom TTL")
        void shouldPutValueWithCustomTTL() {
            Duration customTTL = Duration.ofHours(1);
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            cacheService.put(testKey, testValue, customTTL);

            verify(valueOperations).set(testKey, testValue, customTTL);
        }

        @Test
        @DisplayName("Should throw exception when key is null")
        void shouldThrowWhenKeyIsNull() {
            assertThatThrownBy(() -> cacheService.put(null, testValue))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("key cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when value is null")
        void shouldThrowWhenValueIsNull() {
            assertThatThrownBy(() -> cacheService.put(testKey, null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value cannot be null");
        }
    }

    @Nested
    @DisplayName("get Tests")
    class GetTests {

        @Test
        @DisplayName("Should return value when exists")
        void shouldReturnValueWhenExists() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(testKey)).thenReturn(testValue);

            Optional<String> result = cacheService.get(testKey, String.class);

            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(testValue);
        }

        @Test
        @DisplayName("Should return empty when value not exists")
        void shouldReturnEmptyWhenNotExists() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(testKey)).thenReturn(null);

            Optional<String> result = cacheService.get(testKey, String.class);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw when key is null")
        void shouldThrowWhenKeyIsNull() {
            assertThatThrownBy(() -> cacheService.get(null, String.class))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("getOrCompute Tests")
    class GetOrComputeTests {

        @Test
        @DisplayName("Should return cached value when exists")
        void shouldReturnCachedValueWhenExists() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(testKey)).thenReturn(testValue);

            String result = cacheService.getOrCompute(testKey, String.class, () -> "computed");

            assertThat(result).isEqualTo(testValue);
            verify(valueOperations, never()).set(any(), any(), any(Duration.class));
        }

        @Test
        @DisplayName("Should compute and cache when not exists")
        void shouldComputeAndCacheWhenNotExists() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(testKey)).thenReturn(null);

            String result = cacheService.getOrCompute(testKey, String.class, () -> "computed");

            assertThat(result).isEqualTo("computed");
            verify(valueOperations).set(eq(testKey), eq("computed"), any(Duration.class));
        }

        @Test
        @DisplayName("Should not cache null computed value")
        void shouldNotCacheNullComputedValue() {
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(testKey)).thenReturn(null);

            String result = cacheService.getOrCompute(testKey, String.class, () -> null);

            assertThat(result).isNull();
            verify(valueOperations, never()).set(any(), any(), any(Duration.class));
        }
    }

    @Nested
    @DisplayName("exists Tests")
    class ExistsTests {

        @Test
        @DisplayName("Should return true when key exists")
        void shouldReturnTrueWhenKeyExists() {
            when(redisTemplate.hasKey(testKey)).thenReturn(true);

            boolean result = cacheService.exists(testKey);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when key not exists")
        void shouldReturnFalseWhenKeyNotExists() {
            when(redisTemplate.hasKey(testKey)).thenReturn(false);

            boolean result = cacheService.exists(testKey);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should return true when key deleted")
        void shouldReturnTrueWhenKeyDeleted() {
            when(redisTemplate.delete(testKey)).thenReturn(true);

            boolean result = cacheService.delete(testKey);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Should return false when key not found")
        void shouldReturnFalseWhenKeyNotFound() {
            when(redisTemplate.delete(testKey)).thenReturn(false);

            boolean result = cacheService.delete(testKey);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("deleteByPattern Tests")
    class DeleteByPatternTests {

        @Test
        @DisplayName("Should delete keys matching pattern")
        void shouldDeleteKeysMatchingPattern() {
            String pattern = "test:*";
            Set<String> keys = Set.of("test:1", "test:2", "test:3");
            when(redisTemplate.keys(pattern)).thenReturn(keys);
            when(redisTemplate.delete(keys)).thenReturn(3L);

            long result = cacheService.deleteByPattern(pattern);

            assertThat(result).isEqualTo(3L);
        }

        @Test
        @DisplayName("Should return 0 when no keys match")
        void shouldReturnZeroWhenNoKeysMatch() {
            String pattern = "nonexistent:*";
            when(redisTemplate.keys(pattern)).thenReturn(Set.of());

            long result = cacheService.deleteByPattern(pattern);

            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("getKeysByPattern Tests")
    class GetKeysByPatternTests {

        @Test
        @DisplayName("Should return keys matching pattern")
        void shouldReturnKeysMatchingPattern() {
            String pattern = "test:*";
            Set<String> expectedKeys = Set.of("test:1", "test:2");
            when(redisTemplate.keys(pattern)).thenReturn(expectedKeys);

            Set<String> result = cacheService.getKeysByPattern(pattern);

            assertThat(result).containsExactlyInAnyOrderElementsOf(expectedKeys);
        }
    }

    @Nested
    @DisplayName("expire Tests")
    class ExpireTests {

        @Test
        @DisplayName("Should set expiration on key")
        void shouldSetExpirationOnKey() {
            Duration ttl = Duration.ofMinutes(30);
            when(redisTemplate.expire(eq(testKey), anyLong(), eq(TimeUnit.MILLISECONDS))).thenReturn(true);

            boolean result = cacheService.expire(testKey, ttl);

            assertThat(result).isTrue();
            verify(redisTemplate).expire(testKey, ttl.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    @Nested
    @DisplayName("getTimeToLive Tests")
    class GetTimeToLiveTests {

        @Test
        @DisplayName("Should return TTL for key")
        void shouldReturnTTLForKey() {
            long ttlMillis = 60000L;
            when(redisTemplate.getExpire(testKey, TimeUnit.MILLISECONDS)).thenReturn(ttlMillis);

            Duration result = cacheService.getTimeToLive(testKey);

            assertThat(result).isEqualTo(Duration.ofMillis(ttlMillis));
        }

        @Test
        @DisplayName("Should return zero when key has no TTL")
        void shouldReturnZeroWhenNoTTL() {
            when(redisTemplate.getExpire(testKey, TimeUnit.MILLISECONDS)).thenReturn(-1L);

            Duration result = cacheService.getTimeToLive(testKey);

            assertThat(result).isEqualTo(Duration.ZERO);
        }
    }
}

