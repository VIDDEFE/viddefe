package com.viddefe.viddefe_api.worship.application;

import com.viddefe.viddefe_api.worship.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship.domain.repository.WorshipRepository;
import com.viddefe.viddefe_api.worship.infrastructure.dto.CreateWorshipDto;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VerifyWorshipMeetingConflict {

    private final WorshipRepository worshipRepository;

    /**
     * Verifica que no existan conflictos de horario o tipo de culto en la misma iglesia.
     *
     * @param dto       datos del culto
     * @param churchId  iglesia
     * @param worshipId id del culto actual (null si es creación)
     */
    public void verifyHourOfWorshipMeeting(
            CreateWorshipDto dto,
            UUID churchId,
            UUID worshipId
    ) {
        boolean conflict;

        if (worshipId == null) {
            conflict = worshipRepository
                    .existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                            churchId,
                            dto.getWorshipTypeId(),
                            dto.getScheduledDate()
                    );
        } else {
            conflict = worshipRepository
                    .existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                            churchId,
                            dto.getWorshipTypeId(),
                            dto.getScheduledDate(),
                            worshipId
                    );
        }

        if (conflict) {
            throw new DataIntegrityViolationException(
                    "Conflict: Ya existe un culto del mismo tipo en ese horario para esta iglesia."
            );
        }
    }

}