package com.viddefe.viddefe_api.churches.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChurchPastorRepository extends JpaRepository<ChurchPastor, UUID> {
    Optional<ChurchPastor> findByChurch(@NonNull ChurchModel church);
}
