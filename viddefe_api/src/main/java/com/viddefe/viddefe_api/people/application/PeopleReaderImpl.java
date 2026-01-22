package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación de solo lectura para consultas de personas.
 * 
 * IMPORTANTE: Esta clase NO debe tener dependencias con otros dominios
 * que puedan crear ciclos. Solo depende de repositorios del dominio People.
 * 
 * Es segura para ser inyectada en cualquier otro servicio.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PeopleReaderImpl implements PeopleReader {
    
    private final PeopleRepository peopleRepository;
    private final PeopleTypeService peopleTypeService;
    
    @Override
    public PeopleModel getPeopleById(UUID id) {
        return peopleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found: " + id));
    }
    
    @Override
    public Optional<PeopleModel> findPeopleById(UUID id) {
        return peopleRepository.findById(id);
    }
    
    @Override
    public Optional<PeopleModel> getPastorByCcWithoutChurch(String cc) {
        PeopleTypeModel pastorType = peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.getLabel());
        return peopleRepository.findByCcAndTypePersonAndChurchIsNull(cc, pastorType);
    }
    
    @Override
    public boolean existsPastorByCcWithoutChurch(String cc) {
        PeopleTypeModel pastorType = peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.getLabel());
        return peopleRepository.findByCcAndTypePersonAndChurchIsNull(cc, pastorType).isPresent();
    }

    @Override
    public List<PeopleModel> getPeopleByIds(List<UUID> ids) {
        return peopleRepository.findAllById(ids);
    }

    @Override
    public void verifyPersonExistsByCcAndChurchId(String cc, UUID churchId) {
        System.out.println("Hey bro why?");
        System.out.println("Hey bro why? cc: " + cc + ", churchId: " + churchId);
        peopleRepository.findByCcAndChurchId(cc, churchId)
                .ifPresent(p -> {
                    throw new IllegalArgumentException(
                            "La persona con cédula " + cc + " ya existe en la iglesia"
                    );
                });
    }

}
