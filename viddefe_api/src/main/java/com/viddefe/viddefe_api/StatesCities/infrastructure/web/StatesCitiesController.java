package com.viddefe.viddefe_api.StatesCities.infrastructure.web;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/states")
@RequiredArgsConstructor
public class StatesCitiesController {
    private final StatesCitiesService statesCitiesService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<StatesDto>>> getStates(){
        List<StatesDto> states = statesCitiesService.getAllStates();
        return new ResponseEntity<>(ApiResponse.ok(states), HttpStatus.OK);
    }
    @GetMapping("/{id}/cities")
    public ResponseEntity<ApiResponse<List<CitiesDto>>> getStatesCities(@PathVariable Long id){
        List<CitiesDto> cities = statesCitiesService.getAllCitiesByState(id);
        return new ResponseEntity<>(ApiResponse.ok(cities), HttpStatus.OK);
    }
}
