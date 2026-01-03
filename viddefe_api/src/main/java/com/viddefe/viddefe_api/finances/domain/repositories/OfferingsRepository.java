package com.viddefe.viddefe_api.finances.domain.repositories;

import com.viddefe.viddefe_api.finances.domain.model.Offerings;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfferingsRepository extends JpaRepository<Offerings, UUID> {
    Page<OfferingDto> findAllByEventId(UUID eventId, Pageable pageable);
}
