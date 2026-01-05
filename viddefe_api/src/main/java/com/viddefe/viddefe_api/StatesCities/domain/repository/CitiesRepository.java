package com.viddefe.viddefe_api.StatesCities.domain.repository;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitiesRepository extends JpaRepository<CitiesModel, Long> {
}
