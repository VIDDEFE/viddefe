package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.serializable.AttendanceQualityPeopleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(
        name = "attendance_qualities_people",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_quality_people",
                        columnNames = {"people_id", "context_id"}
                )
        }
)

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AttendanceQualityPeople {

    @EmbeddedId
    private AttendanceQualityPeopleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_quality_id", nullable = false)
    private AttendanceQuality attendanceQuality;

    @MapsId("peopleId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "people_id", nullable = false)
    private PeopleModel people;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TopologyEventType eventType;

}