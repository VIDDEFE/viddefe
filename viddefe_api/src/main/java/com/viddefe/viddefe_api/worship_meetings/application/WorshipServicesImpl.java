package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.WorshipDetailedDto;
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

    private final MeetingRepository worshipRepository;
    private final MeetingService meetingService;
    private final VerifyWorshipMeetingConflict verifyWorshipMeetingConflict;
    private final MeetingTypesService meetingTypesService;
    private final ChurchLookup churchLookup;
    private final AttendanceService attendanceService;


    @Override
    public MeetingDto createWorship(CreateWorshipDto dto, @NotNull UUID churchId) {

        verifyWorshipMeetingConflict.verifyHourOfMeeting(dto, churchId, null, null);

        MeetingType worshipMeetingTypes =
                meetingTypesService.getMeetingTypesById(dto.getWorshipTypeId());

        ChurchModel church = churchLookup.getChurchById(churchId);

        // Crear entidad con inicialización normalizada
        Meeting worshipModel = new Meeting();
        worshipModel.fromDto(dto);
        worshipModel.setChurch(church);
        worshipModel.setMeetingType(worshipMeetingTypes);

        // Usar MeetingService para persistir
        Meeting saved = meetingService.create(worshipModel);
        return saved.toDto();
    }

    @Override
    public WorshipDetailedDto getWorshipById(UUID id) {
        // Usa MeetingService para obtener con relaciones (evita N+1)
        Meeting worship = meetingService.findByIdWithRelations(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Worship service not found with id: " + id
                        )
                );

        MeetingDto baseDto = worship.toDto();

        long present = attendanceService.countByEventIdWithDefaults(
                id, TopologyEventType.TEMPLE_WORHSIP, AttendanceStatus.PRESENT
        );

        long absent = attendanceService.countByEventIdWithDefaults(
                id, TopologyEventType.TEMPLE_WORHSIP, AttendanceStatus.ABSENT
        );

        long total = present + absent;

        WorshipDetailedDto detailedDto = new WorshipDetailedDto().fromWorshipDto(baseDto);
        detailedDto.setTotalAttendance(total);
        detailedDto.setPresentCount(present);
        detailedDto.setAbsentCount(absent);

        return detailedDto;
    }


    @Override
    public Page<MeetingDto> getAllWorships(Pageable pageable, @NotNull UUID churchId) {

        // Usar MeetingService para obtener reuniones normalizadas de tipo WORSHIP
        return worshipRepository.findByChurchIdAndGroupIsNull(churchId, pageable).map(Meeting::toDto);
    }

    @Override
    public MeetingDto updateWorship(UUID id, CreateWorshipDto dto, @NotNull UUID churchId) {

        verifyWorshipMeetingConflict.verifyHourOfMeeting(dto, churchId, null,id);

        Meeting worship = meetingService.findById(id);

        // Usar updateFrom para no modificar creationDate
        worship.fromDto(dto);

        if (dto.getWorshipTypeId() != null) {
            MeetingType type =
                    meetingTypesService.getMeetingTypesById(dto.getWorshipTypeId());
            worship.setMeetingType(type);
        }

        Meeting updated = meetingService.update(worship);
        return updated.toDto();
    }

    @Override
    public void deleteWorship(UUID id) {
        // Verificar que existe antes de eliminar
        meetingService.findById(id);

        meetingService.delete(id);
    }
}
