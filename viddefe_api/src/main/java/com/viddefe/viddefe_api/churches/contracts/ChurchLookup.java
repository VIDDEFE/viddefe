package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.EntityIdWithTotalPeople;

import java.util.List;
import java.util.UUID;

public interface ChurchLookup {
    ChurchModel getChurchById(UUID id);

    List<EntityIdWithTotalPeople> findChildrenIdsWithTotalPeopleChurchIdsByChurchId(UUID churchId);
    EntityIdWithTotalPeople findChurchIdWithTotalPeopleByChurchId(UUID churchId);
}
