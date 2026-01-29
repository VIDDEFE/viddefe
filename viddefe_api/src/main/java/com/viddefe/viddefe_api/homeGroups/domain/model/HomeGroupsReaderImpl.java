package com.viddefe.viddefe_api.homeGroups.domain.model;

import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupsRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.EntityIdWithTotalPeople;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
        List<EntityIdWithTotalPeople> result = homeGroupsRepository.findAllIdsWithTotalPeopleByChurchId(churchId);
        return result != null ? result : List.of();
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
