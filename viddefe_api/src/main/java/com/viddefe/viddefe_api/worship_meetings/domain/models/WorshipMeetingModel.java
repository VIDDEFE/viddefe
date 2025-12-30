package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Table(
        name = "worship_services",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_church_type_scheduled",
                        columnNames = {
                                "church_id",
                                "worship_meeting_type_id",
                                "scheduled_date"
                        }
                )
        }
)
@Entity
@Getter @Setter
public class WorshipMeetingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;
    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @ManyToOne
    @JoinColumn(name = "worship_meeting_type_id", nullable = false)
    private WorshipMeetingTypes worshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;

    public WorshipDto toDto() {
        WorshipDto worshipDto = new WorshipDto();
        worshipDto.setId(this.id);
        worshipDto.setName(this.name);
        worshipDto.setDescription(this.description);
        worshipDto.setCreationDate(this.creationDate);
        worshipDto.setScheduledDate(this.scheduledDate
                .withMinute(0)
                .withSecond(0)
                .withNano(0));
        worshipDto.setWorshipType(this.worshipType);
        return worshipDto;
    }

    public WorshipMeetingModel fromDto(CreateWorshipDto dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.scheduledDate = dto.getScheduledDate();
        return this;
    }
}
