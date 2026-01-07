package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
public class GroupMeetingDto {
    protected UUID id;
    protected String name,description;
    protected OffsetDateTime date;
    protected MeetingTypeDto type;
}