package com.viddefe.viddefe_api.people.infrastructure.dto;

import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceQualityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

/**
 * Data Transfer Object for representing person details in responses.
 * Includes personal information and related entities.
 */
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class PeopleResDto
 {
        private UUID id;
        private String cc;
        private String firstName;
        private String lastName;
        private String phone;
        private String avatar;
        private Date birthDate;
        private PeopleTypeDto typePerson;
        private StatesDto state;
        private AttendanceQualityDto attendanceQuality;
}
