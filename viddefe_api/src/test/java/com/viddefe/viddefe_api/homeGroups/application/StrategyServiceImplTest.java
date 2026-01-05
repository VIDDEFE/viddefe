package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.StrategyRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.StrategyDto;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StrategyServiceImpl Tests")
class StrategyServiceImplTest {

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private ChurchLookup churchLookup;

    @InjectMocks
    private StrategyServiceImpl strategyService;

    @Captor
    private ArgumentCaptor<StrategiesModel> strategyCaptor;

    private UUID churchId;
    private UUID strategyId;
    private ChurchModel church;
    private StrategyDto strategyDto;
    private StrategiesModel existingStrategy;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        strategyId = UUID.randomUUID();

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Iglesia Central");

        strategyDto = new StrategyDto();
        strategyDto.setName("Estrategia G12");

        existingStrategy = new StrategiesModel();
        existingStrategy.setId(strategyId);
        existingStrategy.setName("Estrategia G12");
        existingStrategy.setChurch(church);
    }

    @Nested
    @DisplayName("Create Strategy Tests")
    class CreateStrategyTests {

        @Test
        @DisplayName("Debe crear una estrategia correctamente")
        void create_WithValidData_ShouldSaveStrategy() {
            // Arrange
            when(strategyRepository.existsByNameAndChurchId(strategyDto.getName(), churchId))
                    .thenReturn(false);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(strategyRepository.save(any(StrategiesModel.class))).thenReturn(existingStrategy);

            // Act
            strategyService.create(strategyDto, churchId);

            // Assert
            verify(strategyRepository).save(strategyCaptor.capture());
            StrategiesModel captured = strategyCaptor.getValue();
            assertEquals("Estrategia G12", captured.getName());
            assertEquals(church, captured.getChurch());
        }

        @Test
        @DisplayName("Debe lanzar excepción si ya existe una estrategia con el mismo nombre")
        void create_WhenNameExists_ShouldThrowDataIntegrityViolationException() {
            // Arrange
            when(strategyRepository.existsByNameAndChurchId(strategyDto.getName(), churchId))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> strategyService.create(strategyDto, churchId)
            );
            assertTrue(exception.getMessage().contains("Ya existe una estrategia con ese nombre"));
            verify(strategyRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe asignar la iglesia correctamente")
        void create_ShouldAssignChurchCorrectly() {
            // Arrange
            when(strategyRepository.existsByNameAndChurchId(strategyDto.getName(), churchId))
                    .thenReturn(false);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);

            // Act
            strategyService.create(strategyDto, churchId);

            // Assert
            verify(churchLookup).getChurchById(churchId);
            verify(strategyRepository).save(strategyCaptor.capture());
            assertEquals(church, strategyCaptor.getValue().getChurch());
        }
    }

    @Nested
    @DisplayName("Update Strategy Tests")
    class UpdateStrategyTests {

        @Test
        @DisplayName("Debe actualizar una estrategia existente correctamente")
        void update_WithValidData_ShouldReturnUpdatedStrategyDto() {
            // Arrange
            StrategyDto updateDto = new StrategyDto();
            updateDto.setName("Nueva Estrategia");

            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(existingStrategy));
            when(strategyRepository.existsByNameAndChurchId("Nueva Estrategia", churchId))
                    .thenReturn(false);
            when(strategyRepository.save(any(StrategiesModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            StrategyDto result = strategyService.update(updateDto, churchId, strategyId);

            // Assert
            assertNotNull(result);
            verify(strategyRepository).save(any(StrategiesModel.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la estrategia no existe")
        void update_WhenStrategyNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> strategyService.update(strategyDto, churchId, strategyId)
            );
            assertEquals("Estrategia no encontrada", exception.getMessage());
        }

        @Test
        @DisplayName("No debe verificar duplicado si el nombre no cambia")
        void update_WhenNameNotChanged_ShouldNotCheckForDuplicate() {
            // Arrange
            StrategyDto updateDto = new StrategyDto();
            updateDto.setName("Estrategia G12"); // mismo nombre

            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(existingStrategy));
            when(strategyRepository.save(any(StrategiesModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            strategyService.update(updateDto, churchId, strategyId);

            // Assert
            verify(strategyRepository, never()).existsByNameAndChurchId(any(), any());
        }

        @Test
        @DisplayName("Debe verificar duplicado si el nombre cambia")
        void update_WhenNameChanged_ShouldCheckForDuplicate() {
            // Arrange
            StrategyDto updateDto = new StrategyDto();
            updateDto.setName("Nuevo Nombre");

            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(existingStrategy));
            when(strategyRepository.existsByNameAndChurchId("Nuevo Nombre", churchId))
                    .thenReturn(false);
            when(strategyRepository.save(any(StrategiesModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            strategyService.update(updateDto, churchId, strategyId);

            // Assert
            verify(strategyRepository).existsByNameAndChurchId("Nuevo Nombre", churchId);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el nuevo nombre ya existe")
        void update_WhenNewNameExists_ShouldThrowDataIntegrityViolationException() {
            // Arrange
            StrategyDto updateDto = new StrategyDto();
            updateDto.setName("Nombre Duplicado");

            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(existingStrategy));
            when(strategyRepository.existsByNameAndChurchId("Nombre Duplicado", churchId))
                    .thenReturn(true);

            // Act & Assert
            assertThrows(DataIntegrityViolationException.class,
                    () -> strategyService.update(updateDto, churchId, strategyId));
            verify(strategyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Find All Strategies Tests")
    class FindAllStrategiesTests {

        @Test
        @DisplayName("Debe retornar lista de estrategias")
        void findAll_ShouldReturnListOfStrategies() {
            // Arrange
            StrategiesModel strategy1 = new StrategiesModel();
            strategy1.setId(UUID.randomUUID());
            strategy1.setName("Estrategia 1");

            StrategiesModel strategy2 = new StrategiesModel();
            strategy2.setId(UUID.randomUUID());
            strategy2.setName("Estrategia 2");

            when(strategyRepository.findAll()).thenReturn(List.of(strategy1, strategy2));

            // Act
            List<StrategyDto> result = strategyService.findAll();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay estrategias")
        void findAll_WhenNoStrategies_ShouldReturnEmptyList() {
            // Arrange
            when(strategyRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<StrategyDto> result = strategyService.findAll();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe mapear correctamente a DTOs")
        void findAll_ShouldMapToDtosCorrectly() {
            // Arrange
            when(strategyRepository.findAll()).thenReturn(List.of(existingStrategy));

            // Act
            List<StrategyDto> result = strategyService.findAll();

            // Assert
            assertEquals(1, result.size());
            assertEquals("Estrategia G12", result.get(0).getName());
            assertEquals(strategyId, result.get(0).getId());
        }
    }

    @Nested
    @DisplayName("Delete Strategy Tests")
    class DeleteStrategyTests {

        @Test
        @DisplayName("Debe eliminar una estrategia existente correctamente")
        void deleteById_WhenExists_ShouldDeleteSuccessfully() {
            // Arrange
            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(existingStrategy));
            doNothing().when(strategyRepository).delete(existingStrategy);

            // Act
            strategyService.deleteById(strategyId);

            // Assert
            verify(strategyRepository).delete(existingStrategy);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la estrategia no existe")
        void deleteById_WhenNotExists_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(strategyRepository.findById(strategyId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> strategyService.deleteById(strategyId)
            );
            assertEquals("Estrategia no encontrada", exception.getMessage());
            verify(strategyRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Debe buscar la estrategia antes de eliminar")
        void deleteById_ShouldFindBeforeDelete() {
            // Arrange
            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(existingStrategy));

            // Act
            strategyService.deleteById(strategyId);

            // Assert
            verify(strategyRepository).findById(strategyId);
            verify(strategyRepository).delete(existingStrategy);
        }
    }
}

