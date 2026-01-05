package com.viddefe.viddefe_api.churches.domain.repository;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ChurchPastorRepository extends JpaRepository<ChurchPastor, UUID> {
    Optional<ChurchPastor> findByChurch(@NonNull ChurchModel church);

    /**
     * Busca ChurchPastor con pastor y sus relaciones pre-cargadas.
     * Evita N+1 cuando se necesita acceder a pastor.state y pastor.typePerson
     */
    @Query("SELECT cp FROM ChurchPastor cp " +
           "JOIN FETCH cp.pastor p " +
           "LEFT JOIN FETCH p.state " +
           "LEFT JOIN FETCH p.typePerson " +
           "WHERE cp.church = :church")
    Optional<ChurchPastor> findByChurchWithPastorRelations(@Param("church") ChurchModel church);
}
