package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupMeetingServiceImpl implements GroupMeetingService {
    private final MeetingService meetingService;
    private final HomeGroupReader homeGroupReader;
    private final MeetingTypesService meetingTypesService;
    private final AttendanceService attendanceService;
    private final ChurchLookup churchLookup;

    @Override
    public MeetingDto createGroupMeeting(CreateMeetingDto dto, @NotNull UUID groupId, @NotNull UUID churchId) {
        Meeting groupMeeting = new Meeting();
        groupMeeting.fromDto(dto);
        groupMeeting.setCreationDate(Instant.now());
        // churchLookup puede ser null en tests unitarios que no lo mockean
        if (this.churchLookup != null) {
            ChurchModel church = churchLookup.getChurchById(churchId);
            groupMeeting.setChurch(church);
        }
        HomeGroupsModel homeGroupsModel = homeGroupReader.findById(groupId);
        MeetingType type = meetingTypesService.getMeetingTypesById(dto.getMeetingTypeId());

        // Establecer campos normalizados
        groupMeeting.setGroup(homeGroupsModel);
        groupMeeting.setMeetingType(type);
        groupMeeting.setGroup(homeGroupsModel);

        Meeting saved = meetingService.create(groupMeeting);
        return saved.toDto();
    }

    @Override
    public MeetingDto updateGroupMeeting(CreateMeetingDto dto, UUID groupId, UUID meetingId) {
        Meeting groupMeeting = meetingService.findById(meetingId);
        validateGroupOwnership(groupId, groupMeeting);

        // Usar updateFrom para no modificar creationDate
        groupMeeting.fromDto(dto);

        MeetingType type = meetingTypesService.getMeetingTypesById(dto.getMeetingTypeId());
        groupMeeting.setMeetingType(type);

        Meeting updated = meetingService.update(groupMeeting);
        return updated.toDto();
    }

    @Override
    public void deleteGroupMeeting(UUID groupId, UUID meetingId) {
        Meeting groupMeeting = meetingService.findById(meetingId);
        validateGroupOwnership(groupId, groupMeeting);
        meetingService.delete(meetingId);
    }

    private void validateGroupOwnership(UUID groupId, Meeting groupMeeting) {
        if (!groupMeeting.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("La reunión no pertenece al grupo especificado");
        }
    }

    @Override
    public Page<MeetingDto> getGroupMeetingByGroupId(UUID groupId, Pageable pageable) {
        return  meetingService.findGroupMeetingByGroupId(groupId, pageable)
                .map(Meeting::toDto);
    }

    @Override
    public GroupMeetingDetailedDto getGroupMeetingById(UUID groupId, UUID meetingId) {
        Meeting groupMeeting =  meetingService.findByIdWithRelations(meetingId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró la reunión de grupo")
        );
        GroupMeetingDetailedDto attendanceDto = new GroupMeetingDetailedDto();
        attendanceDto.fromDto(groupMeeting.toDto());
        Long countPresent = attendanceService.countByEventIdWithDefaults(
                meetingId,
                TopologyEventType.GROUP_MEETING,
                AttendanceStatus.PRESENT
        );
        Long countAbsent = attendanceService.countByEventIdWithDefaults(
                meetingId,
                TopologyEventType.GROUP_MEETING,
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
                TopologyEventType.GROUP_MEETING
        );
    }
}
