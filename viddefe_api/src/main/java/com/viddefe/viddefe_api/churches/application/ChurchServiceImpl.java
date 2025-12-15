package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDetailedResDto;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ChurchResDto addChurch(ChurchDTO dto) {
        UUID pastorId = dto.getPastorId();
        ChurchModel church = new ChurchModel().fromDto(dto);
        church = createAndPersistChurch(
                church,
                null,
                dto.getCityId()
        );
        churchPastorService.addPastorToChurch(pastorId, church);
        return church.toDto();
    }

    /**
     * Crear una iglesia hija.
     */
    @Override
    @Transactional
    public ChurchResDto addChildChurch(
            UUID parentChurchId,
            ChurchDTO dto,
            UUID creatorPastorId
    ) {
        UUID pastorId = resolvePastorId(dto, creatorPastorId);

        ChurchModel parentChurch = churchRepository.getReferenceById(parentChurchId);
        ChurchModel church = new ChurchModel().fromDto(dto);
        church = createAndPersistChurch(church, parentChurch, dto.getCityId());

        churchPastorService.addPastorToChurch(pastorId, church);

        return church.toDto();
    }


    @Override
    @Transactional
    public ChurchResDto updateChurch(UUID id, ChurchDTO dto, UUID updaterPastorId) {
        ChurchModel church = churchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Iglesia no encontrada: " + id));

        dto.setId(id);
        UUID pastorId = resolvePastorId(dto, updaterPastorId);
        church.fromDto(dto);
        church = createAndPersistChurch(church, church.getParentChurch(), dto.getCityId());
        churchPastorService.changeChurchPastor(pastorId,church);
        return church.toDto();
    }

    @Transactional
    @Override
    public void deleteChurch(UUID id) {
        ChurchModel church = churchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Church not found"));
        churchPastorService.removePastorFromChurch(church);
        churchRepository.delete(church);
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
    public ChurchDetailedResDto getChurchById(UUID id) {
        ChurchModel church = churchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Iglesia no encontrada: " + id));
        PeopleModel pastor = churchPastorService.getPastorFromChurch(church);
        return new ChurchDetailedResDto(
                church.getId(),
                church.getName(),
                church.getLongitude(),
                church.getLatitude(),
                church.getCity().toDto(),
                church.getCity().getState().toDto(),
                pastor.toDto(),
                church.getFoundationDate(),
                church.getPhone(),
                church.getEmail()
        );
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
            ChurchModel childChurch,
            ChurchModel parentChurch,
            Long cityId
    ) {

        if (parentChurch != null) {
            parentChurch.addChildChurch(childChurch);
        }

        CitiesModel city = statesCitiesService.foundCitiesById(cityId);
        childChurch.setCity(city);

        childChurch = churchRepository.save(childChurch);
        return childChurch;
    }
}
