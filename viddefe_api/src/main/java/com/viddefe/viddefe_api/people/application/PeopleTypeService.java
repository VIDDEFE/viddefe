package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleTypeRepository;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PeopleTypeService {
    private final PeopleTypeRepository peopleTypeRepository;

    public List<PeopleTypeDto> getAllPeopleTypes() {
        return peopleTypeRepository.findAll()
                .stream()
                .map(PeopleTypeModel::toDto)
                .toList();
    }

    public PeopleTypeModel getPeopleTypeById(Long id) {
        return peopleTypeRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("Tipo de persona no encontrado: " + id));
    }

    public PeopleTypeModel getPeopleTypeByName(String name) {
        return peopleTypeRepository.findByName(name).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("Tipo de persona no encontrado: " + name));
    }
}