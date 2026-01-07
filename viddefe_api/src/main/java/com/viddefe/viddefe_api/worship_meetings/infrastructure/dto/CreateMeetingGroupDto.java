package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter
public class CreateMeetingGroupDto {
    @NotNull(message = "Group meeting type is required")
    private Long groupMeetingTypeId;

    private String name,description;

    @NotNull(message = "Meeting date is required")
    @FutureOrPresent(message = "Meeting date cannot be in the past")
    private OffsetDateTime date;
}
