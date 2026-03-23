package com.viddefe.viddefe_api.worship_meetings.application;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.viddefe.viddefe_api.churches.contracts.ChurchMemberShip;
import com.viddefe.viddefe_api.homegroups.contracts.HomeGroupMemberShipService;
import com.viddefe.viddefe_api.notifications.Infrastructure.dto.ApplicationSendEventDto;
import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingFacade;
import com.viddefe.viddefe_api.worship_meetings.contracts.MetricsReportingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;

import lombok.RequiredArgsConstructor;

/**
 * Facade que orquesta las operaciones de reuniones.
 * Centraliza la lógica delegando a los servicios específicos según {@link TopologyEventType}.
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
    private final MetricsReportingService metricsReportingService;
    private final NotificationEventPublisher notificationEventPublisher;
    private final HomeGroupMemberShipService homeGroupMemberShipService;
    private final ChurchMemberShip churchMemberShip;

    private static final String MEETING_CREATE_TEMPLATE = """
        Hola, se ha creado una nueva reunion de {meetingType}. \
        En la fecha {meetingDate} se llevará a cabo la reunión. ¡No te lo pierdas!.
        """;

    private static final String MEETING_UPDATE_TEMPLATE = """
            Hola, se ha actualizado la reunión de {meetingType}. \
            La nueva fecha de la reunión es {meetingDate}. ¡No te lo pierdas!.
            """;
    // ==================== CREATE ====================

    @Override
    public MeetingDto createMeeting(CreateMeetingDto dto, UUID contextId, TopologyEventType eventType, UUID churchId) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> {
                MeetingDto result = worshipService.createWorship(dto, contextId);
                sendMeetingNotification(result);
                yield result;
            }
            case GROUP_MEETING -> {
                MeetingDto result = groupMeetingService.createGroupMeeting(dto, contextId, churchId);
                sendMeetingNotification(result);
                yield result;
            }

        };
    }

    // ==================== READ ====================

    @Override
    @Transactional(readOnly = true)
    public MeetingDto getMeetingById(UUID contextId, UUID meetingId, TopologyEventType eventType) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> worshipService.getWorshipById(meetingId);
            case GROUP_MEETING -> groupMeetingService.getGroupMeetingById(contextId, meetingId);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MeetingDto> getAllMeetings(UUID contextId, TopologyEventType eventType, Pageable pageable) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> worshipService.getAllWorships(pageable, contextId);
            case GROUP_MEETING -> groupMeetingService.getGroupMeetingByGroupId(contextId, pageable);
        };
    }

    // ==================== UPDATE ====================

    @Override
    public MeetingDto updateMeeting(CreateMeetingDto dto, UUID contextId, UUID meetingId, TopologyEventType eventType) {
        return switch (eventType) {
            case TEMPLE_WORHSIP ->{

                MeetingDto result = worshipService.updateWorship(meetingId, dto, contextId);
                sendMeetingNotification(result);
                yield result;
            }
            case GROUP_MEETING ->{
                MeetingDto result = groupMeetingService.updateGroupMeeting(dto, contextId, meetingId);
                sendMeetingNotification(result);
                yield result;
            }
        };
    }

    // ==================== DELETE ====================

    @Override
    public void deleteMeeting(UUID contextId, UUID meetingId, TopologyEventType eventType) {
        switch (eventType) {
            case TEMPLE_WORHSIP -> worshipService.deleteWorship(meetingId);
            case GROUP_MEETING -> groupMeetingService.deleteGroupMeeting(contextId, meetingId);
        }
    }

    // ==================== ATTENDANCE ====================

    @Override
    public AttendanceDto recordAttendance(CreateAttendanceDto dto, TopologyEventType eventType) {
        return attendanceService.updateAttendance(dto, eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDto> getAttendance(UUID meetingId, TopologyEventType eventType, Pageable pageable, UUID contextId, AttendanceQualityEnum levelOfAttendance) {
        return attendanceService.getAttendanceByEventIdAndContextId(meetingId, pageable, eventType, contextId, levelOfAttendance);
    }

    /**
     * @param contextId The ID of the context (e.g., church or group)
     * @param eventType The type of topology event {@link TopologyEventType}
     * @param startTime The start time for the metrics retrieval
     * @param endTime The end time for the metrics retrieval
     * @return MetricsAttendanceDto containing attendance metrics {@link MetricsAttendanceDto}
     */
    @Override
    public MetricsAttendanceDto getMetricsAttendance(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime) {
       return resolveMetricsByEventType(contextId, eventType, startTime, endTime);
    }

    private MetricsAttendanceDto resolveMetricsByEventType(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime) {
        return metricsReportingService.getAttendanceMetrics(contextId, eventType, startTime, endTime);
    }

    private void sendMeetingNotification(MeetingDto meetingDto) {
        String template = meetingDto.getEventType() == TopologyEventType.TEMPLE_WORHSIP
        ? MEETING_CREATE_TEMPLATE
        : MEETING_UPDATE_TEMPLATE;
        
        List<UUID> peopleIds = switch (meetingDto.getEventType()) {
            case TEMPLE_WORHSIP -> churchMemberShip.getPeopleIdsByChurchId(meetingDto.getContextId()); // Para reuniones de templo, no se envían notificaciones a personas específicas
            case GROUP_MEETING -> homeGroupMemberShipService.getMemberIdsInHomeGroup(meetingDto.getContextId());
        };

        Map<String, Object> variables = Map.of(
                "meetingType", meetingDto.getEventType() == TopologyEventType.TEMPLE_WORHSIP ? "templo" : "grupo",
                "meetingDate", meetingDto.getScheduledDate().toString()
        );
        ApplicationSendEventDto notificationDto = ApplicationSendEventDto.builder()
                .meetingId(meetingDto.getId())
                .template(template)
                .variables(variables)
                .peopleIds(peopleIds)
                .build();

        notificationEventPublisher.publish(notificationDto);

    }
}
