package com.viddefe.viddefe_api.finances.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.finances.contracts.OfferingTypeService;
import com.viddefe.viddefe_api.finances.domain.model.OfferingType;
import com.viddefe.viddefe_api.finances.domain.model.Offerings;
import com.viddefe.viddefe_api.finances.domain.repositories.OfferingsRepository;
import com.viddefe.viddefe_api.finances.infrastructure.dto.CreateOfferingDto;
import com.viddefe.viddefe_api.finances.infrastructure.dto.OfferingDto;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferingServiceImpl Tests")
class OfferingServiceImplTest {

    @Mock
    private OfferingsRepository offeringsRepository;

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private OfferingTypeService offeringTypeService;

    @InjectMocks
    private OfferingServiceImpl offeringService;

    @Captor
    private ArgumentCaptor<Offerings> offeringsCaptor;

    private UUID eventId;
    private UUID peopleId;
    private UUID offeringId;
    private PeopleModel person;
    private OfferingType offeringType;
    private CreateOfferingDto createDto;

    @BeforeEach
    void setUp() throws Exception {
        eventId = UUID.randomUUID();
        peopleId = UUID.randomUUID();
        offeringId = UUID.randomUUID();

        person = createPeopleModel(peopleId, "Juan", "Pérez");

        offeringType = new OfferingType();
        offeringType.setId(1L);
        offeringType.setName("Diezmo");

        // Usar reflexión para crear CreateOfferingDto ya que solo tiene getters
        createDto = createOfferingDto(eventId, 100.0, peopleId, 1L);
    }

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

    private CreateOfferingDto createOfferingDto(UUID eventId, Double amount, UUID peopleId, Long offeringTypeId) throws Exception {
        CreateOfferingDto dto = new CreateOfferingDto();
        setField(dto, "eventId", eventId);
        setField(dto, "amount", amount);
        setField(dto, "peopleId", peopleId);
        setField(dto, "offeringTypeId", offeringTypeId);
        return dto;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Offerings createOfferings() {
        Offerings offerings = new Offerings();
        offerings.setId(offeringId);
        offerings.setEventId(eventId);
        offerings.setAmount(100.0);
        offerings.setPerson(person);
        offerings.setOfferingType(offeringType);
        return offerings;
    }

    @Nested
    @DisplayName("Register Offering Tests")
    class RegisterOfferingTests {

        @Test
        @DisplayName("Debe registrar una ofrenda correctamente")
        void register_WithValidData_ShouldReturnOfferingDto() {
            // Arrange
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(offeringTypeService.findById(1L)).thenReturn(offeringType);

            Offerings savedOffering = createOfferings();
            when(offeringsRepository.save(any(Offerings.class))).thenReturn(savedOffering);

            // Act
            OfferingDto result = offeringService.register(createDto);

            // Assert
            assertNotNull(result);
            assertEquals(eventId, result.getEventId());
            assertEquals(100.0, result.getAmount());

            verify(peopleReader).getPeopleById(peopleId);
            verify(offeringTypeService).findById(1L);
            verify(offeringsRepository).save(offeringsCaptor.capture());

            Offerings captured = offeringsCaptor.getValue();
            assertEquals(person, captured.getPerson());
            assertEquals(offeringType, captured.getOfferingType());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la persona no existe")
        void register_WhenPersonNotFound_ShouldThrowException() {
            // Arrange
            when(peopleReader.getPeopleById(peopleId))
                    .thenThrow(new EntityNotFoundException("Persona no encontrada"));

            // Act & Assert
            assertThrows(EntityNotFoundException.class, () -> offeringService.register(createDto));
            verify(offeringsRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el tipo de ofrenda no existe")
        void register_WhenOfferingTypeNotFound_ShouldThrowException() {
            // Arrange
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(offeringTypeService.findById(1L))
                    .thenThrow(new EntityNotFoundException("Tipo de ofrenda no encontrado"));

            // Act & Assert
            assertThrows(EntityNotFoundException.class, () -> offeringService.register(createDto));
            verify(offeringsRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe asignar correctamente los valores del DTO a la ofrenda")
        void register_ShouldMapDtoValuesCorrectly() {
            // Arrange
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(offeringTypeService.findById(1L)).thenReturn(offeringType);
            when(offeringsRepository.save(any(Offerings.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            offeringService.register(createDto);

            // Assert
            verify(offeringsRepository).save(offeringsCaptor.capture());
            Offerings captured = offeringsCaptor.getValue();
            assertEquals(eventId, captured.getEventId());
            assertEquals(100.0, captured.getAmount());
        }
    }

    @Nested
    @DisplayName("Update Offering Tests")
    class UpdateOfferingTests {

        @Test
        @DisplayName("Debe actualizar una ofrenda existente correctamente")
        void update_WithValidData_ShouldReturnUpdatedOfferingDto() throws Exception {
            // Arrange
            Offerings existingOffering = createOfferings();
            CreateOfferingDto updateDto = createOfferingDto(eventId, 200.0, peopleId, 1L);

            when(offeringsRepository.findById(offeringId)).thenReturn(Optional.of(existingOffering));
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(offeringTypeService.findById(1L)).thenReturn(offeringType);
            when(offeringsRepository.save(any(Offerings.class))).thenAnswer(inv -> {
                Offerings saved = inv.getArgument(0);
                saved.setAmount(200.0);
                return saved;
            });

            // Act
            OfferingDto result = offeringService.update(updateDto, offeringId);

            // Assert
            assertNotNull(result);
            verify(offeringsRepository).findById(offeringId);
            verify(offeringsRepository).save(any(Offerings.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la ofrenda no existe")
        void update_WhenOfferingNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(offeringsRepository.findById(offeringId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> offeringService.update(createDto, offeringId)
            );
            assertEquals("Ofrenda no encontrada", exception.getMessage());
            verify(offeringsRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe actualizar la persona asociada a la ofrenda")
        void update_ShouldUpdateAssociatedPerson() throws Exception {
            // Arrange
            UUID newPeopleId = UUID.randomUUID();
            PeopleModel newPerson = createPeopleModel(newPeopleId, "María", "García");

            Offerings existingOffering = createOfferings();
            CreateOfferingDto updateDto = createOfferingDto(eventId, 150.0, newPeopleId, 1L);

            when(offeringsRepository.findById(offeringId)).thenReturn(Optional.of(existingOffering));
            when(peopleReader.getPeopleById(newPeopleId)).thenReturn(newPerson);
            when(offeringTypeService.findById(1L)).thenReturn(offeringType);
            when(offeringsRepository.save(any(Offerings.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            offeringService.update(updateDto, offeringId);

            // Assert
            verify(offeringsRepository).save(offeringsCaptor.capture());
            assertEquals(newPerson, offeringsCaptor.getValue().getPerson());
        }

        @Test
        @DisplayName("Debe actualizar el tipo de ofrenda")
        void update_ShouldUpdateOfferingType() throws Exception {
            // Arrange
            OfferingType newType = new OfferingType();
            newType.setId(2L);
            newType.setName("Ofrenda Especial");

            Offerings existingOffering = createOfferings();
            CreateOfferingDto updateDto = createOfferingDto(eventId, 100.0, peopleId, 2L);

            when(offeringsRepository.findById(offeringId)).thenReturn(Optional.of(existingOffering));
            when(peopleReader.getPeopleById(peopleId)).thenReturn(person);
            when(offeringTypeService.findById(2L)).thenReturn(newType);
            when(offeringsRepository.save(any(Offerings.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            offeringService.update(updateDto, offeringId);

            // Assert
            verify(offeringsRepository).save(offeringsCaptor.capture());
            assertEquals(newType, offeringsCaptor.getValue().getOfferingType());
        }
    }

    @Nested
    @DisplayName("Get All By Event Id Tests")
    class GetAllByEventIdTests {

        @Test
        @DisplayName("Debe respetar la paginación solicitada")
        void getAllByEventId_ShouldRespectPagination() {
            // Arrange
            Pageable pageable = PageRequest.of(1, 5);
            when(offeringsRepository.findAllByEventId(eventId, pageable)).thenReturn(Page.empty());

            // Act
            offeringService.getAllByEventId(eventId, pageable);

            // Assert
            verify(offeringsRepository).findAllByEventId(eq(eventId), eq(pageable));
        }
    }

    @Nested
    @DisplayName("Delete Offering Tests")
    class DeleteOfferingTests {

        @Test
        @DisplayName("Debe eliminar una ofrenda correctamente")
        void delete_WithValidId_ShouldDeleteOffering() {
            // Arrange
            doNothing().when(offeringsRepository).deleteById(offeringId);

            // Act
            offeringService.delete(offeringId);

            // Assert
            verify(offeringsRepository).deleteById(offeringId);
        }

        @Test
        @DisplayName("Debe llamar al repositorio para eliminar")
        void delete_ShouldCallRepositoryDelete() {
            // Arrange
            UUID idToDelete = UUID.randomUUID();
            doNothing().when(offeringsRepository).deleteById(idToDelete);

            // Act
            offeringService.delete(idToDelete);

            // Assert
            verify(offeringsRepository, times(1)).deleteById(idToDelete);
        }
    }
}

