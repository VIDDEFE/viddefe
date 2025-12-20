package com.viddefe.viddefe_api.people.application;

import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.people.contracts.ChurchMembershipService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ChurchMembershipServiceImpl.
 * 
 * Estos tests validan que:
 * 1. La arquitectura sin ciclos funciona correctamente
 * 2. Las operaciones de membresía se ejecutan como se espera
 * 3. Los errores se manejan apropiadamente
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChurchMembershipService Tests")
class ChurchMembershipServiceImplTest {

    @Mock
    private PeopleRepository peopleRepository;
    
    @Mock
    private PeopleTypeService peopleTypeService;
    
    @Mock
    private ChurchLookup churchLookup;
    
    @InjectMocks
    private ChurchMembershipServiceImpl churchMembershipService;
    
    private UUID personId;
    private UUID churchId;
    private PeopleModel person;
    private ChurchModel church;
    private PeopleTypeModel pastorType;
    
    @BeforeEach
    void setUp() {
        personId = UUID.randomUUID();
        churchId = UUID.randomUUID();
        
        person = new PeopleModel();
        person.setId(personId);
        person.setFirstName("John");
        person.setLastName("Doe");
        
        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Test Church");
        
        pastorType = new PeopleTypeModel();
        pastorType.setId(1L);
        pastorType.setName("PASTOR");
    }
    
    @Nested
    @DisplayName("assignPersonToChurchAsPastor")
    class AssignPersonToChurchAsPastor {
        
        @Test
        @DisplayName("Should assign person to church as pastor successfully")
        void shouldAssignPersonToChurchAsPastor() {
            // Given
            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleTypeService.getPeopleTypeByName("PASTOR")).thenReturn(pastorType);
            when(peopleRepository.save(any(PeopleModel.class))).thenAnswer(i -> i.getArgument(0));
            
            // When
            PeopleModel result = churchMembershipService.assignPersonToChurchAsPastor(personId, churchId);
            
            // Then
            assertThat(result.getChurch()).isEqualTo(church);
            assertThat(result.getTypePerson()).isEqualTo(pastorType);
            verify(peopleRepository).save(person);
        }
        
        @Test
        @DisplayName("Should throw exception when person not found")
        void shouldThrowWhenPersonNotFound() {
            // Given
            when(peopleRepository.findById(personId)).thenReturn(Optional.empty());
            
            // When/Then
            assertThatThrownBy(() -> 
                churchMembershipService.assignPersonToChurchAsPastor(personId, churchId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Person not found");
        }
    }
    
    @Nested
    @DisplayName("removeChurchAssignment")
    class RemoveChurchAssignment {
        
        @Test
        @DisplayName("Should remove church assignment from person")
        void shouldRemoveChurchAssignment() {
            // Given
            person.setChurch(church);
            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            when(peopleRepository.save(any(PeopleModel.class))).thenAnswer(i -> i.getArgument(0));
            
            // When
            PeopleModel result = churchMembershipService.removeChurchAssignment(personId);
            
            // Then
            assertThat(result.getChurch()).isNull();
            verify(peopleRepository).save(person);
        }
    }
    
    @Nested
    @DisplayName("transferToChurch")
    class TransferToChurch {
        
        @Test
        @DisplayName("Should transfer person to new church")
        void shouldTransferToNewChurch() {
            // Given
            ChurchModel oldChurch = new ChurchModel();
            oldChurch.setId(UUID.randomUUID());
            person.setChurch(oldChurch);
            
            when(peopleRepository.findById(personId)).thenReturn(Optional.of(person));
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleRepository.save(any(PeopleModel.class))).thenAnswer(i -> i.getArgument(0));
            
            // When
            PeopleModel result = churchMembershipService.transferToChurch(personId, churchId);
            
            // Then
            assertThat(result.getChurch()).isEqualTo(church);
            verify(peopleRepository).save(person);
        }
    }
}
