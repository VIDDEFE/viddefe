package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QualifyLevelAttendancePeopleJobRoutine {
    private final PeopleReader peopleReader;
    private final MeetingReader meetingReader;

    @Scheduled(fixedRate = 1000 * 60 * 10) // Even 10 minutes
    public void scheduled() {

    }
}
