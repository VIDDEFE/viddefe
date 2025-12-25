package com.viddefe.viddefe_api.people.infrastructure.dto;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;

import java.util.Date;
import java.util.UUID;

/**
 * People Response Data Transfer Object
 * @param id {@link UUID}
 * @param cc {@link String}
 * @param firstName {@link String}
 * @param lastName {@link String}
 * @param phone {@link String}
 * @param avatar {@link String}
 * @param birthDate {@link Date}
 * @param typePerson {@link PeopleTypeModel}
 * @param state {@link StatesDto}
 */
public record PeopleResDto(
        UUID id,
        String cc,
        String firstName,
        String lastName,
        String phone,
        String avatar,
        Date birthDate,
        PeopleTypeModel typePerson,
        StatesDto state
) {
}
