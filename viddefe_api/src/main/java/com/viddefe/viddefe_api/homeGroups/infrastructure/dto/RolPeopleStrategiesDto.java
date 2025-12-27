package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class RolPeopleStrategiesDto {
    private UUID id;
    private RolesStrategiesDto role;
    private PeopleResDto person;
}
