package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQualityPeople;
import com.viddefe.viddefe_api.worship_meetings.domain.models.serializable.AttendanceQualityPeopleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AttendanceQualityPeopleRepository extends JpaRepository<AttendanceQualityPeople, AttendanceQualityPeopleId> {
    Optional<AttendanceQualityPeople> findByPeopleId(UUID id);
}
