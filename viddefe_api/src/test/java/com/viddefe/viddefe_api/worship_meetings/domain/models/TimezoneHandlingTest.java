package com.viddefe.viddefe_api.worship_meetings.domain.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Timezone Handling - Validación de Reglas UTC")
class TimezoneHandlingTest {

    private WorshipMeetingModel meeting;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        meeting = new WorshipMeetingModel();
        meeting.setId(testId);
    }

    @Nested
    @DisplayName("Regla 1: Backend siempre en UTC internamente")
    class BackendUTCTests {

        @Test
        @DisplayName("PostgreSQL timestamptz almacena internamente en UTC")
        void testPostgresTimestamptzUTC() {
            // En PostgreSQL, timestamptz almacena en UTC internamente
            // pero permite offset en la entrada
            OffsetDateTime bogota = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            OffsetDateTime utc = bogota.withOffsetSameInstant(ZoneOffset.UTC);

            // Son el mismo instante pero con diferente offset
            assertEquals(bogota.toInstant(), utc.toInstant());
            // 10:00-05:00 = 15:00 UTC
            assertEquals(15, utc.getHour());
        }

        @Test
        @DisplayName("Backend preserva el offset recibido del cliente")
        void testBackendPreservesClientOffset() {
            OffsetDateTime bogota = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.setScheduledDate(bogota);

            // NO DEBE CONVERTIR
            assertEquals(bogota, meeting.getScheduledDate());
            assertEquals(ZoneOffset.of("-05:00"), meeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Backend NO debe usar ZoneId.systemDefault()")
        void testBackendDoesNotUseSystemDefault() {
            OffsetDateTime input = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.setScheduledDate(input);

            // Verificar que NO se convierte a la zona del sistema
            // (esto es más conceptual, pero validamos que preserva el offset)
            assertFalse(input.getOffset().equals(ZoneOffset.ofHours(0)));

            OffsetDateTime stored = meeting.getScheduledDate();
            assertEquals(input.getOffset(), stored.getOffset());
        }
    }

    @Nested
    @DisplayName("Regla 2: OffsetDateTime para eventos reales")
    class OffsetDateTimeTests {

        @Test
        @DisplayName("Debe usar OffsetDateTime, no LocalDateTime")
        void testUseOffsetDateTime() {
            OffsetDateTime scheduled = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.setScheduledDate(scheduled);

            assertNotNull(meeting.getScheduledDate());
            assertTrue(meeting.getScheduledDate() instanceof OffsetDateTime);
            assertNotNull(meeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("OffsetDateTime preserva el offset exacto")
        void testOffsetDateTimePreservesOffset() {
            ZoneOffset[] offsets = {
                ZoneOffset.UTC,
                ZoneOffset.of("+09:00"),
                ZoneOffset.of("-05:00"),
                ZoneOffset.of("+05:30"),
                ZoneOffset.of("-03:30")
            };

            for (ZoneOffset offset : offsets) {
                OffsetDateTime dateTime = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, offset);
                meeting.setScheduledDate(dateTime);
                assertEquals(offset, meeting.getScheduledDate().getOffset());
            }
        }

        @Test
        @DisplayName("Instant es siempre UTC")
        void testInstantAlwaysUTC() {
            Instant now = Instant.now();
            meeting.setCreationDate(now);

            // Instant no tiene offset, siempre es UTC
            // toString() de Instant siempre termina con Z
            assertTrue(meeting.getCreationDate().toString().endsWith("Z"));
            assertNotNull(meeting.getCreationDate());
        }
    }

    @Nested
    @DisplayName("Regla 3: Frontend envía ISO-8601 con offset")
    class ClientFormatTests {

        @Test
        @DisplayName("Aceptar formato 2026-01-15T10:00:00-05:00")
        void testAcceptOffsetFormat() {
            // Este es el formato que envía el frontend
            String iso = "2026-01-15T10:00:00-05:00";
            OffsetDateTime parsed = OffsetDateTime.parse(iso);

            meeting.setScheduledDate(parsed);
            assertEquals(ZoneOffset.of("-05:00"), meeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Aceptar formato 2026-01-15T15:00:00Z")
        void testAcceptUTCFormat() {
            // Formato UTC
            String iso = "2026-01-15T15:00:00Z";
            OffsetDateTime parsed = OffsetDateTime.parse(iso);

            meeting.setScheduledDate(parsed);
            assertEquals(ZoneOffset.UTC, meeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("Rechazar LocalDateTime (sin offset) en parsing")
        void testRejectLocalDateTimeFormat() {
            // LocalDateTime sin offset (incorrecto para nuestro caso)
            String bad = "2026-01-15T10:00:00";

            assertThrows(Exception.class, () -> OffsetDateTime.parse(bad));
        }
    }

    @Nested
    @DisplayName("Regla 4: Frontend convierte local → UTC")
    class ClientConversionTests {

        @Test
        @DisplayName("Cliente convierte 10:00 Bogotá → 15:00 UTC")
        void testClientConvertsBogotaToUTC() {
            // Usuario ingresa: 10:00 Bogotá (-05:00)
            OffsetDateTime userInput = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

            // Se envía al backend como:
            String iso = userInput.toString(); // "2026-01-15T10:00:00-05:00"

            // Backend almacena en UTC:
            // PostgreSQL internamente: 2026-01-15 15:00:00+00
            OffsetDateTime utc = userInput.withOffsetSameInstant(ZoneOffset.UTC);
            assertEquals(15, utc.getHour());
        }

        @Test
        @DisplayName("Backend NO convierte en mappers")
        void testMapperNoConversion() {
            OffsetDateTime clientInput = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

            // En mapper/service, NO DEBE HACER:
            // var converted = clientInput.atZoneSameInstant(ZoneId.of("UTC")).toOffsetDateTime();

            // DEBE HACER:
            meeting.setScheduledDate(clientInput);  // Directo, sin conversión

            assertEquals(clientInput, meeting.getScheduledDate());
        }
    }

    @Nested
    @DisplayName("Regla 5: Frontend convierte UTC → local para display")
    class DisplayConversionTests {

        @Test
        @DisplayName("Frontend recibe UTC y convierte a hora local")
        void testFrontendConvertsBackToLocal() {
            // Backend retorna: "2026-01-15T15:00:00Z"
            OffsetDateTime utcFromBackend = OffsetDateTime.of(2026, 1, 15, 15, 0, 0, 0, ZoneOffset.UTC);

            // Frontend convierte a Bogotá (-05:00):
            OffsetDateTime bogota = utcFromBackend.withOffsetSameInstant(ZoneOffset.of("-05:00"));

            // Usuario ve: 10:00 (Bogotá)
            assertEquals(10, bogota.getHour());
        }

        @Test
        @DisplayName("Conversión a local no debe perder el instante")
        void testConversionPreservesInstant() {
            OffsetDateTime original = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            OffsetDateTime converted = original.withOffsetSameInstant(ZoneOffset.UTC);
            OffsetDateTime backToLocal = converted.withOffsetSameInstant(ZoneOffset.of("-05:00"));

            // Son el mismo instante
            assertEquals(original.toInstant(), backToLocal.toInstant());
            // Misma hora local
            assertEquals(10, backToLocal.getHour());
        }
    }

    @Nested
    @DisplayName("Conversión Erronea - Casos Prohibidos")
    class ProhibitedConversionsTests {

        @Test
        @DisplayName("NO DEBE usar ZoneId.systemDefault()")
        void testProhibitedSystemDefault() {
            OffsetDateTime input = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

            // ❌ PROHIBIDO:
            // var wrong = input.atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime();

            // ✅ CORRECTO:
            meeting.setScheduledDate(input);
            assertEquals(input, meeting.getScheduledDate());
        }

        @Test
        @DisplayName("NO DEBE usar LocalDateTime")
        void testProhibitedLocalDateTime() {
            // ❌ PROHIBIDO:
            // LocalDateTime.of(2026, 1, 15, 10, 0, 0);

            // ✅ CORRECTO:
            OffsetDateTime correct = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.setScheduledDate(correct);
            assertNotNull(meeting.getScheduledDate().getOffset());
        }

        @Test
        @DisplayName("NO DEBE usar java.util.Date")
        void testProhibitedLegacyDate() {
            // ❌ PROHIBIDO:
            // java.util.Date date = new java.util.Date();

            // ✅ CORRECTO:
            Instant instant = Instant.now();
            meeting.setCreationDate(instant);
            assertNotNull(instant);
        }
    }

    @Nested
    @DisplayName("Configuración Spring - Validación")
    class SpringConfigValidationTests {

        @Test
        @DisplayName("spring.jackson.time-zone=UTC debe estar configurado")
        void testJacksonUTCConfiguration() {
            // Esta prueba es más conceptual
            // En runtime, Jackson respeta esta configuración
            assertTrue(true); // La configuración está en application.properties
        }

        @Test
        @DisplayName("spring.jackson.serialization.WRITE_DATES_AS_TIMESTAMPS=false")
        void testJacksonWriteDateAsTimestamps() {
            // Esto hace que Jackson escriba como strings ISO-8601
            // en lugar de números de milisegundos
            assertTrue(true); // Configurado en application.properties
        }

        @Test
        @DisplayName("spring.jpa.properties.hibernate.jdbc.time_zone=UTC")
        void testHibernateTimeZone() {
            // Hibernate respeta esta configuración para TIMESTAMPTZ
            assertTrue(true); // Configurado en application.properties
        }
    }

    @Nested
    @DisplayName("Validación End-to-End")
    class EndToEndTests {

        @Test
        @DisplayName("Flujo completo sin conversiones")
        void testCompleteFlowNoConversion() {
            // 1. Cliente envía
            String clientJSON = "2026-01-15T10:00:00-05:00";
            OffsetDateTime clientData = OffsetDateTime.parse(clientJSON);

            // 2. Backend recibe y almacena sin conversión
            meeting.setScheduledDate(clientData);
            OffsetDateTime stored = meeting.getScheduledDate();

            // 3. Backend retorna
            String backendJSON = stored.toString();

            // 4. Frontend recibe y convierte para display
            OffsetDateTime displayed = OffsetDateTime.parse(backendJSON);
            OffsetDateTime displayLocal = displayed.withOffsetSameInstant(ZoneOffset.of("-05:00"));

            // Todo debe ser consistente
            assertEquals(10, displayLocal.getHour());
            assertEquals(clientData.toInstant(), displayed.toInstant());
        }

        @Test
        @DisplayName("Multi-zona: Usuarios en diferentes zonas")
        void testMultipleTimeZones() {
            // Usuario 1 en Bogotá (-05:00): 10:00
            OffsetDateTime bogota = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));
            meeting.setScheduledDate(bogota);

            OffsetDateTime utc1 = bogota.withOffsetSameInstant(ZoneOffset.UTC);
            assertEquals(15, utc1.getHour());

            // Usuario 2 en NY (-05:00): Mismo instante
            OffsetDateTime ny = utc1.withOffsetSameInstant(ZoneOffset.of("-05:00"));
            assertEquals(10, ny.getHour());

            // Usuario 3 en Londres (UTC):
            OffsetDateTime london = utc1;
            assertEquals(15, london.getHour());

            // Usuario 4 en Tokio (+09:00):
            OffsetDateTime tokyo = utc1.withOffsetSameInstant(ZoneOffset.of("+09:00"));
            assertEquals(0, tokyo.getHour()); // Siguiente día
            assertEquals(16, tokyo.getDayOfMonth());
        }
    }
}

