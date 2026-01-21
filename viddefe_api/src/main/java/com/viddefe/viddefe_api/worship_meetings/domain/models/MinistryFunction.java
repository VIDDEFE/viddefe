package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Table(name = "ministry_functions")
@Entity
@Getter @Setter
public class MinistryFunction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ministry_function_type_id", nullable = false)
    private MinistryFunctionTypes ministryFunctionType;

    @ManyToOne
    @JoinColumn(name = "people_id", nullable = false)
    private PeopleModel people;

    @ManyToOne(
        fetch = FetchType.EAGER
    )
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TopologyEventType eventType;

    private Instant reminderSentAt;

    public MinistryFunctionDto toDto() {
        MinistryFunctionDto dto = new MinistryFunctionDto();
        dto.setId(this.id);
        dto.setPeople(this.people.toDto());
        dto.setRole(this.ministryFunctionType.toDto());
        return dto;
    }
}
