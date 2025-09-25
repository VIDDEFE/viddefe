package com.viddefe.viddefe_api.people.service;

import com.viddefe.viddefe_api.catalogs.Model.StatesModel;
import com.viddefe.viddefe_api.catalogs.Services.StatesCitiesService;
import com.viddefe.viddefe_api.churches.ChurchModel;
import com.viddefe.viddefe_api.churches.ChurchService;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.model.PeopleModel;
import com.viddefe.viddefe_api.people.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.repository.PeopleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeopleService {
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;
    private final ChurchService churchService;
    private final StatesCitiesService statesCitiesService;

    public PeopleModel createPeople(@Valid PeopleDTO dto){
        PeopleModel peopleModel = new PeopleModel().fromDto(dto);
        PeopleTypeModel peopleType = peopleTypeService.getPeopleTypeById(dto.getTypePersonId());
        StatesModel state = statesCitiesService.foundStatesById(dto.getStateId());
        ChurchModel church = dto.getChurchId() != null ? churchService.getChurchById(dto.getChurchId()) : null;
        peopleModel.setTypePerson(peopleType);
        peopleModel.setChurch(church);
        peopleModel.setState(state);
        return peopleRepository.save(peopleModel);
    }

    public Page<PeopleModel> getAllPeople(Pageable pageable){
        return peopleRepository.findAll(pageable);
    }

    public PeopleModel updatePeople(@Valid PeopleDTO dto, @NotNull UUID uuid){
        PeopleModel peopleModel = peopleRepository.findById(uuid)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("People not found"));
        peopleModel.fromDto(dto);
        return peopleRepository.save(peopleModel);
    }

    public void deletePeople(@NotNull UUID uuid){
        peopleRepository.deleteById(uuid);
    }
}
