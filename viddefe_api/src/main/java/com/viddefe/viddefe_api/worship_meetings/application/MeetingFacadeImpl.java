package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingFacade;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Facade que orquesta las operaciones de reuniones.
 * Centraliza la lógica delegando a los servicios específicos según {@link AttendanceEventType}.
 *
 * <p>Beneficios:</p>
 * <ul>
 *   <li>Punto único de entrada para operaciones de reuniones</li>
 *   <li>Los controladores solo interactúan con este facade</li>
 *   <li>Facilita agregar nuevos tipos de reuniones sin modificar controladores</li>
 *   <li>Manejo consistente de transacciones</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MeetingFacadeImpl implements MeetingFacade {

    private final WorshipService worshipService;
    private final GroupMeetingService groupMeetingService;
    private final AttendanceService attendanceService;

    // ==================== CREATE ====================

    @Override
    public MeetingDto createMeeting(CreateMeetingDto dto, UUID contextId, AttendanceEventType eventType) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> {
                validateDtoType(dto, CreateWorshipDto.class, eventType);
                yield worshipService.createWorship((CreateWorshipDto) dto, contextId);
            }
            case GROUP_MEETING -> {
                validateDtoType(dto, CreateMeetingGroupDto.class, eventType);
                yield groupMeetingService.createGroupMeeting((CreateMeetingGroupDto) dto, contextId);
            }
        };
    }

    // ==================== READ ====================

    @Override
    @Transactional(readOnly = true)
    public MeetingDto getMeetingById(UUID contextId, UUID meetingId, AttendanceEventType eventType) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> worshipService.getWorshipById(meetingId);
            case GROUP_MEETING -> groupMeetingService.getGroupMeetingById(contextId, meetingId);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Page<? extends MeetingDto> getAllMeetings(UUID contextId, AttendanceEventType eventType, Pageable pageable) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> worshipService.getAllWorships(pageable, contextId);
            case GROUP_MEETING -> groupMeetingService.getGroupMeetingByGroupId(contextId, pageable);
        };
    }

    // ==================== UPDATE ====================

    @Override
    public MeetingDto updateMeeting(CreateMeetingDto dto, UUID contextId, UUID meetingId, AttendanceEventType eventType) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> {
                validateDtoType(dto, CreateWorshipDto.class, eventType);
                yield worshipService.updateWorship(meetingId, (CreateWorshipDto) dto, contextId);
            }
            case GROUP_MEETING -> {
                validateDtoType(dto, CreateMeetingGroupDto.class, eventType);
                yield groupMeetingService.updateGroupMeeting((CreateMeetingGroupDto) dto, contextId, meetingId);
            }
        };
    }

    // ==================== DELETE ====================

    @Override
    public void deleteMeeting(UUID contextId, UUID meetingId, AttendanceEventType eventType) {
        switch (eventType) {
            case TEMPLE_WORHSIP -> worshipService.deleteWorship(meetingId);
            case GROUP_MEETING -> groupMeetingService.deleteGroupMeeting(contextId, meetingId);
        }
    }

    // ==================== ATTENDANCE ====================

    @Override
    public AttendanceDto recordAttendance(CreateAttendanceDto dto, AttendanceEventType eventType) {
        return attendanceService.updateAttendance(dto, eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDto> getAttendance(UUID meetingId, AttendanceEventType eventType, Pageable pageable) {
        return attendanceService.getAttendanceByEventId(meetingId, pageable, eventType);
    }

    // ==================== PRIVATE HELPERS ====================

    /**
     * Valida que el DTO sea del tipo esperado según el tipo de evento.
     *
     * @param dto DTO a validar
     * @param expectedType clase esperada
     * @param eventType tipo de evento para mensaje de error
     * @throws IllegalArgumentException si el DTO no es del tipo esperado
     */
    private void validateDtoType(CreateMeetingDto dto, Class<? extends CreateMeetingDto> expectedType, AttendanceEventType eventType) {
        if (!expectedType.isInstance(dto)) {
            throw new IllegalArgumentException(
                    String.format("DTO inválido para %s. Se esperaba %s pero se recibió %s",
                            eventType.name(),
                            expectedType.getSimpleName(),
                            dto.getClass().getSimpleName())
            );
        }
    }

}
