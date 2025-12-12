package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PeopleService {
    PeopleModel createPeople(PeopleDTO dto);
    Page<PeopleModel> getAllPeople(Pageable pageable);
    PeopleModel updatePeople(PeopleDTO dto, UUID id);
    void deletePeople(UUID id);
    PeopleModel getPeopleById(UUID id);
}

