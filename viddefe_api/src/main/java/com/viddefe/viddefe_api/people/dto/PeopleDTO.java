package com.viddefe.viddefe_api.people.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PeopleDTO {

    private UUID id;
    private String cc,firstName,lastName, email, phone, avatar;
    private LocalDate birthdate;

    private Long typePersonId, stateId;
    private UUID churchId;

}
