package com.viddefe.viddefe_api.statescities.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.statescities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.statescities.domain.model.StatesModel;
import com.viddefe.viddefe_api.statescities.domain.repository.CitiesRepository;
import com.viddefe.viddefe_api.statescities.domain.repository.StatesRepository;
import com.viddefe.viddefe_api.statescities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.statescities.infrastructure.dto.StatesDto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatesCitiesService {
    private final CitiesRepository citiesRepository;
    private final StatesRepository statesRepository;

    public List<StatesDto> getAllStates(){
        return statesRepository.findAll().stream().map(StatesModel::toDto).toList();
    }

    public List<CitiesDto> getAllCitiesByState(@NonNull Long stateId){
        StatesModel state = statesRepository.findById(stateId).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("States not found")
        );
        return state.getCities().stream().map(CitiesModel::toDto).toList();
    }

    public CitiesModel foundCitiesById(@NonNull Long id) {
        return citiesRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("Cities not found with id: " + id)
        );
    }

    public StatesModel foundStatesById(@NonNull Long id) {
        return statesRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("States not found with id: " + id)
        );
    }
}
