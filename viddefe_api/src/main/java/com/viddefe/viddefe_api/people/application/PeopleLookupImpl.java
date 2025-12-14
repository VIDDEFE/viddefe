package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeopleLookupImpl implements PeopleLookup {
    private final PeopleRepository peopleRepository;

    @Override
    public Void enrollPersonToChurch(@NonNull PeopleModel person,@NonNull ChurchModel church){
        person.setChurch(church);
        peopleRepository.save(person);
        return null;
    }

    @Override
    public PeopleModel getPeopleById(UUID id) {
        return peopleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Person not found")
        );
    }

}
