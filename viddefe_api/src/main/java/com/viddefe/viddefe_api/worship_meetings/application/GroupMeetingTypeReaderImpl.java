package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.GroupMeetingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupMeetingTypeReaderImpl implements GroupMeetingTypeReader {
    private final GroupMeetingTypeRepository groupMeetingTypeRepository;
    @Override
    public GroupMeetingTypes getGroupMeetingTypeById(Long id) {
        return groupMeetingTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No existe este tipo de reunion grupal: " + id)
        );
    }

    @Override
    public List<GroupMeetingTypes> getAllGroupMeetingTypes() {
        return groupMeetingTypeRepository.findAll();
    }
}
