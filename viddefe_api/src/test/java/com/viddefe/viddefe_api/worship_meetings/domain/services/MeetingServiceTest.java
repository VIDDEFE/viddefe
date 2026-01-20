package com.viddefe.viddefe_api.worship_meetings.domain.services;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.application.MeetingService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import jakarta.persistence.EntityNotFoundException;
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
@DisplayName("MeetingService - Servicio Unificado")
class MeetingServiceTest {

    @Mock
    private MeetingRepository repository;

    @InjectMocks
    private MeetingService service;

    private UUID testId;
    private UUID churchId;
    private UUID groupId;
    private Meeting worshipMeeting;
    private Meeting groupMeeting;
    private OffsetDateTime testScheduledDate;
    private MeetingType worshipType;
    private MeetingType groupType;
    private ChurchModel church;
    private HomeGroupsModel homeGroup;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        churchId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

        // Setup types
        worshipType = new MeetingType();
        worshipType.setId(1L);
        worshipType.setName("Culto Dominical");

        groupType = new MeetingType();
        groupType.setId(2L);
        groupType.setName("Estudio Bíblico");

        // Setup church
        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Iglesia Test");

        // Setup home group
        homeGroup = new HomeGroupsModel();
        homeGroup.setId(groupId);
        homeGroup.setName("Grupo Test");

        // Worship meeting
        worshipMeeting = new Meeting();
        worshipMeeting.setId(testId);
        worshipMeeting.setName("Culto Dominical");
        worshipMeeting.setChurch(church);
        worshipMeeting.setMeetingType(worshipType);
        worshipMeeting.setScheduledDate(testScheduledDate);
        worshipMeeting.setCreationDate(Instant.now());

        // Group meeting
        groupMeeting = new Meeting();
        groupMeeting.setId(UUID.randomUUID());
        groupMeeting.setName("Estudio Bíblico");
        groupMeeting.setGroup(homeGroup);
        groupMeeting.setMeetingType(groupType);
        groupMeeting.setScheduledDate(testScheduledDate);
        groupMeeting.setCreationDate(Instant.now());
    }

    @Nested
    @DisplayName("CREATE - Crear Meeting")
    class CreateTests {

        @Test
        @DisplayName("Debe crear Meeting de tipo Culto")
        void testCreateWorshipMeeting() {
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.create(worshipMeeting);

            assertNotNull(result);
            assertEquals(testId, result.getId());
            assertEquals("Culto Dominical", result.getName());
            verify(repository, times(1)).save(worshipMeeting);
        }

        @Test
        @DisplayName("Debe crear Meeting de tipo Grupo")
        void testCreateGroupMeeting() {
            when(repository.save(any(Meeting.class))).thenReturn(groupMeeting);

            Meeting result = service.create(groupMeeting);

            assertNotNull(result);
            assertEquals("Estudio Bíblico", result.getName());
            verify(repository, times(1)).save(groupMeeting);
        }

        @Test
        @DisplayName("Debe preservar OffsetDateTime sin conversiones")
        void testCreatePreservesOffsetDateTime() {
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            service.create(worshipMeeting);

            assertEquals(testScheduledDate, worshipMeeting.getScheduledDate());
            assertEquals(ZoneOffset.of("-05:00"), worshipMeeting.getScheduledDate().getOffset());
        }
    }

    @Nested
    @DisplayName("READ - Obtener Meeting")
    class ReadTests {

        @Test
        @DisplayName("findById() debe retornar Meeting")
        void testFindById() {
            when(repository.findById(testId)).thenReturn(Optional.of(worshipMeeting));

            Meeting result = service.findById(testId);

            assertNotNull(result);
            assertEquals(testId, result.getId());
            verify(repository, times(1)).findById(testId);
        }

        @Test
        @DisplayName("findById() debe lanzar excepción cuando no existe")
        void testFindByIdNotFound() {
            UUID randomId = UUID.randomUUID();
            when(repository.findById(randomId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> service.findById(randomId));
        }

        @Test
        @DisplayName("findByIdWithRelations() debe retornar Optional con Meeting")
        void testFindByIdWithRelations() {
            when(repository.findWithRelationsById(testId)).thenReturn(Optional.of(worshipMeeting));

            Optional<Meeting> result = service.findByIdWithRelations(testId);

            assertTrue(result.isPresent());
            assertEquals(testId, result.get().getId());
        }

        @Test
        @DisplayName("findByIdWithRelations() debe retornar Optional vacío cuando no existe")
        void testFindByIdWithRelationsNotFound() {
            UUID randomId = UUID.randomUUID();
            when(repository.findWithRelationsById(randomId)).thenReturn(Optional.empty());

            Optional<Meeting> result = service.findByIdWithRelations(randomId);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("UPDATE - Actualizar Meeting")
    class UpdateTests {

        @Test
        @DisplayName("update() debe guardar y retornar Meeting actualizado")
        void testUpdate() {
            worshipMeeting.setName("Culto Actualizado");
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.update(worshipMeeting);

            assertNotNull(result);
            assertEquals("Culto Actualizado", result.getName());
            verify(repository, times(1)).save(worshipMeeting);
        }

        @Test
        @DisplayName("update() debe preservar OffsetDateTime")
        void testUpdatePreservesOffsetDateTime() {
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 20, 14, 0, 0, 0, ZoneOffset.of("-05:00"));
            worshipMeeting.setScheduledDate(newSchedule);
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.update(worshipMeeting);

            assertEquals(newSchedule, result.getScheduledDate());
            assertEquals(ZoneOffset.of("-05:00"), result.getScheduledDate().getOffset());
        }
    }

    @Nested
    @DisplayName("DELETE - Eliminar Meeting")
    class DeleteTests {

        @Test
        @DisplayName("delete() debe llamar al repositorio")
        void testDelete() {
            doNothing().when(repository).deleteById(testId);

            service.delete(testId);

            verify(repository, times(1)).deleteById(testId);
        }
    }

    @Nested
    @DisplayName("Timezone Tests")
    class TimezoneTests {

        @Test
        @DisplayName("Debe manejar diferentes zonas horarias")
        void testDifferentTimezones() {
            // Crear meeting con hora de Colombia
            OffsetDateTime colombiaTime = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            worshipMeeting.setScheduledDate(colombiaTime);
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.create(worshipMeeting);

            assertEquals(colombiaTime, result.getScheduledDate());
            assertEquals(ZoneOffset.of("-05:00"), result.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("No debe convertir zona horaria automáticamente")
        void testNoAutomaticTimezoneConversion() {
            OffsetDateTime utcTime = OffsetDateTime.of(2026, 1, 15, 15, 0, 0, 0, ZoneOffset.UTC);
            worshipMeeting.setScheduledDate(utcTime);
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.create(worshipMeeting);

            // Debe mantener el offset original, NO convertir a otro
            assertEquals(ZoneOffset.UTC, result.getScheduledDate().getOffset());
            assertEquals(15, result.getScheduledDate().getHour());
        }
    }
}
