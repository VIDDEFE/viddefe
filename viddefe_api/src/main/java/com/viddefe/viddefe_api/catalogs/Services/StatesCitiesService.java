package com.viddefe.viddefe_api.catalogs.Services;

import com.viddefe.viddefe_api.catalogs.Model.CitiesModel;
import com.viddefe.viddefe_api.catalogs.Model.StatesModel;
import com.viddefe.viddefe_api.catalogs.Repository.CitiesRepository;
import com.viddefe.viddefe_api.catalogs.Repository.StatesRepository;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatesCitiesService {
    private final CitiesRepository citiesRepository;
    private final StatesRepository statesRepository;

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
