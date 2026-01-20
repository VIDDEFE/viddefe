package com.viddefe.viddefe_api.worship_meetings.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/meetings/{meetingId}/ministry-functions")
@RequiredArgsConstructor
public class MinistryFunctionController {

    private final MinistryFunctionService ministryFunctionService;

    @PostMapping
    public ResponseEntity<ApiResponse<MinistryFunctionDto>> create(
            @PathVariable UUID meetingId,
            @Valid @RequestBody CreateMinistryFunctionDto dto,
            @RequestParam TopologyEventType eventType
    ) {
        MinistryFunctionDto response =
                ministryFunctionService.create(dto, meetingId, eventType);

        return new ResponseEntity<>(
                ApiResponse.created(response),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MinistryFunctionDto>>> findByMeeting(
            @PathVariable UUID meetingId,
            @RequestParam TopologyEventType eventType

    ) {
        List<MinistryFunctionDto> response =
                ministryFunctionService.findByEventId(meetingId, eventType);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MinistryFunctionDto>> update(
            @PathVariable UUID meetingId,
            @PathVariable UUID id,
            @Valid @RequestBody CreateMinistryFunctionDto dto,
            @RequestParam TopologyEventType eventType
    ) {
        // meetingId se mantiene para semántica y validación de ownership
        MinistryFunctionDto response =
                ministryFunctionService.update(id, dto, eventType);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id
    ) {
        ministryFunctionService.delete(id);
        return new ResponseEntity<>(
                ApiResponse.noContent(),
                HttpStatus.NO_CONTENT
        );
    }
}
