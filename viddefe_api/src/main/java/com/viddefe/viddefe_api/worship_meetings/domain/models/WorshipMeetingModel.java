package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad específica para cultos/servicios de adoración.
 * Extiende Meeting con discriminador WORSHIP.
 *
 * Tabla: meetings (compartida con GroupMeetings)
 * Discriminador: meeting_type = 'WORSHIP'
 *
 * Reglas de timezone:
 * - scheduledDate viene del DTO con offset (ej: -05:00 o Z)
 * - Se almacena en BD como timestamptz
 * - NO hacer conversiones de zona en fromDto()/toDto()
 */
@Entity
@DiscriminatorValue("WORSHIP")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class WorshipMeetingModel extends Meeting {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worship_meeting_type_id", insertable = false, updatable = false)
    private WorshipMeetingTypes worshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id", insertable = false, updatable = false)
    private ChurchModel church;

    /**
     * Constructor para inicialización rápida
     */
    public WorshipMeetingModel(UUID contextId, Long typeId) {
        this.setContextId(contextId);
        this.setTypeId(typeId);
    }

    /**
     * Inicializa desde DTO de creación.
     * NO realiza conversiones de zona.
     */
    public WorshipMeetingModel fromDto(CreateWorshipDto dto) {
        initFromDto(dto.getName(), dto.getDescription(), dto.getScheduledDate());
        return this;
    }

    /**
     * Actualiza desde DTO (no modifica creationDate).
     */
    public WorshipMeetingModel updateFrom(CreateWorshipDto dto) {
        updateFromDto(dto.getName(), dto.getDescription(), dto.getScheduledDate());
        return this;
    }

    /**
     * Convierte a DTO preservando offset sin conversiones.
     */
    public WorshipDto toDto() {
        WorshipDto dto = new WorshipDto();
        dto.setId(getId());
        dto.setName(getName());
        dto.setDescription(getDescription());
        dto.setCreationDate(getCreationDate());
        dto.setScheduledDate(getScheduledDate()
                .withSecond(0)
                .withNano(0));
        if (this.worshipType != null) {
            dto.setWorshipType(this.worshipType.toDto());
        }
        return dto;
    }
}
