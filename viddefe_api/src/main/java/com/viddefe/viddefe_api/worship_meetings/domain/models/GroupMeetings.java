package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad específica para reuniones de grupos pequeños/hogares.
 * Extiende Meeting con discriminador GROUP_MEETING.
 *
 * Tabla: meetings (compartida con WorshipMeetingModel)
 * Discriminador: meeting_type = 'GROUP_MEETING'
 *
 * Reglas de timezone:
 * - scheduledDate viene del DTO con offset (ej: -05:00 o Z)
 * - Se almacena en BD como timestamptz
 * - NO hacer conversiones de zona en fromDto()/toDto()
 */
@Entity
@DiscriminatorValue("GROUP_MEETING")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class GroupMeetings extends Meeting {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_meeting_type_id", insertable = false, updatable = false)
    private GroupMeetingTypes groupMeetingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id", insertable = false, updatable = false)
    private HomeGroupsModel group;

    /**
     * Constructor para inicialización rápida
     */
    public GroupMeetings(UUID contextId, Long typeId) {
        this.setContextId(contextId);
        this.setTypeId(typeId);
    }

    /**
     * Inicializa desde DTO de creación.
     * NO realiza conversiones de zona.
     */
    public GroupMeetings fromDto(CreateMeetingGroupDto dto) {
        initFromDto(dto.getName(), dto.getDescription(), dto.getDate());
        return this;
    }

    /**
     * Actualiza desde DTO (no modifica creationDate).
     */
    public GroupMeetings updateFrom(CreateMeetingGroupDto dto) {
        updateFromDto(dto.getName(), dto.getDescription(), dto.getDate());
        return this;
    }

    /**
     * Convierte a DTO preservando offset sin conversiones.
     */
    public GroupMeetingDto toDto() {
        GroupMeetingDto dto = new GroupMeetingDto();
        dto.setId(getId());
        dto.setName(getName());
        dto.setDate(getScheduledDate());
        dto.setDescription(getDescription());
        if (this.groupMeetingType != null) {
            dto.setType(this.groupMeetingType.toDto());
        }
        return dto;
    }

}
