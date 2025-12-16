package com.viddefe.viddefe_api.people.infrastructure.dto;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;

import java.util.Date;
import java.util.UUID;

public record PeopleResDto(
        UUID id,
        String cc,
        String firstName,
        String lastName,
        String phone,
        String avatar,
        Date birthDate,
        PeopleTypeModel peopleType,
        StatesDto state
) {
}
