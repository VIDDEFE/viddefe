package com.viddefe.viddefe_api.auth.domain.repository;

import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
    boolean existsByName(String name);

    Optional<PermissionModel> findByName(String name);

    List<PermissionModel> findAllByNameIn(List<String> names);
}
