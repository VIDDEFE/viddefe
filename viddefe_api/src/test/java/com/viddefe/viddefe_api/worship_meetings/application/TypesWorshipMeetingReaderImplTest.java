package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingTypeRepository;
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
@DisplayName("MeetingTypeServiceImpl Tests")
class TypesWorshipMeetingReaderImplTest {

    @Mock
    private MeetingTypeRepository meetingTypeRepository;

    @InjectMocks
    private MeetingTypeServiceImpl meetingTypeService;

    private MeetingType meetingType;

    @BeforeEach
    void setUp() {
        meetingType = new MeetingType();
        meetingType.setId(1L);
        meetingType.setName("Culto Dominical");
    }

    @Nested
    @DisplayName("Get Meeting Types By Id Tests")
    class GetMeetingTypesByIdTests {

        @Test
        @DisplayName("Debe retornar tipo de reunión cuando existe")
        void getMeetingTypesById_WhenExists_ShouldReturnType() {
            // Arrange
            when(meetingTypeRepository.findById(1L)).thenReturn(Optional.of(meetingType));

            // Act
            MeetingType result = meetingTypeService.getMeetingTypesById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Culto Dominical", result.getName());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el tipo no existe")
        void getMeetingTypesById_WhenNotExists_ShouldThrowException() {
            // Arrange
            when(meetingTypeRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> meetingTypeService.getMeetingTypesById(99L)
            );
            assertTrue(exception.getMessage().contains("not found"));
        }

        @Test
        @DisplayName("Debe buscar con el ID correcto")
        void getMeetingTypesById_ShouldSearchWithCorrectId() {
            // Arrange
            Long searchId = 5L;
            MeetingType expectedType = new MeetingType();
            expectedType.setId(searchId);
            expectedType.setName("Culto de Jóvenes");
            when(meetingTypeRepository.findById(searchId)).thenReturn(Optional.of(expectedType));

            // Act
            MeetingType result = meetingTypeService.getMeetingTypesById(searchId);

            // Assert
            assertEquals(searchId, result.getId());
            verify(meetingTypeRepository, times(1)).findById(searchId);
        }

        @Test
        @DisplayName("Debe retornar diferentes tipos de reunión correctamente")
        void getMeetingTypesById_ShouldReturnDifferentTypes() {
            // Arrange
            MeetingType sundayType = new MeetingType();
            sundayType.setId(1L);
            sundayType.setName("Culto Dominical");

            MeetingType youthType = new MeetingType();
            youthType.setId(2L);
            youthType.setName("Culto de Jóvenes");

            when(meetingTypeRepository.findById(1L)).thenReturn(Optional.of(sundayType));
            when(meetingTypeRepository.findById(2L)).thenReturn(Optional.of(youthType));

            // Act
            MeetingType result1 = meetingTypeService.getMeetingTypesById(1L);
            MeetingType result2 = meetingTypeService.getMeetingTypesById(2L);

            // Assert
            assertEquals("Culto Dominical", result1.getName());
            assertEquals("Culto de Jóvenes", result2.getName());
        }
    }
}
