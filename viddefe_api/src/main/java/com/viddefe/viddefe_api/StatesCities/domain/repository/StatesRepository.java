package com.viddefe.viddefe_api.StatesCities.domain.repository;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatesRepository extends JpaRepository<StatesModel, Long> {
}
