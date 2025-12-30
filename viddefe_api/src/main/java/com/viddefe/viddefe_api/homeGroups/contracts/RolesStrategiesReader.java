package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;

public interface RolesStrategiesReader {
    RolesStrategiesModel getRoleStrategyById(java.util.UUID roleId);
}
