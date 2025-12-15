package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import com.viddefe.viddefe_api.people.domain.repository.PeopleTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeopleLookupImpl implements PeopleLookup {
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;

    @Override
    public void enrollPersonToChurch(@NonNull PeopleModel person, @NonNull ChurchModel church){
        person.setChurch(church);
        person.setTypePerson(peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.name()));
        peopleRepository.save(person);
    }

    @Override
    public PeopleModel getPeopleById(UUID id) {
        return peopleRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Person not found: "  +id)
        );
    }

}
