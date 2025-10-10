package com.viddefe.viddefe_api.churches;

import com.viddefe.viddefe_api.StatesCities.Model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.Model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.Services.StatesCitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class ChurchService {
    private final ChurchRepository churchRepository;
    private final StatesCitiesService statesCitiesService;

    public ChurchModel addChurch(ChurchDTO dto){
        ChurchModel churchModel = ChurchModel.fromDto(dto);
        CitiesModel statesModel = statesCitiesService.foundCitiesById(dto.getCitiesId());
        churchModel.setCity(statesModel);
        return churchRepository.save(churchModel);
    }

    public ChurchModel getChurchById(UUID id){
        return churchRepository.findById(id).orElseThrow(() -> new RuntimeException("Iglesia no encontrado: " + id));
    }
}
