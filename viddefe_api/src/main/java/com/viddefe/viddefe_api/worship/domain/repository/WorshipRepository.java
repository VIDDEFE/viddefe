package com.viddefe.viddefe_api.worship.domain.repository;

import com.viddefe.viddefe_api.worship.domain.models.WorshipModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorshipRepository extends JpaRepository<WorshipModel, UUID> {
}
