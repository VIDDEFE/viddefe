package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchPastorService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchPastorRepository;
import com.viddefe.viddefe_api.people.contracts.ChurchMembershipService;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio para gestionar la relación Pastor-Iglesia.
 * 
 * ARQUITECTURA SIN CICLOS:
 * - PeopleReader: Solo lectura, sin dependencias externas → SEGURO
 * - ChurchMembershipService: Pertenece a People domain, usa ChurchLookup (solo lectura) → SEGURO
 * 
 * Flujo de dependencias:
 * ChurchPastorImpl → PeopleReader (sin dependencias circulares)
 *                 → ChurchMembershipService → ChurchLookup (sin dependencias circulares)
 */
@Service
@RequiredArgsConstructor
public class ChurchPastorImpl implements ChurchPastorService {
    
    private final ChurchPastorRepository churchPastorRepository;
    private final PeopleReader peopleReader;
    private final ChurchMembershipService churchMembershipService;

    @Override
    @Transactional
    public ChurchPastor addPastorToChurch(@NonNull UUID pastorId, @NonNull ChurchModel church) {
        // Verificar que el pastor existe
        PeopleModel pastor = peopleReader.getPeopleById(pastorId);

        // Crear la relación ChurchPastor
        ChurchPastor churchPastor = new ChurchPastor();
        churchPastor.setPastor(pastor);
        churchPastor.setChurch(church);
        churchPastor = churchPastorRepository.save(churchPastor);
        
        // Actualizar la membresía del pastor (delegar al servicio apropiado)
        churchMembershipService.assignPersonToChurchAsPastor(pastorId, church.getId());
        
        return churchPastor;
    }

    @Override
    @Transactional
    public void removePastorFromChurch(@NonNull ChurchModel church) {
        ChurchPastor churchPastor = churchPastorRepository.findByChurch(church)
                .orElseThrow(() -> new EntityNotFoundException("Church has no assigned pastor"));
        
        UUID pastorId = churchPastor.getPastor().getId();
        
        // Remover asignación de iglesia del pastor
        churchMembershipService.removeChurchAssignment(pastorId);
        
        // Eliminar la relación ChurchPastor
        churchPastorRepository.delete(churchPastor);
    }

    @Override
    @Transactional(readOnly = true)
    public PeopleModel getPastorFromChurch(@NonNull ChurchModel church) {
        // Usa findByChurchWithPastorRelations para evitar N+1 al acceder a pastor.state y pastor.typePerson
        ChurchPastor churchPastor = churchPastorRepository.findByChurchWithPastorRelations(church)
                .orElseThrow(() -> new EntityNotFoundException("Church has no assigned pastor"));
        return churchPastor.getPastor();
    }

    @Override
    @Transactional
    public ChurchPastor changeChurchPastor(@NonNull UUID newPastorId, @NonNull ChurchModel church) {
        // Verificar que el nuevo pastor existe
        peopleReader.getPeopleById(newPastorId);
        
        ChurchPastor churchPastor = churchPastorRepository.findByChurch(church)
                .orElseThrow(() -> new EntityNotFoundException("Church has no assigned pastor"));
        
        UUID oldPastorId = churchPastor.getPastor().getId();
        
        // Remover iglesia del pastor antiguo
        churchMembershipService.removeChurchAssignment(oldPastorId);
        
        // Asignar iglesia al nuevo pastor
        PeopleModel newPastor = churchMembershipService.assignPersonToChurchAsPastor(newPastorId, church.getId());
        
        // Actualizar la relación ChurchPastor
        churchPastor.setPastor(newPastor);
        return churchPastorRepository.save(churchPastor);
    }
}
