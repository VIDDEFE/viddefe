package com.viddefe.viddefe_api.churches.infrastructure.dto;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public record ChurchDetailedResDto(
        UUID id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        CitiesDto city,
        StatesDto states,
        PeopleDTO pastor,
        Date foundationDate,
        Long phone,
        String email
) {
}
