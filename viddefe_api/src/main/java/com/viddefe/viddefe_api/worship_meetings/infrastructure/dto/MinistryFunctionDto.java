package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class MinistryFunctionDto {
    private UUID id;
    private PeopleResDto people;
    private MinistryFunctionTypeDto role;
}
