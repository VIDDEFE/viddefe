package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingsTypesController {

    private final MeetingTypesService meetingTypesService;

    @GetMapping("/worship/types")
    public ResponseEntity<ApiResponse<List<MeetingTypeDto>>> getWorshipMeetingTypes() {
        List<MeetingTypeDto> response = meetingTypesService.getAllWorshipMeetingTypes();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/group/types")
    public ResponseEntity<ApiResponse<List<MeetingTypeDto>>> getGroupMeetingTypes() {
        List<MeetingTypeDto> response = meetingTypesService.getAllGroupMeetingTypes();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
