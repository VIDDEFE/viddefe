package com.viddefe.viddefe_api.config.redis.application;

import com.viddefe.viddefe_api.config.redis.contracts.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implementación de CacheService usando Redis.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    @Override
    public <T> void put(String key, T value) {
        put(key, value, DEFAULT_TTL);
    }

    @Override
    public <T> void put(String key, T value, Duration ttl) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        Objects.requireNonNull(value, "Cache value cannot be null");
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
            log.debug("Cache PUT: key={}, ttl={}", key, ttl);
        } catch (Exception e) {
            log.error("Error putting value in cache: key={}", key, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("Cache MISS: key={}", key);
                return Optional.empty();
            }
            log.debug("Cache HIT: key={}", key);
            return Optional.of((T) value);
        } catch (Exception e) {
            log.error("Error getting value from cache: key={}", key, e);
            return Optional.empty();
        }
    }

    @Override
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier) {
        return getOrCompute(key, type, supplier, DEFAULT_TTL);
    }

    @Override
    public <T> T getOrCompute(String key, Class<T> type, Supplier<T> supplier, Duration ttl) {
        Objects.requireNonNull(supplier, "Supplier cannot be null");
        Optional<T> cached = get(key, type);
        if (cached.isPresent()) {
            return cached.get();
        }
        T value = supplier.get();
        if (value != null) {
            put(key, value, ttl);
        }
        return value;
    }

    @Override
    public boolean exists(String key) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking key existence: key={}", key, e);
            return false;
        }
    }

    @Override
    public boolean delete(String key) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Cache DELETE: key={}, deleted={}", key, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            log.error("Error deleting key from cache: key={}", key, e);
            return false;
        }
    }

    @Override
    public long deleteByPattern(String pattern) {
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys == null || keys.isEmpty()) {
                return 0L;
            }
            Long count = redisTemplate.delete(keys);
            log.debug("Cache DELETE by pattern: pattern={}, deleted={}", pattern, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Error deleting keys by pattern: pattern={}", pattern, e);
            return 0L;
        }
    }

    @Override
    public Set<String> getKeysByPattern(String pattern) {
        Objects.requireNonNull(pattern, "Pattern cannot be null");
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return keys != null ? keys : Set.of();
        } catch (Exception e) {
            log.error("Error getting keys by pattern: pattern={}", pattern, e);
            return Set.of();
        }
    }

    @Override
    public boolean expire(String key, Duration ttl) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        Objects.requireNonNull(ttl, "TTL cannot be null");
        try {
            Boolean result = redisTemplate.expire(key, ttl.toMillis(), TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Error setting expiration: key={}", key, e);
            return false;
        }
    }

    @Override
    public Duration getTimeToLive(String key) {
        Objects.requireNonNull(key, "Cache key cannot be null");
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
            if (ttl == null || ttl < 0) {
                return Duration.ZERO;
            }
            return Duration.ofMillis(ttl);
        } catch (Exception e) {
            log.error("Error getting TTL: key={}", key, e);
            return Duration.ZERO;
        }
    }
}

