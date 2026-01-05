package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateRolesStrategiesDto {
    private UUID strategyId;
    private String name;
    private UUID roleId;
    private UUID parentRoleId;
}
