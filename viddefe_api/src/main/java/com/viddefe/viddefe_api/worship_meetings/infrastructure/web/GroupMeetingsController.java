package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
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

    @GetMapping("/{meetingId}/attendance")
    public ResponseEntity<ApiResponse<Page<AttendanceDto>>> getGroupMeetingAttendance(
            @PathVariable UUID meetingId,
            Pageable pageable
    ){
        Page<AttendanceDto> response = attendanceService.getAttendanceByEventId(meetingId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
