package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinistryFunctionTypeReaderImpl implements MinistryFunctionTypeReader {
    private final MinistryFunctionTypesRepository ministryFunctionTypesRepository;
    @Override
    public MinistryFunctionTypes findById(Long id) {
        return ministryFunctionTypesRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Tipo de rol no encontrado")
        );
    }
}
