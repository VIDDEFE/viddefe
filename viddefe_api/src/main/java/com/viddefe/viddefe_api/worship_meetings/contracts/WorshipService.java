package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDetailedDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service for Worship Service operations.
 */
public interface WorshipService {
    /**
     * Creates a new worship service.
     *
     * @param dto Data transfer object containing worship service details.
     * @param churchId The ID of the church associated with the worship service.
     * @return The created Worship Meeting, {{@link WorshipDto}}.
     */
    WorshipDto createWorship(CreateWorshipDto dto, UUID churchId);
    /**
     * Retrieves a worship service by its ID.
     *
     * @param id The ID of the worship service.
     * @return The Worship Service, {{@link WorshipDto}}.
     */
    WorshipDetailedDto getWorshipById(UUID id);
    /**
     * Retrieves all worship services with pagination.
     *
     * @param pageable Pagination information.
     * @param churchId The ID of the church to filter worship services.
     * @return A paginated list of Worship Services, {{@link Page}} of {{@link WorshipDto}}.
     */
    Page<WorshipDto> getAllWorships(Pageable pageable, UUID churchId);

    /**
     * Updates an existing worship service.
     * @param id The ID of the worship service to update.
     * @param dto Data transfer object containing updated worship service details.
     * @param churchId The ID of the church associated with the worship service.
     * @return The updated Worship Service, {{@link WorshipDto}}.
     */
    WorshipDto updateWorship(UUID id, CreateWorshipDto dto, UUID churchId);

    /**
     * Deletes a worship service by its ID.
     * @param id
     */
    void deleteWorship(UUID id);

    /**
     * Retrieves all worship meeting types.
     * @return A list of Worship Meeting Types, {{@link List}} of {{@link WorshipMeetingTypes}}.
     */
    List<WorshipMeetingTypes> getAllWorshipMeetingTypes();
}
