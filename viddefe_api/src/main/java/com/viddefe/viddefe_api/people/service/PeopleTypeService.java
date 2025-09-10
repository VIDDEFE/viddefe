package com.viddefe.viddefe_api.people.service;

import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.repository.PeopleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PeopleTypeService {
    private final PeopleTypeRepository peopleTypeRepository;

    public PeopleTypeModel getPeopleTypeByName(Optional<String> name) {
        String typeName = name.orElse(TypesPeople.PASTOR.name());

        TypesPeople typesPeople = TypesPeople.valueOf(typeName);

        return peopleTypeRepository.findByName(typesPeople.name())
                .orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado: " + typesPeople.name()));
    }

    public PeopleTypeModel getPeopleTypeById(Long id) {
        return peopleTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("Tipo de persona no encontrado: " + id));
    }
}