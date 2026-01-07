package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinistryFunctionServiceImpl implements MinistryFunctionService {
    private final MinistryFunctionRepository ministryFunctionRepository;
    private final MinistryFunctionTypeReader ministryFunctionTypeReader;
    private final PeopleReader peopleReader;

    @Override
    public MinistryFunctionDto create(CreateMinistryFunctionDto dto, UUID eventId, AttendanceEventType eventType) {
        MinistryFunction ministryFunction = new MinistryFunction();
        MinistryFunctionTypes type = ministryFunctionTypeReader.findById(dto.getRoleId());
        PeopleModel people = peopleReader.getPeopleById(dto.getPeopleId());
        ministryFunction.setEventId(eventId);
        ministryFunction.setPeople(people);
        ministryFunction.setMinistryFunctionType(type);
        return ministryFunctionRepository.save(ministryFunction).toDto();
    }

    @Override
    public MinistryFunctionDto update(UUID id, CreateMinistryFunctionDto dto, AttendanceEventType eventType) {
        MinistryFunction ministryFunction = ministryFunctionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Funcion ministerial no encontrada")
        );
        MinistryFunctionTypes type = ministryFunctionTypeReader.findById(dto.getRoleId());
        PeopleModel people = peopleReader.getPeopleById(dto.getPeopleId());
        ministryFunction.setPeople(people);
        ministryFunction.setMinistryFunctionType(type);
        return ministryFunctionRepository.save(ministryFunction).toDto();
    }

    @Override
    public List<MinistryFunctionDto> findByEventId(UUID eventId, AttendanceEventType eventType) {
        return ministryFunctionRepository.findByEventId(eventId).stream().map(MinistryFunction::toDto).toList();
    }

    @Override
    public void delete(UUID id) {
        if(ministryFunctionRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Funcion ministerial no encontrada");
        }
        ministryFunctionRepository.deleteById(id);
    }
}
