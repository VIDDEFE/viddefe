package com.viddefe.viddefe_api.churches.infrastructure.dto;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class ChurchDetailedResDto {
    private UUID id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private CitiesDto city;
    private StatesDto states;
    private PeopleResDto pastor;
    private Date foundationDate;
    private Long phone;
    private String email;
    private Long totalMembers, activeGroups,servicesMonthly, nextServices;
}
