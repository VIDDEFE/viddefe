package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter @Setter
public class WorshipDto {
    protected UUID id;
    protected String name;
    protected String description;
    protected Date creationDate;
    protected LocalDateTime scheduledDate;
    protected MeetingTypeDto worshipType;
}
