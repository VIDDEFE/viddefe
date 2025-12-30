package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.contracts.StrategyService;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.StrategyRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.StrategyDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StrategyServiceImpl implements StrategyService {
    private final StrategyRepository strategyRepository;
    private final ChurchLookup churchLookup;

    @Override
    public StrategyDto create(StrategyDto strategyDto, UUID churchId) {
        existsByNameAndChurchId(strategyDto.getName(), churchId);
        StrategiesModel strategiesModel = new StrategiesModel().fromDto(strategyDto);
        ChurchModel church = churchLookup.getChurchById(churchId);
        strategiesModel.setChurch(church);
        strategyRepository.save(strategiesModel);
        return null;
    }

    @Override
    public StrategyDto update(StrategyDto strategyDto, UUID churchId, UUID strategyId) {
        StrategiesModel existingStrategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new EntityNotFoundException("Estrategia no encontrada"));

        if (!existingStrategy.getName().equals(strategyDto.getName())) {
            existsByNameAndChurchId(strategyDto.getName(), churchId);
        }

        existingStrategy.fromDto(strategyDto);
        strategyRepository.save(existingStrategy);
        return existingStrategy.toDto();
    }


    @Override
    public List<StrategyDto> findAll() {
        return strategyRepository.findAll().stream().map(StrategiesModel::toDto).toList();
    }

    @Override
    public void deleteById(UUID strategyId) {
        StrategiesModel existingStrategy = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new EntityNotFoundException("Estrategia no encontrada"));
        strategyRepository.delete(existingStrategy);
    }

    private void existsByNameAndChurchId(String name, UUID churchId) {
        if(strategyRepository.existsByNameAndChurchId(name,churchId)){
            throw new DataIntegrityViolationException("Ya existe una estrategia con ese nombre en esta iglesia");
        }
    }
}
