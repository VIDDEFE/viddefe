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
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
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
    private final MeetingService meetingService;
    private final HomeGroupReader homeGroupReader;
    private final GroupMeetingTypeReader groupMeetingTypeReader;
    private final AttendanceService attendanceService;
    private final GroupMeetingRepository groupMeetingsRepository;

    @Override
    public GroupMeetingDto createGroupMeeting(CreateMeetingGroupDto dto,@NotNull UUID groupId) {
        GroupMeetings groupMeeting = new GroupMeetings();
        groupMeeting.fromDto(dto);

        HomeGroupsModel homeGroupsModel = homeGroupReader.findById(groupId);
        GroupMeetingTypes type = groupMeetingTypeReader.getGroupMeetingTypeById(dto.getGroupMeetingTypeId());

        // Establecer campos normalizados
        groupMeeting.setContextId(groupId);
        groupMeeting.setTypeId(type.getId());
        groupMeeting.setGroup(homeGroupsModel);
        groupMeeting.setGroupMeetingType(type);

        GroupMeetings saved = (GroupMeetings) meetingService.create(groupMeeting);
        return saved.toDto();
    }

    @Override
    public GroupMeetingDto updateGroupMeeting(CreateMeetingGroupDto dto, UUID groupId, UUID meetingId) {
        GroupMeetings groupMeeting = (GroupMeetings) meetingService.findById(meetingId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la reunión de grupo")
        );
        validateGroupOwnership(groupId, groupMeeting);

        // Usar updateFrom para no modificar creationDate
        groupMeeting.updateFrom(dto);

        GroupMeetingTypes type = groupMeetingTypeReader.getGroupMeetingTypeById(dto.getGroupMeetingTypeId());
        groupMeeting.setGroupMeetingType(type);
        groupMeeting.setTypeId(type.getId());

        GroupMeetings updated = (GroupMeetings) meetingService.update(groupMeeting);
        return updated.toDto();
    }

    @Override
    public void deleteGroupMeeting(UUID groupId, UUID meetingId) {
        GroupMeetings groupMeeting = (GroupMeetings) meetingService.findById(meetingId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la reunión de grupo")
        );
        validateGroupOwnership(groupId, groupMeeting);
        meetingService.delete(meetingId);
    }

    private void validateGroupOwnership(UUID groupId, GroupMeetings groupMeeting) {
        if (!groupMeeting.getContextId().equals(groupId)) {
            throw new IllegalArgumentException("La reunión no pertenece al grupo especificado");
        }
    }

    @Override
    public Page<GroupMeetingDto> getGroupMeetingByGroupId(UUID groupId, Pageable pageable) {
        return  groupMeetingsRepository.findByContextId(groupId, pageable)
                .map(meeting -> ((GroupMeetings) meeting).toDto());
    }

    @Override
    public GroupMeetingDetailedDto getGroupMeetingById(UUID groupId, UUID meetingId) {
        GroupMeetings groupMeeting = (GroupMeetings) meetingService.findByIdWithRelations(meetingId).orElseThrow(
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
    public Page<AttendanceDto> getGroupMeetingAttendance(UUID groupId, UUID meetingId, Pageable pageable) {
        return attendanceService.getAttendanceByEventId(
                meetingId,
                pageable,
                AttendanceEventType.GROUP_MEETING
        );
    }
}
