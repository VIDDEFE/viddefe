package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Entidad para reuniones de grupos pequeños/hogares.
 * Extiende de Meeting para heredar campos comunes (id, name, description, scheduledDate, creationDate).
 *
 * Nota: Esta entidad usa 'date' como nombre de columna para scheduledDate por compatibilidad
 * con el esquema existente. El campo heredado scheduledDate se mapea a la columna 'date'.
 *
 * Reglas de timezone:
 * - scheduledDate viene del DTO con offset (ej: -05:00 o Z)
 * - Se almacena en BD como timestamptz
 * - NO hacer conversiones de zona en fromDto()/toDto()
 */
@Table(name = "group_meetings")
@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@AttributeOverrides({
    @AttributeOverride(name = "scheduledDate", column = @Column(name = "date", nullable = false, columnDefinition = "timestamptz"))
})
public class GroupMeetings extends Meeting {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_meeting_type_id", nullable = false)
    private GroupMeetingTypes groupMeetingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_groups_id", nullable = false)
    private HomeGroupsModel group;

    /**
     * Inicializa la entidad desde un DTO de creación.
     * NO realiza conversiones de zona - asigna la fecha directamente.
     *
     * @param dto DTO con los datos del meeting a crear
     * @return this (para encadenamiento)
     */
    public GroupMeetings fromDto(CreateMeetingGroupDto dto) {
        initFromDto(dto.getName(), dto.getDescription(), dto.getDate());
        return this;
    }

    /**
     * Actualiza la entidad desde un DTO (no modifica creationDate).
     *
     * @param dto DTO con los datos actualizados
     * @return this (para encadenamiento)
     */
    public GroupMeetings updateFrom(CreateMeetingGroupDto dto) {
        updateFromDto(dto.getName(), dto.getDescription(), dto.getDate());
        return this;
    }

    /**
     * Convierte la entidad a DTO de respuesta.
     * Preserva el scheduledDate sin conversiones de zona.
     *
     * @return GroupMeetingDto con los datos del meeting
     */
    public GroupMeetingDto toDto() {
        GroupMeetingDto groupMeetingDto = new GroupMeetingDto();
        groupMeetingDto.setId(getId());
        groupMeetingDto.setType(this.groupMeetingType.toDto());
        groupMeetingDto.setName(getName());
        groupMeetingDto.setDate(getScheduledDate());
        groupMeetingDto.setDescription(getDescription());
        return groupMeetingDto;
    }

}
