package com.viddefe.viddefe_api.people.domain.seeder;

import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PeopleTypeSeeder implements CommandLineRunner {
    private final PeopleTypeRepository peopleTypesRepository;
    @Override
    public void run(String... args) throws Exception {
        if(!peopleTypesRepository.findAll().isEmpty()) {
            return;
        }
        List<PeopleTypeModel> typesPeople = Arrays.stream(TypesPeople.values()).map(item -> {
            PeopleTypeModel model = new PeopleTypeModel();
            model.setName(item.name());
            return model;
        }).toList();
        peopleTypesRepository.saveAll(typesPeople);
    }
}
