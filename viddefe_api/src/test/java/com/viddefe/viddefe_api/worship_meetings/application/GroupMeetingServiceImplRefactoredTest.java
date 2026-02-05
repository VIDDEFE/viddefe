package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.homeGroups.contracts.HomeGroupReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
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
@DisplayName("GroupMeetingServiceImpl - Servicio de Reuniones de Grupo Refactorizado")
class GroupMeetingServiceImplRefactoredTest {

    @Mock
    private MeetingService meetingService;

    @Mock
    private HomeGroupReader homeGroupReader;

    @Mock
    private MeetingTypesService meetingTypesService;

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private GroupMeetingServiceImpl groupMeetingService;

    private UUID groupId;
    private UUID churchId;
    private UUID meetingId;
    private CreateMeetingDto createDto;
    private Meeting groupMeeting;
    private MeetingType meetingType;
    private HomeGroupsModel homeGroup;
    private OffsetDateTime testScheduledDate;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        churchId = UUID.randomUUID();
        meetingId = UUID.randomUUID();
        testScheduledDate = OffsetDateTime.of(2026, 1, 15, 19, 0, 0, 0, ZoneOffset.of("-05:00"));

        // DTO
        createDto = new CreateMeetingDto();
        createDto.setName("Estudio Bíblico");
        createDto.setDescription("Reunión semanal");
        createDto.setScheduledDate(testScheduledDate);
        createDto.setMeetingTypeId(2L);

        // Type
        meetingType = new MeetingType();
        meetingType.setId(2L);
        meetingType.setName("Estudio Bíblico");

        // HomeGroup
        homeGroup = new HomeGroupsModel();
        homeGroup.setId(groupId);
        homeGroup.setName("Grupo Test");

        // Entity
        groupMeeting = new Meeting();
        groupMeeting.setId(meetingId);
        groupMeeting.setName("Estudio Bíblico");
        groupMeeting.setGroup(homeGroup);
        groupMeeting.setMeetingType(meetingType);
        groupMeeting.setScheduledDate(testScheduledDate);
        groupMeeting.setCreationDate(Instant.now());
    }

    @Nested
    @DisplayName("CREATE - Crear Reunión de Grupo")
    class CreateGroupMeetingTests {

        @Test
        @DisplayName("createGroupMeeting() debe crear sin conversión de zona")
        void testCreateGroupMeetingNoConversion() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(meetingTypesService.getMeetingTypesById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(Meeting.class))).thenReturn(groupMeeting);

            MeetingDto result = groupMeetingService.createGroupMeeting(createDto, groupId, churchId);

            assertNotNull(result);
            assertEquals("Estudio Bíblico", result.getName());
            assertEquals(testScheduledDate.getOffset(), ZoneOffset.of("-05:00"));

            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("createGroupMeeting() debe asignar group correctamente")
        void testCreateGroupMeetingAssignsContext() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(meetingTypesService.getMeetingTypesById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(groupId, meeting.getGroup().getId());
                return groupMeeting;
            });

            groupMeetingService.createGroupMeeting(createDto, groupId, churchId);

            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("createGroupMeeting() debe asignar meetingType correctamente")
        void testCreateGroupMeetingAssignsTypeId() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(meetingTypesService.getMeetingTypesById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(2L, meeting.getMeetingType().getId());
                return groupMeeting;
            });

            groupMeetingService.createGroupMeeting(createDto, groupId, churchId);

            verify(meetingService, times(1)).create(any(Meeting.class));
        }

        @Test
        @DisplayName("createGroupMeeting() debe preservar OffsetDateTime")
        void testCreateGroupMeetingPreservesOffsetDateTime() {
            when(homeGroupReader.findById(groupId)).thenReturn(homeGroup);
            when(meetingTypesService.getMeetingTypesById(2L)).thenReturn(meetingType);
            when(meetingService.create(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(testScheduledDate, meeting.getScheduledDate());
                assertEquals(ZoneOffset.of("-05:00"), meeting.getScheduledDate().getOffset());
                return groupMeeting;
            });

            groupMeetingService.createGroupMeeting(createDto, groupId, churchId);

            verify(meetingService, times(1)).create(any(Meeting.class));
        }
    }

    @Nested
    @DisplayName("UPDATE - Actualizar Reunión de Grupo")
    class UpdateGroupMeetingTests {

        @Test
        @DisplayName("updateGroupMeeting() debe actualizar sin conversión de zona")
        void testUpdateGroupMeetingNoConversion() {
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 22, 19, 0, 0, 0, ZoneOffset.of("-05:00"));
            CreateMeetingDto updateDto = new CreateMeetingDto();
            updateDto.setName("Estudio Actualizado");
            updateDto.setScheduledDate(newSchedule);
            updateDto.setMeetingTypeId(2L);

            when(meetingService.findById(meetingId)).thenReturn(groupMeeting);
            when(meetingTypesService.getMeetingTypesById(2L)).thenReturn(meetingType);
            when(meetingService.update(any(Meeting.class))).thenReturn(groupMeeting);

            MeetingDto result = groupMeetingService.updateGroupMeeting(updateDto, groupId, meetingId);

            assertNotNull(result);
            verify(meetingService, times(1)).update(any(Meeting.class));
        }

        @Test
        @DisplayName("updateGroupMeeting() no debe modificar creationDate")
        void testUpdateGroupMeetingKeepsCreationDate() {
            var createDate = groupMeeting.getCreationDate();
            OffsetDateTime newSchedule = OffsetDateTime.of(2026, 1, 22, 19, 0, 0, 0, ZoneOffset.of("-05:00"));

            CreateMeetingDto updateDto = new CreateMeetingDto();
            updateDto.setName("Estudio Actualizado");
            updateDto.setScheduledDate(newSchedule);
            updateDto.setMeetingTypeId(2L);

            when(meetingService.findById(meetingId)).thenReturn(groupMeeting);
            when(meetingTypesService.getMeetingTypesById(2L)).thenReturn(meetingType);
            when(meetingService.update(any(Meeting.class))).thenAnswer(invocation -> {
                Meeting meeting = invocation.getArgument(0);
                assertEquals(createDate, meeting.getCreationDate());
                return meeting;
            });

            groupMeetingService.updateGroupMeeting(updateDto, groupId, meetingId);

            verify(meetingService, times(1)).update(any(Meeting.class));
        }
    }

    @Nested
    @DisplayName("DELETE - Eliminar Reunión de Grupo")
    class DeleteGroupMeetingTests {

        @Test
        @DisplayName("deleteGroupMeeting() debe validar que pertenece al grupo")
        void testDeleteGroupMeetingValidatesOwnership() {
            when(meetingService.findById(meetingId)).thenReturn(groupMeeting);
            doNothing().when(meetingService).delete(meetingId);

            groupMeetingService.deleteGroupMeeting(groupId, meetingId);

            verify(meetingService, times(1)).delete(meetingId);
        }

        @Test
        @DisplayName("deleteGroupMeeting() debe fallar si no pertenece al grupo")
        void testDeleteGroupMeetingOwnershipCheck() {
            UUID differentGroupId = UUID.randomUUID();
            when(meetingService.findById(meetingId)).thenReturn(groupMeeting);

            assertThrows(IllegalArgumentException.class, () ->
                groupMeetingService.deleteGroupMeeting(differentGroupId, meetingId)
            );
        }

        @Test
        @DisplayName("deleteGroupMeeting() debe fallar si no existe")
        void testDeleteGroupMeetingNotFound() {
            UUID randomId = UUID.randomUUID();
            when(meetingService.findById(randomId)).thenThrow(new jakarta.persistence.EntityNotFoundException("Meeting not found"));

            assertThrows(Exception.class, () ->
                groupMeetingService.deleteGroupMeeting(groupId, randomId)
            );
        }
    }

    @Nested
    @DisplayName("READ - Obtener Reuniones de Grupo")
    class ReadGroupMeetingTests {

        @Test
        @DisplayName("getGroupMeetingById() debe retornar reunión con relaciones")
        void testGetGroupMeetingByIdWithRelations() {
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
            UUID randomId = UUID.randomUUID();
            when(meetingService.findByIdWithRelations(randomId)).thenReturn(Optional.empty());

            assertThrows(Exception.class, () -> groupMeetingService.getGroupMeetingById(groupId, randomId));
        }
    }
}
