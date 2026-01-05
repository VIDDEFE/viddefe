package com.viddefe.viddefe_api.worship_meetings.infrastructure.dto;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Getter
public class AttendanceProjectionDto {
    private PeopleModel people;
    private AttendanceStatus status;

    public AttendanceDto toDto() {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setPeople(this.people.toDto());
        attendanceDto.setStatus(this.status.name());
        return attendanceDto;
    }
}
