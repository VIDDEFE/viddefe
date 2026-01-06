package com.viddefe.viddefe_api.finances.domain.repositories;

import com.viddefe.viddefe_api.finances.domain.model.Offerings;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingAnalityc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OfferingsRepository extends JpaRepository<Offerings, UUID> {
    /**
     * Busca ofrendas por evento con person y offeringType pre-cargados.
     * Evita N+1 en OfferingServiceImpl.getAllByEventId() al llamar toDto()
     */
    @EntityGraph(attributePaths = {"person", "person.state", "person.typePerson", "offeringType"})
    Page<Offerings> findAllByEventId(UUID eventId, Pageable pageable);

    @Query("""
        SELECT new com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingAnalityc(
            ot.code,
            ot.name,
            SUM(o.amount),
            COUNT(o.id)
        )
        FROM Offerings o
        JOIN o.offeringType ot
        WHERE o.eventId = :eventId
        GROUP BY ot.code, ot.name
    """)
    List<OfferingAnalityc> analyticsByEvent(@Param("eventId") UUID eventId);
}
