package com.viddefe.viddefe_api.auth.domain.repository;

import com.viddefe.viddefe_api.auth.domain.model.RolUserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolUserRepository extends JpaRepository<RolUserModel, Long> {
}
