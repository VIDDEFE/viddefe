package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;

import java.util.UUID;

public interface EventMeetingReaderCaseUse {
    /**
     * Find in WorshipService or GroupMeeting
     * @param eventId
     * @return
     */
    MeetingDto getMeetingDto(UUID eventId);
}
