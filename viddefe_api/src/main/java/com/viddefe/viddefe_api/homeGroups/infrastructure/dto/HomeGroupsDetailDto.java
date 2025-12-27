package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class HomeGroupsDetailDto {
    private HomeGroupsDTO homeGroup;
    private StrategyDto strategy;
    private List<RolesStrategiesDto> hierarchy;
}
