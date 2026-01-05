package com.viddefe.viddefe_api.finances.contracts;

import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingTypeDto;

import java.util.List;

/**
 * Service interface for managing OfferingType entities.
 */
public interface OfferingTypeService {
    /** Finds an OfferingType by its ID.
     *
     * @param id the ID of the OfferingType
     * @return the OfferingType with the specified ID
     */
    OfferingType findById(Long id);

    /** Retrieves all OfferingType entities.
     *
     * @return a list of all OfferingTypeDto
     */
    List<OfferingTypeDto> findAll();
}