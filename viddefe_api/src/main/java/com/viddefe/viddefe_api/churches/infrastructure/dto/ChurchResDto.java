package com.viddefe.viddefe_api.churches.infrastructure.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.statescities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.statescities.infrastructure.dto.StatesDto;

import lombok.Getter;
import lombok.Setter;

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
