package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchPastorService;
import com.viddefe.viddefe_api.churches.contracts.ChurchService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDetailedResDto;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.contracts.ChurchMembershipService;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchServiceImpl implements ChurchService {

    private final ChurchRepository churchRepository;
    private final StatesCitiesService statesCitiesService;
    private final ChurchPastorService churchPastorService;
    private final ChurchMembershipService churchMembershipService;

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
        ChurchPastor churchPastor = churchPastorService.addPastorToChurch(pastorId, church);
        ChurchResDto churchResDto = church.toDto();
        churchResDto.setPastor(churchPastor.getPastor().toDto());
        return churchResDto;
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

        ChurchPastor churchPastor = churchPastorService.addPastorToChurch(pastorId, church);

        ChurchResDto churchResDto = church.toDto();
        churchResDto.setPastor(churchPastor.getPastor().toDto());
        return churchResDto;
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
        ChurchPastor churchPastor = churchPastorService.changeChurchPastor(pastorId,church);
        ChurchResDto churchResDto = church.toDto();
        churchResDto.setPastor(churchPastor.getPastor().toDto());
        return churchResDto;
    }

    @Transactional
    @Override
    public void deleteChurch(UUID id) {
        ChurchModel church = churchRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Church not found"));

        PeopleModel pastor = churchPastorService.getPastorFromChurch(church);
        ChurchModel parentChurch = church.getParentChurch();
        churchPastorService.removePastorFromChurch(church);
        churchRepository.delete(church);
        if(parentChurch != null){
            churchMembershipService.transferToChurch(pastor, parentChurch);
        }
    }

    @Override
    public Page<ChurchResDto> getChildrenChurches(Pageable pageable, UUID churchId) {
        return churchRepository.findAllChurchesDtoByParentChurchId(churchId,pageable);
    }

    /**
     * Listar iglesias paginadas.
     */
    public Page<ChurchResDto> getChurches(Pageable pageable) {
        return churchRepository.findAllChurchesDtoByParentChurchId(null, pageable);
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
                church.getLatitude(),
                church.getLongitude(),
                church.getCity().toDto(),
                church.getCity().getStates().toDto(),
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

    private Pageable mapSort(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return pageable;
        }

        Sort newSort = Sort.unsorted();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            switch (property) {
                case "pastor" ->
                        newSort = newSort.and(
                                Sort.by(order.getDirection(), "p.lastName")
                        );

                case "department" ->
                        newSort = newSort.and(
                                Sort.by(order.getDirection(), "s.name")
                        );

                case "name" ->
                        newSort = newSort.and(
                                Sort.by(order.getDirection(), "c.name")
                        );

                default ->
                        throw new IllegalArgumentException(
                                "Ordenamiento no permitido: " + property
                        );
            }
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                newSort
        );
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
