package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionTypesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinistryFunctionTypeReaderImpl Tests")
class MinistryFunctionTypeReaderImplTest {

    @Mock
    private MinistryFunctionTypesRepository ministryFunctionTypesRepository;

    @InjectMocks
    private MinistryFunctionTypeReaderImpl ministryFunctionTypeReader;

    private MinistryFunctionTypes functionType;

    @BeforeEach
    void setUp() {
        functionType = new MinistryFunctionTypes();
        functionType.setId(1L);
        functionType.setName("Worship Leader");
    }

    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should return ministry function type when found")
        void shouldReturnMinistryFunctionTypeWhenFound() {
            when(ministryFunctionTypesRepository.findById(1L)).thenReturn(Optional.of(functionType));

            MinistryFunctionTypes result = ministryFunctionTypeReader.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Worship Leader");
        }

        @Test
        @DisplayName("Should throw exception when not found")
        void shouldThrowWhenNotFound() {
            when(ministryFunctionTypesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ministryFunctionTypeReader.findById(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tipo de rol no encontrado");
        }
    }

    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("Should return all ministry function types")
        void shouldReturnAllMinistryFunctionTypes() {
            MinistryFunctionTypes type2 = new MinistryFunctionTypes();
            type2.setId(2L);
            type2.setName("Greeter");

            when(ministryFunctionTypesRepository.findAll()).thenReturn(List.of(functionType, type2));

            List<MinistryFunctionTypes> result = ministryFunctionTypeReader.findAll();

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when no types exist")
        void shouldReturnEmptyListWhenNoTypesExist() {
            when(ministryFunctionTypesRepository.findAll()).thenReturn(List.of());

            List<MinistryFunctionTypes> result = ministryFunctionTypeReader.findAll();

            assertThat(result).isEmpty();
        }
    }
}

