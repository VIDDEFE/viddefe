package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;


import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeopleAttendanceEventDto {
    private UUID meetingId;
    private UUID contextId;
    private TopologyEventType eventType;
}