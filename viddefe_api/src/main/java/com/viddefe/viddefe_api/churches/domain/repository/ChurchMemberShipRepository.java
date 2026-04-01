package com.viddefe.viddefe_api.churches.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;

import io.lettuce.core.dynamic.annotation.Param;

public interface ChurchMemberShipRepository extends  JpaRepository<ChurchModel, UUID> {
    // Aquí puedes definir métodos personalizados para acceder a la información de membresía de la iglesia
    @Query(value = """
        SELECT p.id FROM ChurchModel cm
        JOIN PeopleModel p ON p.church.id = cm.id
        WHERE cm.id = :churchId
    """)
    List<UUID> findPeopleIdsByChurchId(@Param("churchId") UUID churchId);

    @Query(value = """
        SELECT u.id FROM ChurchModel cm
        JOIN PeopleModel p ON p.church.id = cm.id
        JOIN UserModel u ON u.people.id = p.id
        WHERE cm.id = :churchId
    """)
    List<UUID> findUserIdsByChurchId(@Param("churchId") UUID churchId);

}
