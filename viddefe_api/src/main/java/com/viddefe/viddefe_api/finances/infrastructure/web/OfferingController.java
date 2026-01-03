package com.viddefe.viddefe_api.finances.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.finances.contracts.OfferingService;
import com.viddefe.viddefe_api.finances.infrastructure.dto.CreateOfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/offerings")
@RequiredArgsConstructor
public class OfferingController {
    private final OfferingService offeringService;

    @PostMapping
    public ResponseEntity<ApiResponse<OfferingDto>> register(@Valid @RequestBody CreateOfferingDto dto) {
        OfferingDto offeringDto = offeringService.register(dto);
        return ResponseEntity.ok(ApiResponse.created(offeringDto));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<Page<OfferingDto>>> getAllByEventId(
            @PathVariable("eventId") String eventId,
            Pageable pageable
    ) {
        UUID id = UUID.fromString(eventId);
        Page<OfferingDto> result = offeringService.getAllByEventId(id, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<OfferingDto>> update(@Valid @RequestBody CreateOfferingDto dto, @RequestParam("id") String id) {
        OfferingDto offeringDto = offeringService.update(dto, java.util.UUID.fromString(id));
        return ResponseEntity.ok(ApiResponse.ok(offeringDto));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam("id") String id) {
        offeringService.delete(java.util.UUID.fromString(id));
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }
}
