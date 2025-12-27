package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateRolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolesStrategiesDto;

import java.util.List;
import java.util.UUID;

/**
 * Application service responsible for managing role hierarchies
 * within a {@code Strategy}.
 *
 * <p>
 * A {@code RoleStrategy} represents a node in a hierarchical structure
 * (tree) scoped to a specific strategy. Each role:
 * </p>
 *
 * <ul>
 *   <li>Belongs to exactly one strategy</li>
 *   <li>May have a parent role (for hierarchy)</li>
 *   <li>Must have a unique name within the same strategy</li>
 * </ul>
 *
 * <p>
 * This service enforces hierarchy consistency and strategy scoping.
 * </p>
 */
public interface RolesStrategiesService {

    /**
     * Creates a new role within a strategy hierarchy.
     *
     * <p>
     * The created role:
     * </p>
     * <ul>
     *   <li>Is associated with the given strategy</li>
     *   <li>May reference a parent role to form a hierarchy</li>
     *   <li>Must have a unique name within the same strategy</li>
     * </ul>
     *
     * @param dto     role data including name and optional parent role
     * @param groupId identifier of the strategy where the role is created
     * @return the created role representation
     */
    RolesStrategiesDto create(CreateRolesStrategiesDto dto, UUID groupId);

    /**
     * Updates an existing role inside a strategy hierarchy.
     *
     * <p>
     * This operation may update:
     * </p>
     * <ul>
     *   <li>The role name</li>
     *   <li>The role position in the hierarchy (parent role)</li>
     * </ul>
     *
     * <p>
     * Strategy ownership must remain consistent.
     * </p>
     *
     * @param dto     updated role data
     * @param groupId identifier of the strategy owning the role
     * @param roleId id of the rol
     * @return the updated role representation
     */
    RolesStrategiesDto update(CreateRolesStrategiesDto dto, UUID groupId, UUID roleId);

    /**
     * Deletes a role from the hierarchy.
     *
     * <p>
     * Implementations must define the deletion strategy:
     * </p>
     * <ul>
     *   <li>Cascade delete children</li>
     *   <li>Reassign children to parent</li>
     *   <li>Or prevent deletion if children exist</li>
     * </ul>
     *
     * @param id role identifier
     */
    void delete(UUID id);

    /**
     *
     * @param strategyId
     * @return
     */
    List<RolesStrategiesDto> getTree(UUID strategyId);
}
