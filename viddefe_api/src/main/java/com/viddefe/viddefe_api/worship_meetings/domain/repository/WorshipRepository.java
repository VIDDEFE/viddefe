package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingModel;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface WorshipRepository extends JpaRepository<WorshipMeetingModel, UUID> {
    /**
     * Busca cultos por iglesia con worshipType pre-cargado.
     * Evita N+1 en WorshipServicesImpl.getAllWorships() al llamar toDto()
     */
    @EntityGraph(attributePaths = {"worshipType"})
    Page<WorshipMeetingModel> findAllByChurchId(@NotNull UUID churchId, Pageable pageable);

    /**
     * Busca culto por ID con worshipType pre-cargado.
     * Evita N+1 en WorshipServicesImpl.getWorshipById()
     */
    @EntityGraph(attributePaths = {"worshipType", "church"})
    Optional<WorshipMeetingModel> findWithRelationsById(UUID id);

    boolean existsByChurchIdAndWorshipTypeIdAndScheduledDate(UUID churchId, @NotNull(message = "Worship type ID is required") Long worshipTypeId, @NotNull(message = "Scheduled date is required") @FutureOrPresent(message = "Scheduled date cannot be in the past") OffsetDateTime scheduledDate);

    boolean existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(UUID churchId, @NotNull(message = "Worship type ID is required") Long worshipTypeId, @NotNull(message = "Scheduled date is required") @FutureOrPresent(message = "Scheduled date cannot be in the past") OffsetDateTime scheduledDate, UUID worshipId);
}
