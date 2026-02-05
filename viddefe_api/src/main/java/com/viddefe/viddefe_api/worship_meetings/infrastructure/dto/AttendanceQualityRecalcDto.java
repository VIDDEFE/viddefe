package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class AttendanceQualityRecalcDto {
    private UUID peopleId;
    private UUID contextId;
    private TopologyEventType eventType;
    private OffsetDateTime from;
    private OffsetDateTime to;
}
