package com.viddefe.viddefe_api.homegroups.contracts;

import com.viddefe.viddefe_api.homegroups.domain.model.RolesStrategiesModel;

public interface RolesStrategiesReader {
    RolesStrategiesModel getRoleStrategyById(java.util.UUID roleId);
}
