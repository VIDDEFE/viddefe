package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VerifyWorshipMeetingConflict {

    private final MeetingRepository meetingRepository;

    public void verifyHourOfMeeting(
            CreateMeetingDto dto,
            UUID churchId,
            UUID groupId,
            UUID meetingId
    ) {
        boolean conflict;

        if (meetingId == null) {
            // CREATE
            conflict = existsForCreate(dto, churchId, groupId);
        } else {
            // UPDATE
            conflict = existsForUpdate(dto, churchId, groupId, meetingId);
        }

        if (conflict) {
            throw new DataIntegrityViolationException(
                    "Conflict: A meeting of the same type already exists at this time for the given context."
            );
        }
    }

    private boolean existsForCreate(
            CreateMeetingDto dto,
            UUID churchId,
            UUID groupId
    ) {
        if (groupId == null) {
            return meetingRepository
                    .existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                            churchId,
                            dto.getMeetingTypeId(),
                            dto.getScheduledDate()
                    );
        }

        return meetingRepository
                .existsByChurchIdAndGroupIdAndMeetingTypeIdAndScheduledDate(
                        churchId,
                        groupId,
                        dto.getMeetingTypeId(),
                        dto.getScheduledDate()
                );
    }

    private boolean existsForUpdate(
            CreateMeetingDto dto,
            UUID churchId,
            UUID groupId,
            UUID meetingId
    ) {
        if (groupId == null) {
            return meetingRepository
                    .existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                            churchId,
                            dto.getMeetingTypeId(),
                            dto.getScheduledDate(),
                            meetingId
                    );
        }

        return meetingRepository
                .existsByChurchIdAndGroupIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                        churchId,
                        groupId,
                        dto.getMeetingTypeId(),
                        dto.getScheduledDate(),
                        meetingId
                );
    }
}
