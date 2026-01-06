package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;

import java.util.List;

public interface MeetingTypesService {
    /**
     * Retrieves all worship meeting types.
     *
     * @return A list of WorshipMeetingTypes.
     */
    List<MeetingTypeDto> getAllWorshipMeetingTypes();

    /**
     * Retrieves all group meeting types.
     *
     * @return A list of GroupMeetingTypes.
     */
    List<MeetingTypeDto> getAllGroupMeetingTypes();
}
