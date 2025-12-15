package com.viddefe.viddefe_api.people.contracts;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;

import java.util.UUID;

/**
 * Abstracción para obtener información de personas desde el dominio/infrastructure.
 * Permite desacoplar otros contextos de la implementación concreta (repositorio).
 */
public interface PeopleLookup {
    PeopleModel getPeopleById(UUID id);
    void enrollPersonToChurch(PeopleModel personId, ChurchModel churchModel);
}

