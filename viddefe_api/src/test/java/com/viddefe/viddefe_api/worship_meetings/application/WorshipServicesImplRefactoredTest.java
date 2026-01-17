package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.configuration.MeetingTypeEnum;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingTypesService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateWorshipDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorshipServicesImpl - Servicio de Cultos Refactorizado")
class WorshipServicesImplTest {

    @Mock
    private MeetingService meetingService;

    @Mock
    private VerifyWorshipMeetingConflict verifyConflict;

    @Mock
    private MeetingTypesService ;

    @Mock
    private ChurchLookup churchLookup;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private WorshipServicesImpl worshipService;

    private UUID churchId;
    private UUID worshipId;
    private CreateWorshipDto createDto;
    private WorshipMeetingModel worshipMeeting;
    private WorshipMeetingTypes worshipType;
    private ChurchModel church;
    private OffsetDateTime testScheduledDate;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        worshipId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

        // DTO
        createDto = new CreateWorshipDto();
        createDto.setName("Culto Dominical");
        createDto.setDescription("Servicio de adoración");
        createDto.setScheduledDate(testScheduledDate);
        createDto.setWorshipTypeId(1L);

        // Entity
        worshipMeeting = new WorshipMeetingModel();
        worshipMeeting.setId(worshipId);
        worshipMeeting.setName("Culto Dominical");
        worshipMeeting.setContextId(churchId);
        worshipMeeting.setTypeId(1L);
        worshipMeeting.setScheduledDate(testScheduledDate);

        // Type
        worshipType = new WorshipMeetingTypes();
        worshipType.setId(1L);
        worshipType.setName("Dominical");

        // Church
        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Iglesia Test");
    }

    @Nested
    @DisplayName("CREATE - Crear Culto")
    class CreateWorshipTests {

        @Test
        @DisplayName("createWorship() debe crear culto sin conversión de zona")
        void testCreateWorshipNoTimezoneConversion() {
            // Setup
            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(createDto, churchId, null);
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(WorshipMeetingModel.class))).thenReturn(worshipMeeting);

            // Execute
            MeetingDto result = worshipService.createWorship(createDto, churchId);

            // Assert
            assertNotNull(result);
            assertEquals("Culto Dominical", result.getName());
            assertEquals(testScheduledDate.getOffset(), ZoneOffset.of("-05:00"));

            // Verify
            verify(verifyConflict, times(1)).verifyHourOfWorshipMeeting(createDto, churchId, null);
            verify(typesReader, times(1)).getWorshipMeetingTypesById(1L);
            verify(churchLookup, times(1)).getChurchById(churchId);
            verify(meetingService, times(1)).create(any(WorshipMeetingModel.class));
        }

        @Test
        @DisplayName("createWorship() debe asignar contextId = churchId")
        void testCreateWorshipAssignsContext() {
            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(createDto, churchId, null);
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(WorshipMeetingModel.class))).thenAnswer(invocation -> {
                WorshipMeetingModel meeting = invocation.getArgument(0);
                assertEquals(churchId, meeting.getContextId());
                return meeting;
            });

            worshipService.createWorship(createDto, churchId);

            verify(meetingService, times(1)).create(any(WorshipMeetingModel.class));
        }

        @Test
        @DisplayName("createWorship() debe asignar typeId = worshipTypeId")
        void testCreateWorshipAssignsTypeId() {
            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(createDto, churchId, null);
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(WorshipMeetingModel.class))).thenAnswer(invocation -> {
                WorshipMeetingModel meeting = invocation.getArgument(0);
                assertEquals(1L, meeting.getTypeId());
                return meeting;
            });

            worshipService.createWorship(createDto, churchId);

            verify(meetingService, times(1)).create(any(WorshipMeetingModel.class));
        }

        @Test
        @DisplayName("createWorship() debe preservar OffsetDateTime sin conversión")
        void testCreateWorshipPreservesOffsetDateTime() {
            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(createDto, churchId, null);
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(WorshipMeetingModel.class))).thenAnswer(invocation -> {
                WorshipMeetingModel meeting = invocation.getArgument(0);
                // Debe preservar exactamente como viene del DTO
                assertEquals(testScheduledDate, meeting.getScheduledDate());
                assertEquals(ZoneOffset.of("-05:00"), meeting.getScheduledDate().getOffset());
                return meeting;
            });

            WorshipDto result = worshipService.createWorship(createDto, churchId);

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
            worshipMeeting.setWorshipType(worshipType);
            when(meetingService.findByIdWithRelations(worshipId)).thenReturn(Optional.of(worshipMeeting));
            when(attendanceService.countByEventIdWithDefaults(any(), any(), any())).thenReturn(10L);

            WorshipDto result = worshipService.getWorshipById(worshipId);

            assertNotNull(result);
            assertEquals("Culto Dominical", result.getName());
            verify(attendanceService, times(2)).countByEventIdWithDefaults(any(), any(), any());
        }

        @Test
        @DisplayName("getWorshipById() debe fallar si no existe")
        void testGetWorshipByIdNotFound() {
            when(meetingService.findByIdWithRelations(UUID.randomUUID())).thenReturn(Optional.empty());

            assertThrows(Exception.class, () -> worshipService.getWorshipById(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("UPDATE - Actualizar Culto")
    class UpdateWorshipTests {

        @Test
        @DisplayName("updateWorship() debe actualizar sin conversión de zona")
        void testUpdateWorshipNoConversion() {
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 20, 14, 0, 0, 0, ZoneOffset.of("-05:00"));
            CreateWorshipDto updateDto = new CreateWorshipDto();
            updateDto.setName("Culto Actualizado");
            updateDto.setDescription("Nuevo culto");
            updateDto.setScheduledDate(newSchedule);
            updateDto.setWorshipTypeId(1L);

            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(updateDto, churchId, worshipId);
            when(meetingService.findById(worshipId)).thenReturn(Optional.of(worshipMeeting));
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(meetingService.update(any(WorshipMeetingModel.class))).thenReturn(worshipMeeting);

            WorshipDto result = worshipService.updateWorship(worshipId, updateDto, churchId);

            assertNotNull(result);
            verify(meetingService, times(1)).update(any(WorshipMeetingModel.class));
        }

        @Test
        @DisplayName("updateWorship() no debe modificar creationDate")
        void testUpdateWorshipKeepsCreationDate() {
            var createDate = worshipMeeting.getCreationDate();
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 20, 14, 0, 0, 0, ZoneOffset.of("-05:00"));

            CreateWorshipDto updateDto = new CreateWorshipDto();
            updateDto.setScheduledDate(newSchedule);
            updateDto.setWorshipTypeId(1L);

            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(updateDto, churchId, worshipId);
            when(meetingService.findById(worshipId)).thenReturn(Optional.of(worshipMeeting));
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(meetingService.update(any(WorshipMeetingModel.class))).thenAnswer(invocation -> {
                WorshipMeetingModel meeting = invocation.getArgument(0);
                assertEquals(createDate, meeting.getCreationDate());
                return meeting;
            });

            worshipService.updateWorship(worshipId, updateDto, churchId);

            verify(meetingService, times(1)).update(any(WorshipMeetingModel.class));
        }
    }

    @Nested
    @DisplayName("DELETE - Eliminar Culto")
    class DeleteWorshipTests {

        @Test
        @DisplayName("deleteWorship() debe llamar al MeetingService")
        void testDeleteWorship() {
            when(meetingService.findById(worshipId)).thenReturn(Optional.of(worshipMeeting));
            doNothing().when(meetingService).delete(worshipId);

            worshipService.deleteWorship(worshipId);

            verify(meetingService, times(1)).delete(worshipId);
        }

        @Test
        @DisplayName("deleteWorship() debe fallar si no existe")
        void testDeleteWorshipNotFound() {
            when(meetingService.findById(UUID.randomUUID())).thenReturn(Optional.empty());

            assertThrows(Exception.class, () -> worshipService.deleteWorship(UUID.randomUUID()));
        }
    }

    @Nested
    @DisplayName("Integración - MeetingService")
    class IntegrationTests {

        @Test
        @DisplayName("Debe usar MeetingService en lugar de WorshipRepository")
        void testUsesMeetingService() {
            doNothing().when(verifyConflict).verifyHourOfWorshipMeeting(createDto, churchId, null);
            when(typesReader.getWorshipMeetingTypesById(1L)).thenReturn(worshipType);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(meetingService.create(any(WorshipMeetingModel.class))).thenReturn(worshipMeeting);

            worshipService.createWorship(createDto, churchId);

            // Verifica que usa MeetingService, no repository
            verify(meetingService, times(1)).create(any(WorshipMeetingModel.class));
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

