package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.people.config.TypesPeople;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
import com.viddefe.viddefe_api.people.domain.model.PeopleTypeModel;
import com.viddefe.viddefe_api.people.domain.repository.PeopleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para PeopleReaderImpl.
 * 
 * Valida que las operaciones de solo lectura funcionan correctamente
 * y que la implementación no tiene dependencias circulares.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PeopleReader Tests")
class PeopleReaderImplTest {

    @Mock
    private PeopleRepository peopleRepository;
    
    @Mock
    private PeopleTypeService peopleTypeService;
    
    @InjectMocks
    private PeopleReaderImpl peopleReader;
    
    private UUID personId;
    private PeopleModel person;
    private PeopleTypeModel pastorType;
    
    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        
        person = new PeopleModel();
        person.setId(personId);
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setCc("123456789");
        
        pastorType = new PeopleTypeModel();
        pastorType.setId(1L);
        pastorType.setName(TypesPeople.PASTOR.getLabel());
    }
    
    @Nested
    @DisplayName("getPeopleById")
    class GetPeopleById {
        
        @Test
        @DisplayName("Should return person when found")
        void shouldReturnPersonWhenFound() {
            // Given
            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            
            // When
            PeopleModel result = peopleReader.getPeopleById(personId);
            
            // Then
            assertThat(result).isEqualTo(person);
        }
        
        @Test
        @DisplayName("Should throw exception when person not found")
        void shouldThrowWhenPersonNotFound() {
            // Given
            when(peopleRepository.findById(personId)).thenReturn(Optional.empty());
            
            // When/Then
            assertThatThrownBy(() -> peopleReader.getPeopleById(personId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Person not found");
        }
    }
    
    @Nested
    @DisplayName("findPeopleById")
    class FindPeopleById {
        
        @Test
        @DisplayName("Should return Optional with person when found")
        void shouldReturnOptionalWithPerson() {
            // Given
            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            
            // When
            Optional<PeopleModel> result = peopleReader.findPeopleById(personId);
            
            // Then
            assertThat(result).isPresent().contains(person);
        }
        
        @Test
        @DisplayName("Should return empty Optional when not found")
        void shouldReturnEmptyOptional() {
            // Given
            when(peopleRepository.findById(personId)).thenReturn(Optional.empty());
            
            // When
            Optional<PeopleModel> result = peopleReader.findPeopleById(personId);
            
            // Then
            assertThat(result).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("getPastorByCcWithoutChurch")
    class GetPastorByCcWithoutChurch {
        
        @Test
        @DisplayName("Should return pastor when found")
        void shouldReturnPastorWhenFound() {
            // Given
            String cc = "123456789";
            person.setTypePerson(pastorType);
            when(peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.getLabel())).thenReturn(pastorType);
            when(peopleRepository.findByCcAndTypePersonAndChurchIsNull(cc, pastorType))
                .thenReturn(Optional.of(person));
            
            // When
            Optional<PeopleModel> result = peopleReader.getPastorByCcWithoutChurch(cc);
            
            // Then
            assertThat(result.isPresent()).isEqualTo(person != null);
            assertThat(result.isPresent()).isTrue();
            assertThat(result.get()).isEqualTo(person);
        }
        
    }
    
    @Nested
    @DisplayName("existsPastorByCcWithoutChurch")
    class ExistsPastorByCcWithoutChurch {
        
        @Test
        @DisplayName("Should return true when pastor exists")
        void shouldReturnTrueWhenExists() {
            // Given
            String cc = "123456789";
            when(peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.getLabel())).thenReturn(pastorType);
            when(peopleRepository.findByCcAndTypePersonAndChurchIsNull(cc, pastorType))
                .thenReturn(Optional.of(person));
            
            // When
            boolean result = peopleReader.existsPastorByCcWithoutChurch(cc);
            
            // Then
            assertThat(result).isTrue();
        }
        
        @Test
        @DisplayName("Should return false when pastor does not exist")
        void shouldReturnFalseWhenNotExists() {
            // Given
            String cc = "123456789";
            when(peopleTypeService.getPeopleTypeByName(TypesPeople.PASTOR.getLabel())).thenReturn(pastorType);
            when(peopleRepository.findByCcAndTypePersonAndChurchIsNull(cc, pastorType))
                .thenReturn(Optional.empty());
            
            // When
            boolean result = peopleReader.existsPastorByCcWithoutChurch(cc);
            
            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getPeopleByIds")
    class GetPeopleByIds {

        @Test
        @DisplayName("Should return list of people when found")
        void shouldReturnListOfPeopleWhenFound() {
            // Given
            PeopleModel person2 = new PeopleModel();
            person2.setId(UUID.randomUUID());
            person2.setFirstName("Jane");
            person2.setLastName("Doe");

            java.util.List<UUID> ids = java.util.List.of(personId, person2.getId());
            when(peopleRepository.findAllById(ids)).thenReturn(java.util.List.of(person, person2));

            // When
            java.util.List<PeopleModel> result = peopleReader.getPeopleByIds(ids);

            // Then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty list when no people found")
        void shouldReturnEmptyListWhenNoneFound() {
            // Given
            java.util.List<UUID> ids = java.util.List.of(UUID.randomUUID());
            when(peopleRepository.findAllById(ids)).thenReturn(java.util.List.of());

            // When
            java.util.List<PeopleModel> result = peopleReader.getPeopleByIds(ids);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("verifyPersonExistsByCcAndChurchId")
    class VerifyPersonExistsByCcAndChurchId {

        @Test
        @DisplayName("Should not throw when person does not exist")
        void shouldNotThrowWhenPersonDoesNotExist() {
            // Given
            String cc = "123456789";
            UUID churchId = UUID.randomUUID();
            when(peopleRepository.findByCcAndChurchId(cc, churchId)).thenReturn(Optional.empty());

            // When/Then
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                    peopleReader.verifyPersonExistsByCcAndChurchId(cc, churchId));
        }

        @Test
        @DisplayName("Should throw when person exists")
        void shouldThrowWhenPersonExists() {
            // Given
            String cc = "123456789";
            UUID churchId = UUID.randomUUID();
            when(peopleRepository.findByCcAndChurchId(cc, churchId)).thenReturn(Optional.of(person));

            // When/Then
            assertThatThrownBy(() -> peopleReader.verifyPersonExistsByCcAndChurchId(cc, churchId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ya existe en la iglesia");
        }
    }
}
