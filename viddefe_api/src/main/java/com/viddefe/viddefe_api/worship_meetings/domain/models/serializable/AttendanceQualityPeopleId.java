package com.viddefe.viddefe_api.worship_meetings.domain.models.serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@EqualsAndHashCode
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AttendanceQualityPeopleId implements Serializable {

    @Column(name = "attendance_quality_id")
    private Long attendanceQualityId;

    @Column(name = "people_id")
    private UUID peopleId;
}
