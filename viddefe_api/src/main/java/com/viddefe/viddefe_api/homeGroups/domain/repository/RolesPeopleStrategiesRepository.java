package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.homeGroups.domain.model.RolPeopleStrategiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolesPeopleStrategiesRepository extends JpaRepository<RolPeopleStrategiesModel, UUID> {
}
