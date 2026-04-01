package com.viddefe.viddefe_api.statescities.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viddefe.viddefe_api.statescities.domain.model.CitiesModel;

public interface CitiesRepository extends JpaRepository<CitiesModel, Long> {
}
