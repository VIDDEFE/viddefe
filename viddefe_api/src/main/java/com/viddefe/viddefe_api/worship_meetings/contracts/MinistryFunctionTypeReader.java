package com.viddefe.viddefe_api.worship_meetings.contracts;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;

import java.util.List;

public interface MinistryFunctionTypeReader {
    MinistryFunctionTypes findById(Long id);
    List<MinistryFunctionTypes> findAll();
}
