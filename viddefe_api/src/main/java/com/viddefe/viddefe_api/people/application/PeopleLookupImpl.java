package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.viddefe.viddefe_api.people.application.PeopleServiceImpl.savePeople;

@Service
@RequiredArgsConstructor
public class PeopleLookupImpl implements PeopleLookup {
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;
    private final StatesCitiesService statesCitiesService;
    private final ChurchLookup churchLookup;

    @Override
    public void enrollPersonToChurch(@NonNull PeopleModel person, @NonNull ChurchModel church){
        person.setChurch(church);
        person.setTypePerson(peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.name()));
        peopleRepository.save(person);
    }

    @Override
    public PeopleModel getPastorByCcWithoutChurch(String cc) {
        PeopleTypeModel pastorType = peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.name());
        return peopleRepository.findByCcAndTypePersonAndChurchIsNull(cc, pastorType).orElseThrow(
            () -> new EntityNotFoundException("Pastor not found with CC: "  +cc)
        );
    }

    @Override
    public PeopleModel getPeopleById(UUID id) {
        return peopleRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("Person not found: "  +id)
        );
    }

    @Override
    public PeopleModel save(PeopleDTO dto) {
        return savePeople(dto, peopleTypeService, statesCitiesService, churchLookup, peopleRepository);
    }
}
