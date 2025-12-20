package com.viddefe.viddefe_api.auth.contracts;

import com.viddefe.viddefe_api.auth.Infrastructure.dto.PermissionSeedRequest;
import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing permissions.
 */
public interface PermissionService {
    List<PermissionModel> findAll();
    /**
     * Seeds permissions into the system based on the provided request.
     *
     * @param request {{@link PermissionSeedRequest}} the permission seed request containing permissions to be added
     */
    void seed(PermissionSeedRequest request);
    /**
     * Finds a permission by its name.
     *
     * @param name the name of the permission
     * @return the {@link PermissionModel} corresponding to the given name
     */
    PermissionModel findByName(String name);
    /**
     * Finds permissions by a list of names.
     *
     * @param names {@link String} the list of permission names
     * @return a list of {@link PermissionModel} corresponding to the given names
     */
    List<PermissionModel> findByListNames(List<String> names);

    /**
     * Finds permissions associated with a specific user by their user ID.
     * @param userId
     * @return
     */
    List<PermissionModel> findByUserId(UUID userId);
}
