package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<AttendanceModel, UUID> {
    Optional<AttendanceModel> findByPeopleIdAndEventId(UUID peopleId, UUID eventId);
}
