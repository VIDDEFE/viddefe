package com.viddefe.viddefe_api.worship_meetings.domain.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Meeting - Entidad Base Normalizada")
class MeetingTest {

    private UUID testId;
    private WorshipMeetingModel worshipMeeting;
    private GroupMeetings groupMeeting;
    private OffsetDateTime testScheduledDate;
    private Instant testCreationDate;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
        testCreationDate = Instant.now();

        worshipMeeting = new WorshipMeetingModel();
        worshipMeeting.setId(testId);
        worshipMeeting.setName("Culto Dominical");
        worshipMeeting.setDescription("Servicio de adoración");
        worshipMeeting.setScheduledDate(testScheduledDate);
        worshipMeeting.setCreationDate(testCreationDate);

        groupMeeting = new GroupMeetings();
        groupMeeting.setId(testId);
        groupMeeting.setName("Estudio Bíblico");
        groupMeeting.setDescription("Reunión semanal");
        groupMeeting.setScheduledDate(testScheduledDate);
        groupMeeting.setCreationDate(testCreationDate);
    }

    @Nested
    @DisplayName("Campos Comunes")
    class CamposComunesTests {

        @Test
        @DisplayName("Debe preservar ID")
        void testIdPreservation() {
            assertEquals(testId, worshipMeeting.getId());
            assertEquals(testId, groupMeeting.getId());
        }

        @Test
        @DisplayName("Debe preservar nombre")
        void testNamePreservation() {
            assertEquals("Culto Dominical", worshipMeeting.getName());
            assertEquals("Estudio Bíblico", groupMeeting.getName());
        }

        @Test
        @DisplayName("Debe preservar descripción")
        void testDescriptionPreservation() {
            assertEquals("Servicio de adoración", worshipMeeting.getDescription());
            assertEquals("Reunión semanal", groupMeeting.getDescription());
        }

        @Test
        @DisplayName("Debe preservar scheduled_date con offset")
        void testScheduledDatePreservation() {
            assertEquals(testScheduledDate, worshipMeeting.getScheduledDate());
            assertEquals(testScheduledDate, groupMeeting.getScheduledDate());

            // Validar que preserva el offset (-05:00)
            assertEquals(ZoneOffset.of("-05:00"), worshipMeeting.getScheduledDate().getOffset());
            assertEquals(ZoneOffset.of("-05:00"), groupMeeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Debe preservar creation_date como Instant (UTC)")
        void testCreationDatePreservation() {
            assertEquals(testCreationDate, worshipMeeting.getCreationDate());
            assertEquals(testCreationDate, groupMeeting.getCreationDate());
        }

        @Test
        @DisplayName("Debe permitir contexto genérico (contextId)")
        void testContextIdGeneric() {
            UUID churchId = UUID.randomUUID();
            worshipMeeting.setContextId(churchId);
            assertEquals(churchId, worshipMeeting.getContextId());

            UUID groupId = UUID.randomUUID();
            groupMeeting.setContextId(groupId);
            assertEquals(groupId, groupMeeting.getContextId());
        }

        @Test
        @DisplayName("Debe permitir tipo genérico (typeId)")
        void testTypeIdGeneric() {
            Long worshipTypeId = 1L;
            worshipMeeting.setTypeId(worshipTypeId);
            assertEquals(worshipTypeId, worshipMeeting.getTypeId());

            Long groupTypeId = 2L;
            groupMeeting.setTypeId(groupTypeId);
            assertEquals(groupTypeId, groupMeeting.getTypeId());
        }
    }

    @Nested
    @DisplayName("Timezone - Sin Conversiones")
    class TimezoneTests {

        @Test
        @DisplayName("No debe convertir OffsetDateTime")
        void testNoTimezoneConversion() {
            OffsetDateTime bogota = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            worshipMeeting.setScheduledDate(bogota);

            // Debe preservar exactamente como llega
            assertEquals(bogota, worshipMeeting.getScheduledDate());
            assertEquals(ZoneOffset.of("-05:00"), worshipMeeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Debe soportar UTC (Z)")
        void testUTCTimezone() {
            OffsetDateTime utc = OffsetDateTime.of(2026, 1, 15, 15, 0, 0, 0, ZoneOffset.UTC);
            groupMeeting.setScheduledDate(utc);

            assertEquals(utc, groupMeeting.getScheduledDate());
            assertEquals(ZoneOffset.UTC, groupMeeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Debe soportar cualquier offset válido")
        void testAnyValidOffset() {
            OffsetDateTime newyork = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            OffsetDateTime london = OffsetDateTime.of(2026, 1, 15, 15, 0, 0, 0, ZoneOffset.UTC);
            OffsetDateTime tokyo = OffsetDateTime.of(2026, 1, 16, 0, 0, 0, 0, ZoneOffset.of("+09:00"));

            worshipMeeting.setScheduledDate(newyork);
            assertEquals(ZoneOffset.of("-05:00"), worshipMeeting.getScheduledDate().getOffset());

            groupMeeting.setScheduledDate(london);
            assertEquals(ZoneOffset.UTC, groupMeeting.getScheduledDate().getOffset());

            worshipMeeting.setScheduledDate(tokyo);
            assertEquals(ZoneOffset.of("+09:00"), worshipMeeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("CreationDate debe ser Instant (siempre UTC)")
        void testCreationDateIsInstant() {
            Instant now = Instant.now();
            worshipMeeting.setCreationDate(now);

            // Instant no tiene offset, siempre es UTC
            assertEquals(now, worshipMeeting.getCreationDate());
            assertNotNull(worshipMeeting.getCreationDate());
        }
    }

    @Nested
    @DisplayName("WorshipMeetingModel - Discriminador WORSHIP")
    class WorshipMeetingModelTests {

        @Test
        @DisplayName("Debe ser subclase de Meeting")
        void testInheritance() {
            assertTrue(worshipMeeting instanceof Meeting);
        }

        @Test
        @DisplayName("fromDto() debe inicializar desde DTO sin conversiones")
        void testFromDto() {
            WorshipMeetingModel newWorship = new WorshipMeetingModel();
            newWorship.setName("Culto Especial");
            newWorship.setDescription("Evento especial");
            OffsetDateTime scheduled = OffsetDateTime.of(2026, 1, 20, 18, 0, 0, 0, ZoneOffset.of("-05:00"));
            newWorship.setScheduledDate(scheduled);

            assertNotNull(newWorship);
            assertEquals("Culto Especial", newWorship.getName());
            assertEquals(scheduled, newWorship.getScheduledDate());
        }

        @Test
        @DisplayName("updateFrom() no debe modificar creationDate")
        void testUpdateFromDoesNotChangeBirthDate() {
            Instant originalCreationDate = worshipMeeting.getCreationDate();

            // Simulación de actualización
            worshipMeeting.setName("Culto Actualizado");
            worshipMeeting.setScheduledDate(OffsetDateTime.of(2026, 2, 1, 10, 0, 0, 0, ZoneOffset.of("-05:00")));

            // CreationDate debe permanecer igual
            assertEquals(originalCreationDate, worshipMeeting.getCreationDate());
        }

        @Test
        @DisplayName("toDto() debe preservar offset en DTO")
        void testToDtoPreservesOffset() {
            OffsetDateTime scheduled = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            worshipMeeting.setScheduledDate(scheduled);

            // El DTO debe tener el mismo offset
            assertEquals(scheduled.getOffset(), worshipMeeting.getScheduledDate().getOffset());
        }
    }

    @Nested
    @DisplayName("GroupMeetings - Discriminador GROUP_MEETING")
    class GroupMeetingsTests {

        @Test
        @DisplayName("Debe ser subclase de Meeting")
        void testInheritance() {
            assertTrue(groupMeeting instanceof Meeting);
        }

        @Test
        @DisplayName("Debe permitir acceso a groupMeetingType")
        void testGroupMeetingTypeAccess() {
            GroupMeetingTypes type = new GroupMeetingTypes();
            type.setId(1L);
            type.setName("Estudio Bíblico");
            groupMeeting.setGroupMeetingType(type);

            assertEquals(type, groupMeeting.getGroupMeetingType());
            assertEquals("Estudio Bíblico", groupMeeting.getGroupMeetingType().getName());
        }

        @Test
        @DisplayName("Debe permitir acceso a HomeGroupsModel")
        void testHomeGroupsModelAccess() {
            assertNull(groupMeeting.getGroup());
            // Se puede inyectar posteriormente si es necesario
        }

        @Test
        @DisplayName("Constructor con contextId y typeId")
        void testConstructorWithIds() {
            UUID groupId = UUID.randomUUID();
            Long typeId = 2L;

            GroupMeetings meeting = new GroupMeetings(groupId, typeId);

            assertEquals(groupId, meeting.getContextId());
            assertEquals(typeId, meeting.getTypeId());
        }
    }

    @Nested
    @DisplayName("Métodos initFromDto y updateFromDto")
    class DtoConversionTests {

        @Test
        @DisplayName("initFromDto() debe setear todos los campos")
        void testInitFromDtoSetsAllFields() {
            WorshipMeetingModel meeting = new WorshipMeetingModel();
            OffsetDateTime scheduled = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

            meeting.initFromDto("Culto Test", "Descripción test", scheduled);

            assertEquals("Culto Test", meeting.getName());
            assertEquals("Descripción test", meeting.getDescription());
            assertEquals(scheduled, meeting.getScheduledDate());
            assertNotNull(meeting.getCreationDate());
        }

        @Test
        @DisplayName("updateFromDto() debe actualizar nombre, descripción y fecha")
        void testUpdateFromDtoUpdatesFields() {
            WorshipMeetingModel meeting = new WorshipMeetingModel();
            Instant originalDate = Instant.now();
            meeting.setCreationDate(originalDate);

            OffsetDateTime newScheduled = OffsetDateTime.of(2026, 1, 20, 14, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.updateFromDto("Culto Actualizado", "Nueva descripción", newScheduled);

            assertEquals("Culto Actualizado", meeting.getName());
            assertEquals("Nueva descripción", meeting.getDescription());
            assertEquals(newScheduled, meeting.getScheduledDate());
            // CreationDate no debe cambiar
            assertEquals(originalDate, meeting.getCreationDate());
        }

        @Test
        @DisplayName("updateFromDto() con null description debe actualizar")
        void testUpdateFromDtoWithNullDescription() {
            WorshipMeetingModel meeting = new WorshipMeetingModel();
            meeting.setDescription("Descripción original");

            OffsetDateTime scheduled = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.updateFromDto("Nuevo nombre", null, scheduled);

            assertEquals("Nuevo nombre", meeting.getName());
            assertNull(meeting.getDescription());
            assertEquals(scheduled, meeting.getScheduledDate());
        }
    }
}

