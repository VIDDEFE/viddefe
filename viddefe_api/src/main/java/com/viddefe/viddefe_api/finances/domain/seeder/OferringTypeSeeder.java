package com.viddefe.viddefe_api.finances.domain.seeder;

import com.viddefe.viddefe_api.finances.configuration.OfferingTypeEnum;
import com.viddefe.viddefe_api.finances.contracts.OfferingService;
import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.model.Offerings;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OferringTypeSeeder {
    private final OfferingTypeRepository offeringTypeRepository;
    @PostConstruct
    public void seed() {
        // Seed logic for OfferingType entities goes here
        List<String> existingNames = offeringTypeRepository.findAll()
                .stream()
                .map(OfferingType::getName)
                .toList();
        List<OfferingType> toSave = Arrays.stream(OfferingTypeEnum.values())
                .filter(offeringTypeEnum -> !existingNames.contains(offeringTypeEnum.getDescription()))
                .map(offeringTypeEnum -> {
                    OfferingType offeringType = new OfferingType();
                    offeringType.setCode(offeringTypeEnum.name());
                    offeringType.setName(offeringTypeEnum.getDescription());
                    return offeringType;
                })
                .toList();
        offeringTypeRepository.saveAll(toSave);
    }
}
