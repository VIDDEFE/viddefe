package com.viddefe.viddefe_api.churches.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChurchRepository extends JpaRepository<ChurchModel, UUID> {
}
