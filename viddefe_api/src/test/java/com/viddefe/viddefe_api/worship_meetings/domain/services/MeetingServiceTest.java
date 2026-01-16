package com.viddefe.viddefe_api.worship_meetings.domain.services;

import com.viddefe.viddefe_api.worship_meetings.application.MeetingService;
import com.viddefe.viddefe_api.worship_meetings.domain.models.*;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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
    private UUID contextId;
    private WorshipMeetingModel worshipMeeting;
    private GroupMeetings groupMeeting;
    private OffsetDateTime testScheduledDate;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 10, 0, 0, 0, ZoneOffset.of("-05:00"));

        worshipMeeting = new WorshipMeetingModel();
        worshipMeeting.setId(testId);
        worshipMeeting.setName("Culto Dominical");
        worshipMeeting.setContextId(contextId);
        worshipMeeting.setTypeId(1L);
        worshipMeeting.setScheduledDate(testScheduledDate);
        worshipMeeting.setCreationDate(Instant.now());

        groupMeeting = new GroupMeetings();
        groupMeeting.setId(UUID.randomUUID());
        groupMeeting.setName("Estudio Bíblico");
        groupMeeting.setContextId(contextId);
        groupMeeting.setTypeId(2L);
        groupMeeting.setScheduledDate(testScheduledDate);
        groupMeeting.setCreationDate(Instant.now());
    }

    @Nested
    @DisplayName("CREATE - Crear Meeting")
    class CreateTests {

        @Test
        @DisplayName("Debe crear WorshipMeetingModel")
        void testCreateWorshipMeeting() {
            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.create(worshipMeeting);

            assertNotNull(result);
            assertEquals(testId, result.getId());
            assertEquals("Culto Dominical", result.getName());
            verify(repository, times(1)).save(worshipMeeting);
        }

        @Test
        @DisplayName("Debe crear GroupMeetings")
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
        @DisplayName("findById() debe retornar Optional con Meeting")
        void testFindById() {
            when(repository.findById(testId)).thenReturn(Optional.of(worshipMeeting));

            Optional<Meeting> result = service.findById(testId);

            assertTrue(result.isPresent());
            assertEquals(worshipMeeting, result.get());
            verify(repository, times(1)).findById(testId);
        }

        @Test
        @DisplayName("findById() debe retornar Optional vacío si no existe")
        void testFindByIdNotFound() {
            when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

            Optional<Meeting> result = service.findById(UUID.randomUUID());

            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("findByIdWithRelations() debe cargar relaciones")
        void testFindByIdWithRelations() {
            when(repository.findWithRelationsById(testId)).thenReturn(Optional.of(worshipMeeting));

            Optional<Meeting> result = service.findByIdWithRelations(testId);

            assertTrue(result.isPresent());
            assertEquals(worshipMeeting, result.get());
            verify(repository, times(1)).findWithRelationsById(testId);
        }
    }

    @Nested
    @DisplayName("READ - Obtener por Contexto y Tipo")
    class FilterByContextAndTypeTests {

        @Test
        @DisplayName("findByContextId() debe retornar Page de meetings")
        void testFindByContextId() {
            List<Meeting> meetings = new ArrayList<>();
            meetings.add(worshipMeeting);
            meetings.add(groupMeeting);
            Page<Meeting> page = new PageImpl<>(meetings);

            when(repository.findByContextId(eq(contextId), any(Pageable.class))).thenReturn(page);

            Pageable pageable = PageRequest.of(0, 10);
            Page<Meeting> result = service.findByContextId(contextId, pageable);

            assertEquals(2, result.getContent().size());
            verify(repository, times(1)).findByContextId(contextId, pageable);
        }
    }


    @Nested
    @DisplayName("UPDATE - Actualizar Meeting")
    class UpdateTests {

        @Test
        @DisplayName("update() debe guardar cambios")
        void testUpdate() {
            WorshipMeetingModel updated = new WorshipMeetingModel();
            updated.setId(testId);
            updated.setName("Culto Actualizado");

            when(repository.save(any(Meeting.class))).thenReturn(updated);

            Meeting result = service.update(updated);

            assertEquals("Culto Actualizado", result.getName());
            verify(repository, times(1)).save(updated);
        }

        @Test
        @DisplayName("update() debe preservar offset después de actualización")
        void testUpdatePreservesOffset() {
            worshipMeeting.setName("Nombre nuevo");
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 2, 1, 14, 0, 0, 0, ZoneOffset.of("-05:00"));
            worshipMeeting.setScheduledDate(newSchedule);

            when(repository.save(any(Meeting.class))).thenReturn(worshipMeeting);

            Meeting result = service.update(worshipMeeting);

            assertEquals(ZoneOffset.of("-05:00"), result.getScheduledDate().getOffset());
        }
    }

    @Nested
    @DisplayName("DELETE - Eliminar Meeting")
    class DeleteTests {

        @Test
        @DisplayName("delete() debe llamar al repository")
        void testDelete() {
            doNothing().when(repository).deleteById(testId);

            service.delete(testId);

            verify(repository, times(1)).deleteById(testId);
        }
    }

    @Nested
    @DisplayName("CONFLICTO - Validar Duplicados")
    class ConflictTests {

        @Test
        @DisplayName("existsConflict() debe retornar true si existe duplicado")
        void testExistsConflictTrue() {
            when(repository.existsByContextIdAndTypeIdAndScheduledDate(contextId, 1L, testScheduledDate))
                    .thenReturn(true);

            boolean result = service.existsConflict(contextId, 1L, testScheduledDate);

            assertTrue(result);
            verify(repository, times(1)).existsByContextIdAndTypeIdAndScheduledDate(contextId, 1L, testScheduledDate);
        }

        @Test
        @DisplayName("existsConflict() debe retornar false si no existe duplicado")
        void testExistsConflictFalse() {
            when(repository.existsByContextIdAndTypeIdAndScheduledDate(any(UUID.class), any(Long.class), any(OffsetDateTime.class)))
                    .thenReturn(false);

            boolean result = service.existsConflict(UUID.randomUUID(), 99L, testScheduledDate);

            assertFalse(result);
        }

        @Test
        @DisplayName("Conflicto debe considerar contexto + tipo + fecha")
        void testConflictConsidersAllThreeFactors() {
            UUID otherContext = UUID.randomUUID();
            Long otherType = 999L;

            when(repository.existsByContextIdAndTypeIdAndScheduledDate(contextId, 1L, testScheduledDate)).thenReturn(true);
            when(repository.existsByContextIdAndTypeIdAndScheduledDate(otherContext, 1L, testScheduledDate)).thenReturn(false);
            when(repository.existsByContextIdAndTypeIdAndScheduledDate(contextId, otherType, testScheduledDate)).thenReturn(false);

            assertTrue(service.existsConflict(contextId, 1L, testScheduledDate));
            assertFalse(service.existsConflict(otherContext, 1L, testScheduledDate));
            assertFalse(service.existsConflict(contextId, otherType, testScheduledDate));
        }
    }

    @Nested
    @DisplayName("Polymorphism - Polimorfismo en Herencia")
    class PolymorphismTests {

        @Test
        @DisplayName("Repository debe retornar Meeting polimórficas")
        void testRepositoryReturnsPolymorphic() {
            List<Meeting> meetings = new ArrayList<>();
            meetings.add(worshipMeeting);
            meetings.add(groupMeeting);

            when(repository.findAll()).thenReturn(meetings);

            List<Meeting> result = repository.findAll();

            assertEquals(2, result.size());
            assertTrue(result.get(0) instanceof WorshipMeetingModel);
            assertTrue(result.get(1) instanceof GroupMeetings);
        }

        @Test
        @DisplayName("Debe permitir cast a tipo específico")
        void testCastToSpecificType() {
            Optional<Meeting> meeting = Optional.of(worshipMeeting);

            assertTrue(meeting.get() instanceof WorshipMeetingModel);
            WorshipMeetingModel worship = (WorshipMeetingModel) meeting.get();
            assertEquals("Culto Dominical", worship.getName());
        }
        }
}

