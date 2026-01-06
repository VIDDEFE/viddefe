package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetings;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.GroupMeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupMeetingServiceImpl implements GroupMeetingService {
    private final GroupMeetingRepository groupMeetingRepository;
    private final HomeGroupReader homeGroupReader;
    private final GroupMeetingTypeReader groupMeetingTypeReader;

    @Override
    public GroupMeetingDto createGroupMeeting(CreateMeetingGroupDto dto,@NotNull UUID groupId) {
        GroupMeetings groupMeeting = new GroupMeetings().fromDto(dto);
        HomeGroupsModel homeGroupsModel = homeGroupReader.findById(groupId);
        GroupMeetingTypes type = groupMeetingTypeReader.getGroupMeetingTypeById(dto.getGroupMeetingTypeId());
        groupMeeting.setGroup(homeGroupsModel);
        groupMeeting.setGroupMeetingType(type);
        return groupMeetingRepository.save(groupMeeting).toDto();
    }

    @Override
    public Page<GroupMeetingDto> getGroupMeetingByGroupId(UUID groupId, Pageable pageable) {
        return groupMeetingRepository.findAllByGroupId(groupId, pageable);
    }
}
