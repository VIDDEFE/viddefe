package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.StrategyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StrategyReaderImpl Tests")
class StrategyReaderImplTest {

    @Mock
    private StrategyRepository strategyRepository;

    @InjectMocks
    private StrategyReaderImpl strategyReader;

    private UUID strategyId;
    private StrategiesModel strategy;

    @BeforeEach
    void setUp() {
        strategyId = UUID.randomUUID();

        strategy = new StrategiesModel();
        strategy.setId(strategyId);
        strategy.setName("Estrategia G12");
    }

    @Nested
    @DisplayName("Exists By Name Tests")
    class ExistsByNameTests {

        @Test
        @DisplayName("Debe retornar true cuando la estrategia existe")
        void existsByName_WhenExists_ShouldReturnTrue() {
            // Arrange
            when(strategyRepository.existsByName("Estrategia G12")).thenReturn(true);

            // Act
            boolean result = strategyReader.existsByName("Estrategia G12");

            // Assert
            assertTrue(result);
            verify(strategyRepository).existsByName("Estrategia G12");
        }

        @Test
        @DisplayName("Debe retornar false cuando la estrategia no existe")
        void existsByName_WhenNotExists_ShouldReturnFalse() {
            // Arrange
            when(strategyRepository.existsByName("No Existe")).thenReturn(false);

            // Act
            boolean result = strategyReader.existsByName("No Existe");

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Debe buscar con el nombre exacto")
        void existsByName_ShouldSearchWithExactName() {
            // Arrange
            String searchName = "Estrategia Especifica";
            when(strategyRepository.existsByName(searchName)).thenReturn(true);

            // Act
            strategyReader.existsByName(searchName);

            // Assert
            verify(strategyRepository, times(1)).existsByName(searchName);
        }
    }

    @Nested
    @DisplayName("Find By Name Tests")
    class FindByNameTests {

        @Test
        @DisplayName("Debe retornar estrategia cuando existe")
        void findByName_WhenExists_ShouldReturnStrategy() {
            // Arrange
            when(strategyRepository.findByName("Estrategia G12"))
                    .thenReturn(Optional.of(strategy));

            // Act
            StrategiesModel result = strategyReader.findByName("Estrategia G12");

            // Assert
            assertNotNull(result);
            assertEquals("Estrategia G12", result.getName());
            assertEquals(strategyId, result.getId());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la estrategia no existe")
        void findByName_WhenNotExists_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(strategyRepository.findByName("No Existe")).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> strategyReader.findByName("No Existe")
            );
            assertEquals("Strategy not found", exception.getMessage());
        }

        @Test
        @DisplayName("Debe buscar con el nombre correcto")
        void findByName_ShouldSearchWithCorrectName() {
            // Arrange
            String searchName = "Estrategia Buscada";
            StrategiesModel foundStrategy = new StrategiesModel();
            foundStrategy.setName(searchName);
            when(strategyRepository.findByName(searchName)).thenReturn(Optional.of(foundStrategy));

            // Act
            StrategiesModel result = strategyReader.findByName(searchName);

            // Assert
            assertEquals(searchName, result.getName());
            verify(strategyRepository).findByName(searchName);
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar estrategia cuando existe")
        void findById_WhenExists_ShouldReturnStrategy() {
            // Arrange
            when(strategyRepository.findById(strategyId)).thenReturn(Optional.of(strategy));

            // Act
            StrategiesModel result = strategyReader.findById(strategyId);

            // Assert
            assertNotNull(result);
            assertEquals(strategyId, result.getId());
            assertEquals("Estrategia G12", result.getName());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la estrategia no existe")
        void findById_WhenNotExists_ShouldThrowEntityNotFoundException() {
            // Arrange
            UUID nonExistentId = UUID.randomUUID();
            when(strategyRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> strategyReader.findById(nonExistentId)
            );
            assertEquals("Strategy not found", exception.getMessage());
        }

        @Test
        @DisplayName("Debe buscar con el ID correcto")
        void findById_ShouldSearchWithCorrectId() {
            // Arrange
            UUID searchId = UUID.randomUUID();
            StrategiesModel foundStrategy = new StrategiesModel();
            foundStrategy.setId(searchId);
            when(strategyRepository.findById(searchId)).thenReturn(Optional.of(foundStrategy));

            // Act
            StrategiesModel result = strategyReader.findById(searchId);

            // Assert
            assertEquals(searchId, result.getId());
            verify(strategyRepository, times(1)).findById(searchId);
        }

        @Test
        @DisplayName("Debe retornar diferentes estrategias por ID")
        void findById_ShouldReturnDifferentStrategies() {
            // Arrange
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();

            StrategiesModel strategy1 = new StrategiesModel();
            strategy1.setId(id1);
            strategy1.setName("Estrategia 1");

            StrategiesModel strategy2 = new StrategiesModel();
            strategy2.setId(id2);
            strategy2.setName("Estrategia 2");

            when(strategyRepository.findById(id1)).thenReturn(Optional.of(strategy1));
            when(strategyRepository.findById(id2)).thenReturn(Optional.of(strategy2));

            // Act
            StrategiesModel result1 = strategyReader.findById(id1);
            StrategiesModel result2 = strategyReader.findById(id2);

            // Assert
            assertEquals("Estrategia 1", result1.getName());
            assertEquals("Estrategia 2", result2.getName());
            assertNotEquals(result1.getId(), result2.getId());
        }
    }
}

