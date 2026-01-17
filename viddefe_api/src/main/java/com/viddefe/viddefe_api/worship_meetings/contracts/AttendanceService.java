package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * AttendanceService interface for managing attendance-related operations in the church or group.
 */
public interface AttendanceService {

    /**
     * Updates the attendance record based on the provided DTO and event type.
     *
     * @param dto  Data transfer object containing attendance details.
     * @param type The type of attendance event (e.g., TEMPLE_WORHSIP, GROUP_MEETING).
     * @return Updated AttendanceDto object.
     */
    AttendanceDto updateAttendance(CreateAttendanceDto dto, TopologyEventType type);

    /**
     * Retrieves the attendance records for a specific event.
     *
     * @param eventId The ID of the event.
     * @param pageable Pagination information.
     * @return A list of AttendanceDto objects representing the attendance records.
     */
    Page<AttendanceDto> getAttendanceByEventId(UUID eventId, Pageable pageable, TopologyEventType type);

    /**
     * Counts the total number of people available for attendance.
     * @param eventId
     * @param eventType
     * @return
     */
    long countTotalByEventId(UUID eventId, TopologyEventType eventType);

    /**
     * Counts the number of attendance records for a specific event with default status handling.
     *
     * @param eventId The ID of the event.
     * @param eventType The type of attendance event.
     * @param status The attendance status to count.
     * @return The count of attendance records matching the criteria.
     */
    long countByEventIdWithDefaults(UUID eventId, TopologyEventType eventType, AttendanceStatus status);
}
