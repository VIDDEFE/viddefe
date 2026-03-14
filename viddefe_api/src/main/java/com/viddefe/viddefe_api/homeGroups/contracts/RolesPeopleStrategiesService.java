package com.viddefe.viddefe_api.homegroups.contracts;

import java.util.UUID;

import com.viddefe.viddefe_api.homegroups.infrastructure.dto.AssignPeopleToRoleDto;

public interface RolesPeopleStrategiesService {
    /**
     * Assign a role to multiple people within a strategy.
     *
     * @param roleId   The ID of the role to be assigned.
     * @param peopleIds {@link AssignPeopleToRoleDto} DTO containing the list of people IDs to whom the role will be assigned.
     */
    void assignRoleToPeopleInStrategy(UUID roleId, AssignPeopleToRoleDto peopleIds);

    /**
     * Remove a role from multiple people within a strategy.
     * @param roleId
     * @param peopleId
     */
    void removeRoleFromPeopleInStrategy(UUID roleId,AssignPeopleToRoleDto peopleId);
}
