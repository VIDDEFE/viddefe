package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetingTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMeetingTypeRepository extends JpaRepository<GroupMeetingTypes, Long> {
}
