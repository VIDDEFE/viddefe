package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import lombok.NonNull;
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
     * Crear una iglesia raíz.
     */
    @Override
    public ChurchResDto addChurch(ChurchDTO dto, UUID creatorPastorId) {
        UUID pastorId = resolvePastorId(dto, creatorPastorId);

        ChurchModel church = createAndPersistChurch(
                dto,
                pastorId,
                null
        );

        return church.toDto();
    }

    /**
     * Crear una iglesia hija.
     */
    @Override
    public ChurchResDto addChildChurch(UUID parentChurchId, ChurchDTO dto, UUID creatorPastorId) {
        UUID pastorId = resolvePastorId(dto, creatorPastorId);

        ChurchModel parentChurch = churchRepository.getReferenceById(parentChurchId);

        ChurchModel church = createAndPersistChurch(
                dto,
                pastorId,
                parentChurch
        );

        return church.toDto();
    }

    @Override
    public Page<ChurchResDto> getChildrenChurches(Pageable pageable, UUID churchId) {
        return churchRepository.findByParentChurchId(churchId, pageable)
                .map(ChurchModel::toDto);
    }

    /**
     * Listar iglesias paginadas.
     */
    public Page<ChurchResDto> getChurches(Pageable pageable, @NonNull UUID pastorId) {
        Page<ChurchModel> page = churchRepository.findAll(pageable);

        List<ChurchResDto> content = page.getContent()
                .stream()
                .map(ChurchModel::toDto)
                .toList();

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    /**
     * Obtener iglesia por ID.
     */
    @Override
    public ChurchModel getChurchById(UUID id) {
        return churchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Iglesia no encontrada: " + id));
    }

    /* ===========================
       Métodos privados
       =========================== */

    /**
     * Resuelve el pastor que quedará asignado a la iglesia.
     */
    private UUID resolvePastorId(ChurchDTO dto, UUID creatorPastorId) {
        return dto.getPastorId() != null
                ? dto.getPastorId()
                : creatorPastorId;
    }

    /**
     * Construye, persiste y asigna el pastor a una iglesia.
     */
    private ChurchModel createAndPersistChurch(
            ChurchDTO dto,
            UUID pastorId,
            ChurchModel parentChurch
    ) {
        ChurchModel church = ChurchModel.fromDto(dto);

        if (parentChurch != null) {
            church.setParentChurch(parentChurch);
        }

        CitiesModel city = statesCitiesService.foundCitiesById(dto.getCityId());
        church.setCity(city);

        church = churchRepository.save(church);

        churchPastorService.addPastorToChurch(pastorId, church);

        return church;
    }
}
