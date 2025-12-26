package com.viddefe.viddefe_api.worship.infrastructure.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter
public class CreateGroupDto {
    @NotNull(message = "Group meeting type is required")
    private UUID groupMeetingTypeId;

    @NotNull(message = "Group id is required")
    private UUID groupId;

    @NotNull(message = "Meeting date is required")
    @FutureOrPresent(message = "Meeting date cannot be in the past")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;
}
