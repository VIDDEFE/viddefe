package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupService;
import com.viddefe.viddefe_api.homeGroups.contracts.RolesStrategiesService;
import com.viddefe.viddefe_api.homeGroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupsRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.*;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeGroupServiceImpl implements HomeGroupService {

    private final HomeGroupsRepository homeGroupsRepository;
    private final ChurchLookup churchLookup;
    private final PeopleReader peopleReader;
    private final StrategyReader strategyReader;
    private final RolesStrategiesService rolesStrategiesService;

    /**
     * Valida si una persona ya es líder de otro grupo.
     * @param personId ID de la persona a validar.
     * @throws IllegalArgumentException si la persona ya es líder de otro grupo.
     */
    private void validateIfPersonIsLeaderInOtherHomeGroup(UUID personId, UUID groupId) {
        Optional<HomeGroupsModel> homeGroup = homeGroupsRepository.findByLeaderId(personId);
        if (homeGroup.isPresent() && homeGroup.get().getId() != groupId) {
            throw new IllegalArgumentException("La persona ya es líder de otro grupo");
        }
    }

    @Override
    public HomeGroupsDTO createHomeGroup(CreateHomeGroupsDto dto, UUID churchId) {
        validateIfPersonIsLeaderInOtherHomeGroup(dto.getLeaderId(),null);
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
        validateIfPersonIsLeaderInOtherHomeGroup(dto.getLeaderId(),id);
        StrategiesModel strategy = strategyReader.findById(dto.getStrategyId());

        homeGroup.fromDto(dto);
        homeGroup.setStrategy(strategy);

        return homeGroupsRepository.save(homeGroup).toDto();
    }

    @Override
    public HomeGroupsDetailDto getHomeGroupById(UUID id) {
        // Usa findWithRelationsById para evitar N+1 al acceder a leader, strategy
        HomeGroupsDTO homeDto = homeGroupsRepository.findWithRelationsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado")).toDto();
        List<RolesStrategiesWithPeopleDto> hierarchy = rolesStrategiesService.getTreeRolesWithPeople(homeDto.getStrategy().getId());
        HomeGroupsDetailDto dto = new HomeGroupsDetailDto();
        dto.setHomeGroup(homeDto);
        dto.setStrategy(homeDto.getStrategy());
        dto.setHierarchy(hierarchy);
        return dto;
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

    @Override
    public List<HomeGroupsDTO> getHomeGroupsByPositionInMap(UUID churchId, BigDecimal southLat, BigDecimal westLng, BigDecimal northLat, BigDecimal eastLng) {
        double latDiff = northLat.subtract(southLat).doubleValue();
        double lngDiff = eastLng.subtract(westLng).doubleValue();

        if (latDiff > 0.6 || lngDiff > 0.6) {
            throw new IllegalArgumentException("Zoom too large");
        }
        return homeGroupsRepository.findByChurchIdInBoundingBox(
                        southLat,
                        northLat,
                        westLng,
                        eastLng,
                        churchId
                )
                .stream()
                .map(HomeGroupsModel::toDto)
                .toList();
    }

    @Override
    public HomeGroupsDetailDto getHomeGroupByIntegrantId(UUID leaderId) {
        HomeGroupsDTO homeGroupsDTO = homeGroupsRepository.getHomeGroupByIntegrantId(leaderId)
                .orElseThrow(() -> new EntityNotFoundException("No pertence a ningún grupo"))
                .toDto();
        List<RolesStrategiesWithPeopleDto> hierarchy = rolesStrategiesService.getTreeRolesWithPeople(homeGroupsDTO.getStrategy().getId());
        HomeGroupsDetailDto dto = new HomeGroupsDetailDto();
        dto.setHomeGroup(homeGroupsDTO);
        dto.setStrategy(homeGroupsDTO.getStrategy());
        dto.setHierarchy(hierarchy);
        return dto;
    }
}