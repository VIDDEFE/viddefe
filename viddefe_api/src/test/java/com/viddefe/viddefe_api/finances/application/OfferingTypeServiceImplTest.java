package com.viddefe.viddefe_api.finances.application;

import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingTypeRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferingTypeServiceImpl Tests")
class OfferingTypeServiceImplTest {

    @Mock
    private OfferingTypeRepository offeringTypeRepository;

    @InjectMocks
    private OfferingTypeServiceImpl offeringTypeService;

    private OfferingType offeringType;

    @BeforeEach
    void setUp() {
        offeringType = new OfferingType();
        offeringType.setId(1L);
        offeringType.setName("Diezmo");
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar OfferingType cuando existe")
        void findById_WhenExists_ShouldReturnOfferingType() {
            // Arrange
            when(offeringTypeRepository.findById(1L)).thenReturn(Optional.of(offeringType));

            // Act
            OfferingType result = offeringTypeService.findById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Diezmo", result.getName());
            verify(offeringTypeRepository).findById(1L);
        }

        @Test
        @DisplayName("Debe lanzar EntityNotFoundException cuando no existe")
        void findById_WhenNotExists_ShouldThrowEntityNotFoundException() {
            // Arrange
            Long nonExistentId = 999L;
            when(offeringTypeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> offeringTypeService.findById(nonExistentId)
            );

            assertEquals("OfferingType not found with id: " + nonExistentId, exception.getMessage());
            verify(offeringTypeRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Debe buscar con el ID correcto")
        void findById_ShouldSearchWithCorrectId() {
            // Arrange
            Long searchId = 5L;
            OfferingType expectedType = new OfferingType();
            expectedType.setId(searchId);
            expectedType.setName("Ofrenda Especial");
            when(offeringTypeRepository.findById(searchId)).thenReturn(Optional.of(expectedType));

            // Act
            OfferingType result = offeringTypeService.findById(searchId);

            // Assert
            assertEquals(searchId, result.getId());
            verify(offeringTypeRepository, times(1)).findById(searchId);
        }

        @Test
        @DisplayName("Debe retornar diferentes tipos de ofrenda correctamente")
        void findById_ShouldReturnDifferentOfferingTypes() {
            // Arrange
            OfferingType titheType = new OfferingType();
            titheType.setId(1L);
            titheType.setName("Diezmo");

            OfferingType missionType = new OfferingType();
            missionType.setId(2L);
            missionType.setName("Misiones");

            when(offeringTypeRepository.findById(1L)).thenReturn(Optional.of(titheType));
            when(offeringTypeRepository.findById(2L)).thenReturn(Optional.of(missionType));

            // Act
            OfferingType result1 = offeringTypeService.findById(1L);
            OfferingType result2 = offeringTypeService.findById(2L);

            // Assert
            assertEquals("Diezmo", result1.getName());
            assertEquals("Misiones", result2.getName());
            assertNotEquals(result1.getId(), result2.getId());
        }

        @Test
        @DisplayName("No debe retornar null cuando el tipo existe")
        void findById_WhenExists_ShouldNeverReturnNull() {
            // Arrange
            when(offeringTypeRepository.findById(1L)).thenReturn(Optional.of(offeringType));

            // Act
            OfferingType result = offeringTypeService.findById(1L);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getId());
            assertNotNull(result.getName());
        }
    }
}

