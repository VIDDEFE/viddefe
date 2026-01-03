package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleTypeRepository;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PeopleTypeService.
 *
 * Valida que:
 * 1. La consulta de tipos de personas funciona correctamente
 * 2. Las excepciones se manejan apropiadamente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PeopleTypeService Tests")
class PeopleTypeServiceTest {

    @Mock
    private PeopleTypeRepository peopleTypeRepository;

    @InjectMocks
    private PeopleTypeService peopleTypeService;

    private PeopleTypeModel memberType;
    private PeopleTypeModel pastorType;
    private PeopleTypeModel leaderType;

    @BeforeEach
    void setUp() {
        memberType = new PeopleTypeModel();
        memberType.setId(1L);
        memberType.setName("MEMBER");

        pastorType = new PeopleTypeModel();
        pastorType.setId(2L);
        pastorType.setName("PASTOR");

        leaderType = new PeopleTypeModel();
        leaderType.setId(3L);
        leaderType.setName("LEADER");
    }

    @Nested
    @DisplayName("getAllPeopleTypes")
    class GetAllPeopleTypes {

        @Test
        @DisplayName("Should return all people types as DTOs")
        void shouldReturnAllPeopleTypesAsDtos() {
            // Given
            when(peopleTypeRepository.findAll())
                .thenReturn(List.of(memberType, pastorType, leaderType));

            // When
            List<PeopleTypeDto> result = peopleTypeService.getAllPeopleTypes();

            // Then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("Should return empty list when no types exist")
        void shouldReturnEmptyListWhenNoTypesExist() {
            // Given
            when(peopleTypeRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<PeopleTypeDto> result = peopleTypeService.getAllPeopleTypes();

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getPeopleTypeById")
    class GetPeopleTypeById {

        @Test
        @DisplayName("Should return MEMBER type when found")
        void shouldReturnMemberTypeWhenFound() {
            // Given
            when(peopleTypeRepository.findById(1L)).thenReturn(Optional.of(memberType));

            // When
            PeopleTypeModel result = peopleTypeService.getPeopleTypeById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("MEMBER");
        }

        @Test
        @DisplayName("Should return PASTOR type when found")
        void shouldReturnPastorTypeWhenFound() {
            // Given
            when(peopleTypeRepository.findById(2L)).thenReturn(Optional.of(pastorType));

            // When
            PeopleTypeModel result = peopleTypeService.getPeopleTypeById(2L);

            // Then
            assertThat(result.getName()).isEqualTo("PASTOR");
        }

        @Test
        @DisplayName("Should throw exception when type not found by ID")
        void shouldThrowWhenTypeNotFoundById() {
            // Given
            when(peopleTypeRepository.findById(999L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> peopleTypeService.getPeopleTypeById(999L))
                .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                .hasMessageContaining("Tipo de persona no encontrado: 999");
        }
    }

    @Nested
    @DisplayName("getPeopleTypeByName")
    class GetPeopleTypeByName {

        @Test
        @DisplayName("Should return type when found by name")
        void shouldReturnTypeWhenFoundByName() {
            // Given
            when(peopleTypeRepository.findByName("MEMBER")).thenReturn(Optional.of(memberType));

            // When
            PeopleTypeModel result = peopleTypeService.getPeopleTypeByName("MEMBER");

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("MEMBER");
        }

        @Test
        @DisplayName("Should find PASTOR by name")
        void shouldFindPastorByName() {
            // Given
            when(peopleTypeRepository.findByName("PASTOR")).thenReturn(Optional.of(pastorType));

            // When
            PeopleTypeModel result = peopleTypeService.getPeopleTypeByName("PASTOR");

            // Then
            assertThat(result.getName()).isEqualTo("PASTOR");
            assertThat(result.getId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("Should throw exception when type not found by name")
        void shouldThrowWhenTypeNotFoundByName() {
            // Given
            when(peopleTypeRepository.findByName("NON_EXISTENT")).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> peopleTypeService.getPeopleTypeByName("NON_EXISTENT"))
                .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                .hasMessageContaining("Tipo de persona no encontrado: NON_EXISTENT");
        }
    }
}

