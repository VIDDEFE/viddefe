package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import com.viddefe.viddefe_api.people.infrastructure.dto.PeopleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PeopleWriterImpl Tests")
class PeopleWriterImplTest {

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private PeopleTypeService peopleTypeService;

    @Mock
    private StatesCitiesService statesCitiesService;

    @Mock
    private ChurchLookup churchLookup;

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PeopleWriterImpl peopleWriter;

    private UUID personId;
    private UUID churchId;
    private PeopleDTO peopleDTO;
    private PeopleModel person;
    private PeopleTypeModel peopleType;
    private StatesModel state;
    private ChurchModel church;

    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        churchId = UUID.randomUUID();

        state = new StatesModel();
        state.setId(1L);
        state.setName("California");

        peopleType = new PeopleTypeModel();
        peopleType.setId(1L);
        peopleType.setName("Member");

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Test Church");

        person = new PeopleModel();
        person.setId(personId);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setCc("123456789");
        person.setPhone("1234567890");
        person.setBirthDate(LocalDate.of(1990, 1, 1));
        person.setState(state);
        person.setTypePerson(peopleType);
        person.setChurch(church);

        peopleDTO = new PeopleDTO();
        peopleDTO.setFirstName("John");
        peopleDTO.setLastName("Doe");
        peopleDTO.setCc("123456789");
        peopleDTO.setPhone("1234567890");
        peopleDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        peopleDTO.setTypePersonId(1L);
        peopleDTO.setStateId(1L);
        peopleDTO.setChurchId(churchId);
    }

    @Nested
    @DisplayName("createPerson Tests")
    class CreatePersonTests {

        @Test
        @DisplayName("Should create person successfully")
        void shouldCreatePersonSuccessfully() {
            doNothing().when(peopleReader).verifyPersonExistsByCcAndChurchId(anyString(), any(UUID.class));
            when(peopleTypeService.getPeopleTypeById(1L)).thenReturn(peopleType);
            when(statesCitiesService.foundStatesById(1L)).thenReturn(state);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleRepository.save(any(PeopleModel.class))).thenReturn(person);

            PeopleModel result = peopleWriter.createPerson(peopleDTO);

            assertThat(result).isNotNull();
            verify(peopleRepository).save(any(PeopleModel.class));
            verify(applicationEventPublisher).publishEvent(any(UUID.class));
        }

        @Test
        @DisplayName("Should verify person does not exist before creating")
        void shouldVerifyPersonDoesNotExistBeforeCreating() {
            doNothing().when(peopleReader).verifyPersonExistsByCcAndChurchId(anyString(), any(UUID.class));
            when(peopleTypeService.getPeopleTypeById(1L)).thenReturn(peopleType);
            when(statesCitiesService.foundStatesById(1L)).thenReturn(state);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleRepository.save(any(PeopleModel.class))).thenReturn(person);

            peopleWriter.createPerson(peopleDTO);

            verify(peopleReader).verifyPersonExistsByCcAndChurchId(peopleDTO.getCc(), peopleDTO.getChurchId());
        }
    }

    @Nested
    @DisplayName("updatePerson Tests")
    class UpdatePersonTests {

        @Test
        @DisplayName("Should update person successfully")
        void shouldUpdatePersonSuccessfully() {
            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            when(peopleRepository.save(any(PeopleModel.class))).thenReturn(person);

            PeopleModel result = peopleWriter.updatePerson(peopleDTO, personId);

            assertThat(result).isNotNull();
            verify(peopleRepository).save(any(PeopleModel.class));
        }

        @Test
        @DisplayName("Should throw exception when person not found")
        void shouldThrowWhenPersonNotFound() {
            when(peopleRepository.findById(personId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> peopleWriter.updatePerson(peopleDTO, personId))
                    .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                    .hasMessageContaining("Person not found");
        }

        @Test
        @DisplayName("Should update type person if provided")
        void shouldUpdateTypePersonIfProvided() {
            peopleDTO.setTypePersonId(2L);
            PeopleTypeModel newType = new PeopleTypeModel();
            newType.setId(2L);
            newType.setName("Pastor");

            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            when(peopleTypeService.getPeopleTypeById(2L)).thenReturn(newType);
            when(peopleRepository.save(any(PeopleModel.class))).thenReturn(person);

            peopleWriter.updatePerson(peopleDTO, personId);

            verify(peopleTypeService).getPeopleTypeById(2L);
        }
    }

    @Nested
    @DisplayName("deletePerson Tests")
    class DeletePersonTests {

        @Test
        @DisplayName("Should delete person successfully")
        void shouldDeletePersonSuccessfully() {
            when(peopleRepository.existsById(personId)).thenReturn(true);
            doNothing().when(peopleRepository).deleteById(personId);

            peopleWriter.deletePerson(personId);

            verify(peopleRepository).deleteById(personId);
        }

        @Test
        @DisplayName("Should throw exception when person not found")
        void shouldThrowWhenDeletingNonExistentPerson() {
            when(peopleRepository.existsById(personId)).thenReturn(false);

            assertThatThrownBy(() -> peopleWriter.deletePerson(personId))
                    .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                    .hasMessageContaining("Person not found");
        }
    }
}

