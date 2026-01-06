package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
public class GroupMeetingDto {
    protected UUID id;
    protected String name,description;
    protected LocalDateTime date;
    protected GroupMeetingTypeDto type;
}