package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeConfigDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Tabla de configuración para tipos de reuniones base.
 * Esta tabla NO reemplaza WorshipMeetingTypes ni GroupMeetingTypes,
 * sino que es un catálogo de tipos de meetings DESPUÉS del discriminador.
 *
 * Estructura:
 * - WORSHIP types: Dominical, Miércoles, Especiales, etc.
 * - GROUP_MEETING types: Estudio Bíblico, Oración, Confraternidad, etc.
 *
 * meetingTypeEnum: El tipo genérico (WORSHIP o GROUP_MEETING)
 * subtypeId: Referencia a WorshipMeetingTypes.id o GroupMeetingTypes.id según meetingTypeEnum
 */
@Entity
@Table(name = "meeting_type_configs")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class MeetingTypeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type_enum", nullable = false)
    private MeetingTypeEnum meetingTypeEnum;

    /**
     * Referencia al tipo específico (WorshipMeetingTypes.id o GroupMeetingTypes.id)
     */
    @Column(name = "subtype_id", nullable = false)
    private Long subtypeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    public MeetingTypeConfigDto toDto() {
        MeetingTypeConfigDto dto = new MeetingTypeConfigDto();
        dto.setId(this.id);
        dto.setMeetingTypeEnum(this.meetingTypeEnum);
        dto.setSubtypeId(this.subtypeId);
        dto.setName(this.name);
        dto.setDescription(this.description);
        return dto;
    }
}

