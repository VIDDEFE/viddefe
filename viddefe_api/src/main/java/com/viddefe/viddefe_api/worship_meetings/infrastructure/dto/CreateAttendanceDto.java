package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateAttendanceDto {
    @NotNull(message = "No se especifico la persona")
    private UUID peopleId;
    @NotNull(message = "No se especifico el evento")
    private UUID eventId;
}
