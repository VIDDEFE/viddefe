package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class RolesStrategiesDto {
    private UUID id;
    private String name;
    private Set<RolesStrategiesDto> children;
    private Set<PeopleResDto> people;
}
