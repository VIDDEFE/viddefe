package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChurchLookupImpl implements ChurchLookup {
    private final ChurchRepository peopleRepository;

    @Override
    public ChurchModel getChurchById(java.util.UUID id) {
        return peopleRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Church not found")
        );
    }
}
