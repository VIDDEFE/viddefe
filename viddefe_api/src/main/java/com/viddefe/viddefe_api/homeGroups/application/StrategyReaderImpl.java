package com.viddefe.viddefe_api.homegroups.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.homegroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homegroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homegroups.domain.repository.StrategyRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StrategyReaderImpl implements StrategyReader {

    private final StrategyRepository strategyRepository;

    @Override
    public boolean existsByName(String name) {
        return strategyRepository.existsByName(name);
    }

    @Override
    public StrategiesModel findByName(String name) {
        return strategyRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException("Strategy not found")
        );
    }

    @Override
    public StrategiesModel findById(UUID id) {
        return strategyRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Strategy not found")
        );
    }
}
