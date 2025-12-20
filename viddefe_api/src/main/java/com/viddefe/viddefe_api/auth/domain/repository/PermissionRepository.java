package com.viddefe.viddefe_api.auth.domain.repository;

import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
    boolean existsByName(String name);

    Optional<PermissionModel> findByName(String name);

    List<PermissionModel> findAllByNameIn(List<String> names);

    /**
     * Find all permissions by user
     * @param user
     * @return list of permissions for the user
     */
    @Query("""
    SELECT p
    FROM UserPermissions u
    JOIN u.permissionModel p
    WHERE u.userModel = :user
""")
    List<PermissionModel> findAllByUser(@Param("user") UserModel user);

    /**
     * Retrieves all permissions associated with a given user.
     *
     * <p>This method returns the list of {@link PermissionModel} entities that are
     * linked to the specified user through the {@code UserPermissions} relationship.</p>
     *
     * @param userId the unique identifier of the user whose permissions are being retrieved
     * @return a list of {@link PermissionModel} associated with the given user;
     *         returns an empty list if the user has no permissions
     */
    @Query("""
    SELECT p
    FROM UserPermissions u
    JOIN u.permissionModel p
    WHERE u.userModel.id = :userId
""")
    List<PermissionModel> findAllByUserId(@Param("userId") UUID userId);

}
