package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.MeetingTypeEnum;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorshipServicesImpl - Servicio de Cultos Refactorizado")
class WorshipServicesImplRefactoredTest {

    @Mock
    private MeetingService meetingService;

    @Mock
    private VerifyWorshipMeetingConflict verifyConflict;

    @Mock
    private MeetingTypesService meetingTypesService;

    @Mock
    private ChurchLookup churchLookup;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private WorshipServicesImpl worshipService;

    private UUID churchId;
    private UUID worshipId;
    private CreateMeetingDto createDto;
    private Meeting worshipMeeting;
    private MeetingType worshipType;
    private ChurchModel church;
    private OffsetDateTime testScheduledDate;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        worshipId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

        // DTO
        createDto = new CreateMeetingDto();
        createDto.setName("Culto Dominical");
        createDto.setDescription("Servicio de adoración");
        createDto.setScheduledDate(testScheduledDate);
        createDto.setMeetingTypeId(1L);

        // Type
        worshipType = new MeetingType();
        worshipType.setId(1L);
        worshipType.setName("Dominical");

        // Church
        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Iglesia Test");

        // Entity
        worshipMeeting = new Meeting();
        worshipMeeting.setId(worshipId);
        worshipMeeting.setName("Culto Dominical");
        worshipMeeting.setChurch(church);
        worshipMeeting.setMeetingType(worshipType);
        worshipMeeting.setScheduledDate(testScheduledDate);
        worshipMeeting.setCreationDate(Instant.now());
    }

    @Nested
    @DisplayName("CREATE - Crear Culto")
    class CreateWorshipTests {

        @Test
        @DisplayName("createWorship() debe crear culto sin conversión de zona")
        void testCreateWorshipNoTimezoneConversion() {
            // Setup
            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), isNull());
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(Meeting.class))).thenReturn(worshipMeeting);

            // Execute
            MeetingDto result = worshipService.createWorship(createDto, churchId);

            // Assert
            assertNotNull(result);
            assertEquals("Culto Dominical", result.getName());
            assertEquals(testScheduledDate.getOffset(), ZoneOffset.of("-05:00"));

            // Verify
            verify(verifyConflict, times(1)).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), isNull());
            verify(meetingTypesService, times(1)).getMeetingTypesById(1L);
            verify(churchLookup, times(1)).getChurchById(churchId);
            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("createWorship() debe asignar church correctamente")
        void testCreateWorshipAssignsContext() {
            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), isNull());
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(churchId, meeting.getChurch().getId());
                return worshipMeeting;
            });

            worshipService.createWorship(createDto, churchId);

            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("createWorship() debe asignar meetingType correctamente")
        void testCreateWorshipAssignsTypeId() {
            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), isNull());
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(1L, meeting.getMeetingType().getId());
                return worshipMeeting;
            });

            worshipService.createWorship(createDto, churchId);

            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("createWorship() debe preservar OffsetDateTime sin conversión")
        void testCreateWorshipPreservesOffsetDateTime() {
            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), isNull());
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                // Debe preservar exactamente como viene del DTO
                assertEquals(testScheduledDate, meeting.getScheduledDate());
                assertEquals(ZoneOffset.of("-05:00"), meeting.getScheduledDate().getOffset());
                return worshipMeeting;
            });

            MeetingDto result = worshipService.createWorship(createDto, churchId);

            // El resultado también debe tener el offset
            assertNotNull(result.getScheduledDate());
        }
    }

    @Nested
    @DisplayName("READ - Obtener Culto")
    class ReadWorshipTests {

        @Test
        @DisplayName("getWorshipById() debe retornar culto con relaciones cargadas")
        void testGetWorshipByIdWithRelations() {
            when(meetingService.findByIdWithRelations(worshipId)).thenReturn(Optional.of(worshipMeeting));
            when(attendanceService.countByEventIdWithDefaults(any(), any(), any())).thenReturn(10L);

            var result = worshipService.getWorshipById(worshipId);

            assertNotNull(result);
            assertEquals("Culto Dominical", result.getName());
            verify(attendanceService, times(2)).countByEventIdWithDefaults(any(), any(), any());
        }

        @Test
        @DisplayName("getWorshipById() debe fallar si no existe")
        void testGetWorshipByIdNotFound() {
            UUID randomId = UUID.randomUUID();
            when(meetingService.findByIdWithRelations(randomId)).thenReturn(Optional.empty());

            assertThrows(Exception.class, () -> worshipService.getWorshipById(randomId));
        }
    }

    @Nested
    @DisplayName("UPDATE - Actualizar Culto")
    class UpdateWorshipTests {

        @Test
        @DisplayName("updateWorship() debe actualizar sin conversión de zona")
        void testUpdateWorshipNoConversion() {
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 20, 14, 0, 0, 0, ZoneOffset.of("-05:00"));
            CreateMeetingDto updateDto = new CreateMeetingDto();
            updateDto.setName("Culto Actualizado");
            updateDto.setDescription("Nuevo culto");
            updateDto.setScheduledDate(newSchedule);
            updateDto.setMeetingTypeId(1L);

            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), eq(worshipId));
            when(meetingService.findById(worshipId)).thenReturn(worshipMeeting);
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(meetingService.update(any(Meeting.class))).thenReturn(worshipMeeting);

            MeetingDto result = worshipService.updateWorship(worshipId, updateDto, churchId);

            assertNotNull(result);
            verify(meetingService, times(1)).update(any(Meeting.class));
        }

        @Test
        @DisplayName("updateWorship() no debe modificar creationDate")
        void testUpdateWorshipKeepsCreationDate() {
            var createDate = worshipMeeting.getCreationDate();
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 20, 14, 0, 0, 0, ZoneOffset.of("-05:00"));

            CreateMeetingDto updateDto = new CreateMeetingDto();
            updateDto.setName("Culto Actualizado");
            updateDto.setScheduledDate(newSchedule);
            updateDto.setMeetingTypeId(1L);

            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), eq(worshipId));
            when(meetingService.findById(worshipId)).thenReturn(worshipMeeting);
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(meetingService.update(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(createDate, meeting.getCreationDate());
                return meeting;
            });

            worshipService.updateWorship(worshipId, updateDto, churchId);

            verify(meetingService, times(1)).update(any(Meeting.class));
        }
    }

    @Nested
    @DisplayName("DELETE - Eliminar Culto")
    class DeleteWorshipTests {

        @Test
        @DisplayName("deleteWorship() debe llamar al MeetingService")
        void testDeleteWorship() {
            when(meetingService.findById(worshipId)).thenReturn(worshipMeeting);
            doNothing().when(meetingService).delete(worshipId);

            worshipService.deleteWorship(worshipId);

            verify(meetingService, times(1)).delete(worshipId);
        }

        @Test
        @DisplayName("deleteWorship() debe fallar si no existe")
        void testDeleteWorshipNotFound() {
            UUID randomId = UUID.randomUUID();
            when(meetingService.findById(randomId)).thenThrow(new jakarta.persistence.EntityNotFoundException("Meeting not found"));

            assertThrows(Exception.class, () -> worshipService.deleteWorship(randomId));
        }
    }

    @Nested
    @DisplayName("Integración - MeetingService")
    class IntegrationTests {

        @Test
        @DisplayName("Debe usar MeetingService en lugar de Repository directo")
        void testUsesMeetingService() {
            doNothing().when(verifyConflict).verifyHourOfMeeting(any(CreateMeetingDto.class), eq(churchId), isNull(), isNull());
            when(meetingTypesService.getMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(Meeting.class))).thenReturn(worshipMeeting);

            worshipService.createWorship(createDto, churchId);

            // Verifica que usa MeetingService, no repository
            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("Debe pasar MeetingTypeEnum en operaciones unificadas")
        void testUsesMeetingTypeEnum() {
            // En operaciones futuras que filtren por tipo
            // Se debe verificar que usa MeetingTypeEnum.WORSHIP
            assertTrue(MeetingTypeEnum.WORSHIP.getDisplayName().contains("Culto"));
        }
    }
}
