package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "group_meeting_types")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class GroupMeetingTypes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public MeetingTypeDto toDto() {
        MeetingTypeDto dto = new MeetingTypeDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}
