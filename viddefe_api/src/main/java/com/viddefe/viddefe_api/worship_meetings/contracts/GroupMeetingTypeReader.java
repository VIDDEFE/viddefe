package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;

import java.util.List;

public interface GroupMeetingTypeReader {
    GroupMeetingTypes getGroupMeetingTypeById(Long id);
    List<GroupMeetingTypes> getAllGroupMeetingTypes();
}
