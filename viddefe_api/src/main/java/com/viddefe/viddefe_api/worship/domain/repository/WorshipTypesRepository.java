package com.viddefe.viddefe_api.worship.domain.repository;

import com.viddefe.viddefe_api.worship.domain.models.WorshipMeetingTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorshipTypesRepository extends JpaRepository<WorshipMeetingTypes, Long>
{
}
