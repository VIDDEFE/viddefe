package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionTypeDto;

import java.util.List;
import java.util.UUID;

public interface MinistryFunctionService {
    /**
     * Create a new Ministry Function
     * @param dto Data Transfer Object containing the details of the ministry function to be created
     * @param eventId UUID of the event to which the ministry function is associated
     * @return MinistryFunctionDto representing the created ministry function
     */
    MinistryFunctionDto create(CreateMinistryFunctionDto dto, UUID eventId, TopologyEventType eventType);
    /**
     * Update an existing Ministry Function
     * @param id UUID of the ministry function to be updated
     * @param dto Data Transfer Object containing the updated details of the ministry function
     * @return MinistryFunctionDto representing the updated ministry function
     */
    MinistryFunctionDto update(UUID id, CreateMinistryFunctionDto dto, TopologyEventType eventType);
    /**
     * Find Ministry Functions by Event ID
     * @param eventId UUID of the event
     * @return List of MinistryFunctionDto associated with the specified event
     */
    List<MinistryFunctionDto> findByEventId(UUID eventId, TopologyEventType eventType);
    /**
     * Delete a Ministry Function by ID
     * @param id UUID of the ministry function to be deleted
     */
    void delete(UUID id);

    /**
     * Get all Ministry Function Types
     * @return List of MinistryFunctionTypeDto representing all ministry function types
     */
    List<MinistryFunctionTypeDto> getAllMinistryFunctionTypes();
}
