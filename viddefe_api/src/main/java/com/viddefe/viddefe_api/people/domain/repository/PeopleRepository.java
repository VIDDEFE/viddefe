package com.viddefe.viddefe_api.people.domain.repository;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PeopleRepository extends JpaRepository<PeopleModel, UUID> {
    Optional<PeopleModel> findByCcAndTypePersonAndChurchIsNull(String cc, PeopleTypeModel typePerson);

    /**
     * Buscar una persona por cédula, tipo de persona e iglesia.
     * @param typePersonId
     * @param churchId
     * @param pageable
     * @return
     */
    @Query("""
    SELECT p
    FROM PeopleModel p
    WHERE p.church.id = :churchId
      AND (:typePersonId IS NULL OR p.typePerson.id = :typePersonId)
""")
    Page<PeopleModel> findByChurchAndOptionalType(
            @Param("churchId") UUID churchId,
            @Param("typePersonId") Long typePersonId,
            Pageable pageable
    );

}
