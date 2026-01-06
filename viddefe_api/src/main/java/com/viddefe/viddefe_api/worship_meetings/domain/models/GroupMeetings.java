package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Table(name = "group_meetings")
@Entity
@Getter @Setter
public class GroupMeetings {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_meeting_type_id", nullable = false)
    private GroupMeetingTypes groupMeetingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private HomeGroupsModel group;

    @Column(name = "meeting_date", nullable = false)
    private LocalDateTime date;

    public GroupMeetings fromDto(CreateMeetingGroupDto dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.date = dto.getDate();
        return this;
    }

    public GroupMeetingDto toDto() {
        GroupMeetingDto groupMeetingDto = new GroupMeetingDto();
        groupMeetingDto.setId(this.id);
        groupMeetingDto.setType(this.groupMeetingType.toDto());
        groupMeetingDto.setName(this.name);
        groupMeetingDto.setDate(this.date);
        groupMeetingDto.setDescription(this.description);
        return groupMeetingDto;
    }

}
