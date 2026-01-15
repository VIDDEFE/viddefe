package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad para cultos/servicios de adoración.
 * Extiende de Meeting para heredar campos comunes (id, name, description, scheduledDate, creationDate).
 *
 * Reglas de timezone:
 * - scheduledDate viene del DTO con offset (ej: -05:00 o Z)
 * - Se almacena en BD como timestamptz
 * - NO hacer conversiones de zona en fromDto()/toDto()
 */
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
public class WorshipMeetingModel extends Meeting {

    @ManyToOne
    @JoinColumn(name = "worship_meeting_type_id", nullable = false)
    private WorshipMeetingTypes worshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private ChurchModel church;

    /**
     * Convierte la entidad a DTO de respuesta.
     * Preserva el scheduledDate sin conversiones de zona.
     *
     * @return WorshipDto con los datos del culto
     */
    public WorshipDto toDto() {
        WorshipDto worshipDto = new WorshipDto();
        worshipDto.setId(getId());
        worshipDto.setName(getName());
        worshipDto.setDescription(getDescription());
        worshipDto.setCreationDate(getCreationDate());
        // Preservar offset, solo limpiar segundos/nanos para consistencia
        worshipDto.setScheduledDate(getScheduledDate()
                .withSecond(0)
                .withNano(0));
        worshipDto.setWorshipType(this.worshipType.toDto());
        return worshipDto;
    }

    /**
     * Inicializa la entidad desde un DTO de creación.
     * NO realiza conversiones de zona - asigna scheduledDate directamente.
     *
     * @param dto DTO con los datos del culto a crear
     * @return this (para encadenamiento)
     */
    public WorshipMeetingModel fromDto(CreateWorshipDto dto) {
        // Usa el método heredado para campos comunes
        initFromDto(dto.getName(), dto.getDescription(), dto.getScheduledDate());
        return this;
    }

    /**
     * Actualiza la entidad desde un DTO (no modifica creationDate).
     *
     * @param dto DTO con los datos actualizados
     * @return this (para encadenamiento)
     */
    public WorshipMeetingModel updateFrom(CreateWorshipDto dto) {
        updateFromDto(dto.getName(), dto.getDescription(), dto.getScheduledDate());
        return this;
    }
}
