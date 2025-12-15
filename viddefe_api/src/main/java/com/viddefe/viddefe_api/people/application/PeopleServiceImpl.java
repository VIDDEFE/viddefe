package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService {
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;
    private final ChurchLookup churchLookup;
    private final StatesCitiesService statesCitiesService;

    @Override
    public PeopleModel createPeople(PeopleDTO dto) {
        PeopleModel peopleModel = new PeopleModel().fromDto(dto);
        PeopleTypeModel peopleType = peopleTypeService.getPeopleTypeById(dto.getTypePersonId());
        StatesModel state = statesCitiesService.foundStatesById(dto.getStateId());
        ChurchModel church = dto.getChurchId() != null ? churchLookup.getChurchById(dto.getChurchId()) : null;
        peopleModel.setTypePerson(peopleType);
        peopleModel.setChurch(church);
        peopleModel.setState(state);
        return peopleRepository.save(peopleModel);
    }

    @Override
    public Page<PeopleDTO> getAllPeople(Pageable pageable) {
        return peopleRepository.findAll(pageable).map(PeopleModel::toDto);
    }

    @Override
    public PeopleDTO updatePeople(PeopleDTO dto, UUID id) {
        PeopleModel peopleModel = peopleRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("People not found"));
        peopleModel.fromDto(dto);
        return peopleRepository.save(peopleModel).toDto();
    }

    @Override
    public void deletePeople(UUID id) {
        peopleRepository.deleteById(id);
    }

    @Override
    public PeopleDTO getPeopleById(UUID id) {
        return peopleRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("People not found")).toDto();
    }
}

