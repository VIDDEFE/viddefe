package com.viddefe.viddefe_api.worship_meetings.domain.models;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
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
    private Meeting worshipMeeting;
    private Meeting groupMeeting;
    private OffsetDateTime testScheduledDate;
    private Instant testCreationDate;
    private MeetingType worshipType;
    private MeetingType groupType;
    private ChurchModel church;
    private HomeGroupsModel homeGroup;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
        testCreationDate = Instant.now();

        // Setup types
        worshipType = new MeetingType();
        worshipType.setId(1L);
        worshipType.setName("Culto Dominical");

        groupType = new MeetingType();
        groupType.setId(2L);
        groupType.setName("Estudio Bíblico");

        // Setup church
        church = new ChurchModel();
        church.setId(UUID.randomUUID());
        church.setName("Iglesia Test");

        // Setup home group
        homeGroup = new HomeGroupsModel();
        homeGroup.setId(UUID.randomUUID());
        homeGroup.setName("Grupo Test");

        // Worship meeting (church-level)
        worshipMeeting = new Meeting();
        worshipMeeting.setId(testId);
        worshipMeeting.setName("Culto Dominical");
        worshipMeeting.setDescription("Servicio de adoración");
        worshipMeeting.setScheduledDate(testScheduledDate);
        worshipMeeting.setCreationDate(testCreationDate);
        worshipMeeting.setChurch(church);
        worshipMeeting.setMeetingType(worshipType);

        // Group meeting
        groupMeeting = new Meeting();
        groupMeeting.setId(UUID.randomUUID());
        groupMeeting.setName("Estudio Bíblico");
        groupMeeting.setDescription("Reunión semanal");
        groupMeeting.setScheduledDate(testScheduledDate);
        groupMeeting.setCreationDate(testCreationDate);
        groupMeeting.setGroup(homeGroup);
        groupMeeting.setMeetingType(groupType);
    }

    @Nested
    @DisplayName("Campos Comunes")
    class CamposComunesTests {

        @Test
        @DisplayName("Debe preservar ID")
        void testIdPreservation() {
            assertEquals(testId, worshipMeeting.getId());
            assertNotNull(groupMeeting.getId());
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
        @DisplayName("Debe permitir church para cultos")
        void testChurchForWorship() {
            assertNotNull(worshipMeeting.getChurch());
            assertEquals(church.getId(), worshipMeeting.getChurch().getId());
        }

        @Test
        @DisplayName("Debe permitir group para reuniones de grupo")
        void testGroupForGroupMeetings() {
            assertNotNull(groupMeeting.getGroup());
            assertEquals(homeGroup.getId(), groupMeeting.getGroup().getId());
        }

        @Test
        @DisplayName("Debe permitir meetingType")
        void testMeetingType() {
            assertNotNull(worshipMeeting.getMeetingType());
            assertEquals(1L, worshipMeeting.getMeetingType().getId());

            assertNotNull(groupMeeting.getMeetingType());
            assertEquals(2L, groupMeeting.getMeetingType().getId());
        }
    }

    @Nested
    @DisplayName("Timezone Tests")
    class TimezoneTests {

        @Test
        @DisplayName("Debe manejar diferentes offsets")
        void testDifferentOffsets() {
            // UTC
            OffsetDateTime utcDate = OffsetDateTime.of(2026, 1, 15, 15, 0, 0, 0, ZoneOffset.UTC);
            worshipMeeting.setScheduledDate(utcDate);
            assertEquals(ZoneOffset.UTC, worshipMeeting.getScheduledDate().getOffset());

            // +05:30 (India)
            OffsetDateTime indiaDate = OffsetDateTime.of(2026, 1, 15, 20, 30, 0, 0, ZoneOffset.of("+05:30"));
            groupMeeting.setScheduledDate(indiaDate);
            assertEquals(ZoneOffset.of("+05:30"), groupMeeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Debe preservar hora exacta con offset")
        void testExactTimePreservation() {
            // 10:00 AM en Colombia (-05:00) = 15:00 UTC
            OffsetDateTime colombiaTime = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            worshipMeeting.setScheduledDate(colombiaTime);

            assertEquals(10, worshipMeeting.getScheduledDate().getHour());
            assertEquals(ZoneOffset.of("-05:00"), worshipMeeting.getScheduledDate().getOffset());

            // Verificar que el instante UTC es correcto
            assertEquals(15, worshipMeeting.getScheduledDate().atZoneSameInstant(ZoneOffset.UTC).getHour());
        }
    }

    @Nested
    @DisplayName("DTO Conversion Tests")
    class DtoConversionTests {

        @Test
        @DisplayName("toDto() debe convertir correctamente")
        void testToDtoConversion() {
            var dto = worshipMeeting.toDto();

            assertNotNull(dto);
            assertEquals(worshipMeeting.getId(), dto.getId());
            assertEquals(worshipMeeting.getName(), dto.getName());
            assertEquals(worshipMeeting.getDescription(), dto.getDescription());
            assertEquals(worshipMeeting.getScheduledDate(), dto.getScheduledDate());
        }

        @Test
        @DisplayName("toDto() debe preservar offset en scheduledDate")
        void testToDtoPreservesOffset() {
            var dto = worshipMeeting.toDto();

            assertEquals(ZoneOffset.of("-05:00"), dto.getScheduledDate().getOffset());
        }
    }
}
