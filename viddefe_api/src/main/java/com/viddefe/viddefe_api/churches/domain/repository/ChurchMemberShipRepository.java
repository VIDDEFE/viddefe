package com.viddefe.viddefe_api.churches.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;

public interface ChurchMemberShipRepository extends  JpaRepository<UUID, UUID> {
    // Aquí puedes definir métodos personalizados para acceder a la información de membresía de la iglesia
    @Query(value = """
        SELECT p.id FROM PeopleModel p
        JOIN ChurchModel cm ON cm.id = p.church.id
        WHERE cm.id = :churchId
    """)
    List<UUID> findPeopleIdsByChurchId(@Param("churchId") UUID churchId);

}
