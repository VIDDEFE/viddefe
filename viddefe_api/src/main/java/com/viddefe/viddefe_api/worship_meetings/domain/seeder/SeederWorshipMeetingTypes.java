package com.viddefe.viddefe_api.worship_meetings.domain.seeder;

import com.viddefe.viddefe_api.worship_meetings.configuration.TempleMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.models.WorshipMeetingTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.WorshipTypesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederWorshipMeetingTypes {
    private final WorshipTypesRepository worshipTypesRepository;

    @PostConstruct
    public void seed() {

        // Implement seeding logic here
        if (worshipTypesRepository.count() != 0 && worshipTypesRepository.count() != TempleMeetingTypes.values().length) {
            return;
        }
        List<String> worshipMeetingTypesBank = worshipTypesRepository.findAll().stream().map(WorshipMeetingTypes::getName).toList();
        List<WorshipMeetingTypes> toInsert = Arrays.stream(TempleMeetingTypes.values())
                .filter(type -> !worshipMeetingTypesBank.contains(type.getLabel()))
                .map(type -> {
                    WorshipMeetingTypes entity = new WorshipMeetingTypes();
                    entity.setName(type.getLabel());
                    return entity;
                })
                .toList();

        worshipTypesRepository.saveAll(toInsert);
        System.out.println("Finalizando seeder");
    }
}
