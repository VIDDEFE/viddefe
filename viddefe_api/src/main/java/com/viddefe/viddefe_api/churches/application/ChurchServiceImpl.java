package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChurchServiceImpl implements ChurchService {
    private final ChurchRepository churchRepository;
    private final StatesCitiesService statesCitiesService;

    @Override
    public ChurchModel addChurch(ChurchDTO dto) {
        ChurchModel churchModel = ChurchModel.fromDto(dto);
        CitiesModel statesModel = statesCitiesService.foundCitiesById(dto.getCityId());
        churchModel.setCity(statesModel);
        return churchRepository.save(churchModel);
    }

    @Override
    public Page<ChurchModel> getChurches(Pageable pageable) {
        return churchRepository.findAll(pageable);
    }

    @Override
    public ChurchModel getChurchById(java.util.UUID id) {
        return churchRepository.findById(id).orElseThrow(() -> new RuntimeException("Iglesia no encontrado: " + id));
    }
}
