package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.GroupMeetings;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.GroupMeetingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupMeetingRepository extends JpaRepository<GroupMeetings, UUID> {
    Page<GroupMeetingDto> findAllByGroupId(UUID groupId, Pageable pageable);
}
