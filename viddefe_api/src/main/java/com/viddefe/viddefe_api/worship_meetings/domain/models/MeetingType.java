package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class MeetingType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topology_metting_id", nullable = false)
    private TopologyMeetingModel topologyMeeting;

    @Column(name = "name", nullable = false)
    private String name;

    public MeetingTypeDto toDto() {
        MeetingTypeDto dto = new MeetingTypeDto();
        dto.setId(this.id);
        dto.setName(this.name);
        return dto;
    }
}

