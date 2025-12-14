package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PeopleService {
    PeopleModel createPeople(PeopleDTO dto);
    Page<PeopleDTO> getAllPeople(Pageable pageable);
    PeopleDTO updatePeople(PeopleDTO dto, UUID id);
    void deletePeople(UUID id);
    PeopleDTO getPeopleById(UUID id);
}

