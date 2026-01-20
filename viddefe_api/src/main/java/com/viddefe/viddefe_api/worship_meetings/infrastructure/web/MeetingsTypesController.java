package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionTypeDto;
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
    private final MinistryFunctionService ministryFunctionService;

    @GetMapping("/worship/types")
    public ResponseEntity<ApiResponse<List<MeetingTypeDto>>> getWorshipMeetingTypes() {
        List<MeetingTypeDto> response = meetingTypesService.
                getAllMeetingByTopologyEventTypes(TopologyEventType.TEMPLE_WORHSIP);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/group/types")
    public ResponseEntity<ApiResponse<List<MeetingTypeDto>>> getGroupMeetingTypes() {
        List<MeetingTypeDto> response = meetingTypesService.
                getAllMeetingByTopologyEventTypes(TopologyEventType.GROUP_MEETING);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/ministry-functions/roles")
    public ResponseEntity<ApiResponse<List<MinistryFunctionTypeDto>>> getAvailableRoles() {
        List<MinistryFunctionTypeDto> response =
                ministryFunctionService.getAllMinistryFunctionTypes();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
