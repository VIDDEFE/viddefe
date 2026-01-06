package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingTypeServiceImpl implements MeetingTypesService {
    private final TypesWorshipMeetingReaderImpl typesWorshipMeetingReader;
    private final GroupMeetingTypeReaderImpl groupMeetingTypeReader;
    @Override
    public List<MeetingTypeDto> getAllWorshipMeetingTypes() {
        return typesWorshipMeetingReader.getAllWorshipMeetingTypes().stream()
                .map(WorshipMeetingTypes::toDto).toList();
    }

    @Override
    public List<MeetingTypeDto> getAllGroupMeetingTypes() {
        return groupMeetingTypeReader.getAllGroupMeetingTypes().stream()
                .map(GroupMeetingTypes::toDto).toList();
    }
}
