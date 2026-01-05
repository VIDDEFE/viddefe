package com.viddefe.viddefe_api.finances.contracts;

import com.viddefe.viddefe_api.finances.infrastructure.dto.CreateOfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDtoPageWithAnalityc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * OfferingService interface defines the contract for registering offerings.
 */
public interface OfferingService {
    /**
     * Registers a new offering based on the provided data transfer object.
     *
     * @param dto Data transfer object containing offering details.
     * @return Data transfer object representing the registered offering.
     */
    OfferingDto register(CreateOfferingDto dto);

    /**
     * Updates an existing offering based on the provided data transfer object.
     *
     * @param dto Data transfer object containing updated offering details.
     * @return Data transfer object representing the updated offering.
     */
    OfferingDto update(CreateOfferingDto dto, UUID id);

    /**
     * Retrieves all offerings associated with a specific event.
     *
     * @param eventId Unique identifier of the event.
     * @param pageable Pagination information.
     * @return {@link Page<OfferingDto>} List of data transfer objects representing the offerings .
     */
    OfferingDtoPageWithAnalityc getAllByEventId(UUID eventId, Pageable pageable);

    /**
     * Deletes an offering by its unique identifier.
     *
     * @param id Unique identifier of the offering to be deleted.
     * @return void.
     */
    void delete(UUID id);
}
