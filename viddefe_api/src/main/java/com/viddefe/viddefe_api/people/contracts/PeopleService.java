package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PeopleService {
    PeopleResDto createPeople(PeopleDTO dto);
    Page<PeopleResDto> getAllPeople(Pageable pageable);
    PeopleResDto updatePeople(PeopleDTO dto, UUID id);
    void deletePeople(UUID id);
    PeopleResDto getPeopleById(UUID id);
}

