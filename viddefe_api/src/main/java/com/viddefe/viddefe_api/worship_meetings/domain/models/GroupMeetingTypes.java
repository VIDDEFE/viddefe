package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingTypeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "group_meeting_types")
@Getter @Setter
public class GroupMeetingTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public GroupMeetingTypeDto toDto() {
        GroupMeetingTypeDto dto = new GroupMeetingTypeDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}
