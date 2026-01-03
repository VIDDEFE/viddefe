package com.viddefe.viddefe_api.auth.domain.repository;

import com.viddefe.viddefe_api.auth.domain.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByPhone(String phone);
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

}
