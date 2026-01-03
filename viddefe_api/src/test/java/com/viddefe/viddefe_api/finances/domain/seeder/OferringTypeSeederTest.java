package com.viddefe.viddefe_api.finances.domain.seeder;

import com.viddefe.viddefe_api.finances.configuration.OfferingTypeEnum;
import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingTypeRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OferringTypeSeeder Tests")
class OferringTypeSeederTest {

    @Mock
    private OfferingTypeRepository offeringTypeRepository;

    @InjectMocks
    private OferringTypeSeeder offeringTypeSeeder;

    @Captor
    private ArgumentCaptor<List<OfferingType>> offeringTypeListCaptor;

    @Nested
    @DisplayName("Seed Method Tests")
    class SeedMethodTests {

        @Test
        @DisplayName("Debe crear todos los tipos de ofrenda cuando la BD está vacía")
        void seed_WhenDatabaseEmpty_ShouldCreateAllTypes() {
            // Arrange
            when(offeringTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<OfferingType> savedTypes = offeringTypeListCaptor.getValue();
            assertEquals(OfferingTypeEnum.values().length, savedTypes.size());
        }

        @Test
        @DisplayName("No debe crear tipos duplicados cuando todos existen")
        void seed_WhenAllTypesExist_ShouldNotCreateAny() {
            // Arrange
            List<OfferingType> existingTypes = new ArrayList<>();
            for (OfferingTypeEnum type : OfferingTypeEnum.values()) {
                OfferingType offeringType = new OfferingType();
                offeringType.setName(type.getDescription());
                existingTypes.add(offeringType);
            }
            when(offeringTypeRepository.findAll()).thenReturn(existingTypes);

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            assertTrue(offeringTypeListCaptor.getValue().isEmpty());
        }

        @Test
        @DisplayName("Debe crear solo los tipos faltantes")
        void seed_WhenSomeTypesExist_ShouldCreateOnlyMissing() {
            // Arrange
            OfferingType existingType = new OfferingType();
            existingType.setName(OfferingTypeEnum.OFFERING.getDescription());
            when(offeringTypeRepository.findAll()).thenReturn(List.of(existingType));

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<OfferingType> savedTypes = offeringTypeListCaptor.getValue();
            assertEquals(OfferingTypeEnum.values().length - 1, savedTypes.size());

            // Verificar que "Ofrenda general" no está en los guardados
            boolean containsOffering = savedTypes.stream()
                    .anyMatch(t -> t.getName().equals(OfferingTypeEnum.OFFERING.getDescription()));
            assertFalse(containsOffering);
        }

        @Test
        @DisplayName("Debe crear tipos con nombres correctos del enum")
        void seed_ShouldCreateTypesWithCorrectNames() {
            // Arrange
            when(offeringTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<OfferingType> savedTypes = offeringTypeListCaptor.getValue();

            // Verificar que contiene los nombres esperados
            List<String> savedNames = savedTypes.stream()
                    .map(OfferingType::getName)
                    .toList();

            assertTrue(savedNames.contains("Diezmo"));
            assertTrue(savedNames.contains("Ofrenda general"));
            assertTrue(savedNames.contains("Misiones"));
        }

        @Test
        @DisplayName("Debe llamar a findAll primero para verificar existentes")
        void seed_ShouldCallFindAllFirst() {
            // Arrange
            when(offeringTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository, times(1)).findAll();
            verify(offeringTypeRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("Debe manejar múltiples tipos existentes correctamente")
        void seed_WithMultipleExisting_ShouldCreateOnlyMissing() {
            // Arrange
            List<OfferingType> existingTypes = new ArrayList<>();

            OfferingType type1 = new OfferingType();
            type1.setName(OfferingTypeEnum.TITHING.getDescription());
            existingTypes.add(type1);

            OfferingType type2 = new OfferingType();
            type2.setName(OfferingTypeEnum.MISSIONS.getDescription());
            existingTypes.add(type2);

            OfferingType type3 = new OfferingType();
            type3.setName(OfferingTypeEnum.CHARITY.getDescription());
            existingTypes.add(type3);

            when(offeringTypeRepository.findAll()).thenReturn(existingTypes);

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<OfferingType> savedTypes = offeringTypeListCaptor.getValue();
            assertEquals(OfferingTypeEnum.values().length - 3, savedTypes.size());
        }

        @Test
        @DisplayName("Cada tipo guardado debe tener nombre no nulo")
        void seed_AllSavedTypes_ShouldHaveNonNullName() {
            // Arrange
            when(offeringTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<OfferingType> savedTypes = offeringTypeListCaptor.getValue();

            for (OfferingType type : savedTypes) {
                assertNotNull(type.getName());
                assertFalse(type.getName().isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("OfferingTypeEnum Coverage Tests")
    class OfferingTypeEnumTests {

        @Test
        @DisplayName("Debe cubrir todos los valores del enum")
        void seed_ShouldCoverAllEnumValues() {
            // Arrange
            when(offeringTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<OfferingType> savedTypes = offeringTypeListCaptor.getValue();

            for (OfferingTypeEnum enumValue : OfferingTypeEnum.values()) {
                boolean found = savedTypes.stream()
                        .anyMatch(t -> t.getName().equals(enumValue.getDescription()));
                assertTrue(found, "Falta el tipo: " + enumValue.getDescription());
            }
        }

        @Test
        @DisplayName("Debe incluir tipos especiales como FAITH_PROMISE")
        void seed_ShouldIncludeSpecialTypes() {
            // Arrange
            when(offeringTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            offeringTypeSeeder.seed();

            // Assert
            verify(offeringTypeRepository).saveAll(offeringTypeListCaptor.capture());
            List<String> savedNames = offeringTypeListCaptor.getValue().stream()
                    .map(OfferingType::getName)
                    .toList();

            assertTrue(savedNames.contains("Promesa de fe"));
            assertTrue(savedNames.contains("Ofrenda de amor"));
            assertTrue(savedNames.contains("Sostenimiento pastoral"));
        }
    }
}

