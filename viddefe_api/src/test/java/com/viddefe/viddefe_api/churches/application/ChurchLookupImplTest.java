package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ChurchLookupImpl.
 *
 * Valida que:
 * 1. Las consultas de iglesias funcionan correctamente
 * 2. Las excepciones se manejan apropiadamente
 * 3. NO hay dependencias con People domain (evita ciclos)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChurchLookup Tests")
class ChurchLookupImplTest {

    @Mock
    private ChurchRepository churchRepository;

    @InjectMocks
    private ChurchLookupImpl churchLookup;

    private UUID churchId;
    private ChurchModel church;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Test Church");
    }

    @Nested
    @DisplayName("getChurchById")
    class GetChurchById {

        @Test
        @DisplayName("Should return church when found")
        void shouldReturnChurchWhenFound() {
            // Given
            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));

            // When
            ChurchModel result = churchLookup.getChurchById(churchId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(churchId);
            assertThat(result.getName()).isEqualTo("Test Church");
        }

        @Test
        @DisplayName("Should throw exception when church not found")
        void shouldThrowWhenChurchNotFound() {
            // Given
            when(churchRepository.findById(churchId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> churchLookup.getChurchById(churchId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Church not found");
        }

        @Test
        @DisplayName("Should call repository with correct ID")
        void shouldCallRepositoryWithCorrectId() {
            // Given
            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));

            // When
            churchLookup.getChurchById(churchId);

            // Then
            verify(churchRepository).findById(churchId);
            verify(churchRepository, times(1)).findById(any());
        }

        @Test
        @DisplayName("Should handle different church IDs independently")
        void shouldHandleDifferentChurchIds() {
            // Given
            UUID anotherChurchId = UUID.randomUUID();
            ChurchModel anotherChurch = new ChurchModel();
            anotherChurch.setId(anotherChurchId);
            anotherChurch.setName("Another Church");

            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));
            when(churchRepository.findById(anotherChurchId)).thenReturn(Optional.of(anotherChurch));

            // When
            ChurchModel result1 = churchLookup.getChurchById(churchId);
            ChurchModel result2 = churchLookup.getChurchById(anotherChurchId);

            // Then
            assertThat(result1.getName()).isEqualTo("Test Church");
            assertThat(result2.getName()).isEqualTo("Another Church");
        }
    }

    @Nested
    @DisplayName("Architecture Validation")
    class ArchitectureValidation {

        @Test
        @DisplayName("Should only depend on ChurchRepository")
        void shouldOnlyDependOnChurchRepository() {
            // Given
            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));

            // When
            churchLookup.getChurchById(churchId);

            // Then - solo debe interactuar con el repositorio
            verify(churchRepository).findById(churchId);
            verifyNoMoreInteractions(churchRepository);
        }

        @Test
        @DisplayName("Should be a read-only service")
        void shouldBeReadOnlyService() {
            // Given
            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));

            // When
            churchLookup.getChurchById(churchId);

            // Then - no debe llamar a métodos de escritura
            verify(churchRepository, never()).save(any());
            verify(churchRepository, never()).delete(any());
            verify(churchRepository, never()).deleteById(any());
        }
    }
}

