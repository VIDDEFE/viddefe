package com.viddefe.viddefe_api.worship.domain.repository;

import com.viddefe.viddefe_api.worship.domain.models.GroupMeetings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupMeetingRepository extends JpaRepository<GroupMeetings, UUID> {
}
