package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface MeetingReader {
    Meeting getById(UUID id);
    MetricsAttendanceDto getMetricsAttendance(UUID contextId, TopologyEventType eventType,
                                              OffsetDateTime startTime, OffsetDateTime endTime);
    Page<Meeting> getMeetingsByScheduledDateBetween(UUID contextId, TopologyEventType eventType,
                                                    OffsetDateTime startTime, OffsetDateTime endTime, Pageable pageable);
}
