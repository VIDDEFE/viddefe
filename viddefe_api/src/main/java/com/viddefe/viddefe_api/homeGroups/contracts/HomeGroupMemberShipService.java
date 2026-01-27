package com.viddefe.viddefe_api.homeGroups.contracts;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface HomeGroupMemberShipService {
    PeopleResDto addMemberToHomeGroup(UUID homeGroupId, UUID peopleId);
    Void removeMemberFromHomeGroup(UUID homeGroupId, UUID peopleId);
    Page<PeopleResDto> getMembersInHomeGroup(UUID homeGroupId, Pageable pageable);
}
