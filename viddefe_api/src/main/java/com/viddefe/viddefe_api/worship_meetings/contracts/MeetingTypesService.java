package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceQualityDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;

import java.util.List;

public interface MeetingTypesService {
    /**
     * Get all Worship Meeting (in the temple , Group or other) Types by Topology Event Types
     * @param topologyEventType {@link TopologyEventType}
     * @return the Worship Meeting Types {@link MeetingTypeDto}
     */
    List<MeetingTypeDto> getAllMeetingByTopologyEventTypes(TopologyEventType topologyEventType);
    /**
     * Get Worship Meeting (in the temple , Group or other) Types by Id
     * @param id {@link Long}
     * @return the Worship Meeting Types {@link MeetingType}
     */
    MeetingType getMeetingTypesById(Long id);

    /**
     * Get all Attendance Levels
     * @return the Attendance Levels {@link String}
     */
    List<AttendanceQualityDto> getAttendanceLevels();

}
