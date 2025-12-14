package com.viddefe.viddefe_api.churches.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChurchPastorRepository extends JpaRepository<ChurchPastor, UUID> {
}
