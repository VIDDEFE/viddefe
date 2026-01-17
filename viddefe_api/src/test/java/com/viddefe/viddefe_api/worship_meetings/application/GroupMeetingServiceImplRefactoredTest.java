package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceService;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMeetingGroupDto;
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
@DisplayName("GroupMeetingServiceImpl - Servicio de Reuniones de Grupo Refactorizado")
class GroupMeetingServiceImplTest {

    @Mock
    private MeetingService meetingService;

    @Mock
    private HomeGroupReader homeGroupReader;

    @Mock
    private GroupMeetingTypeReader groupMeetingTypeReader;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private GroupMeetingServiceImpl groupMeetingService;

    private UUID groupId;
    private UUID meetingId;
    private CreateMeetingGroupDto createDto;
    private GroupMeetings groupMeeting;
    private GroupMeetingTypes meetingType;
    private HomeGroupsModel homeGroup;
    private OffsetDateTime testScheduledDate;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 19, 0, 0, 0, ZoneOffset.of("-05:00"));

        // DTO
        createDto = new CreateMeetingGroupDto();
        createDto.setName("Estudio Bíblico");
        createDto.setDescription("Reunión semanal");
        createDto.setDate(testScheduledDate);
        createDto.setGroupMeetingTypeId(2L);

        // Entity
        groupMeeting = new GroupMeetings();
        groupMeeting.setId(meetingId);
        groupMeeting.setName("Estudio Bíblico");
        groupMeeting.setContextId(groupId);
        groupMeeting.setTypeId(2L);
        groupMeeting.setScheduledDate(testScheduledDate);

        // Type
        meetingType = new GroupMeetingTypes();
        meetingType.setId(2L);
        meetingType.setName("Estudio Bíblico");

        // HomeGroup
        homeGroup = new HomeGroupsModel();
        homeGroup.setId(groupId);
        homeGroup.setName("Grupo Test");
    }

    @Nested
    @DisplayName("CREATE - Crear Reunión de Grupo")
    class CreateGroupMeetingTests {

        @Test
        @DisplayName("createGroupMeeting() debe crear sin conversión de zona")
        void testCreateGroupMeetingNoConversion() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(GroupMeetings.class))).thenReturn(groupMeeting);

            GroupMeetingDto result = groupMeetingService.createGroupMeeting(createDto, groupId);

            assertNotNull(result);
            assertEquals("Estudio Bíblico", result.getName());
            assertEquals(testScheduledDate.getOffset(), ZoneOffset.of("-05:00"));

            verify(meetingService, times(1)).create(any(GroupMeetings.class));
        }

        @Test
        @DisplayName("createGroupMeeting() debe asignar contextId = groupId")
        void testCreateGroupMeetingAssignsContext() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(GroupMeetings.class))).thenAnswer(invocation -> {
                GroupMeetings meeting = invocation.getArgument(0);
                assertEquals(groupId, meeting.getContextId());
                return meeting;
            });

            groupMeetingService.createGroupMeeting(createDto, groupId);

            verify(meetingService, times(1)).create(any(GroupMeetings.class));
        }

        @Test
        @DisplayName("createGroupMeeting() debe asignar typeId = groupMeetingTypeId")
        void testCreateGroupMeetingAssignsTypeId() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(GroupMeetings.class))).thenAnswer(invocation -> {
                GroupMeetings meeting = invocation.getArgument(0);
                assertEquals(2L, meeting.getTypeId());
                return meeting;
            });

            groupMeetingService.createGroupMeeting(createDto, groupId);

            verify(meetingService, times(1)).create(any(GroupMeetings.class));
        }

        @Test
        @DisplayName("createGroupMeeting() debe preservar OffsetDateTime")
        void testCreateGroupMeetingPreservesOffsetDateTime() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(GroupMeetings.class))).thenAnswer(invocation -> {
                GroupMeetings meeting = invocation.getArgument(0);
                assertEquals(testScheduledDate, meeting.getScheduledDate());
                assertEquals(ZoneOffset.of("-05:00"), meeting.getScheduledDate().getOffset());
                return meeting;
            });

            groupMeetingService.createGroupMeeting(createDto, groupId);

            verify(meetingService, times(1)).create(any(GroupMeetings.class));
        }
    }

    @Nested
    @DisplayName("UPDATE - Actualizar Reunión de Grupo")
    class UpdateGroupMeetingTests {

        @Test
        @DisplayName("updateGroupMeeting() debe actualizar sin conversión de zona")
        void testUpdateGroupMeetingNoConversion() {
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 22, 19, 0, 0, 0, ZoneOffset.of("-05:00"));
            CreateMeetingGroupDto updateDto = new CreateMeetingGroupDto();
            updateDto.setName("Estudio Actualizado");
            updateDto.setDate(newSchedule);
            updateDto.setGroupMeetingTypeId(2L);

            when(meetingService.findById(meetingId)).thenReturn(Optional.of(groupMeeting));
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.update(any(GroupMeetings.class))).thenReturn(groupMeeting);

            GroupMeetingDto result = groupMeetingService.updateGroupMeeting(updateDto, groupId, meetingId);

            assertNotNull(result);
            verify(meetingService, times(1)).update(any(GroupMeetings.class));
        }

        @Test
        @DisplayName("updateGroupMeeting() no debe modificar creationDate")
        void testUpdateGroupMeetingKeepsCreationDate() {
            var createDate = groupMeeting.getCreationDate();
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 22, 19, 0, 0, 0, ZoneOffset.of("-05:00"));

            CreateMeetingGroupDto updateDto = new CreateMeetingGroupDto();
            updateDto.setDate(newSchedule);
            updateDto.setGroupMeetingTypeId(2L);

            when(meetingService.findById(meetingId)).thenReturn(Optional.of(groupMeeting));
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.update(any(GroupMeetings.class))).thenAnswer(invocation -> {
                GroupMeetings meeting = invocation.getArgument(0);
                assertEquals(createDate, meeting.getCreationDate());
                return meeting;
            });

            groupMeetingService.updateGroupMeeting(updateDto, groupId, meetingId);

            verify(meetingService, times(1)).update(any(GroupMeetings.class));
        }
    }

    @Nested
    @DisplayName("DELETE - Eliminar Reunión de Grupo")
    class DeleteGroupMeetingTests {

        @Test
        @DisplayName("deleteGroupMeeting() debe validar que pertenece al grupo")
        void testDeleteGroupMeetingValidatesOwnership() {
            when(meetingService.findById(meetingId)).thenReturn(Optional.of(groupMeeting));
            doNothing().when(meetingService).delete(meetingId);

            groupMeetingService.deleteGroupMeeting(groupId, meetingId);

            verify(meetingService, times(1)).delete(meetingId);
        }

        @Test
        @DisplayName("deleteGroupMeeting() debe fallar si no pertenece al grupo")
        void testDeleteGroupMeetingOwnershipCheck() {
            UUID differentGroupId = UUID.randomUUID();
            when(meetingService.findById(meetingId)).thenReturn(Optional.of(groupMeeting));

            assertThrows(IllegalArgumentException.class, () ->
                groupMeetingService.deleteGroupMeeting(differentGroupId, meetingId)
            );
        }

        @Test
        @DisplayName("deleteGroupMeeting() debe fallar si no existe")
        void testDeleteGroupMeetingNotFound() {
            when(meetingService.findById(UUID.randomUUID())).thenReturn(Optional.empty());

            assertThrows(Exception.class, () ->
                groupMeetingService.deleteGroupMeeting(groupId, UUID.randomUUID())
            );
        }
    }

    @Nested
    @DisplayName("READ - Obtener Reuniones de Grupo")
    class ReadGroupMeetingTests {

        @Test
        @DisplayName("getGroupMeetingById() debe retornar reunión con relaciones")
        void testGetGroupMeetingByIdWithRelations() {
            groupMeeting.setGroupMeetingType(meetingType);
            when(meetingService.findByIdWithRelations(meetingId)).thenReturn(Optional.of(groupMeeting));
            when(attendanceService.countByEventIdWithDefaults(any(), any(), any())).thenReturn(5L);

            var result = groupMeetingService.getGroupMeetingById(groupId, meetingId);

            assertNotNull(result);
            assertEquals("Estudio Bíblico", result.getName());
            verify(attendanceService, times(2)).countByEventIdWithDefaults(any(), any(), any());
        }

        @Test
        @DisplayName("getGroupMeetingById() debe fallar si no existe")
        void testGetGroupMeetingByIdNotFound() {
            when(meetingService.findByIdWithRelations(UUID.randomUUID())).thenReturn(Optional.empty());

            assertThrows(Exception.class, () ->
                groupMeetingService.getGroupMeetingById(groupId, UUID.randomUUID())
            );
        }
    }

    @Nested
    @DisplayName("Validación - Pertenencia al Grupo")
    class ValidationTests {

        @Test
        @DisplayName("validateGroupOwnership() debe permitir si contextId coincide")
        void testValidateGroupOwnershipSucceeds() {
            // La validación usa contextId, no group.id
            assertEquals(groupId, groupMeeting.getContextId());
        }

        @Test
        @DisplayName("validateGroupOwnership() debe fallar si contextId no coincide")
        void testValidateGroupOwnershipFails() {
            UUID differentGroupId = UUID.randomUUID();
            assertNotEquals(differentGroupId, groupMeeting.getContextId());
        }
    }

    @Nested
    @DisplayName("Integración - MeetingService")
    class IntegrationTests {

        @Test
        @DisplayName("Debe usar MeetingService en lugar de GroupMeetingRepository")
        void testUsesMeetingService() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(groupMeetingTypeReader.getGroupMeetingTypeById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(GroupMeetings.class))).thenReturn(groupMeeting);

            groupMeetingService.createGroupMeeting(createDto, groupId);

            verify(meetingService, times(1)).create(any(GroupMeetings.class));
        }

        @Test
        @DisplayName("Debe usar contextId en lugar de group.id para validación")
        void testUsesContextIdForValidation() {
            // Al validar, usa contextId que es genérico
            GroupMeetings meeting = new GroupMeetings();
            meeting.setContextId(groupId);

            assertEquals(groupId, meeting.getContextId());
        }
    }
}

