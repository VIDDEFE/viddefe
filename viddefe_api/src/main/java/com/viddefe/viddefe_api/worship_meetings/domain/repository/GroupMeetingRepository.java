package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupMeetingRepository extends JpaRepository<GroupMeetings, UUID> {
}
