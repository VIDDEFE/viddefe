package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.worship_meetings.configuration.MinistryFunctionTypesEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionTypesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederMinistryFunctionTypes {
    private final MinistryFunctionTypesRepository ministryFunctionTypesRepository;

    @PostConstruct
    public void seed(){
        List<String> existingTypes = ministryFunctionTypesRepository.findAll()
                .stream()
                .map(MinistryFunctionTypes::getName)
                .toList();

        List<MinistryFunctionTypes> typesToSeed = Arrays.stream(MinistryFunctionTypesEnum.values())
                .filter(ministryFunctionTypesEnum -> !existingTypes.contains(ministryFunctionTypesEnum.getLabel()))
                .map(gt -> new MinistryFunctionTypes(null, gt.getLabel()))
                .toList();
        ministryFunctionTypesRepository.saveAll(typesToSeed);
    }
}
