package com.viddefe.viddefe_api.homegroups.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viddefe.viddefe_api.homegroups.domain.model.RolPeopleStrategiesModel;

import java.util.List;
import java.util.UUID;

public interface RolesPeopleStrategiesRepository extends JpaRepository<RolPeopleStrategiesModel, UUID> {
    List<RolPeopleStrategiesModel> findAllByPersonIdInAndRoleId(List<UUID> peopleIds, UUID roleId);
}
