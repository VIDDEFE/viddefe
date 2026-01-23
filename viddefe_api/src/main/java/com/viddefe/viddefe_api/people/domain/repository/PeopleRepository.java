package com.viddefe.viddefe_api.people.domain.repository;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleRowProjection;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
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
    SELECT
        p.id AS id,
        p.cc AS cc,
        p.firstName AS firstName,
        p.lastName AS lastName,
        p.phone AS phone,
        p.avatar AS avatar,
        p.birthDate AS birthDate,
        tp.id AS typePersonId,
        tp.name AS typePersonName,
        s.id AS stateId,
        s.name AS stateName,
        aq.attendanceQuality AS attendanceQuality
    FROM PeopleModel p
    LEFT JOIN p.typePerson tp
    LEFT JOIN p.state s
    LEFT JOIN AttendanceQualityPeople aqp ON aqp.people.id = p.id
    LEFT JOIN AttendanceQuality aq ON aq.id = aqp.attendanceQuality.id
    WHERE p.church.id = :churchId
      AND (:typePersonId IS NULL OR tp.id = :typePersonId)
      AND (:attendanceQuality IS NULL OR aq.attendanceQuality = :attendanceQuality)
""")
    Page<PeopleRowProjection> findByChurchAndOptionalType(
            UUID churchId,
            Long typePersonId,
            AttendanceQualityEnum attendanceQuality,
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
