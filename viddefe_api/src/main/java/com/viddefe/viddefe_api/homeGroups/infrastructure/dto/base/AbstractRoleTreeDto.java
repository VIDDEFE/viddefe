package com.viddefe.viddefe_api.homeGroups.infrastructure.dto.base;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class AbstractRoleTreeDto {

    private UUID id;
    private String name;
    private List<AbstractRoleTreeDto> children = new ArrayList<>();

}
