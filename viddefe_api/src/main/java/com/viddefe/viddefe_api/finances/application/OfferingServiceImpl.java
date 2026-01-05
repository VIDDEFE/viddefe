package com.viddefe.viddefe_api.finances.application;

import com.viddefe.viddefe_api.finances.contracts.OfferingService;
import com.viddefe.viddefe_api.finances.contracts.OfferingTypeService;
import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.model.Offerings;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingsRepository;
import com.viddefe.viddefe_api.finances.infrastructure.dto.CreateOfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDtoPageWithAnalityc;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferingServiceImpl implements OfferingService {
    private final OfferingsRepository offeringsRepository;
    private final PeopleReader peopleReader;
    private final OfferingTypeService offeringTypeService;
    @Override
    public OfferingDto register(CreateOfferingDto dto) {
        Offerings offerings = new Offerings().fromDto(dto);
        PeopleModel person = peopleReader.getPeopleById(dto.getPeopleId());
        OfferingType type = offeringTypeService.findById(dto.getOfferingTypeId());
        offerings.setPerson(person);
        offerings.setOfferingType(type);
        return offeringsRepository.save(offerings).toDto();
    }

    @Override
    public OfferingDto update(CreateOfferingDto dto, UUID id) {
        Offerings existingOffering = offeringsRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Ofrenda no encontrada"));
        existingOffering.fromDto(dto);
        PeopleModel person = peopleReader.getPeopleById(dto.getPeopleId());
        OfferingType type = offeringTypeService.findById(dto.getOfferingTypeId());
        existingOffering.setPerson(person);
        existingOffering.setOfferingType(type);
        return offeringsRepository.save(existingOffering).toDto();
    }

    @Override
    public OfferingDtoPageWithAnalityc getAllByEventId(UUID eventId, Pageable pageable) {
        Page<OfferingDto> offeringDtos = offeringsRepository.findAllByEventId(eventId, pageable).map(Offerings::toDto);
        OfferingDtoPageWithAnalityc offeringdtoWithAnalityc = new OfferingDtoPageWithAnalityc();
        offeringdtoWithAnalityc.setOfferings(offeringDtos);
        return offeringdtoWithAnalityc;
    }

    @Override
    public void delete(UUID id) {
        offeringsRepository.deleteById(id);
    }
}
