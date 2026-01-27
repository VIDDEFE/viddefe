package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingReaderImpl implements MeetingReader {
    private final MeetingRepository meetingRepository;


    @Override
    public Meeting getById(UUID id) {
        return meetingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Meeting not found with id: " + id)
        );
    }

    @Override
    public MetricsAttendanceDto getMetricsAttendance(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime) {
        return meetingRepository.getMetricsAttendanceById(contextId, eventType, startTime, endTime);
    }

}
