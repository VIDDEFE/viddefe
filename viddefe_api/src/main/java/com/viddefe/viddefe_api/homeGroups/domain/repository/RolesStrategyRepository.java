package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolesStrategyRepository extends JpaRepository<RolesStrategiesModel, UUID> {
    List<RolesStrategiesModel> findAllByStrategyId(UUID strategyId);
}
