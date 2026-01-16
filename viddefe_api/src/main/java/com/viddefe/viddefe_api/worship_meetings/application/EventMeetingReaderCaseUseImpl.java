package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.EventMeetingReaderCaseUse;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetings;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.GroupMeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.WorshipRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventMeetingReaderCaseUseImpl implements EventMeetingReaderCaseUse {

    private final WorshipRepository worshipRepository;
    private final GroupMeetingRepository groupMeetingRepository;

    @Override
    public MeetingDto getMeetingDto(UUID eventId) {

        return worshipRepository.findById(eventId)
                .map(w -> (MeetingDto) w.toDto())
                .or(() -> groupMeetingRepository.findById(eventId)
                        .map(g -> (MeetingDto) g.toDto()))
                .orElseThrow(() ->
                        new EntityNotFoundException("Meeting not found with id: " + eventId)
                );
    }
}
