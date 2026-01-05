package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.domain.repository.WorshipRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VerifyWorshipMeetingConflict Tests")
class VerifyWorshipMeetingConflictTest {

    @Mock
    private WorshipRepository worshipRepository;

    @InjectMocks
    private VerifyWorshipMeetingConflict verifyConflict;

    private UUID churchId;
    private UUID worshipId;
    private CreateWorshipDto createDto;
    private LocalDateTime scheduledDate;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        worshipId = UUID.randomUUID();
        scheduledDate = LocalDateTime.now().plusDays(1);

        createDto = new CreateWorshipDto();
        createDto.setName("Servicio Dominical");
        createDto.setDescription("Descripción");
        createDto.setScheduledDate(scheduledDate);
        setWorshipTypeId(createDto, 1L);
    }

    private void setWorshipTypeId(CreateWorshipDto dto, Long typeId) {
        try {
            var field = dto.getClass().getDeclaredField("worshipTypeId");
            field.setAccessible(true);
            field.set(dto, typeId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("Verify Hour Of Worship Meeting - Creation Tests")
    class CreationConflictTests {

        @Test
        @DisplayName("No debe lanzar excepción cuando no hay conflicto en creación")
        void verifyHourOfWorshipMeeting_Creation_NoConflict_ShouldNotThrow() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(false);

            // Act & Assert
            assertDoesNotThrow(() ->
                verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, null)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando hay conflicto en creación")
        void verifyHourOfWorshipMeeting_Creation_WithConflict_ShouldThrow() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, null)
            );
            assertTrue(exception.getMessage().contains("Conflict"));
        }

        @Test
        @DisplayName("Debe usar método sin worshipId para creación")
        void verifyHourOfWorshipMeeting_Creation_ShouldUseCorrectRepositoryMethod() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, null);

            // Assert
            verify(worshipRepository).existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate);
            verify(worshipRepository, never())
                    .existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Verify Hour Of Worship Meeting - Update Tests")
    class UpdateConflictTests {

        @Test
        @DisplayName("No debe lanzar excepción cuando no hay conflicto en actualización")
        void verifyHourOfWorshipMeeting_Update_NoConflict_ShouldNotThrow() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId))
                    .thenReturn(false);

            // Act & Assert
            assertDoesNotThrow(() ->
                verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, worshipId)
            );
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando hay conflicto en actualización")
        void verifyHourOfWorshipMeeting_Update_WithConflict_ShouldThrow() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, worshipId)
            );
            assertTrue(exception.getMessage().contains("Conflict"));
        }

        @Test
        @DisplayName("Debe usar método con worshipId para actualización")
        void verifyHourOfWorshipMeeting_Update_ShouldUseCorrectRepositoryMethod() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, worshipId);

            // Assert
            verify(worshipRepository).existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId);
            verify(worshipRepository, never())
                    .existsByChurchIdAndWorshipTypeIdAndScheduledDate(any(), any(), any());
        }

        @Test
        @DisplayName("Debe excluir el propio culto de la verificación de conflicto")
        void verifyHourOfWorshipMeeting_Update_ShouldExcludeOwnId() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                    eq(churchId), eq(1L), eq(scheduledDate), eq(worshipId)))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, worshipId);

            // Assert
            verify(worshipRepository).existsByChurchIdAndWorshipTypeIdAndScheduledDateAndIdNot(
                    churchId, 1L, scheduledDate, worshipId);
        }
    }

    @Nested
    @DisplayName("Verification Parameters Tests")
    class VerificationParametersTests {

        @Test
        @DisplayName("Debe verificar con el churchId correcto")
        void verifyHourOfWorshipMeeting_ShouldUseCorrectChurchId() {
            // Arrange
            UUID specificChurchId = UUID.randomUUID();
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    eq(specificChurchId), any(), any()))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfWorshipMeeting(createDto, specificChurchId, null);

            // Assert
            verify(worshipRepository).existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    eq(specificChurchId), any(), any());
        }

        @Test
        @DisplayName("Debe verificar con el worshipTypeId correcto")
        void verifyHourOfWorshipMeeting_ShouldUseCorrectWorshipTypeId() {
            // Arrange
            setWorshipTypeId(createDto, 5L);
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    any(), eq(5L), any()))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, null);

            // Assert
            verify(worshipRepository).existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    any(), eq(5L), any());
        }

        @Test
        @DisplayName("Debe verificar con la fecha programada correcta")
        void verifyHourOfWorshipMeeting_ShouldUseCorrectScheduledDate() {
            // Arrange
            LocalDateTime specificDate = LocalDateTime.of(2026, 6, 15, 10, 0);
            createDto.setScheduledDate(specificDate);
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    any(), any(), eq(specificDate)))
                    .thenReturn(false);

            // Act
            verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, null);

            // Assert
            verify(worshipRepository).existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    any(), any(), eq(specificDate));
        }
    }

    @Nested
    @DisplayName("Error Message Tests")
    class ErrorMessageTests {

        @Test
        @DisplayName("El mensaje de error debe ser descriptivo")
        void verifyHourOfWorshipMeeting_ErrorMessage_ShouldBeDescriptive() {
            // Arrange
            when(worshipRepository.existsByChurchIdAndWorshipTypeIdAndScheduledDate(
                    churchId, 1L, scheduledDate))
                    .thenReturn(true);

            // Act & Assert
            DataIntegrityViolationException exception = assertThrows(
                    DataIntegrityViolationException.class,
                    () -> verifyConflict.verifyHourOfWorshipMeeting(createDto, churchId, null)
            );

            String message = exception.getMessage();
            assertTrue(message.contains("Ya existe un culto del mismo tipo"));
        }
    }
}

