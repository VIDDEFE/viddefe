package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class PersonCreatedQualityAttendanceEvent {
    private UUID personId;
    private UUID churchId;
}
