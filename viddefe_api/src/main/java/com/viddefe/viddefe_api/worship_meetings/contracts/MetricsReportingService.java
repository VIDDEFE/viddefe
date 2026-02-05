package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface MetricsReportingService {
    MetricsAttendanceDto getAttendanceMetrics(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime);
}
