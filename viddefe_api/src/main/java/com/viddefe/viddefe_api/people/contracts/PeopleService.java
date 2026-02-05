package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PeopleService {
    /**
     * Crea una nueva persona en el sistema.
     * @param dto
     * @return
     */
    PeopleResDto createPeople(PeopleDTO dto);
    /**
     * Get a paginated list of people with optional filters.
     * @param pageable
     * @param personTypeId Opcional filter by person type ID
     * @param churchId ID of the church (non-null)
     * @param attendanceQuality Filter by attendance quality enum
     * @return
     */
    Page<PeopleResDto> getAllPeople(Pageable pageable, Long personTypeId, UUID  churchId, AttendanceQualityEnum attendanceQuality);
    /**
     * Actualiza la información de una persona existente.
     * @param dto
     * @param id
     * @return
     */
    PeopleResDto updatePeople(PeopleDTO dto, UUID id);
    /**
     * Elimina una persona del sistema.
     * @param id
     */
    void deletePeople(UUID id);
    /**
     * Obtiene una persona por su ID.
     * @param id
     * @return
     */
    PeopleResDto getPeopleById(UUID id);
}

