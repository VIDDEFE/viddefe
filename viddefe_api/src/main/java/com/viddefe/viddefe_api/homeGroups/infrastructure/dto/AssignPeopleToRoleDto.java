package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class AssignPeopleToRoleDto {
    List<UUID> peopleIds;
}
