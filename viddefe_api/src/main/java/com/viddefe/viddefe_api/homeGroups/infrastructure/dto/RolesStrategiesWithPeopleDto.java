package com.viddefe.viddefe_api.homegroups.infrastructure.dto;

import com.viddefe.viddefe_api.homegroups.infrastructure.dto.base.AbstractRoleTreeDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class RolesStrategiesWithPeopleDto extends AbstractRoleTreeDto {

    private Set<PeopleResDto> people = new HashSet<>();

}
