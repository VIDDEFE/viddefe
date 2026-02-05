package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.*;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    // ==================== CREATE ====================

    @Override
    public MeetingDto createMeeting(CreateMeetingDto dto, UUID contextId, TopologyEventType eventType, UUID churchId) {
        return switch (eventType) {
            case TEMPLE_WORHSIP ->
                 worshipService.createWorship( dto, contextId);
            case GROUP_MEETING ->
                groupMeetingService.createGroupMeeting(dto, contextId, churchId);

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
            case TEMPLE_WORHSIP ->
                    worshipService.updateWorship(meetingId, dto, contextId);
            case GROUP_MEETING ->
                groupMeetingService.updateGroupMeeting(dto, contextId, meetingId);
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
}
