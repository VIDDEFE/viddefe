package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopologyMeetingRepository extends JpaRepository<TopologyMeetingModel, Long> {
    Optional<TopologyMeetingModel> findByType(TopologyEventType topologyEventType);
}
