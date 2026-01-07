package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class HomeGroupsDTO {
    private UUID id;
    private String name, description;
    private BigDecimal latitude, longitude;
    private PeopleResDto manager;
    private StrategyDto strategy;
}
