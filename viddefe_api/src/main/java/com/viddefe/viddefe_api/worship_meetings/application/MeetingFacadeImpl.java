package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.GroupMeetingService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingFacade;
import com.viddefe.viddefe_api.worship_meetings.contracts.WorshipService;
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
    private final MeetingRepository meetingRepository;
    private final ChurchLookup churchLookup;
    private final HomeGroupReader homeGroupReader;

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
     * @param contextId
     * @param eventType
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public MetricsAttendanceDto getMetricsAttendance(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime) {
       return resolveMetricsByEventType(contextId, eventType, startTime, endTime);
    }


    private MetricsAttendanceDto resolveMetricsByEventType(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> getMetricsWorshipAttendanceById(contextId,
                    startTime, endTime);
            case GROUP_MEETING -> getMetricsGroupAttendanceById(contextId, startTime, endTime);
            default -> throw new IllegalArgumentException("Unsupported TopologyEventType: " + eventType);
        };
    }

    private MetricsAttendanceDto getMetricsWorshipAttendanceById(
            UUID churchId,
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ) {
        List<EntityIdWithTotalPeople> groupIdWithTotalPeople =
                Optional.ofNullable(
                        homeGroupReader.findAllIdsWithTotalPeopleByChurchId(churchId)
                ).orElse(List.of());

        List<EntityIdWithTotalPeople> childrenIdsWithTotalPeople =
                Optional.ofNullable(
                        churchLookup.findChildrenIdsWithTotalPeopleChurchIdsByChurchId(churchId)
                ).orElse(List.of());

        EntityIdWithTotalPeople churchIdWithTotalPeople =
                churchLookup.findChurchIdWithTotalPeopleByChurchId(churchId);

        List<UUID> groupIds = groupIdWithTotalPeople.stream()
                .map(EntityIdWithTotalPeople::getEntityId)
                .toList();

        List<UUID> childrenChurchIds = childrenIdsWithTotalPeople.stream()
                .map(EntityIdWithTotalPeople::getEntityId)
                .toList();

        Map<UUID, Long> groupTotalPeopleMap =
                groupIdWithTotalPeople.stream()
                        .collect(Collectors.toMap(
                                EntityIdWithTotalPeople::getEntityId,
                                EntityIdWithTotalPeople::getTotalPeople
                        ));

        Map<UUID, Long> childrenChurchTotalPeopleMap =
                childrenIdsWithTotalPeople.stream()
                        .collect(Collectors.toMap(
                                EntityIdWithTotalPeople::getEntityId,
                                EntityIdWithTotalPeople::getTotalPeople
                        ));

        long churchTotalPeople = churchIdWithTotalPeople.getTotalPeople();

        // ================== CHURCH METRICS ==================
        MetricsAttendanceDto churchMetrics =
                meetingRepository
                        .getMetricsWorshipAttendanceByInId(
                                List.of(churchId),
                                TopologyEventType.TEMPLE_WORHSIP,
                                startTime,
                                endTime
                        )
                        .stream()
                        .map(row -> buildFromProjection(row, churchTotalPeople))
                        .findFirst()
                        .orElseGet(() -> MetricsAttendanceDto.builder()
                                .newAttendees(0L)
                                .attendanceRate(0.0)
                                .absenceRate(0.0)
                                .totalMeetings(0L)
                                .averageAttendancePerMeeting(0.0)
                                .build()
                        );

        // ================== GROUP METRICS ==================
        List<MetricsAttendanceDto> groupsMetrics =
                groupIds.isEmpty()
                        ? List.of()
                        : meetingRepository
                        .getMetricsGroupAttendanceByInId(
                                groupIds,
                                TopologyEventType.GROUP_MEETING,
                                startTime,
                                endTime
                        )
                        .stream()
                        .map(row ->
                                buildFromProjection(
                                        row,
                                        groupTotalPeopleMap.getOrDefault(row.getId(), 0L)
                                )
                        )
                        .toList();

        // ================== CHILD CHURCH METRICS ==================
        List<MetricsAttendanceDto> churchesMetrics =
                childrenChurchIds.isEmpty()
                        ? List.of()
                        : meetingRepository
                        .getMetricsWorshipAttendanceByInId(
                                childrenChurchIds,
                                TopologyEventType.TEMPLE_WORHSIP,
                                startTime,
                                endTime
                        )
                        .stream()
                        .map(row ->
                                buildFromProjection(
                                        row,
                                        childrenChurchTotalPeopleMap.getOrDefault(row.getId(), 0L)
                                )
                        )
                        .toList();

        // ================== FINAL DTO ==================
        return ChurchMetricsDto.builder()
                .totalGroups(groupIds.size())
                .newAttendees(churchMetrics.getNewAttendees())
                .groupMetrics(groupsMetrics)
                .churchMetrics(churchesMetrics)
                .attendanceRate(churchMetrics.getAttendanceRate())
                .absenceRate(churchMetrics.getAbsenceRate())
                .totalMeetings(churchMetrics.getTotalMeetings())
                .averageAttendancePerMeeting(churchMetrics.getAverageAttendancePerMeeting())
                .totalPeopleAttended(churchMetrics.getTotalPeopleAttended())
                .totalPeople(churchTotalPeople)
                .build();
    }

    private MetricsAttendanceDto getMetricsGroupAttendanceById(
            UUID groupId,
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ) {
        Long totalPeople =
                homeGroupReader.findTotalPeopleByGroupId(groupId);

        return meetingRepository.getMetricsGroupAttendanceByInId(
                        List.of(groupId),
                        TopologyEventType.GROUP_MEETING,
                        startTime,
                        endTime
                )
                .stream()
                .map(row -> buildFromProjection(row, totalPeople))
                .findFirst()
                .orElse(null);
    }


    private MetricsAttendanceDto buildFromProjection(MetricAttendanceProjectionRow row, Long totalPeople) {
        long totalAttended = Optional.ofNullable(row.getTotalPeopleAttended()).orElse(0L);
        long newAttendees  = Optional.ofNullable(row.getTotalNewAttendees()).orElse(0L);

        double attendanceRate =
                totalPeople == 0 ? 0.0 :
                        (row.getTotalPeopleAttended() / (double) totalPeople) * 100.0;
        double absenceRate = 100.0 - attendanceRate;

        double averageAttendancePerMeeting =
                row.getTotalMeetings() == 0 ? 0.0 :
                        row.getTotalPeopleAttended() / (double) row.getTotalMeetings();

        return MetricsAttendanceDto.builder()
                .newAttendees(newAttendees)
                .totalPeopleAttended(totalAttended)
                .totalMeetings(row.getTotalMeetings())
                .averageAttendancePerMeeting(averageAttendancePerMeeting)
                .attendanceRate(attendanceRate)
                .absenceRate(absenceRate)
                .totalPeople(totalPeople)
                .build();
    }

}
