package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDetailedDto;
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
     * Update an existing group meeting.
     * @param dto {@link CreateMeetingGroupDto} containing the updated details of the group meeting
     * @param groupId UUID of the group to which the meeting belongs
     * @param meetingId UUID of the meeting to be updated
     * @return the updated GroupMeetingDto {@link GroupMeetingDto}
     */
    GroupMeetingDto updateGroupMeeting(CreateMeetingGroupDto dto, UUID groupId, UUID meetingId);

    /**
     * Delete a group meeting.
     * @param groupId UUID of the group to which the meeting belongs
     * @param meetingId UUID of the meeting to be deleted
     */
    void deleteGroupMeeting(UUID groupId, UUID meetingId);

    /**
     * Get a paginated list of group meetings for a specific group.
     * @param groupId UUID of the group whose meetings are to be retrieved
     * @return Page<GroupMeetingDto> {@link GroupMeetingDto} containing the paginated list of group meetings
     */
    Page<GroupMeetingDto> getGroupMeetingByGroupId(UUID groupId, Pageable pageable);

    GroupMeetingDetailedDto getGroupMeetingById(UUID groupId, UUID meetingId);

    /**
     * Get attendance details for a specific group meeting.
     * @param groupId UUID of the group to which the meeting belongs
     * @param meetingId UUID of the meeting whose attendance is to be retrieved
     * @param pageable Pagination information
     * @return GroupMeetingAttendanceDto {@link GroupMeetingDetailedDto} containing attendance details
     */
    Page<AttendanceDto> getGroupMeetingAttendance(UUID groupId, UUID meetingId, Pageable pageable);
}
