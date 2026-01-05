package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AttendanceDto {
    private PeopleResDto people;
    private String status;
}
