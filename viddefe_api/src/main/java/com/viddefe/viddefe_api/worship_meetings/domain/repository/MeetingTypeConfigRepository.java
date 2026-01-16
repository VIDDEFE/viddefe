package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeConfig;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la tabla de configuración de tipos de reuniones.
 */
@Repository
public interface MeetingTypeConfigRepository extends JpaRepository<MeetingTypeConfig, UUID> {

    /**
     * Busca todas las configuraciones de un tipo de reunión.
     */
    List<MeetingTypeConfig> findByMeetingTypeEnum(MeetingTypeEnum meetingTypeEnum);

    /**
     * Busca configuración por tipo y subtype.
     */
    MeetingTypeConfig findByMeetingTypeEnumAndSubtypeId(MeetingTypeEnum meetingTypeEnum, Long subtypeId);
}

