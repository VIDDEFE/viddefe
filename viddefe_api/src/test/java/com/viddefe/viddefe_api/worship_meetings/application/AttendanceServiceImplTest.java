package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceStatus;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceModel;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceProjectionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateAttendanceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceServiceImpl Tests")
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private MeetingReader meetingReader;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @Captor
    private ArgumentCaptor<AttendanceModel> attendanceCaptor;

    private UUID eventId;
    private UUID peopleId;
    private UUID attendanceId;
    private UUID contextId;
    private PeopleModel person;
    private CreateAttendanceDto createDto;
    private AttendanceQualityEnum attendanceQualityEnum;
    private Meeting meeting;
    private ChurchModel church;
    private HomeGroupsModel homeGroup;

    private PeopleModel createPeopleModel(UUID id, String firstName, String lastName) {
        PeopleModel people = new PeopleModel();
        people.setId(id);
        people.setFirstName(firstName);
        people.setLastName(lastName);
        people.setBirthDate(java.time.LocalDate.of(1990, 1, 1));

        StatesModel state = new StatesModel();
        state.setId(1L);
        state.setName("Antioquia");
        people.setState(state);

        return people;
    }

    private Meeting createMeeting() {
        Meeting meeting = new Meeting();
        meeting.setId(eventId);
        meeting.setName("Sunday Service");
        meeting.setChurch(church);
        meeting.setGroup(homeGroup);
        return meeting;
    }

    @BeforeEach
    void setUp() throws Exception {
        eventId = UUID.randomUUID();
        peopleId = UUID.randomUUID();
        attendanceId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        attendanceQualityEnum = AttendanceQualityEnum.HIGH;

        // Crear church y homeGroup antes de crear el meeting
        church = new ChurchModel();
        church.setId(contextId);
        church.setName("Test Church");

        homeGroup = new HomeGroupsModel();
        homeGroup.setId(UUID.randomUUID());
        homeGroup.setName("Test Group");

        person = createPeopleModel(peopleId, "Juan", "Pérez");
        meeting = createMeeting();

        createDto = createAttendanceDto(peopleId, eventId);
    }

    private CreateAttendanceDto createAttendanceDto(UUID peopleId, UUID eventId) throws Exception {
        CreateAttendanceDto dto = new CreateAttendanceDto();
        setField(dto, "peopleId", peopleId);
        setField(dto, "eventId", eventId);
        return dto;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private AttendanceModel createAttendanceModel(UUID id, AttendanceStatus status) {
        return new AttendanceModel(
                id,
                person,
                meeting,
                TopologyEventType.TEMPLE_WORHSIP,
                status,
                true
        );
    }

    @Nested
    @DisplayName("Update Attendance Tests")
    class UpdateAttendanceTests {

        @Test
        @DisplayName("Debe crear nueva asistencia cuando no existe registro previo")
        void updateAttendance_WhenNoExistingRecord_ShouldCreateNewAttendance() {
            // Arrange
            when(meetingReader.getById(eventId)).thenReturn(meeting);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(attendanceRepository.findByPeopleIdAndEventId(peopleId, eventId))
                    .thenReturn(Optional.empty());

            AttendanceModel savedModel = createAttendanceModel(attendanceId, AttendanceStatus.PRESENT);
            when(attendanceRepository.save(any(AttendanceModel.class))).thenReturn(savedModel);

            // Act
            AttendanceDto result = attendanceService.updateAttendance(
                    createDto,
                    TopologyEventType.TEMPLE_WORHSIP
            );

            // Assert
            assertNotNull(result);
            verify(attendanceRepository).save(attendanceCaptor.capture());
            assertEquals(AttendanceStatus.PRESENT, attendanceCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("Debe eliminar asistencia cuando ya existe registro")
        void updateAttendance_WhenExistingRecord_ShouldDeleteAttendance() {
            // Arrange
            AttendanceModel existingModel = createAttendanceModel(attendanceId, AttendanceStatus.PRESENT);

            when(meetingReader.getById(eventId)).thenReturn(meeting);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(attendanceRepository.findByPeopleIdAndEventId(peopleId, eventId))
                    .thenReturn(Optional.of(existingModel));
            doNothing().when(attendanceRepository).deleteById(attendanceId);

            // Act
            AttendanceDto result = attendanceService.updateAttendance(
                    createDto,
                    TopologyEventType.TEMPLE_WORHSIP
            );

            // Assert
            assertNotNull(result);
            verify(attendanceRepository).deleteById(attendanceId);
            verify(attendanceRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe asignar tipo de evento correcto - TEMPLE_WORSHIP")
        void updateAttendance_WithTempleWorship_ShouldSetCorrectEventType() {
            // Arrange
            when(meetingReader.getById(eventId)).thenReturn(meeting);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(attendanceRepository.findByPeopleIdAndEventId(peopleId, eventId))
                    .thenReturn(Optional.empty());
            when(attendanceRepository.save(any(AttendanceModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            attendanceService.updateAttendance(createDto, TopologyEventType.TEMPLE_WORHSIP);

            // Assert
            verify(attendanceRepository).save(attendanceCaptor.capture());
            assertEquals(TopologyEventType.TEMPLE_WORHSIP, attendanceCaptor.getValue().getEventType());
        }

        @Test
        @DisplayName("Debe asignar tipo de evento correcto - GROUP_MEETING")
        void updateAttendance_WithGroupMeeting_ShouldSetCorrectEventType() {
            // Arrange
            when(meetingReader.getById(eventId)).thenReturn(meeting);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(attendanceRepository.findByPeopleIdAndEventId(peopleId, eventId))
                    .thenReturn(Optional.empty());
            when(attendanceRepository.save(any(AttendanceModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            attendanceService.updateAttendance(createDto, TopologyEventType.GROUP_MEETING);

            // Assert
            verify(attendanceRepository).save(attendanceCaptor.capture());
            assertEquals(TopologyEventType.GROUP_MEETING, attendanceCaptor.getValue().getEventType());
        }

        @Test
        @DisplayName("Debe buscar persona correctamente")
        void updateAttendance_ShouldLookupPersonCorrectly() {
            // Arrange
            when(meetingReader.getById(eventId)).thenReturn(meeting);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(attendanceRepository.findByPeopleIdAndEventId(peopleId, eventId))
                    .thenReturn(Optional.empty());
            when(attendanceRepository.save(any(AttendanceModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            attendanceService.updateAttendance(createDto, TopologyEventType.TEMPLE_WORHSIP);

            // Assert
            verify(peopleReader).getPeopleById(peopleId);
            verify(attendanceRepository).save(attendanceCaptor.capture());
            assertEquals(person, attendanceCaptor.getValue().getPeople());
        }

        @Test
        @DisplayName("Debe establecer status ABSENT cuando existe registro previo")
        void updateAttendance_WhenExisting_ShouldSetStatusAbsent() {
            // Arrange
            AttendanceModel existingModel = createAttendanceModel(attendanceId, AttendanceStatus.PRESENT);

            when(meetingReader.getById(eventId)).thenReturn(meeting);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(attendanceRepository.findByPeopleIdAndEventId(peopleId, eventId))
                    .thenReturn(Optional.of(existingModel));

            // Act
            AttendanceDto result = attendanceService.updateAttendance(
                    createDto,
                    TopologyEventType.TEMPLE_WORHSIP
            );

            // Assert
            assertEquals("No asistió", result.getStatus());
        }
    }

    @Nested
    @DisplayName("Get Attendance By Event Id Tests")
    class GetAttendanceByEventIdTests {

        @Test
        @DisplayName("Debe retornar página de asistencias por evento")
        void getAttendanceByEventId_ShouldReturnPageOfAttendances() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            AttendanceProjectionDto projection = new AttendanceProjectionDto(person, AttendanceStatus.PRESENT);
            Page<AttendanceProjectionDto> projectionPage = new PageImpl<>(List.of(projection));

            when(attendanceRepository.findAttendanceByEventIdAndChurchId(
                    eventId, TopologyEventType.TEMPLE_WORHSIP,contextId, attendanceQualityEnum, pageable ))
                    .thenReturn(projectionPage);

            // Act
            Page<AttendanceDto> result = attendanceService.getAttendanceByEventIdAndContextId(eventId, pageable, TopologyEventType.TEMPLE_WORHSIP, contextId, attendanceQualityEnum);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Debe retornar página vacía cuando no hay asistencias")
        void getAttendanceByEventId_WhenNoAttendances_ShouldReturnEmptyPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(attendanceRepository.findAttendanceByEventIdAndChurchId(
                    eventId, TopologyEventType.TEMPLE_WORHSIP, contextId,attendanceQualityEnum,pageable))
                    .thenReturn(Page.empty());

            // Act
            Page<AttendanceDto> result = attendanceService.getAttendanceByEventIdAndContextId(eventId, pageable, TopologyEventType.TEMPLE_WORHSIP, contextId, attendanceQualityEnum);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe usar tipo de evento TEMPLE_WORSHIP por defecto")
        void getAttendanceByEventId_ShouldUseTempleWorshipEventType() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(attendanceRepository.findAttendanceByEventIdAndChurchId(
                    eq(eventId), eq(TopologyEventType.TEMPLE_WORHSIP),eq(contextId), eq(attendanceQualityEnum), eq(pageable)))
                    .thenReturn(Page.empty());

            // Act
            attendanceService.getAttendanceByEventIdAndContextId(eventId, pageable, TopologyEventType.TEMPLE_WORHSIP, contextId,attendanceQualityEnum);

            // Assert
            verify(attendanceRepository).findAttendanceByEventIdAndChurchId(
                    eventId, TopologyEventType.TEMPLE_WORHSIP,contextId, attendanceQualityEnum,pageable);
        }

        @Test
        @DisplayName("Debe respetar la paginación solicitada")
        void getAttendanceByEventId_ShouldRespectPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(2, 5);
            when(attendanceRepository.findAttendanceByEventIdAndChurchId(
                    eventId, TopologyEventType.TEMPLE_WORHSIP,contextId,attendanceQualityEnum, pageable))
                    .thenReturn(Page.empty());

            // Act
            attendanceService.getAttendanceByEventIdAndContextId(eventId, pageable, TopologyEventType.TEMPLE_WORHSIP, contextId, attendanceQualityEnum);

            // Assert
            verify(attendanceRepository).findAttendanceByEventIdAndChurchId(
                    eventId, TopologyEventType.TEMPLE_WORHSIP,contextId, attendanceQualityEnum,pageable);
        }
    }

    @Nested
    @DisplayName("Count Total By Event Id Tests")
    class CountTotalByEventIdTests {

        @Test
        @DisplayName("Debe contar total de asistencias por evento")
        void countTotalByEventId_ShouldReturnCorrectCount() {
            // Arrange
            when(attendanceRepository.countTotalByEventId(eventId)).thenReturn(25L);

            // Act
            long result = attendanceService.countTotalByEventId(eventId, TopologyEventType.TEMPLE_WORHSIP);

            // Assert
            assertEquals(25L, result);
            verify(attendanceRepository).countTotalByEventId(eventId);
        }

        @Test
        @DisplayName("Debe retornar cero cuando no hay asistencias")
        void countTotalByEventId_WhenNoAttendances_ShouldReturnZero() {
            // Arrange
            when(attendanceRepository.countTotalByEventId(eventId)).thenReturn(0L);

            // Act
            long result = attendanceService.countTotalByEventId(eventId, TopologyEventType.TEMPLE_WORHSIP);

            // Assert
            assertEquals(0L, result);
        }
    }

    @Nested
    @DisplayName("Count By Event Id With Defaults Tests")
    class CountByEventIdWithDefaultsTests {

        @Test
        @DisplayName("Debe contar asistencias presentes correctamente")
        void countByEventIdWithDefaults_Present_ShouldReturnCorrectCount() {
            // Arrange
            when(attendanceRepository.countByEventIdWithDefaults(
                    eventId, TopologyEventType.TEMPLE_WORHSIP, AttendanceStatus.PRESENT))
                    .thenReturn(15L);

            // Act
            long result = attendanceService.countByEventIdWithDefaults(
                    eventId,
                    TopologyEventType.TEMPLE_WORHSIP,
                    AttendanceStatus.PRESENT
            );

            // Assert
            assertEquals(15L, result);
        }

        @Test
        @DisplayName("Debe contar asistencias ausentes correctamente")
        void countByEventIdWithDefaults_Absent_ShouldReturnCorrectCount() {
            // Arrange
            when(attendanceRepository.countByEventIdWithDefaults(
                    eventId, TopologyEventType.TEMPLE_WORHSIP, AttendanceStatus.ABSENT))
                    .thenReturn(10L);

            // Act
            long result = attendanceService.countByEventIdWithDefaults(
                    eventId,
                    TopologyEventType.TEMPLE_WORHSIP,
                    AttendanceStatus.ABSENT
            );

            // Assert
            assertEquals(10L, result);
        }

        @Test
        @DisplayName("Debe pasar parámetros correctos al repositorio")
        void countByEventIdWithDefaults_ShouldPassCorrectParameters() {
            // Arrange
            TopologyEventType eventType = TopologyEventType.GROUP_MEETING;
            AttendanceStatus status = AttendanceStatus.PRESENT;

            when(attendanceRepository.countByEventIdWithDefaults(eventId, eventType, status))
                    .thenReturn(5L);

            // Act
            attendanceService.countByEventIdWithDefaults(eventId, eventType, status);

            // Assert
            verify(attendanceRepository).countByEventIdWithDefaults(eventId, eventType, status);
        }
    }
}
