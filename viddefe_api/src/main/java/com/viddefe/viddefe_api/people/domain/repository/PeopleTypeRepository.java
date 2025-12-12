package com.viddefe.viddefe_api.people.domain.repository;

import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeopleTypeRepository extends JpaRepository<PeopleTypeModel, Long> {
    public Optional<PeopleTypeModel> findByName(String name);
}
