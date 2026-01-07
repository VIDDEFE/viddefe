package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeGroupsRepository extends JpaRepository<HomeGroupsModel, UUID> {
    /**
     * Busca grupos por iglesia con manager, strategy y church pre-cargados.
     * Evita N+1 en HomeGroupServiceImpl.getHomeGroups() al llamar toDto()
     */
    @EntityGraph(attributePaths = {"manager", "manager.state", "manager.typePerson", "strategy", "church"})
    Page<HomeGroupsModel> findAllByChurchId(UUID churchId, Pageable pageable);

    /**
     * Busca grupo por ID con todas las relaciones pre-cargadas.
     * Evita N+1 en HomeGroupServiceImpl.getHomeGroupById()
     */
    @EntityGraph(attributePaths = {"manager", "manager.state", "manager.typePerson", "strategy", "church"})
    Optional<HomeGroupsModel> findWithRelationsById(UUID id);

    @Query("""
    SELECT hg
    FROM HomeGroupsModel hg
    WHERE hg.latitude BETWEEN :southLat AND :northLat
      AND hg.longitude BETWEEN :westLng AND :eastLng
      AND hg.church.id = :churchId
""")
    List<HomeGroupsModel> findByChurchIdInBoundingBox(
            @Param("southLat") BigDecimal  southLat,
            @Param("northLat")  BigDecimal  northLat,
            @Param("westLng")  BigDecimal westLng,
            @Param("eastLng")  BigDecimal eastLng,
            @Param("churchId") UUID churchId
    );

    Optional<HomeGroupsModel> findByManagerId(UUID personId);

    @Query(value = """
        SELECT 
            hg
        FROM HomeGroupsModel hg
        JOIN FETCH RolesStrategiesModel rss ON rss.strategy.id = hg.strategy.id
        JOIN FETCH RolPeopleStrategiesModel rps ON rps.role.id = rss.id
        WHERE hg.manager.id = :personId OR rps.person.id = :personId
    """)
    Optional<HomeGroupsModel> getHomeGroupByIntegrantId(@Param("personId") UUID personId);

}
