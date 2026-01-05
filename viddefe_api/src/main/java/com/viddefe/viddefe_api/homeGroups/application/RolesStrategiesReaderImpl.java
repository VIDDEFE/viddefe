package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.contracts.RolesStrategiesReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.RolesStrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
