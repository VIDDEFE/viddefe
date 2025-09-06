package com.viddefe.viddefe_api.homeGroups;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class HomeGroupsDTO {
    private BigDecimal latitude, longitude;
    private String name, strategyName, description;
    private UUID leaderId;
}
