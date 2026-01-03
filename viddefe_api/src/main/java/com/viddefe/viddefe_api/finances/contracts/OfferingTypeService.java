package com.viddefe.viddefe_api.finances.contracts;

import com.viddefe.viddefe_api.finances.domain.model.OfferingType;

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
}