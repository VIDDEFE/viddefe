package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.contracts.RolesStrategiesService;
import com.viddefe.viddefe_api.homeGroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.RolPeopleStrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.RolesStrategyRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateRolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolPeopleStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolesStrategiesDto;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleResDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesStrategiesServiceImpl implements RolesStrategiesService {

    private final RolesStrategyRepository rolesStrategyRepository;
    private final StrategyReader strategyReader;

    @Override
    @Transactional
    public RolesStrategiesDto create(CreateRolesStrategiesDto dto, UUID strategyId) {

        StrategiesModel strategy = strategyReader.findById(strategyId);

        RolesStrategiesModel parent = null;

        if (dto.getParentRoleId() != null) {
            parent = rolesStrategyRepository.findById(dto.getParentRoleId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("No se encontró el rol padre")
                    );

            // Regla 1: mismo strategy
            if (!parent.getStrategy().getId().equals(strategyId)) {
                throw new DataIntegrityViolationException(
                        "El rol padre pertenece a otra estrategia"
                );
            }
        }

        RolesStrategiesModel role = new RolesStrategiesModel().fromDto(dto);
        role.setStrategy(strategy);
        role.setParentRole(parent);

        rolesStrategyRepository.save(role);

        return role.toDto();
    }

    @Override
    @Transactional
    public RolesStrategiesDto update(CreateRolesStrategiesDto dto, UUID strategyId, UUID roleId) {

        RolesStrategiesModel role = rolesStrategyRepository.findById(roleId)
                .orElseThrow(() ->
                        new EntityNotFoundException("No se encontró el rol")
                );

        if (!role.getStrategy().getId().equals(strategyId)) {
            throw new DataIntegrityViolationException(
                    "El rol no pertenece a la estrategia indicada"
            );
        }

        role.fromDto(dto);
        RolesStrategiesModel newParent = null;

        if (dto.getParentRoleId() != null) {
            newParent = rolesStrategyRepository.findById(dto.getParentRoleId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("No se encontró el rol padre")
                    );

            // Regla 1: mismo strategy
            if (!newParent.getStrategy().getId().equals(strategyId)) {
                throw new DataIntegrityViolationException(
                        "El rol padre pertenece a otra estrategia"
                );
            }

            // Regla 2: no ciclos
            validateNoCycles(role, newParent);
        }

        role.setParentRole(newParent);

        return role.toDto();
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        RolesStrategiesModel role = rolesStrategyRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("No se encontró el rol")
                );

        // Política clara: NO borrar si tiene hijos
        if (!role.getChildren().isEmpty()) {
            throw new DataIntegrityViolationException(
                    "No se puede eliminar el rol porque tiene roles hijos"
            );
        }

        rolesStrategyRepository.delete(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolesStrategiesDto> getTree(UUID strategyId) {

        // 1. Traer todos los roles de la estrategia (flat)
        List<RolesStrategiesModel> roles =
                rolesStrategyRepository.findAllByStrategyId(strategyId);

        // 2. Mapa indexado por ID (lookup O(1))
        Map<UUID, RolesStrategiesDto> index = new HashMap<>();

        // 3. Raíces ordenadas (TreeMap para orden estable)
        Map<String, RolesStrategiesDto> roots = new TreeMap<>();

        // 4. Crear nodos sin relaciones
        for (RolesStrategiesModel role : roles) {
            RolesStrategiesDto node = new RolesStrategiesDto();
            node.setId(role.getId());
            node.setName(role.getName());
            Set<PeopleResDto> people = role.getRolPeople().stream().map(
                    RolPeopleStrategiesModel::getPerson
            ).map(PeopleModel::toDto).collect(Collectors.toSet());
            node.setPeople(people);
            index.put(role.getId(), node);
        }

        // 5. Armar jerarquía
        for (RolesStrategiesModel role : roles) {
            RolesStrategiesDto current = index.get(role.getId());

            if (role.getParentRole() == null) {
                // root
                roots.put(current.getName(), current);
            } else {
                RolesStrategiesDto parent = index.get(role.getParentRole().getId());
                parent.getChildren().add(current);
            }
        }

        // 6. Ordenar hijos recursivamente (opcional pero recomendado)
        roots.values().forEach(this::sortRecursively);

        return new ArrayList<>(roots.values());
    }

    private void sortRecursively(RolesStrategiesDto node) {
        node.getChildren().stream().sorted(Comparator.comparing(RolesStrategiesDto::getName));
        node.getChildren().forEach(this::sortRecursively);
    }

    private void validateNoCycles(
            RolesStrategiesModel role,
            RolesStrategiesModel newParent
    ) {
        RolesStrategiesModel current = newParent;

        while (current != null) {
            if (current.getId().equals(role.getId())) {
                throw new DataIntegrityViolationException(
                        "Se detectó una jerarquía circular de roles"
                );
            }
            current = current.getParentRole();
        }
    }
}
