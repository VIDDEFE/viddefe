package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WorshipDto extends MeetingDto {
    protected MeetingTypeDto worshipType;
}
