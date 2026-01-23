package com.viddefe.viddefe_api.churches.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChurchRepository extends JpaRepository<ChurchModel, UUID> {

    /**
     * Busca iglesia por ID con city y states pre-cargados.
     * Evita N+1 en ChurchServiceImpl.getChurchById() al llamar toDto()
     */
    @Query("SELECT c FROM ChurchModel c " +
           "JOIN FETCH c.city ci " +
           "JOIN FETCH ci.states " +
           "WHERE c.id = :id")
    Optional<ChurchModel> findByIdWithCityAndState(@Param("id") UUID id);

    @Query(
            value = """
        select new com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto(
            c.id,
            c.name,
            c.longitude,
            c.latitude,
            new com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto(
                s.id,
                s.name
            ),
            new com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto(
                ci.id,
                ci.name
            ),
            new com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto(
                pastor.id,
                pastor.cc,
                pastor.firstName,
                pastor.lastName,
                pastor.phone,
                pastor.avatar,
                cast(pastor.birthDate as java.sql.Date),
                new com.viddefe.viddefe_api.people.infrastructure.dto.PeopleTypeDto(
                    pastor.typePerson.id,
                    pastor.typePerson.name
                ),
                new com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto(
                    s.id,
                    s.name
                ),
               aq.name
            )
        )
        from ChurchModel c
            join c.city ci
            join ci.states s
            left join ChurchPastor cp on cp.church = c
            left join cp.pastor pastor
            left join AttendanceQualityPeople aqp on aqp.people = pastor
            left join AttendanceQuality aq on aq.id = aqp.attendanceQuality.id
        where (
            (:parentChurchId is null and c.parentChurch is null)
            or
            (:parentChurchId is not null and c.parentChurch.id = :parentChurchId)
        )
        """,
            countQuery = """
        select count(c)
        from ChurchModel c
        where (
            (:parentChurchId is null and c.parentChurch is null)
            or
            (:parentChurchId is not null and c.parentChurch.id = :parentChurchId)
        )
        """
    )
    Page<ChurchResDto> findAllChurchesDtoByParentChurchId(
            @Param("parentChurchId") UUID parentChurchId,
            Pageable pageable
    );

    @Query("""
        SELECT c
        FROM ChurchModel c
        WHERE c.parentChurch.id = :churchId
        AND c.latitude BETWEEN :southLat AND :northLat
        AND c.longitude BETWEEN :westLng AND :eastLng
    """)
    List<ChurchModel> findChildrenInBoundingBox(
            UUID churchId,
            BigDecimal southLat,
            BigDecimal northLat,
            BigDecimal westLng,
            BigDecimal eastLng
    );

}
