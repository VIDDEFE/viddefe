package com.viddefe.viddefe_api.worship_meetings.application;

import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TypesWorshipMeetingReaderImpl Tests")
class TypesWorshipMeetingReaderImplTest {

    @Mock
    private WorshipTypesRepository worshipTypesRepository;

    @InjectMocks
    private TypesWorshipMeetingReaderImpl typesWorshipMeetingReader;

    private WorshipMeetingTypes worshipType;

    @BeforeEach
    void setUp() {
        worshipType = new WorshipMeetingTypes();
        worshipType.setId(1L);
        worshipType.setName("Culto Dominical");
    }

    @Nested
    @DisplayName("Get Worship Meeting Types By Id Tests")
    class GetWorshipMeetingTypesByIdTests {

        @Test
        @DisplayName("Debe retornar tipo de culto cuando existe")
        void getWorshipMeetingTypesById_WhenExists_ShouldReturnType() {
            // Arrange
            when(worshipTypesRepository.findById(1L)).thenReturn(Optional.of(worshipType));

            // Act
            WorshipMeetingTypes result = typesWorshipMeetingReader.getWorshipMeetingTypesById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Culto Dominical", result.getName());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el tipo no existe")
        void getWorshipMeetingTypesById_WhenNotExists_ShouldThrowException() {
            // Arrange
            when(worshipTypesRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> typesWorshipMeetingReader.getWorshipMeetingTypesById(99L)
            );
            assertEquals("Worship Meeting Type not found", exception.getMessage());
        }

        @Test
        @DisplayName("Debe buscar con el ID correcto")
        void getWorshipMeetingTypesById_ShouldSearchWithCorrectId() {
            // Arrange
            Long searchId = 5L;
            WorshipMeetingTypes expectedType = new WorshipMeetingTypes();
            expectedType.setId(searchId);
            expectedType.setName("Culto de Jóvenes");
            when(worshipTypesRepository.findById(searchId)).thenReturn(Optional.of(expectedType));

            // Act
            WorshipMeetingTypes result = typesWorshipMeetingReader.getWorshipMeetingTypesById(searchId);

            // Assert
            assertEquals(searchId, result.getId());
            verify(worshipTypesRepository, times(1)).findById(searchId);
        }

        @Test
        @DisplayName("Debe retornar diferentes tipos de culto correctamente")
        void getWorshipMeetingTypesById_ShouldReturnDifferentTypes() {
            // Arrange
            WorshipMeetingTypes sundayType = new WorshipMeetingTypes();
            sundayType.setId(1L);
            sundayType.setName("Culto Dominical");

            WorshipMeetingTypes youthType = new WorshipMeetingTypes();
            youthType.setId(2L);
            youthType.setName("Culto de Jóvenes");

            when(worshipTypesRepository.findById(1L)).thenReturn(Optional.of(sundayType));
            when(worshipTypesRepository.findById(2L)).thenReturn(Optional.of(youthType));

            // Act
            WorshipMeetingTypes result1 = typesWorshipMeetingReader.getWorshipMeetingTypesById(1L);
            WorshipMeetingTypes result2 = typesWorshipMeetingReader.getWorshipMeetingTypesById(2L);

            // Assert
            assertEquals("Culto Dominical", result1.getName());
            assertEquals("Culto de Jóvenes", result2.getName());
        }
    }

    @Nested
    @DisplayName("Get All Worship Meeting Types Tests")
    class GetAllWorshipMeetingTypesTests {

        @Test
        @DisplayName("Debe retornar lista de tipos de culto")
        void getAllWorshipMeetingTypes_ShouldReturnListOfTypes() {
            // Arrange
            WorshipMeetingTypes type1 = new WorshipMeetingTypes();
            type1.setId(1L);
            type1.setName("Culto Dominical");

            WorshipMeetingTypes type2 = new WorshipMeetingTypes();
            type2.setId(2L);
            type2.setName("Culto de Oración");

            when(worshipTypesRepository.findAll()).thenReturn(List.of(type1, type2));

            // Act
            List<WorshipMeetingTypes> result = typesWorshipMeetingReader.getAllWorshipMeetingTypes();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay tipos")
        void getAllWorshipMeetingTypes_WhenNoTypes_ShouldReturnEmptyList() {
            // Arrange
            when(worshipTypesRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<WorshipMeetingTypes> result = typesWorshipMeetingReader.getAllWorshipMeetingTypes();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe llamar al repositorio una vez")
        void getAllWorshipMeetingTypes_ShouldCallRepositoryOnce() {
            // Arrange
            when(worshipTypesRepository.findAll()).thenReturn(List.of(worshipType));

            // Act
            typesWorshipMeetingReader.getAllWorshipMeetingTypes();

            // Assert
            verify(worshipTypesRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe retornar todos los tipos disponibles")
        void getAllWorshipMeetingTypes_ShouldReturnAllAvailableTypes() {
            // Arrange
            WorshipMeetingTypes type1 = new WorshipMeetingTypes();
            type1.setId(1L);
            type1.setName("Dominical");

            WorshipMeetingTypes type2 = new WorshipMeetingTypes();
            type2.setId(2L);
            type2.setName("Oración");

            WorshipMeetingTypes type3 = new WorshipMeetingTypes();
            type3.setId(3L);
            type3.setName("Jóvenes");

            when(worshipTypesRepository.findAll()).thenReturn(List.of(type1, type2, type3));

            // Act
            List<WorshipMeetingTypes> result = typesWorshipMeetingReader.getAllWorshipMeetingTypes();

            // Assert
            assertEquals(3, result.size());
            assertTrue(result.stream().anyMatch(t -> t.getName().equals("Dominical")));
            assertTrue(result.stream().anyMatch(t -> t.getName().equals("Oración")));
            assertTrue(result.stream().anyMatch(t -> t.getName().equals("Jóvenes")));
        }
    }
}

