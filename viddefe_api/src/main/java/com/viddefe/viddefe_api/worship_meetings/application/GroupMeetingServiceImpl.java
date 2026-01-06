package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetings;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.GroupMeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDetailedDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import jakarta.persistence.EntityNotFoundException;
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
    private final AttendanceService attendanceService;

    @Override
    public GroupMeetingDto createGroupMeeting(CreateMeetingGroupDto dto,@NotNull UUID groupId) {
        GroupMeetings groupMeeting = new GroupMeetings().fromDto(dto);
        HomeGroupsModel homeGroupsModel = homeGroupReader.findById(groupId);
        GroupMeetingTypes type = groupMeetingTypeReader.getGroupMeetingTypeById(dto.getGroupMeetingTypeId());
        groupMeeting.setGroup(homeGroupsModel);
        groupMeeting.setGroupMeetingType(type);
        System.out.println("Creating group meeting for group ID ss: " + homeGroupsModel.getId());
        return groupMeetingRepository.save(groupMeeting).toDto();
    }

    @Override
    public GroupMeetingDto updateGroupMeeting(CreateMeetingGroupDto dto, UUID groupId, UUID meetingId) {
        GroupMeetings groupMeeting = groupMeetingRepository.findById(meetingId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la reunión de grupo")
        );
        validateGroupOwnership(groupId, groupMeeting);
        groupMeeting.fromDto(dto);
        GroupMeetingTypes type = groupMeetingTypeReader.getGroupMeetingTypeById(dto.getGroupMeetingTypeId());
        groupMeeting.setGroupMeetingType(type);
        return groupMeetingRepository.save(groupMeeting).toDto();
    }

    @Override
    public void deleteGroupMeeting(UUID groupId, UUID meetingId) {
        GroupMeetings groupMeeting = groupMeetingRepository.findById(meetingId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la reunión de grupo")
        );
        validateGroupOwnership(groupId, groupMeeting);
        groupMeetingRepository.delete(groupMeeting);
    }

    private void validateGroupOwnership(UUID groupId, GroupMeetings groupMeeting) {
        if (!groupMeeting.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("La reunión no pertenece al grupo especificado");
        }
    }

    @Override
    public Page<GroupMeetingDto> getGroupMeetingByGroupId(UUID groupId, Pageable pageable) {
        return groupMeetingRepository.findAllByGroupId(groupId, pageable).map(GroupMeetings::toDto);
    }

    @Override
    public GroupMeetingDetailedDto getGroupMeetingById(UUID groupId, UUID meetingId) {
        GroupMeetings groupMeeting = groupMeetingRepository.findById(meetingId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la reunión de grupo")
        );
        GroupMeetingDetailedDto attendanceDto = new GroupMeetingDetailedDto();
        attendanceDto.fromDto(groupMeeting.toDto());
        Long countPresent = attendanceService.countByEventIdWithDefaults(
                meetingId,
                AttendanceEventType.GROUP_MEETING,
                AttendanceStatus.PRESENT
        );
        Long countAbsent = attendanceService.countByEventIdWithDefaults(
                meetingId,
                AttendanceEventType.GROUP_MEETING,
                AttendanceStatus.ABSENT
        );
        Long countTotal = countPresent + countAbsent;
        attendanceDto.setTotalAttendance(countTotal);
        attendanceDto.setPresentCount(countPresent);
        attendanceDto.setAbsentCount(countAbsent);
        return attendanceDto;
    }

    @Override
    public Page<AttendanceDto> getGroupMeetingAttendance(UUID groupId, UUID meetingId, Pageable pageable) {
        return attendanceService.getAttendanceByEventId(
                meetingId,
                pageable,
                AttendanceEventType.GROUP_MEETING
        );
    }
}
