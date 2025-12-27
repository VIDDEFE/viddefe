package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupService;
import com.viddefe.viddefe_api.homeGroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupsRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateHomeGroupsDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.HomeGroupsDTO;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeGroupServiceImpl implements HomeGroupService {

    private final HomeGroupsRepository homeGroupsRepository;
    private final ChurchLookup churchLookup;
    private final PeopleReader peopleReader;
    private final StrategyReader strategyReader;

    @Override
    public HomeGroupsDTO createHomeGroup(CreateHomeGroupsDto dto, UUID churchId) {
        StrategiesModel strategy = strategyReader.findById(dto.getStrategyId());

        HomeGroupsModel homeGroup = new HomeGroupsModel();
        homeGroup.fromDto(dto);
        homeGroup.setStrategy(strategy);
        ChurchModel church = churchLookup.getChurchById(churchId);
        PeopleModel leader = peopleReader.getPeopleById(dto.getLeaderId());
        homeGroup.setChurch(church);
        homeGroup.setLeader(leader);
        return homeGroupsRepository.save(homeGroup).toDto();
    }

    @Override
    public HomeGroupsDTO updateHomeGroup(CreateHomeGroupsDto dto, UUID id) {
        HomeGroupsModel homeGroup = homeGroupsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));

        StrategiesModel strategy = strategyReader.findById(dto.getStrategyId());

        homeGroup.fromDto(dto);
        homeGroup.setStrategy(strategy);

        return homeGroupsRepository.save(homeGroup).toDto();
    }

    @Override
    public HomeGroupsDTO getHomeGroupById(UUID id) {
        return homeGroupsRepository.findById(id)
                .map(HomeGroupsModel::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));
    }

    @Override
    public Page<HomeGroupsDTO> getHomeGroups(Pageable pageable, UUID churchId) {
        return homeGroupsRepository
                .findAllByChurchId(churchId, pageable)
                .map(HomeGroupsModel::toDto);
    }

    @Override
    public Void deleteHomeGroup(UUID id) {
        if (!homeGroupsRepository.existsById(id)) {
            throw new EntityNotFoundException("Grupo no encontrado");
        }
        homeGroupsRepository.deleteById(id);
        return null;
    }
}