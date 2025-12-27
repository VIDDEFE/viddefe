package com.viddefe.viddefe_api.homeGroups.infrastructure.web;

import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupService;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateHomeGroupsDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDTO;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDetailDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class HomeGroupsController {
    private final HomeGroupService homeGroupService;
    private final JwtUtil jwtUtil;
    @PostMapping
    public ResponseEntity<ApiResponse<HomeGroupsDTO>> createHomeGroup(
            @Valid @RequestBody CreateHomeGroupsDto dto,
            @CookieValue("access_token") String accessToken
    ) {
        UUID churchId = jwtUtil.getChurchId(accessToken);
        HomeGroupsDTO createdGroup = homeGroupService.createHomeGroup(dto,churchId);
        return new ResponseEntity<>(ApiResponse.created(createdGroup), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeGroupsDTO>> updateHomeGroup(
            @PathVariable UUID id,
            @Valid @RequestBody CreateHomeGroupsDto dto
    ) {
        HomeGroupsDTO updatedGroup = homeGroupService.updateHomeGroup(dto, id);
        return ResponseEntity.ok(ApiResponse.ok(updatedGroup));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeGroupsDetailDto>> getHomeGroupById(
            @PathVariable UUID id
    ) {
        HomeGroupsDetailDto homeGroup = homeGroupService.getHomeGroupById(id);
        return ResponseEntity.ok(ApiResponse.ok(homeGroup));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HomeGroupsDTO>>> getHomeGroups(
            Pageable pageable,
            @CookieValue("access_token") String accessToken
    ) {
        UUID churchId = jwtUtil.getChurchId(accessToken);
        Page<HomeGroupsDTO> groups = homeGroupService.getHomeGroups(pageable, churchId);
        return ResponseEntity.ok(ApiResponse.ok(groups));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHomeGroup(
            @PathVariable UUID id
    ) {
        homeGroupService.deleteHomeGroup(id);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

}
