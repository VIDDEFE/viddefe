package com.viddefe.viddefe_api.worship.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship.contracts.TypesWorshipMeetingReader;
import com.viddefe.viddefe_api.worship.contracts.WorshipService;
import com.viddefe.viddefe_api.worship.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship.domain.models.WorshipModel;
import com.viddefe.viddefe_api.worship.domain.repository.WorshipRepository;
import com.viddefe.viddefe_api.worship.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship.infrastructure.dto.WorshipDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorshipServicesImpl implements WorshipService {

    private final WorshipRepository worshipRepository;
    private final VerifyWorshipMeetingConflict verifyWorshipMeetingConflict;
    private final TypesWorshipMeetingReader typesWorshipMeetingReader;
    private final ChurchLookup churchLookup;

    @Override
    public WorshipDto createWorship(CreateWorshipDto dto, @NotNull UUID churchId) {

        verifyWorshipMeetingConflict.verifyHourOfWorshipMeeting(dto, churchId, null);

        WorshipMeetingTypes worshipMeetingTypes =
                typesWorshipMeetingReader.getWorshipMeetingTypesById(dto.getWorshipTypeId());

        ChurchModel church = churchLookup.getChurchById(churchId);

        WorshipModel worshipModel = new WorshipModel().fromDto(dto);
        worshipModel.setCreationDate(new Date());
        worshipModel.setWorshipType(worshipMeetingTypes);
        worshipModel.setChurch(church);

        return worshipRepository.save(worshipModel).toDto();
    }

    @Override
    public WorshipDto getWorshipById(UUID id) {
        WorshipModel worship = worshipRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Worship service not found with id: " + id)
                );

        return worship.toDto();
    }

    @Override
    public Page<WorshipDto> getAllWorships(Pageable pageable, @NotNull UUID churchId) {

        // asumimos que el repo ya filtra por church
        return worshipRepository.findAllByChurchId(churchId, pageable)
                .map(WorshipModel::toDto);
    }

    @Override
    public WorshipDto updateWorship(UUID id, CreateWorshipDto dto, @NotNull UUID churchId) {

        verifyWorshipMeetingConflict.verifyHourOfWorshipMeeting(dto, churchId, id);

        WorshipModel worship = worshipRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Worship service not found with id: " + id)
                );

        // solo actualizamos lo que realmente puede cambiar
        worship.fromDto(dto);

        if (dto.getWorshipTypeId() != null) {
            WorshipMeetingTypes type =
                    typesWorshipMeetingReader.getWorshipMeetingTypesById(dto.getWorshipTypeId());
            worship.setWorshipType(type);
        }

        return worshipRepository.save(worship).toDto();
    }

    @Override
    public void deleteWorship(UUID id) {

        WorshipModel worship = worshipRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Worship service not found with id: " + id)
                );

        worshipRepository.delete(worship);
    }

    @Override
    public List<WorshipMeetingTypes> getAllWorshipMeetingTypes() {
        return typesWorshipMeetingReader.getAllWorshipMeetingTypes();
    }
}
