package com.viddefe.viddefe_api.homeGroups.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.homeGroups.contracts.RolesStrategiesService;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateRolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolesStrategiesDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}/strategies/roles")
@RequiredArgsConstructor
public class RolesStrategiesController {

    private final RolesStrategiesService rolesStrategiesService;

    @PostMapping
    public ResponseEntity<ApiResponse<RolesStrategiesDto>> create(
            @Valid @RequestBody CreateRolesStrategiesDto dto,
            @PathVariable UUID groupId
    ) {
        RolesStrategiesDto response = rolesStrategiesService.create(dto, groupId);
        return new ResponseEntity<>(
                ApiResponse.created(response),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RolesStrategiesDto>> update(
            @Valid @RequestBody CreateRolesStrategiesDto dto,
            @PathVariable UUID groupId,
            @PathVariable UUID roleId
    ) {
        RolesStrategiesDto response =
                rolesStrategiesService.update(dto, groupId, roleId);

        return ResponseEntity.ok(
                ApiResponse.ok(response)
        );
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID roleId
    ) {
        rolesStrategiesService.delete(roleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.noContent());
    }
}
