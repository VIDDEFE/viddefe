package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;

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
    AttendanceDto updateAttendance(CreateAttendanceDto dto, AttendanceEventType type);

}
