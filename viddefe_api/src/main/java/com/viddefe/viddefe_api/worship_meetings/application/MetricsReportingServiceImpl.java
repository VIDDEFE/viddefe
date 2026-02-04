package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MetricsReportingService;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.ChurchMetricsDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.EntityIdWithTotalPeople;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricAttendanceProjectionRow;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MetricsAttendanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricsReportingServiceImpl implements MetricsReportingService {
    private final MeetingRepository meetingRepository;
    private final HomeGroupReader homeGroupReader;
    private final ChurchLookup churchLookup;
    private final MetricsRedisService metricsRedisService;
    private final static Integer MINUTES_CACHE = 20;

    private MetricsAttendanceDto buildingChurchMetrics(
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

    private MetricsAttendanceDto buildingGroupMetrics(
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

    /**
     * Retrieves worship attendance metrics for a specific church, utilizing Redis caching.
     *
     * @param churchId  The UUID of the church for which to retrieve metrics.
     * @param startTime The start time for the metrics retrieval.
     * @param endTime   The end time for the metrics retrieval.
     * @return MetricsAttendanceDto containing attendance metrics {@link ChurchMetricsDto}.
     */
    private MetricsAttendanceDto getMetricsWorshipAttendanceById(
            UUID churchId,
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ) {
        TopologyEventType eventType = TopologyEventType.TEMPLE_WORHSIP;
        MetricsAttendanceDto metricsWorship = metricsRedisService.getMetrics(eventType, churchId)
                .orElseGet(
                        () -> buildingChurchMetrics(churchId, startTime, endTime)
                );
        metricsRedisService.saveMetrics(
                eventType,
                churchId,
                metricsWorship,
                Duration.ofMinutes(MINUTES_CACHE)
        );
        return metricsWorship;
    }

    /**
     * Retrieves group meeting attendance metrics for a specific group, utilizing Redis caching.
     *
     * @param groupId   The UUID of the group for which to retrieve metrics.
     * @param startTime The start time for the metrics retrieval.
     * @param endTime   The end time for the metrics retrieval.
     * @return MetricsAttendanceDto containing attendance metrics {@link MetricsAttendanceDto}.
     */
    private MetricsAttendanceDto getMetricsGroupMetrics(
            UUID groupId,
            OffsetDateTime startTime,
            OffsetDateTime endTime
    ) {
        TopologyEventType eventType = TopologyEventType.GROUP_MEETING;
        MetricsAttendanceDto groupMetrics = metricsRedisService.getMetrics(eventType, groupId)
                .orElseGet(
                        () -> buildingGroupMetrics(groupId, startTime, endTime)
                );
        metricsRedisService.saveMetrics(
                eventType,
                groupId,
                groupMetrics,
                Duration.ofMinutes(MINUTES_CACHE)
        );
        return groupMetrics;
    }

    /**
     * @param contextId The ID of the context (e.g., church or group)
     * @param eventType The type of topology event {@link TopologyEventType}
     * @param startTime The start time for the metrics retrieval
     * @param endTime The end time for the metrics retrieval
     * @return MetricsAttendanceDto containing attendance metrics {@link MetricsAttendanceDto}
     */
    @Override
    public MetricsAttendanceDto getAttendanceMetrics(UUID contextId, TopologyEventType eventType, OffsetDateTime startTime, OffsetDateTime endTime) {
        return switch (eventType) {
            case TEMPLE_WORHSIP -> getMetricsWorshipAttendanceById(contextId,
                    startTime, endTime);
            case GROUP_MEETING -> getMetricsGroupMetrics(contextId, startTime, endTime);
            default -> throw new IllegalArgumentException("Unsupported TopologyEventType: " + eventType);
        };
    }
}
