package com.viddefe.viddefe_api.finances.application;

import com.viddefe.viddefe_api.finances.contracts.OfferingTypeService;
import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferingTypeServiceImpl implements OfferingTypeService {
    private final OfferingTypeRepository offeringTypeRepository;


    @Override
    public OfferingType findById(Long id) {
        return offeringTypeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("OfferingType not found with id: " + id)
        );
    }
}
