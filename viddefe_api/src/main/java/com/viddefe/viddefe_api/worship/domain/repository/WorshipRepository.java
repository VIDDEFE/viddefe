package com.viddefe.viddefe_api.worship.domain.repository;

import com.viddefe.viddefe_api.worship.domain.models.WorshipModel;
import com.viddefe.viddefe_api.worship.infrastructure.dto.WorshipDto;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface WorshipRepository extends JpaRepository<WorshipModel, UUID> {
    Page<WorshipModel> findAllByChurchId(@NotNull UUID churchId, Pageable pageable);

    boolean existsByChurchIdAndWorshipTypeIdAndScheduledDate(UUID churchId, @NotNull(message = "Worship type ID is required") Long worshipTypeId, @NotNull(message = "Scheduled date is required") @FutureOrPresent(message = "Scheduled date cannot be in the past") LocalDateTime scheduledDate);

    boolean existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(UUID churchId, @NotNull(message = "Worship type ID is required") Long worshipTypeId, @NotNull(message = "Scheduled date is required") @FutureOrPresent(message = "Scheduled date cannot be in the past") LocalDateTime scheduledDate, UUID worshipId);
}
