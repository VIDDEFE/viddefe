package com.viddefe.viddefe_api.auth.repository;

import com.viddefe.viddefe_api.auth.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {
    public Optional<UserModel> findByEmail(String email);
}
