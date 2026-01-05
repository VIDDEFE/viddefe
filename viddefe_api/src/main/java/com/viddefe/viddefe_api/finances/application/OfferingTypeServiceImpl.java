package com.viddefe.viddefe_api.finances.application;

import com.viddefe.viddefe_api.finances.contracts.OfferingTypeService;
import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingTypeRepository;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingTypeDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<OfferingTypeDto> findAll() {
        return offeringTypeRepository.findAll()
                .stream()
                .map(OfferingType::toDto)
                .toList();
    }
}
