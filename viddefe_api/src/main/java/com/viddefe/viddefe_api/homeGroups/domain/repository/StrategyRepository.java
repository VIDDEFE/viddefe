package com.viddefe.viddefe_api.homegroups.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viddefe.viddefe_api.homegroups.domain.model.StrategiesModel;

import java.util.Optional;
import java.util.UUID;

public interface StrategyRepository extends JpaRepository<StrategiesModel, UUID> {
    boolean existsByName(String name);
    Optional<StrategiesModel> findByName(String name);

    Boolean existsByNameAndChurchId(String name, UUID churchId);
}
