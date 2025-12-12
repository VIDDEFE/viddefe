package com.viddefe.viddefe_api.homeGroups.infrastructure.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class HomeGroupsDTO {
    private BigDecimal latitude, longitude;
    private String name, strategyName, description;
    private UUID leaderId;
}
