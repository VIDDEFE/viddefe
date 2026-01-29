package com.viddefe.viddefe_api.homeGroups.infrastructure.web;

import com.viddefe.viddefe_api.common.response.ApiResponse;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupMemberShipService;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}/members")
@RequiredArgsConstructor
public class HomeGroupsMembersController {
    private final HomeGroupMemberShipService homeGroupMemberShipService;

    @PostMapping("/{peopleId}")
    public ResponseEntity<ApiResponse<PeopleResDto>> addMemberToHomeGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID peopleId
    ) {
        PeopleResDto addedMember = homeGroupMemberShipService.addMemberToHomeGroup(groupId, peopleId);
        return ResponseEntity.ok(ApiResponse.ok(addedMember));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PeopleResDto>>> getMembersInHomeGroup(
            @PathVariable UUID groupId,
            @PageableDefault(size = 20 ) Pageable pageable
    ) {
        Page<PeopleResDto> members = homeGroupMemberShipService.getMembersInHomeGroup(groupId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(members));
    }

    @DeleteMapping("/{peopleId}")
    public ResponseEntity<ApiResponse<Void>> removeMemberFromHomeGroup(
            @PathVariable UUID groupId,
            @PathVariable UUID peopleId
    ) {
        homeGroupMemberShipService.removeMemberFromHomeGroup(groupId, peopleId);
        return new ResponseEntity<>(ApiResponse.noContent(), HttpStatus.NO_CONTENT);
    }
}
