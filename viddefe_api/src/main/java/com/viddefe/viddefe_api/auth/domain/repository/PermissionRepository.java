package com.viddefe.viddefe_api.auth.domain.repository;

import com.viddefe.viddefe_api.auth.domain.model.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
    boolean existsByName(String name);
}
