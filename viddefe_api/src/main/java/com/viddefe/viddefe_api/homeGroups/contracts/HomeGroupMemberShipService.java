package com.viddefe.viddefe_api.homegroups.contracts;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

public interface HomeGroupMemberShipService {
    PeopleResDto addMemberToHomeGroup(UUID homeGroupId, UUID peopleId);
    void removeMemberFromHomeGroup(UUID homeGroupId, UUID peopleId);
    Page<PeopleResDto> getMembersInHomeGroup(UUID homeGroupId, Pageable pageable);
    List<UUID> getMemberIdsInHomeGroup(UUID homeGroupId);
}
