package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChurchLookupImpl implements ChurchLookup {
    private final ChurchRepository peopleRepository;
    private final ChurchPastorService churchPastorService;

    @Override
    public ChurchModel getChurchById(java.util.UUID id) {
        return peopleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Church not found")
        );
    }

    @Override
    public PeopleModel getPastorByChurch(ChurchModel church) {
        return churchPastorService.getPastorFromChurch(church);
    }
}
