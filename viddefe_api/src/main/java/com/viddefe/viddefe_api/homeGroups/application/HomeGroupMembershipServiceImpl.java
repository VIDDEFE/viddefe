package com.viddefe.viddefe_api.homegroups.application;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.viddefe.viddefe_api.homegroups.contracts.HomeGroupMemberShipService;
import com.viddefe.viddefe_api.homegroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.homegroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homegroups.domain.model.HomeGroupsPeopleMembers;
import com.viddefe.viddefe_api.homegroups.domain.model.serializable.HomeGroupPeopleMembersId;
import com.viddefe.viddefe_api.homegroups.domain.repository.HomeGroupMembersRepository;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HomeGroupMembershipServiceImpl implements HomeGroupMemberShipService {
    private final HomeGroupMembersRepository homeGroupMembersRepository;
    private final HomeGroupReader homeGroupReader;
    private final PeopleReader peopleReader;
    private void verifyMembershipExistence(@NonNull HomeGroupPeopleMembersId membershipId) {
        boolean exists = homeGroupMembersRepository.existsById(membershipId);
        if (!exists) return;
        throw new EntityNotFoundException("Miembro ya existente");
    }

    @Override
    public List<UUID> getMemberIdsInHomeGroup(UUID homeGroupId) {
        return homeGroupMembersRepository.findMemberIdsByHomeGroupId(homeGroupId);
    }

    @Override
    public PeopleResDto addMemberToHomeGroup(UUID homeGroupId, UUID peopleId) {
        HomeGroupsModel homeGroup = homeGroupReader.findById(homeGroupId);
        PeopleModel person = peopleReader.getPeopleById(peopleId);
        HomeGroupPeopleMembersId memberId = HomeGroupPeopleMembersId
                .builder()
                .homeGroupId(homeGroupId)
                .peopleId(peopleId)
                .build();
        verifyMembershipExistence(memberId);
        HomeGroupsPeopleMembers memberShip = HomeGroupsPeopleMembers.builder()
                .homeGroupPeopleMembersId(memberId)
                .homeGroup(homeGroup)
                .people(person)
                .build();
        homeGroupMembersRepository.save(memberShip);
        return person.toDto();
    }

    @Override
    public void removeMemberFromHomeGroup(UUID homeGroupId, UUID peopleId) {
        HomeGroupPeopleMembersId memberId = HomeGroupPeopleMembersId
                .builder()
                .homeGroupId(homeGroupId)
                .peopleId(peopleId)
                .build();
        if (!homeGroupMembersRepository.existsById(memberId)) {
            throw  new EntityNotFoundException("Miembro no encontrado en el grupo");
        }
        homeGroupMembersRepository.deleteById(memberId);
    }

    @Override
    public Page<PeopleResDto> getMembersInHomeGroup(UUID homeGroupId, Pageable pageable) {
        if(pageable==null || pageable.isUnpaged()){
            throw new IllegalArgumentException("El paginado no puede ser nulo");
        }
        return homeGroupMembersRepository.findMembersByHomeGroupId(homeGroupId, pageable)
                .map(PeopleModel::toDto);
    }

}
