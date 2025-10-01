package com.viddefe.viddefe_api.StatesCities.Controller;

import com.viddefe.viddefe_api.StatesCities.Model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.Model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.Services.StatesCitiesService;
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
    public ResponseEntity<ApiResponse<List<StatesModel>>> getStates(){
        List<StatesModel> states = statesCitiesService.getAllStates();
        return new ResponseEntity<>(ApiResponse.success("List of States",states), HttpStatus.OK);
    }
    @GetMapping("/{id}/cities")
    public ResponseEntity<ApiResponse<List<CitiesModel>>> getStatesCities(@PathVariable Long id){
        List<CitiesModel> cities = statesCitiesService.getAllCitiesByState(id);
        return new ResponseEntity<>(ApiResponse.success("Cities from state"+id,cities), HttpStatus.OK);
    }
}
