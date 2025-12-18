package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.contracts.ChurchMembershipService;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementación del servicio de membresía de iglesias.
 * 
 * Este servicio gestiona la relación entre personas e iglesias,
 * manteniéndose dentro del dominio de People pero usando ChurchLookup
 * (interfaz de solo lectura) para obtener referencias de iglesias.
 * 
 * NO HAY RIESGO DE CICLO porque:
 * - ChurchLookupImpl NO depende de ningún servicio de People
 * - Este servicio solo CONSUME datos de Churches, no los modifica
 */
@Service
@RequiredArgsConstructor
public class ChurchMembershipServiceImpl implements ChurchMembershipService {
    
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;
    private final ChurchLookup churchLookup;
    
    @Override
    @Transactional
    public PeopleModel assignPersonToChurchAsPastor(UUID personId, UUID churchId) {
        PeopleModel person = findPersonOrThrow(personId);
        ChurchModel church = churchLookup.getChurchById(churchId);
        PeopleTypeModel pastorType = peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.name());
        
        person.setChurch(church);
        person.setTypePerson(pastorType);
        
        return peopleRepository.save(person);
    }
    
    @Override
    @Transactional
    public PeopleModel assignPersonToChurch(UUID personId, UUID churchId, Long typePersonId) {
        PeopleModel person = findPersonOrThrow(personId);
        ChurchModel church = churchLookup.getChurchById(churchId);
        PeopleTypeModel type = peopleTypeService.getPeopleTypeById(typePersonId);
        
        person.setChurch(church);
        person.setTypePerson(type);
        
        return peopleRepository.save(person);
    }
    
    @Override
    @Transactional
    public PeopleModel removeChurchAssignment(UUID personId) {
        PeopleModel person = findPersonOrThrow(personId);
        person.setChurch(null);
        return peopleRepository.save(person);
    }
    
    @Override
    @Transactional
    public PeopleModel transferToChurch(UUID personId, UUID newChurchId) {
        PeopleModel person = findPersonOrThrow(personId);
        ChurchModel newChurch = churchLookup.getChurchById(newChurchId);
        person.setChurch(newChurch);
        return peopleRepository.save(person);
    }
    
    private PeopleModel findPersonOrThrow(UUID personId) {
        return peopleRepository.findById(personId)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + personId));
    }
}
