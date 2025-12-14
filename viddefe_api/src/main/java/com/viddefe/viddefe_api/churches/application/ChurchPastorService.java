package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchPastorRepository;
import com.viddefe.viddefe_api.people.contracts.PeopleLookup;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchPastorService {
    private final ChurchPastorRepository churchPastorRepository;
    private final PeopleLookup peopleLookup;

    public void addPastorToChurch(@NonNull UUID pastorId, @NonNull ChurchModel church) {
        PeopleModel pastor = peopleLookup.getPeopleById(pastorId);
        ChurchPastor churchPastor = new ChurchPastor();
        churchPastor.setPastor(pastor);
        churchPastor.setChurch(church);
        churchPastorRepository.save(churchPastor);
    }
}
