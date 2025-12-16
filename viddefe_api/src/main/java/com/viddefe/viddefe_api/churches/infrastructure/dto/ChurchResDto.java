package com.viddefe.viddefe_api.churches.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class ChurchResDto {

    private UUID id;
    private String name;

    @JsonProperty("longitude")
    private BigDecimal longitude;

    @JsonProperty("latitude")
    private BigDecimal latitude;

    private StatesDto states;
    private CitiesDto city;
    private PeopleResDto pastor;

    public ChurchResDto() {
        // Constructor vacío para Jackson
    }

    public ChurchResDto(UUID id, String name, BigDecimal longitude, BigDecimal latitude,
                        StatesDto state, CitiesDto city, PeopleResDto pastor) {
        this.id = id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.states = state;
        this.city = city;
        this.pastor = pastor;
    }
}
