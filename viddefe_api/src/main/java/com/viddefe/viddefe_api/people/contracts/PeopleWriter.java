package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;

import java.util.UUID;

/**
 * Interfaz de escritura para operaciones de personas.
 * Sigue el principio de segregación de interfaces (ISP) y facilita CQRS.
 * 
 * Esta interfaz debe usarse solo dentro del dominio de People o por
 * servicios de aplicación que orquestan operaciones cross-domain.
 */
public interface PeopleWriter {
    
    /**
     * Crea una nueva persona en el sistema.
     * @param dto Datos de la persona a crear
     * @return PeopleModel creado
     */
    PeopleModel createPerson(PeopleDTO dto);
    
    /**
     * Actualiza una persona existente.
     * @param dto Datos actualizados
     * @param id ID de la persona a actualizar
     * @return PeopleModel actualizado
     */
    PeopleModel updatePerson(PeopleDTO dto, UUID id);
    
    /**
     * Elimina una persona.
     * @param id ID de la persona a eliminar
     */
    void deletePerson(UUID id);
}
