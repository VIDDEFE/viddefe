package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz de solo lectura para consultas de personas.
 * Sigue el principio de segregación de interfaces (ISP) y facilita CQRS.
 * 
 * Esta interfaz es segura para ser inyectada en otros dominios sin riesgo
 * de crear ciclos de dependencia.
 */
public interface PeopleReader {
    
    /**
     * Obtiene una persona por su ID.
     * @param id UUID de la persona
     * @return PeopleModel encontrado
     * @throws jakarta.persistence.EntityNotFoundException si no existe
     */
    PeopleModel getPeopleById(UUID id);
    
    /**
     * Busca una persona por ID de forma segura.
     * @param id UUID de la persona
     * @return Optional con la persona si existe
     */
    Optional<PeopleModel> findPeopleById(UUID id);
    
    /**
     * Busca un pastor por CC que no tenga iglesia asignada.
     * @param cc Cédula de ciudadanía
     * @return PeopleModel del pastor
     * @throws jakarta.persistence.EntityNotFoundException si no existe
     */
    Optional<PeopleModel> getPastorByCcWithoutChurch(String cc);

    /**
     * Verifica si existe un pastor con el CC dado sin iglesia asignada.
     * @param cc Cédula de ciudadanía
     * @return true si existe
     */
    boolean existsPastorByCcWithoutChurch(String cc);

    /**
     * Obtiene una lista de personas por sus IDs.
     * @param ids Lista de UUIDs de personas
     * @return Lista de PeopleModel encontrados
     */
    List<PeopleModel> getPeopleByIds(List<UUID> ids);

    void verifyPersonExistsByCcAndChurchId(String cc, UUID churchId);
}
