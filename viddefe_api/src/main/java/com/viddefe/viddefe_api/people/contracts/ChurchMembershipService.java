package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;

import java.util.UUID;

/**
 * Servicio de dominio para gestionar la membresía de personas a iglesias.
 * 
 * Este servicio pertenece al dominio de People porque modifica el estado
 * de PeopleModel, pero acepta IDs de iglesia para evitar dependencias
 * directas con el dominio de Churches.
 * 
 * Patrón: Domain Service
 * Principio: Single Responsibility - Solo maneja la asignación de iglesias a personas
 */
public interface ChurchMembershipService {
    
    /**
     * Asigna una persona a una iglesia como pastor.
     * @param personId ID de la persona
     * @param churchId ID de la iglesia
     * @return PeopleModel actualizado
     */
    PeopleModel assignPersonToChurchAsPastor(UUID personId, UUID churchId);
    
    /**
     * Asigna una persona a una iglesia con un tipo específico.
     * @param personId ID de la persona
     * @param churchId ID de la iglesia
     * @param typePersonId ID del tipo de persona
     * @return PeopleModel actualizado
     */
    PeopleModel assignPersonToChurch(UUID personId, UUID churchId, Long typePersonId);
    
    /**
     * Remueve la asignación de iglesia de una persona.
     * @param personId ID de la persona
     * @return PeopleModel actualizado
     */
    PeopleModel removeChurchAssignment(UUID personId);
    
    /**
     * Transfiere una persona de una iglesia a otra.
     * @param personId ID de la persona
     * @param newChurchId ID de la nueva iglesia
     * @return PeopleModel actualizado
     */
    PeopleModel transferToChurch(UUID personId, UUID newChurchId);
}
