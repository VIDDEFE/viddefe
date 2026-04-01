package com.viddefe.viddefe_api.statescities.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.viddefe.viddefe_api.statescities.domain.model.StatesModel;

public interface StatesRepository extends JpaRepository<StatesModel, Long> {
}
