package com.viddefe.viddefe_api.worship.contracts;

import com.viddefe.viddefe_api.worship.domain.models.WorshipMeetingTypes;

import java.util.List;

public interface TypesWorshipMeetingReader {
    /**
     * Get Worship Meeting (in the temple) Types by Id
     * @param id {@link Long}
     * @return the Worship Meeting Types {@link WorshipMeetingTypes}
     */
    WorshipMeetingTypes getWorshipMeetingTypesById(Long id);
    List<WorshipMeetingTypes> getAllWorshipMeetingTypes();
}
