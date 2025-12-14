package com.viddefe.viddefe_api.people.infrastructure;

import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PeopleRepositoryAdapter implements PeopleLookup {
    private final PeopleRepository peopleRepository;

    @Override
    public PeopleModel getPeopleById(UUID id) {
        return peopleRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("People not found with id: " + id));
    }
}
