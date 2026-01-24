package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.contracts.PeopleWriter;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityPeopleRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.PersonCreatedQualityAttendanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación de escritura para operaciones de personas.
 * 
 * Esta clase maneja la creación y modificación de personas.
 * Usa ChurchLookup (solo lectura) para obtener referencias de iglesias,
 * lo cual es seguro y no crea ciclos.
 */
@Service
@RequiredArgsConstructor
public class PeopleWriterImpl implements PeopleWriter {
    
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;
    private final StatesCitiesService statesCitiesService;
    private final ChurchLookup churchLookup;
    private final PeopleReader peopleReader;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public PeopleModel createPerson(PeopleDTO dto) {
        peopleReader.verifyPersonExistsByCcAndChurchId(dto.getCc(), dto.getChurchId());
        PeopleModel person = buildPersonFromDto(dto);
        PeopleModel saved = peopleRepository.save(person);
        PersonCreatedQualityAttendanceEvent event = new PersonCreatedQualityAttendanceEvent(saved.getId(), dto.getChurchId());
        applicationEventPublisher.publishEvent(event.getPersonId());
        return saved;
    }
    
    @Override
    @Transactional
    public PeopleModel updatePerson(PeopleDTO dto, UUID id) {
        PeopleModel person = peopleRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Person not found: " + id));
        
        person.fromDto(dto);
        if(!person.getCc().equals(dto.getCc())) {
            peopleReader.verifyPersonExistsByCcAndChurchId(dto.getCc(), dto.getId());
        }
        // Actualizar relaciones si se proporcionan
        if (dto.getTypePersonId() != null) {
            person.setTypePerson(peopleTypeService.getPeopleTypeById(dto.getTypePersonId()));
        }
        if (dto.getStateId() != null) {
            person.setState(statesCitiesService.foundStatesById(dto.getStateId()));
        }
        if (dto.getChurchId() != null) {
            person.setChurch(churchLookup.getChurchById(dto.getChurchId()));
        }
        
        return peopleRepository.save(person);
    }
    
    @Override
    @Transactional
    public void deletePerson(UUID id) {
        if (!peopleRepository.existsById(id)) {
            throw new CustomExceptions.ResourceNotFoundException("Person not found: " + id);
        }
        peopleRepository.deleteById(id);
    }
    
    /**
     * Construye un PeopleModel a partir de un DTO, resolviendo todas las relaciones.
     */
    private PeopleModel buildPersonFromDto(PeopleDTO dto) {
        PeopleModel person = new PeopleModel().fromDto(dto);
        
        // Resolver tipo de persona
        PeopleTypeModel type = peopleTypeService.getPeopleTypeById(dto.getTypePersonId());
        person.setTypePerson(type);
        
        // Resolver estado/ubicación
        StatesModel state = statesCitiesService.foundStatesById(dto.getStateId());
        person.setState(state);
        
        // Resolver iglesia (opcional)
        if (dto.getChurchId() != null) {
            ChurchModel church = churchLookup.getChurchById(dto.getChurchId());
            person.setChurch(church);
        }
        
        return person;
    }
}
