package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"people_id", "event_id", "event_type"})
        }
)
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class AttendanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "people_id")
    private PeopleModel people;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TopologyEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    private Boolean isNewAttendee;

    public AttendanceDto toDto() {
        AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setStatus(this.status.getDisplayName());
        attendanceDto.setPeople(this.people.toDto());
        return attendanceDto;
    }

}
