package com.viddefe.viddefe_api.catalogs.Seeder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viddefe.viddefe_api.catalogs.Model.StatesModel;
import com.viddefe.viddefe_api.catalogs.Repository.StatesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatesCitiesSeeder implements CommandLineRunner {

    private final StatesRepository statesRepository;
    private final ObjectMapper objectMapper;

    @Value("classpath:data_geo.json")
    private Resource jsonResource;

    @Override
    public void run(String... args) throws Exception {
        if (!statesRepository.findAll().isEmpty()) {
            System.out.println("⚠️ States already seeded, skipping...");
        }
        try (InputStream inputStream = jsonResource.getInputStream()) {
            List<StatesModel> states = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<StatesModel>>() {}
            );
            statesRepository.saveAll(states);
            System.out.println("✅ States seeded successfully");
        }
    }
}