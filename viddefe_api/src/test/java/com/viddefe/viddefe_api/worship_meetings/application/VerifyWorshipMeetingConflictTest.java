package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifyWorshipMeetingConflict Tests")
class VerifyWorshipMeetingConflictTest {

    @Mock
    private MeetingRepository meetingRepository;

    @InjectMocks
    private VerifyWorshipMeetingConflict verifyConflict;

    private UUID churchId;
    private UUID worshipId;
    private CreateMeetingDto createDto;
    private OffsetDateTime scheduledDate;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        worshipId = UUID.randomUUID();
        scheduledDate = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);

        createDto = new CreateMeetingDto();
        createDto.setName("Servicio Dominical");
        createDto.setDescription("Descripción");
        createDto.setScheduledDate(scheduledDate);
        createDto.setMeetingTypeId(1L);
        createDto.setMeetingTypeId(1L); // Required for verifyHourOfMeeting
    }

    @Nested
    @DisplayName("Verify Hour Of Meeting - Creation Tests")
    class CreationConflictTests {

        @Test
        @DisplayName("No debe lanzar excepción cuando no hay conflicto en creación")
        void verifyHourOfMeeting_Creation_NoConflict_ShouldNotThrow() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(false);

            // Act & Assert
            assertDoesNotThrow(() ->
                verifyConflict.verifyHourOfMeeting(createDto, churchId, null, null)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando hay conflicto en creación")
        void verifyHourOfMeeting_Creation_WithConflict_ShouldThrow() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> verifyConflict.verifyHourOfMeeting(createDto, churchId, null, null)
            );
            assertTrue(exception.getMessage().contains("Conflict"));
        }

        @Test
        @DisplayName("Debe usar método sin meetingId para creación")
        void verifyHourOfMeeting_Creation_ShouldUseCorrectRepositoryMethod() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfMeeting(createDto, churchId, null, null);

            // Assert
            verify(meetingRepository).existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate);
            verify(meetingRepository, never())
                    .existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Verify Hour Of Meeting - Update Tests")
    class UpdateConflictTests {

        @Test
        @DisplayName("No debe lanzar excepción cuando no hay conflicto en actualización")
        void verifyHourOfMeeting_Update_NoConflict_ShouldNotThrow() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId))
                    .thenReturn(false);

            // Act & Assert
            assertDoesNotThrow(() ->
                verifyConflict.verifyHourOfMeeting(createDto, churchId, null, worshipId)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando hay conflicto en actualización")
        void verifyHourOfMeeting_Update_WithConflict_ShouldThrow() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> verifyConflict.verifyHourOfMeeting(createDto, churchId, null, worshipId)
            );
            assertTrue(exception.getMessage().contains("Conflict"));
        }

        @Test
        @DisplayName("Debe usar método con meetingId para actualización")
        void verifyHourOfMeeting_Update_ShouldUseCorrectRepositoryMethod() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfMeeting(createDto, churchId, null, worshipId);

            // Assert
            verify(meetingRepository).existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId);
            verify(meetingRepository, never())
                    .existsByChurchIdAndMeetingTypeIdAndScheduledDate(any(), any(), any());
        }

        @Test
        @DisplayName("Debe excluir el propio meeting de la verificación de conflicto")
        void verifyHourOfMeeting_Update_ShouldExcludeOwnId() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                    eq(churchId), eq(1L), eq(scheduledDate), eq(worshipId)))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfMeeting(createDto, churchId, null, worshipId);

            // Assert
            verify(meetingRepository).existsByChurchIdAndMeetingTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId);
        }
    }

    @Nested
    @DisplayName("Verification Parameters Tests")
    class VerificationParametersTests {

        @Test
        @DisplayName("Debe verificar con el churchId correcto")
        void verifyHourOfMeeting_ShouldUseCorrectChurchId() {
            // Arrange
            UUID specificChurchId = UUID.randomUUID();
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    eq(specificChurchId), any(), any()))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfMeeting(createDto, specificChurchId, null, null);

            // Assert
            verify(meetingRepository).existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    eq(specificChurchId), any(), any());
        }

        @Test
        @DisplayName("Debe verificar con el meetingTypeId correcto")
        void verifyHourOfMeeting_ShouldUseCorrectMeetingTypeId() {
            // Arrange
            createDto.setMeetingTypeId(5L);
            createDto.setMeetingTypeId(5L);
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    any(), eq(5L), any()))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfMeeting(createDto, churchId, null, null);

            // Assert
            verify(meetingRepository).existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    any(), eq(5L), any());
        }

        @Test
        @DisplayName("Debe verificar con la fecha programada correcta")
        void verifyHourOfMeeting_ShouldUseCorrectScheduledDate() {
            // Arrange
            OffsetDateTime specificDate = OffsetDateTime.of(2026, 6, 15, 10, 0, 0, 0, ZoneOffset.UTC);
            createDto.setScheduledDate(specificDate);
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    any(), any(), eq(specificDate)))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfMeeting(createDto, churchId, null, null);

            // Assert
            verify(meetingRepository).existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    any(), any(), eq(specificDate));
        }
    }

    @Nested
    @DisplayName("Error Message Tests")
    class ErrorMessageTests {

        @Test
        @DisplayName("El mensaje de error debe ser descriptivo")
        void verifyHourOfMeeting_ErrorMessage_ShouldBeDescriptive() {
            // Arrange
            when(meetingRepository.existsByChurchIdAndMeetingTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> verifyConflict.verifyHourOfMeeting(createDto, churchId, null, null)
            );

            String message = exception.getMessage();
            assertTrue(message.contains("Conflict"));
        }
    }
}
