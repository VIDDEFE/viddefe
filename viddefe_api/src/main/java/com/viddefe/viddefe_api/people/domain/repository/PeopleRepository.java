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
     * Buscar personas por iglesia y tipo con relaciones pre-cargadas.
     * Evita N+1 en PeopleServiceImpl.getAllPeople() al llamar toDto()
     * @param churchId ID de la iglesia
     * @param typePersonId ID del tipo de persona (opcional)
     * @param pageable Paginación
     * @return Página de personas con state y typePerson pre-cargados
     */
    @Query("""
    SELECT p
    FROM PeopleModel p
    LEFT JOIN FETCH p.state
    LEFT JOIN FETCH p.typePerson
    WHERE p.church.id = :churchId
      AND (:typePersonId IS NULL OR p.typePerson.id = :typePersonId)
""")
    Page<PeopleModel> findByChurchAndOptionalType(
            @Param("churchId") UUID churchId,
            @Param("typePersonId") Long typePersonId,
            Pageable pageable
    );

    /**
     * Busca persona por ID con todas las relaciones pre-cargadas.
     * Evita N+1 en métodos que acceden a state de la persona
     */
    @Query("SELECT p FROM PeopleModel p " +
           "LEFT JOIN FETCH p.state " +
           "LEFT JOIN FETCH p.typePerson " +
           "LEFT JOIN FETCH p.church " +
           "WHERE p.id = :id")
    Optional<PeopleModel> findByIdWithRelations(@Param("id") UUID id);

    Optional<PeopleModel> findByCcAndChurchId(String cc, UUID churchId);
}
