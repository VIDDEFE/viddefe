package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MinistryFunctionRepository extends JpaRepository<MinistryFunction,UUID> {
    List<MinistryFunction> findByEventId(UUID eventId);
}
