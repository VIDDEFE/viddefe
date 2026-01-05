package com.viddefe.viddefe_api.finances.infrastructure.dto;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class OfferingDto {
    private UUID eventId;
    private Double amount;
    private PeopleResDto people;
    private OfferingTypeDto type;
}
