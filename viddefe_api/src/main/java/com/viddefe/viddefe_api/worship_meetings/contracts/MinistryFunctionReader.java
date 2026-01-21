package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;

import java.util.UUID;

public interface MinistryFunctionReader {
    MinistryFunction getByPeopleIdAndMeetingId(
            UUID peopleId,
            UUID meetingId
    );
}
