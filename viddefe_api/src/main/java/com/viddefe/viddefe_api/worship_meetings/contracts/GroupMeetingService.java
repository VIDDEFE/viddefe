package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GroupMeetingService {
    /**
     * Create a new group meeting.
     * @param dto {@link CreateMeetingGroupDto} containing the details of the group meeting to be created
     * @param groupId UUID of the group for which the meeting is to be created
     * @return the created GroupMeetingDto {@link GroupMeetingDto}
     */
    GroupMeetingDto createGroupMeeting(CreateMeetingGroupDto dto, UUID groupId);
    /**
     * Get a paginated list of group meetings for a specific group.
     * @param groupId UUID of the group whose meetings are to be retrieved
     * @return Page<GroupMeetingDto> {@link GroupMeetingDto} containing the paginated list of group meetings
     */
    Page<GroupMeetingDto> getGroupMeetingByGroupId(UUID groupId, Pageable pageable);
}
