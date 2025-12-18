package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.contracts.PeopleService;
import com.viddefe.viddefe_api.people.contracts.PeopleWriter;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio de fachada para operaciones de personas.
 * 
 * Este servicio ahora delega a las interfaces segregadas:
 * - PeopleReader: Para consultas
 * - PeopleWriter: Para escrituras
 * 
 * Sigue el patrón Facade para mantener compatibilidad con los controladores
 * existentes mientras internamente usa la arquitectura mejorada.
 */
@Service
@RequiredArgsConstructor
public class PeopleServiceImpl implements PeopleService {
    
    private final PeopleRepository peopleRepository;
    private final PeopleWriter peopleWriter;
    private final PeopleReader peopleReader;

    @Override
    public PeopleResDto createPeople(PeopleDTO dto) {
        return peopleWriter.createPerson(dto).toDto();
    }

    @Override
    public Page<PeopleResDto> getAllPeople(Pageable pageable) {
        return peopleRepository.findAll(pageable).map(PeopleModel::toDto);
    }

    @Override
    public PeopleResDto updatePeople(PeopleDTO dto, UUID id) {
        return peopleWriter.updatePerson(dto, id).toDto();
    }

    @Override
    public void deletePeople(UUID id) {
        peopleWriter.deletePerson(id);
    }

    @Override
    public PeopleResDto getPeopleById(UUID id) {
        return peopleReader.getPeopleById(id).toDto();
    }
}

