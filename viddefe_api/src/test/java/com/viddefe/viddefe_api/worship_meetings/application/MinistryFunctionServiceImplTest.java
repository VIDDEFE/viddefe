package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.notifications.contracts.NotificationEventPublisher;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.MeetingReader;
import com.viddefe.viddefe_api.worship_meetings.contracts.MinistryFunctionTypeReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.Meeting;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunction;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MinistryFunctionTypes;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MinistryFunctionRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.CreateMinistryFunctionDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MinistryFunctionDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MinistryFunctionServiceImpl Tests")
class MinistryFunctionServiceImplTest {

    @Mock
    private MinistryFunctionRepository ministryFunctionRepository;

    @Mock
    private MinistryFunctionTypeReader ministryFunctionTypeReader;

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private NotificationEventPublisher notificatorPublisher;

    @Mock
    private MeetingReader meetingReader;

    @InjectMocks
    private MinistryFunctionServiceImpl ministryFunctionService;

    private UUID eventId;
    private UUID peopleId;
    private UUID ministryFunctionId;
    private PeopleModel person;
    private Meeting meeting;
    private MinistryFunctionTypes functionType;
    private CreateMinistryFunctionDto createDto;
    private MinistryFunction ministryFunction;
    private PeopleTypeModel peopleType;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        peopleId = UUID.randomUUID();
        ministryFunctionId = UUID.randomUUID();

        peopleType = new PeopleTypeModel();
        peopleType.setId(1L);
        peopleType.setName("Member");

        person = new PeopleModel();
        person.setId(peopleId);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setPhone("1234567890");
        person.setTypePerson(peopleType);

        meeting = new Meeting();
        meeting.setId(eventId);
        meeting.setName("Sunday Service");
        meeting.setScheduledDate(OffsetDateTime.now().plusDays(1));

        functionType = new MinistryFunctionTypes();
        functionType.setId(1L);
        functionType.setName("Worship Leader");


        ministryFunction = new MinistryFunction();
        ministryFunction.setId(ministryFunctionId);
        ministryFunction.setMeeting(meeting);
        ministryFunction.setPeople(person);
        ministryFunction.setMinistryFunctionType(functionType);
        ministryFunction.setEventType(TopologyEventType.TEMPLE_WORHSIP);
    }

    @Nested
    @DisplayName("create Tests")
    class CreateTests {

        @BeforeEach
        void setUpCreateDto() {
            createDto = mock(CreateMinistryFunctionDto.class);
            lenient().when(createDto.getPeopleId()).thenReturn(peopleId);
            lenient().when(createDto.getRoleId()).thenReturn(1L);
        }

        @Test
        @DisplayName("Should throw exception when person has no phone")
        void shouldThrowWhenPersonHasNoPhone() {
            person.setPhone(null);
            when(ministryFunctionTypeReader.findById(1L)).thenReturn(functionType);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);

            assertThatThrownBy(() -> ministryFunctionService.create(
                    createDto, eventId, TopologyEventType.TEMPLE_WORHSIP))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("número de teléfono");
        }

        @Test
        @DisplayName("Should throw exception when person has blank phone")
        void shouldThrowWhenPersonHasBlankPhone() {
            person.setPhone("   ");
            when(ministryFunctionTypeReader.findById(1L)).thenReturn(functionType);
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);

            assertThatThrownBy(() -> ministryFunctionService.create(
                    createDto, eventId, TopologyEventType.TEMPLE_WORHSIP))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("número de teléfono");
        }
    }

    @Nested
    @DisplayName("findByEventId Tests")
    class FindByEventIdTests {

        @Test
        @DisplayName("Should return list of ministry functions")
        void shouldReturnListOfMinistryFunctions() {
            when(ministryFunctionRepository.findByMeetingId(eventId))
                    .thenReturn(List.of(ministryFunction));

            List<MinistryFunctionDto> result = ministryFunctionService.findByEventId(eventId, TopologyEventType.TEMPLE_WORHSIP);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should return empty list when no functions found")
        void shouldReturnEmptyListWhenNoFunctionsFound() {
            when(ministryFunctionRepository.findByMeetingId(eventId))
                    .thenReturn(List.of());

            List<MinistryFunctionDto> result = ministryFunctionService.findByEventId(eventId, TopologyEventType.TEMPLE_WORHSIP);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should delete ministry function successfully")
        void shouldDeleteMinistryFunctionSuccessfully() {
            when(ministryFunctionRepository.findById(ministryFunctionId)).thenReturn(Optional.of(ministryFunction));
            doNothing().when(ministryFunctionRepository).deleteById(ministryFunctionId);

            ministryFunctionService.delete(ministryFunctionId);

            verify(ministryFunctionRepository).deleteById(ministryFunctionId);
        }

        @Test
        @DisplayName("Should throw exception when function not found")
        void shouldThrowWhenFunctionNotFound() {
            UUID notFoundId = UUID.randomUUID();
            when(ministryFunctionRepository.findById(notFoundId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ministryFunctionService.delete(notFoundId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}

