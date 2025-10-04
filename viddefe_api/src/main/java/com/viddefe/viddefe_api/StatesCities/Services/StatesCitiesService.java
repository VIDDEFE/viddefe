package com.viddefe.viddefe_api.StatesCities.Services;

import com.viddefe.viddefe_api.StatesCities.Dto.StatesDto;
import com.viddefe.viddefe_api.StatesCities.Model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.Model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.Repository.CitiesRepository;
import com.viddefe.viddefe_api.StatesCities.Repository.StatesRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatesCitiesService {
    private final CitiesRepository citiesRepository;
    private final StatesRepository statesRepository;

    public List<StatesModel> getAllStates(){
        return statesRepository.findAll();
    }

    public List<CitiesModel> getAllCitiesByState(Long stateId){
        StatesModel state = statesRepository.findById(stateId).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("States not found")
        );
        return state.getCities();
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
