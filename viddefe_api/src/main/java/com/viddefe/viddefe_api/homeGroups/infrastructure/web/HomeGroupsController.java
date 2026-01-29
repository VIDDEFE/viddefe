package com.viddefe.viddefe_api.homeGroups.infrastructure.web;

import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.common.Components.JwtUtil;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupService;
import com.viddefe.viddefe_api.homeGroups.contracts.RolesPeopleStrategiesService;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.*;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class HomeGroupsController {
    private final HomeGroupService homeGroupService;
    private final RolesPeopleStrategiesService rolesPeopleStrategiesService;
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

    @GetMapping(value = "/nearby")
    public ResponseEntity<ApiResponse<List<HomeGroupsDTO>>> getChildChurchesByPositionInMap(
            @CookieValue("access_token") String accessToken,
            @RequestParam BigDecimal southLat,
            @RequestParam BigDecimal westLng,
            @RequestParam BigDecimal northLat,
            @RequestParam BigDecimal eastLng
    ){
        UUID churchId = jwtUtil.getChurchId(accessToken);

        List<HomeGroupsDTO> response = homeGroupService.getHomeGroupsByPositionInMap(
                churchId,
                southLat,
                westLng,
                northLat,
                eastLng
        );
        return new ResponseEntity<>(ApiResponse.ok(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHomeGroup(
            @PathVariable UUID id
    ) {
        homeGroupService.deleteHomeGroup(id);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    @PostMapping("/strategy/role/{roleId}/assign")
    public ResponseEntity<ApiResponse<Void>> assignRoleToGroupMember(
            @PathVariable UUID roleId,
            @RequestBody @Valid AssignPeopleToRoleDto dto
    ) {
        rolesPeopleStrategiesService.assignRoleToPeopleInStrategy(roleId, dto);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/strategy/role/{roleId}/remove")
    public ResponseEntity<ApiResponse<Void>> removeRoleFromGroupMember(
            @PathVariable UUID roleId,
            @RequestBody @Valid AssignPeopleToRoleDto dto
    ) {
        rolesPeopleStrategiesService.removeRoleFromPeopleInStrategy(roleId, dto);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<HomeGroupsDetailDto >> getMyHomeGroup(
            @CookieValue("access_token") String accessToken
    ) {
        UUID personId = jwtUtil.getPersonId(accessToken);
        HomeGroupsDetailDto myGroup = homeGroupService.getHomeGroupByIntegrantId(personId);
        return ResponseEntity.ok(ApiResponse.ok(myGroup));
    }

}