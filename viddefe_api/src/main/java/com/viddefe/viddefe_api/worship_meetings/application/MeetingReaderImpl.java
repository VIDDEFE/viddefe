package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeetingReaderImpl implements MeetingReader {
    private final MeetingRepository meetingRepository;
    private final ChurchLookup churchLookup;
    private final HomeGroupReader homeGroupReader;

    @Override
    public Meeting getById(UUID id) {
        return meetingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Meeting not found with id: " + id)
        );
    }

}
