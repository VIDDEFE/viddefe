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

    @Column(name = "people_id")
    private UUID peopleId;

    @Column(name = "context_id")
    private UUID contextId;

}
