package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.RequestPath;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}/meetings")
@RequiredArgsConstructor
public class GroupMeetingsController {
    private final GroupMeetingService groupMeetingService;
    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupMeetingDto>> createGroupMeeting(
            @Valid @RequestBody CreateMeetingGroupDto dto,
            @PathVariable UUID groupId
            ){
        GroupMeetingDto response = groupMeetingService.createGroupMeeting(dto, groupId);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GroupMeetingDto>>> getGroupMeeting(
            @PathVariable UUID groupId,
            Pageable pageable
    ){
        Page<GroupMeetingDto> response = groupMeetingService.getGroupMeetingByGroupId(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<GroupMeetingDto>> updateGroupMeeting(
            @PathVariable UUID groupId,
            @PathVariable UUID meetingId,
            @Valid @RequestBody CreateMeetingGroupDto dto
    ){
        GroupMeetingDto response = groupMeetingService.updateGroupMeeting(dto, groupId, meetingId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroupMeeting(
            @PathVariable UUID groupId,
            @PathVariable UUID meetingId
    ){
        groupMeetingService.deleteGroupMeeting(groupId, meetingId);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{meetingId}/attendance")
    public ResponseEntity<ApiResponse<GroupMeetingAttendanceDto>> getGroupMeetingAttendance(
            @PathVariable UUID groupId,
            @PathVariable UUID meetingId,
            Pageable pageable
    ){
        GroupMeetingAttendanceDto response = groupMeetingService.getGroupMeetingAttendance(groupId, meetingId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceDto>> recordAttendance(
            @RequestBody @Valid CreateAttendanceDto dto
    ) {
        AttendanceDto response = attendanceService.updateAttendance(dto, AttendanceEventType.GROUP_MEETING);
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

}
