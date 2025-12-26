package com.viddefe.viddefe_api.worship.contracts;

import com.viddefe.viddefe_api.worship.domain.models.WorshipModel;
import com.viddefe.viddefe_api.worship.infrastructure.dto.CreateWorshipDto;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.UUID;

/**
 * Service for Worship Service operations.
 */
public interface WorshipService {
    /**
     * Creates a new worship service.
     *
     * @param dto Data transfer object containing worship service details.
     * @return The created WorshipModel.
     */
    WorshipModel createWorship(CreateWorshipDto dto);
    WorshipModel getWorshipById(UUID id);
    Page<WorshipModel> getAllWorships(Pageable pageable);
    void deleteWorship(UUID id);
}
