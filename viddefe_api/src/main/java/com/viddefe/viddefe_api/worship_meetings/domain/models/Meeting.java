package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "meetings")
@Getter @Setter
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    /**
     * Fecha de creación - Instant en UTC
     */
    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz")
    private Instant creationDate;

    /**
     * Fecha programada con offset del cliente
     */
    @Column(name = "scheduled_date", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime scheduledDate;


    @ManyToOne
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private HomeGroupsModel group;

    @ManyToOne
    @JoinColumn(name = "meeting_type_id", nullable = false)
    private MeetingType meetingType;


    public Meeting fromDto(CreateMeetingDto dto){
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.scheduledDate = dto.getScheduledDate();
        return this;
    }

    public MeetingDto toDto() {
        MeetingDto meetingDto = new MeetingDto();
        meetingDto.setId(this.id);
        meetingDto.setName(this.name);
        meetingDto.setDescription(this.description);
        meetingDto.setCreationDate(this.creationDate);
        meetingDto.setScheduledDate(this.scheduledDate);
        meetingDto.setType(this.meetingType.toDto());
        return meetingDto;
    }
}