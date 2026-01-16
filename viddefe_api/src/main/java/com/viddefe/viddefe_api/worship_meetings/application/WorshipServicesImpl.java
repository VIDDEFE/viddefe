package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.TypesWorshipMeetingReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingTypeEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.WorshipRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDetailedDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorshipServicesImpl implements WorshipService {

    private final WorshipRepository worshipRepository;
    private final MeetingService meetingService;
    private final VerifyWorshipMeetingConflict verifyWorshipMeetingConflict;
    private final TypesWorshipMeetingReader typesWorshipMeetingReader;
    private final ChurchLookup churchLookup;
    private final AttendanceService attendanceService;


    @Override
    public WorshipDto createWorship(CreateWorshipDto dto, @NotNull UUID churchId) {

        verifyWorshipMeetingConflict.verifyHourOfWorshipMeeting(dto, churchId, null);

        WorshipMeetingTypes worshipMeetingTypes =
                typesWorshipMeetingReader.getWorshipMeetingTypesById(dto.getWorshipTypeId());

        ChurchModel church = churchLookup.getChurchById(churchId);

        // Crear entidad con inicialización normalizada
        WorshipMeetingModel worshipModel = new WorshipMeetingModel();
        worshipModel.fromDto(dto);
        worshipModel.setContextId(churchId);
        worshipModel.setTypeId(worshipMeetingTypes.getId());
        worshipModel.setWorshipType(worshipMeetingTypes);
        worshipModel.setChurch(church);

        // Usar MeetingService para persistir
        WorshipMeetingModel saved = (WorshipMeetingModel) meetingService.create(worshipModel);
        return saved.toDto();
    }

    @Override
    public WorshipDetailedDto getWorshipById(UUID id) {
        // Usa MeetingService para obtener con relaciones (evita N+1)
        WorshipMeetingModel worship = (WorshipMeetingModel) meetingService.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Worship service not found with id: " + id
                        )
                );

        WorshipDto baseDto = worship.toDto();

        long present = attendanceService.countByEventIdWithDefaults(
                id, AttendanceEventType.TEMPLE_WORHSIP, AttendanceStatus.PRESENT
        );

        long absent = attendanceService.countByEventIdWithDefaults(
                id, AttendanceEventType.TEMPLE_WORHSIP, AttendanceStatus.ABSENT
        );

        long total = present + absent;

        WorshipDetailedDto detailedDto = new WorshipDetailedDto().fromWorshipDto(baseDto);
        detailedDto.setTotalAttendance(total);
        detailedDto.setPresentCount(present);
        detailedDto.setAbsentCount(absent);

        return detailedDto;
    }


    @Override
    public Page<WorshipDto> getAllWorships(Pageable pageable, @NotNull UUID churchId) {

        // Usar MeetingService para obtener reuniones normalizadas de tipo WORSHIP
        return worshipRepository.findByContextId(churchId, pageable)
                .map(meeting -> ((WorshipMeetingModel) meeting).toDto());
    }

    @Override
    public WorshipDto updateWorship(UUID id, CreateWorshipDto dto, @NotNull UUID churchId) {

        verifyWorshipMeetingConflict.verifyHourOfWorshipMeeting(dto, churchId, id);

        WorshipMeetingModel worship = (WorshipMeetingModel) meetingService.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Worship service not found with id: " + id)
                );

        // Usar updateFrom para no modificar creationDate
        worship.updateFrom(dto);

        if (dto.getWorshipTypeId() != null) {
            WorshipMeetingTypes type =
                    typesWorshipMeetingReader.getWorshipMeetingTypesById(dto.getWorshipTypeId());
            worship.setWorshipType(type);
            worship.setTypeId(type.getId());
        }

        WorshipMeetingModel updated = (WorshipMeetingModel) meetingService.update(worship);
        return updated.toDto();
    }

    @Override
    public void deleteWorship(UUID id) {
        // Verificar que existe antes de eliminar
        meetingService.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Worship service not found with id: " + id)
                );

        meetingService.delete(id);
    }
}
