package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeConfig;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingTypeConfigRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para gestionar configuraciones de tipos de reuniones.
 */
@Service
public class MeetingTypeConfigService {

    private final MeetingTypeConfigRepository repository;

    public MeetingTypeConfigService(MeetingTypeConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene todas las configuraciones de un tipo de reunión.
     */
    public List<MeetingTypeConfig> findByMeetingType(MeetingTypeEnum type) {
        return repository.findByMeetingTypeEnum(type);
    }

    /**
     * Obtiene configuración específica por tipo y subtype.
     */
    public Optional<MeetingTypeConfig> findByTypeAndSubtype(MeetingTypeEnum type, Long subtypeId) {
        return Optional.ofNullable(repository.findByMeetingTypeEnumAndSubtypeId(type, subtypeId));
    }

    /**
     * Crea una nueva configuración de tipo.
     */
    public MeetingTypeConfig create(MeetingTypeConfig config) {
        return repository.save(config);
    }

    /**
     * Obtiene configuración por ID.
     */
    public Optional<MeetingTypeConfig> findById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Elimina una configuración.
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}

