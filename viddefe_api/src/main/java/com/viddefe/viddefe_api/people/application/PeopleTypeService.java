package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeopleTypeService {
    private final PeopleTypeRepository peopleTypeRepository;

    public PeopleTypeModel getPeopleTypeById(Long id) {
        return peopleTypeRepository.findById(id).orElseThrow(
                () -> new CustomExceptions.ResourceNotFoundException("Tipo de persona no encontrado: " + id));
    }
}