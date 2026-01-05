package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;

import java.util.UUID;

public interface StrategyReader {
    /**
     * Check if a strategy with the given name exists.
     * @param name the name of the strategy to check
     * @return true if the strategy exists, false otherwise
     */
    boolean existsByName(String name);
    /**
     * Find a strategy by its name.
     * @param name the name of the strategy to find
     * @return the StrategiesModel with the given name
     */
    StrategiesModel findByName(String name);
    /**
     * Find a strategy by its ID.
     * @param id {@link UUID} the ID of the strategy to find
     * @return the StrategiesModel with the given ID
     */
    StrategiesModel findById(UUID id);
}
