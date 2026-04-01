package com.viddefe.viddefe_api.churches.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.EntityIdWithTotalPeople;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChurchLookupImpl implements ChurchLookup {
    private final ChurchRepository churchRepository;

    @Override
    public ChurchModel getChurchById(@NonNull UUID id) {
        return churchRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Church not found")
        );
    }

    /**
     * Find all children church IDs by the given church ID.
     * @param churchId
     * @return
     */
    @Override
    public List<EntityIdWithTotalPeople> findChildrenIdsWithTotalPeopleChurchIdsByChurchId(UUID churchId) {
        return churchRepository.findAllChildrenIdsWithTotalPeopleByChurchId(churchId);
    }

    /**
     * @param churchId
     * @return
     */
    @Override
    public EntityIdWithTotalPeople findChurchIdWithTotalPeopleByChurchId(UUID churchId) {
        return churchRepository.findChurchIdWithTotalPeopleByChurchId(churchId);
    }
}
