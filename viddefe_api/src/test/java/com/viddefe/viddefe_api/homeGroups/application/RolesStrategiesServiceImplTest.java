package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.RolesStrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.RolesStrategyRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.CreateRolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolesStrategiesDto;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.RolesStrategiesWithPeopleDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RolesStrategiesServiceImpl Tests")
class RolesStrategiesServiceImplTest {

    @Mock
    private RolesStrategyRepository rolesStrategyRepository;

    @Mock
    private StrategyReader strategyReader;

    @InjectMocks
    private RolesStrategiesServiceImpl rolesStrategiesService;

    @Captor
    private ArgumentCaptor<RolesStrategiesModel> roleCaptor;

    private UUID strategyId;
    private UUID roleId;
    private UUID parentRoleId;
    private StrategiesModel strategy;
    private RolesStrategiesModel existingRole;
    private RolesStrategiesModel parentRole;
    private CreateRolesStrategiesDto createDto;

    @BeforeEach
    void setUp() throws Exception {
        strategyId = UUID.randomUUID();
        roleId = UUID.randomUUID();
        parentRoleId = UUID.randomUUID();

        strategy = new StrategiesModel();
        strategy.setId(strategyId);
        strategy.setName("Estrategia G12");

        parentRole = new RolesStrategiesModel();
        parentRole.setId(parentRoleId);
        parentRole.setName("Líder Principal");
        parentRole.setStrategy(strategy);
        parentRole.setChildren(new HashSet<>());

        existingRole = new RolesStrategiesModel();
        existingRole.setId(roleId);
        existingRole.setName("Sub-Líder");
        existingRole.setStrategy(strategy);
        existingRole.setParentRole(null);
        existingRole.setChildren(new HashSet<>());

        createDto = createRolesStrategiesDto("Nuevo Rol", null);
    }

    private CreateRolesStrategiesDto createRolesStrategiesDto(String name, UUID parentRoleId) throws Exception {
        CreateRolesStrategiesDto dto = new CreateRolesStrategiesDto();
        setField(dto, "name", name);
        setField(dto, "parentRoleId", parentRoleId);
        return dto;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    @Nested
    @DisplayName("Create Role Tests")
    class CreateRoleTests {

        @Test
        @DisplayName("Debe crear un rol sin padre correctamente")
        void create_WithoutParent_ShouldReturnRolesStrategiesDto() {
            // Arrange
            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(rolesStrategyRepository.save(any(RolesStrategiesModel.class)))
                    .thenAnswer(inv -> {
                        RolesStrategiesModel saved = inv.getArgument(0);
                        saved.setId(roleId);
                        return saved;
                    });

            // Act
            RolesStrategiesDto result = rolesStrategiesService.create(createDto, strategyId);

            // Assert
            assertNotNull(result);
            verify(rolesStrategyRepository).save(roleCaptor.capture());
            RolesStrategiesModel captured = roleCaptor.getValue();
            assertEquals("Nuevo Rol", captured.getName());
            assertEquals(strategy, captured.getStrategy());
            assertNull(captured.getParentRole());
        }

        @Test
        @DisplayName("Debe crear un rol con padre correctamente")
        void create_WithParent_ShouldSetParentRole() throws Exception {
            // Arrange
            CreateRolesStrategiesDto dtoWithParent = createRolesStrategiesDto("Rol Hijo", parentRoleId);

            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(rolesStrategyRepository.findById(parentRoleId)).thenReturn(Optional.of(parentRole));
            when(rolesStrategyRepository.save(any(RolesStrategiesModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            rolesStrategiesService.create(dtoWithParent, strategyId);

            // Assert
            verify(rolesStrategyRepository).save(roleCaptor.capture());
            assertEquals(parentRole, roleCaptor.getValue().getParentRole());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el rol padre no existe")
        void create_WhenParentNotFound_ShouldThrowEntityNotFoundException() throws Exception {
            // Arrange
            CreateRolesStrategiesDto dtoWithParent = createRolesStrategiesDto("Rol Hijo", parentRoleId);

            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(rolesStrategyRepository.findById(parentRoleId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> rolesStrategiesService.create(dtoWithParent, strategyId));
            verify(rolesStrategyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el rol padre pertenece a otra estrategia")
        void create_WhenParentBelongsToDifferentStrategy_ShouldThrowException() throws Exception {
            // Arrange
            UUID otherStrategyId = UUID.randomUUID();
            StrategiesModel otherStrategy = new StrategiesModel();
            otherStrategy.setId(otherStrategyId);

            RolesStrategiesModel parentFromOtherStrategy = new RolesStrategiesModel();
            parentFromOtherStrategy.setId(parentRoleId);
            parentFromOtherStrategy.setStrategy(otherStrategy);

            CreateRolesStrategiesDto dtoWithParent = createRolesStrategiesDto("Rol Hijo", parentRoleId);

            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(rolesStrategyRepository.findById(parentRoleId))
                    .thenReturn(Optional.of(parentFromOtherStrategy));

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> rolesStrategiesService.create(dtoWithParent, strategyId)
            );
            assertTrue(exception.getMessage().contains("El rol padre pertenece a otra estrategia"));
        }
    }

    @Nested
    @DisplayName("Update Role Tests")
    class UpdateRoleTests {

        @Test
        @DisplayName("Debe actualizar un rol existente correctamente")
        void update_WithValidData_ShouldReturnUpdatedDto() throws Exception {
            // Arrange
            CreateRolesStrategiesDto updateDto = createRolesStrategiesDto("Rol Actualizado", null);

            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

            // Act
            RolesStrategiesDto result = rolesStrategiesService.update(updateDto, strategyId, roleId);

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el rol no existe")
        void update_WhenRoleNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> rolesStrategiesService.update(createDto, strategyId, roleId));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el rol no pertenece a la estrategia indicada")
        void update_WhenRoleBelongsToDifferentStrategy_ShouldThrowException() {
            // Arrange
            UUID otherStrategyId = UUID.randomUUID();
            StrategiesModel otherStrategy = new StrategiesModel();
            otherStrategy.setId(otherStrategyId);
            existingRole.setStrategy(otherStrategy);

            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

            // Act & Assert
            assertThrows(DataIntegrityViolationException.class,
                    () -> rolesStrategiesService.update(createDto, strategyId, roleId));
        }

        @Test
        @DisplayName("Debe detectar jerarquía circular y lanzar excepción")
        void update_WhenCreatingCircularHierarchy_ShouldThrowException() throws Exception {
            // Arrange - Crear jerarquía: parentRole -> existingRole
            existingRole.setParentRole(parentRole);

            // Intentar hacer que parentRole sea hijo de existingRole (circular)
            CreateRolesStrategiesDto updateDto = createRolesStrategiesDto("Parent Actualizado", roleId);

            when(rolesStrategyRepository.findById(parentRoleId)).thenReturn(Optional.of(parentRole));
            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

            // Act & Assert
            assertThrows(DataIntegrityViolationException.class,
                    () -> rolesStrategiesService.update(updateDto, strategyId, parentRoleId));
        }
    }

    @Nested
    @DisplayName("Delete Role Tests")
    class DeleteRoleTests {

        @Test
        @DisplayName("Debe eliminar un rol sin hijos correctamente")
        void delete_WhenNoChildren_ShouldDeleteSuccessfully() {
            // Arrange
            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
            doNothing().when(rolesStrategyRepository).delete(existingRole);

            // Act
            rolesStrategiesService.delete(roleId);

            // Assert
            verify(rolesStrategyRepository).delete(existingRole);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el rol no existe")
        void delete_WhenRoleNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> rolesStrategiesService.delete(roleId));
            verify(rolesStrategyRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el rol tiene hijos")
        void delete_WhenHasChildren_ShouldThrowDataIntegrityViolationException() {
            // Arrange
            RolesStrategiesModel childRole = new RolesStrategiesModel();
            childRole.setId(UUID.randomUUID());
            childRole.setName("Rol Hijo");
            existingRole.getChildren().add(childRole);

            when(rolesStrategyRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> rolesStrategiesService.delete(roleId)
            );
            assertTrue(exception.getMessage().contains("tiene roles hijos"));
            verify(rolesStrategyRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Get Tree Roles Tests")
    class GetTreeRolesTests {

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay roles")
        void getTreeRoles_WhenNoRoles_ShouldReturnEmptyList() {
            // Arrange
            when(rolesStrategyRepository.findAllByStrategyId(strategyId))
                    .thenReturn(Collections.emptyList());

            // Act
            List<RolesStrategiesDto> result = rolesStrategiesService.getTreeRoles(strategyId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe retornar árbol de roles")
        void getTreeRoles_ShouldReturnRolesTree() {
            // Arrange
            existingRole.setRolPeople(new HashSet<>());
            when(rolesStrategyRepository.findAllByStrategyId(strategyId))
                    .thenReturn(List.of(existingRole));

            // Act
            List<RolesStrategiesDto> result = rolesStrategiesService.getTreeRoles(strategyId);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Tree Roles With People Tests")
    class GetTreeRolesWithPeopleTests {

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay roles")
        void getTreeRolesWithPeople_WhenNoRoles_ShouldReturnEmptyList() {
            // Arrange
            when(rolesStrategyRepository.findAllByStrategyId(strategyId))
                    .thenReturn(Collections.emptyList());

            // Act
            List<RolesStrategiesWithPeopleDto> result =
                    rolesStrategiesService.getTreeRolesWithPeople(strategyId);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe incluir personas en los roles")
        void getTreeRolesWithPeople_ShouldIncludePeopleInRoles() {
            // Arrange
            existingRole.setRolPeople(new HashSet<>());
            when(rolesStrategyRepository.findAllByStrategyId(strategyId))
                    .thenReturn(List.of(existingRole));

            // Act
            List<RolesStrategiesWithPeopleDto> result =
                    rolesStrategiesService.getTreeRolesWithPeople(strategyId);

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }
}

