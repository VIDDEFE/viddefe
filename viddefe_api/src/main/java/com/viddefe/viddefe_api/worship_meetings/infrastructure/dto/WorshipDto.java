package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
public class WorshipDto {
    protected UUID id;
    protected String name;
    protected String description;
    protected Instant creationDate;
    protected OffsetDateTime scheduledDate;
    protected MeetingTypeDto worshipType;
}
