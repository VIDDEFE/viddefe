package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;

import java.util.UUID;

public interface MeetingReader {
    Meeting getById(UUID id);
}
