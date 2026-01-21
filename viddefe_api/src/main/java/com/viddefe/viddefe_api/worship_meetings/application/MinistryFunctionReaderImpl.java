package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinistryFunctionReaderImpl implements MinistryFunctionReader {
    private final MinistryFunctionRepository ministryFunctionRepository;
    @Override
    public MinistryFunction getByPeopleIdAndMeetingId(UUID peopleId, UUID meetingId) {
        return ministryFunctionRepository.findByMeetingIdAndPeopleId(meetingId, peopleId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Ministry function not found for peopleId: " +
                                peopleId + " and meetingId: " + meetingId)
                );
    }
}
