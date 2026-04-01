package com.viddefe.viddefe_api.homegroups.domain.model;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.homegroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.homegroups.domain.repository.HomeGroupsRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.EntityIdWithTotalPeople;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeGroupsReaderImpl implements HomeGroupReader {
    private final HomeGroupsRepository homeGroupsRepository;
    @Override
    public HomeGroupsModel findById(UUID groupId) {
        return homeGroupsRepository.findById(groupId).orElseThrow(
                () -> new EntityNotFoundException("No se encontró el grupo")
        );
    }

    /**
     * @param churchId
     * @return
     */
    @Override
    public List<EntityIdWithTotalPeople> findAllIdsWithTotalPeopleByChurchId(UUID churchId) {
        return homeGroupsRepository.findAllIdsWithTotalPeopleByChurchId(churchId);
    }

    /**
     * @param groupId
     * @return
     */
    @Override
    public Long findTotalPeopleByGroupId(UUID groupId) {
       return homeGroupsRepository.findTotalPeopleByGroupId(groupId);
    }
}
