package com.viddefe.viddefe_api.homegroups.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viddefe.viddefe_api.homegroups.domain.model.RolesStrategiesModel;

import java.util.List;
import java.util.UUID;

public interface RolesStrategyRepository extends JpaRepository<RolesStrategiesModel, UUID> {
    List<RolesStrategiesModel> findAllByStrategyId(UUID strategyId);
}
