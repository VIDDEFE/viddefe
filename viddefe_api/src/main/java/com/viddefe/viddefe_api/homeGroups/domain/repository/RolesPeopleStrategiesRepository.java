package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RolesPeopleStrategiesRepository extends JpaRepository<RolesStrategiesModel, UUID> {
}
