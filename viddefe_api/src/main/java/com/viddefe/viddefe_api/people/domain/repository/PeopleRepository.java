package com.viddefe.viddefe_api.people.domain.repository;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PeopleRepository extends JpaRepository<PeopleModel, UUID> {
    Optional<PeopleModel> findByCcAndTypePersonAndChurchIsNull(String cc, PeopleTypeModel typePerson);
}
