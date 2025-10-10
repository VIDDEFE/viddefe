package com.viddefe.viddefe_api.people.repository;

import com.viddefe.viddefe_api.people.model.PeopleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PeopleRepository extends JpaRepository<PeopleModel, UUID> {
    Optional<PeopleModel> findByCc(String cc);
}
