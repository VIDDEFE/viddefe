package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;

import java.util.UUID;

public interface ChurchPastorService {
    PeopleModel getPastorFromChurch(ChurchModel church);
    ChurchPastor addPastorToChurch(UUID pastorId, ChurchModel church);
    void removePastorFromChurch(ChurchModel church);
    ChurchPastor changeChurchPastor(UUID newPastorId, ChurchModel church);
}
