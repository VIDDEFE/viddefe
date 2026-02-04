package com.viddefe.viddefe_api.worship_meetings.infrastructure.redis;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Redis adapter for storing and retrieving metrics using the cache-aside pattern.
 * This is a localized, technical cache specific to metrics computation.
 * NOT a generic cache abstraction.
 */
@Component
@RequiredArgsConstructor
public class MetricsRedisAdapter {

    private final RedisTemplate<String, Object> redisTemplate;

    private String resolveKey(TopologyEventType eventType, UUID contextId) {
        return String.format(
                "viddefe:metrics:%s:%s",
                eventType.name(),
                contextId
        );
    }

    public void saveMetrics(TopologyEventType eventType, UUID contextId, MetricsAttendanceDto metrics, Duration ttl) {
        String key = resolveKey(eventType, contextId);
        redisTemplate.opsForValue().set(key, metrics, ttl);
    }

    public Optional<MetricsAttendanceDto> getMetrics(
            TopologyEventType eventType,
            UUID contextId
    ) {
        String key = resolveKey(eventType, contextId);
        Object val = redisTemplate.opsForValue().get(key);

        if (val instanceof MetricsAttendanceDto metrics) {
            return Optional.of(metrics);
        }

        return Optional.empty();
    }

    public void deleteMetrics(TopologyEventType eventType, UUID contextId) {
        String key = resolveKey(eventType, contextId);
        redisTemplate.delete(key);
    }

    public boolean exists(TopologyEventType eventType, UUID contextId) {
        String key = resolveKey(eventType, contextId);
        return redisTemplate.hasKey(key);
    }
}

