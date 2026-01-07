package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreateMinistryFunctionDto {
    private UUID peopleId;
    private Long roleId;
}
