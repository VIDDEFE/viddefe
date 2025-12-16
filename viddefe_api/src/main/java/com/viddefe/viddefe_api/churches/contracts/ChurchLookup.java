package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;

import java.util.UUID;

public interface ChurchLookup {
    ChurchModel getChurchById(UUID id);
    PeopleModel getPastorByChurch(ChurchModel church);
}
