package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.homeGroups.domain.model.RolPeopleStrategiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolesPeopleStrategiesRepository extends JpaRepository<RolPeopleStrategiesModel, UUID> {
    List<RolPeopleStrategiesModel> findAllByPersonIdInAndRoleId(List<UUID> peopleIds, UUID roleId);
}
