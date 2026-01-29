package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.EntityIdWithTotalPeople;

import java.util.List;
import java.util.UUID;

public interface HomeGroupReader {
    /** find group by id */
    HomeGroupsModel findById(UUID groupId);
    /**
     * find all group ids with total people by churchId
     */
    List<EntityIdWithTotalPeople> findAllIdsWithTotalPeopleByChurchId(UUID churchId);
    /**
     * find the total people in a group by groupId
     */
    Long findTotalPeopleByGroupId(UUID groupId);
}
