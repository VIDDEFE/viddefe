package com.viddefe.viddefe_api.homeGroups.domain.repository;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HomeGroupsRepository extends JpaRepository<HomeGroupsModel, UUID> {
    /**
     * Busca grupos por iglesia con leader, strategy y church pre-cargados.
     * Evita N+1 en HomeGroupServiceImpl.getHomeGroups() al llamar toDto()
     */
    @EntityGraph(attributePaths = {"leader", "leader.state", "leader.typePerson", "strategy", "church"})
    Page<HomeGroupsModel> findAllByChurchId(UUID churchId, Pageable pageable);

    /**
     * Busca grupo por ID con todas las relaciones pre-cargadas.
     * Evita N+1 en HomeGroupServiceImpl.getHomeGroupById()
     */
    @EntityGraph(attributePaths = {"leader", "leader.state", "leader.typePerson", "strategy", "church"})
    Optional<HomeGroupsModel> findWithRelationsById(UUID id);
}
