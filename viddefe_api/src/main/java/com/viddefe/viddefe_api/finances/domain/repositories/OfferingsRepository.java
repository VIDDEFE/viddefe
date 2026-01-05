package com.viddefe.viddefe_api.finances.domain.repositories;

import com.viddefe.viddefe_api.finances.domain.model.Offerings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OfferingsRepository extends JpaRepository<Offerings, UUID> {
    /**
     * Busca ofrendas por evento con person y offeringType pre-cargados.
     * Evita N+1 en OfferingServiceImpl.getAllByEventId() al llamar toDto()
     */
    @EntityGraph(attributePaths = {"person", "person.state", "person.typePerson", "offeringType"})
    Page<Offerings> findAllByEventId(UUID eventId, Pageable pageable);
}
