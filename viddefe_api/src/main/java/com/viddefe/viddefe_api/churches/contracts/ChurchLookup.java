package com.viddefe.viddefe_api.churches.contracts;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;

import java.util.UUID;

public interface ChurchLookup {
    ChurchModel getChurchById(UUID id);
}
