package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchPastorRepository;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchServiceImpl implements ChurchService {
    private final ChurchRepository churchRepository;
    private final StatesCitiesService statesCitiesService;
    private final ChurchPastorService churchPastorService;
    /**
     * Método para agregar una nueva iglesia.
     *
     * @param dto               Objeto DTO que contiene los datos de la iglesia a agregar. En el dto hay
     *                          un campo pastorId para asignar el pastor a la iglesia.
     * @param creatorPastorId   UUID del pastor que crea la iglesia, este se asigna a la iglesia en caso de que
     *                          no se haya especificado el pastor en el dto.
     * @return                  Objeto ChurchResDto que representa la iglesia agregada.
     */
    @Override
    public ChurchResDto addChurch(ChurchDTO dto, UUID creatorPastorId) {
        ChurchModel churchModel = ChurchModel.fromDto(dto);
        UUID pastorId = dto.getPastorId() != null ? dto.getPastorId() : creatorPastorId;
        CitiesModel statesModel = statesCitiesService.foundCitiesById(dto.getCityId());
        churchModel.setCity(statesModel);
        churchModel = churchRepository.save(churchModel);
        churchPastorService.addPastorToChurch(pastorId, churchModel);
        return churchModel.toDto();
    }

    @Override
    public Page<ChurchResDto> getChurches(Pageable pageable) {
        Page<ChurchModel> page = churchRepository.findAll(pageable);

        List<ChurchResDto> content = page.getContent()
                .stream()
                .map(ChurchModel::toDto)
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());

    }

    @Override
    public ChurchModel getChurchById(java.util.UUID id) {
        return churchRepository.findById(id).orElseThrow(() -> new RuntimeException("Iglesia no encontrado: " + id));
    }
}
