package com.viddefe.viddefe_api.StatesCities.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.domain.repository.CitiesRepository;
import com.viddefe.viddefe_api.StatesCities.domain.repository.StatesRepository;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatesCitiesService {
    private final CitiesRepository citiesRepository;
    private final StatesRepository statesRepository;

    public List<StatesDto> getAllStates(){
        return statesRepository.findAll().stream().map(StatesModel::toDto).toList();
    }

    public List<CitiesDto> getAllCitiesByState(Long stateId){
        StatesModel state = statesRepository.findById(stateId).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("States not found")
        );
        return state.getCities().stream().map(CitiesModel::toDto).toList();
    }

    public CitiesModel foundCitiesById(Long id) {
        return citiesRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("Cities not found with id: " + id)
        );
    }

    public StatesModel foundStatesById(Long id) {
        return statesRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("States not found with id: " + id)
        );
    }
}
