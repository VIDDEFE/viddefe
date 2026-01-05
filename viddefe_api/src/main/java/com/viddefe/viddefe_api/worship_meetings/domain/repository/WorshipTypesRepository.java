package com.viddefe.viddefe_api.worship_meetings.domain.repository;

import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipTypesRepository extends JpaRepository<WorshipMeetingTypes, Long>
{
}
