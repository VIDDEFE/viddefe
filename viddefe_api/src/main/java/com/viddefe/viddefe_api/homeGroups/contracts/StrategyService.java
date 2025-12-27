package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.StrategyDto;

import java.util.List;
import java.util.UUID;

public interface StrategyService {
    /**
     * Create a new strategy
     * @param strategyDto
     * @param churchId
     * @return
     */
    StrategyDto create(StrategyDto strategyDto, UUID churchId);
    /**
     * Update an existing strategy
     * @param strategyDto
     * @param churchId
     * @param strategyId
     * @return
     */
    StrategyDto update(StrategyDto strategyDto, UUID churchId, UUID strategyId);
    /**
     * Find all strategies
     * @return
     */
    List<StrategyDto> findAll();
    /**
     * Delete a strategy by its ID
     * @param strategyId
     */
    void deleteById(UUID strategyId);
}
