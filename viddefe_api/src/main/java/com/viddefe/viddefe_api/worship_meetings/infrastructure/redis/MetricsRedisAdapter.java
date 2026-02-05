package com.viddefe.viddefe_api.worship_meetings.infrastructure.redis;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.OffsetDateTime;
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

    private String resolveKey(TopologyEventType eventType, UUID contextId, OffsetDateTime startTime, OffsetDateTime endTime) {
        return String.format(
                "viddefe:metrics:%s:%s:%s:%s",
                eventType.name(),
                contextId,
                startTime.toString(),
                endTime.toString()
        );
    }

    @Async
    public void saveMetrics(TopologyEventType eventType, UUID contextId, MetricsAttendanceDto metrics, Duration ttl,
                            OffsetDateTime startTime, OffsetDateTime endTime) {
        String key = resolveKey(eventType, contextId, startTime, endTime);
        redisTemplate.opsForValue().set(key, metrics, ttl);
    }

    public Optional<MetricsAttendanceDto> getMetrics(
            TopologyEventType eventType,
            UUID contextId,
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ) {
        String key = resolveKey(eventType, contextId, startTime, endTime);
        Object val = redisTemplate.opsForValue().get(key);

        if (val instanceof MetricsAttendanceDto metrics) {
            return Optional.of(metrics);
        }

        return Optional.empty();
    }

    public void deleteMetrics(TopologyEventType eventType, UUID contextId,
    OffsetDateTime startTime, OffsetDateTime endTime) {
        String key = resolveKey(eventType, contextId, startTime, endTime);
        redisTemplate.delete(key);
    }

    public boolean exists(TopologyEventType eventType, UUID contextId, OffsetDateTime startTime, OffsetDateTime endTime) {
        String key = resolveKey(eventType, contextId, startTime, endTime);
        return redisTemplate.hasKey(key);
    }

    public boolean exitsInAnyRangeOfDate(TopologyEventType eventType, UUID contextId) {
        String key = String.format(
                "viddefe:metrics:%s:%s:*",
                eventType.name(),
                contextId
        );
        return !redisTemplate.keys(key).isEmpty();
    }
}

