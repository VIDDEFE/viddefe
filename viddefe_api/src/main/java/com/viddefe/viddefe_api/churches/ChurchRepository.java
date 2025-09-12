package com.viddefe.viddefe_api.churches;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChurchRepository extends JpaRepository<ChurchModel, UUID> {
}
