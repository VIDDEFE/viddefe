package com.viddefe.viddefe_api.churches.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ChurchRepository extends JpaRepository<ChurchModel, UUID> {
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
                p.id,
                p.cc,
                p.firstName,
                p.lastName,
                p.phone,
                p.avatar,
                cast(p.birthdate as java.sql.Date),
                p.typePerson,
                new com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto(
                    s.id,
                    s.name
                )
            )
        )
        from ChurchModel c
            join c.city ci
            join ci.state s
            left join ChurchPastor cp on cp.church = c
            left join cp.pastor p
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


}
