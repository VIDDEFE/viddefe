package com.viddefe.viddefe_api.homegroups.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.viddefe.viddefe_api.homegroups.contracts.RolesStrategiesReader;
import com.viddefe.viddefe_api.homegroups.domain.model.RolesStrategiesModel;
import com.viddefe.viddefe_api.homegroups.domain.repository.RolesStrategyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolesStrategiesReaderImpl implements RolesStrategiesReader {
    private final RolesStrategyRepository rolesStrategyRepository;

    @Override
    public RolesStrategiesModel getRoleStrategyById(UUID roleId) {
        return rolesStrategyRepository.findById(roleId).orElseThrow(
                () -> new IllegalArgumentException("Rol no encontrado")
        );
    }
}
