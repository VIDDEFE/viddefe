package com.viddefe.viddefe_api.auth.repository;

import com.viddefe.viddefe_api.auth.model.RolUserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolUserRepository extends JpaRepository<RolUserModel,Long> {
}
