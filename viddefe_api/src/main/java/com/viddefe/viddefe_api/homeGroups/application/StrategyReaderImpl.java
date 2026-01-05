package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.StrategyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
