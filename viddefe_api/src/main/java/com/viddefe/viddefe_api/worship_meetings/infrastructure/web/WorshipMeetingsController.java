package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/worship/meeting")
@RequiredArgsConstructor
public class WorshipMeetingsController {
    private final WorshipService worshipService;
    private final AttendanceService attendanceService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<WorshipDto>> createWorshipMeeting(
            @RequestBody @Valid CreateWorshipDto dto,
            @CookieValue("access_token") String accessToken
    ) {
        // Implementation goes here
        UUID churchId = jwtUtil.getChurchId(accessToken);
        WorshipDto response = worshipService.createWorship(dto,churchId);
        return ResponseEntity.ok(ApiResponse.created(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WorshipDto>>> getWorshipMeeting(
            @CookieValue("access_token") String accessToken,
            Pageable pageable
    ) {
        // Implementation goes here
        UUID churchId = jwtUtil.getChurchId(accessToken);
        Page<WorshipDto> response = worshipService.getAllWorships(pageable, churchId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WorshipDetailedDto>> getWorshipMeetingById(
            @PathVariable UUID id
    ) {
        // Implementation goes here
        WorshipDetailedDto response = worshipService.getWorshipById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WorshipDto>> updateWorshipMeeting(
            @PathVariable UUID id,
            @RequestBody @Valid CreateWorshipDto dto,
            @CookieValue("access_token") String accessToken
    ) {
        // Implementation goes here
        UUID churchId = jwtUtil.getChurchId(accessToken);
        WorshipDto response = worshipService.updateWorship(id, dto, churchId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWorshipMeeting(
            @PathVariable UUID id
    ) {
        // Implementation goes here
        worshipService.deleteWorship(id);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceDto>> recordAttendance(
            @RequestBody @Valid CreateAttendanceDto dto
            ) {
        AttendanceDto response = attendanceService.updateAttendance(dto, AttendanceEventType.TEMPLE_WORHSIP);
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

    @GetMapping("/{id}/attendance")
    public ResponseEntity<ApiResponse<Page<AttendanceDto>>> getAttendance(
            @PathVariable UUID id,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        attendanceService.getAttendanceByEventId(id, pageable, AttendanceEventType.TEMPLE_WORHSIP)
                )
        );
    }

}
