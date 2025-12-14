package com.viddefe.viddefe_api.churches.infrastructure.dto;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;

import java.math.BigDecimal;
import java.util.UUID;

public record ChurchResDto(UUID id, String name, BigDecimal longitude,
                           BigDecimal latitude, StatesDto state, CitiesDto city) {
}
