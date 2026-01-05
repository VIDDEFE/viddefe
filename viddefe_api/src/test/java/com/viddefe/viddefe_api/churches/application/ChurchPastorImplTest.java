package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchPastorRepository;
import com.viddefe.viddefe_api.people.contracts.ChurchMembershipService;
import com.viddefe.viddefe_api.people.contracts.PeopleReader;
import com.viddefe.viddefe_api.people.domain.model.PeopleModel;
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
 * Tests unitarios para ChurchPastorImpl.
 *
 * Valida que:
 * 1. La asignación de pastor a iglesia funciona correctamente
 * 2. La remoción de pastor funciona correctamente
 * 3. El cambio de pastor funciona correctamente
 * 4. La arquitectura sin ciclos se respeta (usa interfaces segregadas)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChurchPastorImpl Tests")
class ChurchPastorImplTest {

    @Mock
    private ChurchPastorRepository churchPastorRepository;

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private ChurchMembershipService churchMembershipService;

    @InjectMocks
    private ChurchPastorImpl churchPastorService;

    private UUID pastorId;
    private UUID newPastorId;
    private UUID churchId;
    private PeopleModel pastor;
    private PeopleModel newPastor;
    private ChurchModel church;
    private ChurchPastor churchPastor;

    @BeforeEach
    void setUp() {
        pastorId = UUID.randomUUID();
        newPastorId = UUID.randomUUID();
        churchId = UUID.randomUUID();

        pastor = new PeopleModel();
        pastor.setId(pastorId);
        pastor.setFirstName("Pastor");
        pastor.setLastName("Original");

        newPastor = new PeopleModel();
        newPastor.setId(newPastorId);
        newPastor.setFirstName("Pastor");
        newPastor.setLastName("New");

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Test Church");

        churchPastor = new ChurchPastor();
        churchPastor.setId(UUID.randomUUID());
        churchPastor.setPastor(pastor);
        churchPastor.setChurch(church);
    }

    @Nested
    @DisplayName("addPastorToChurch")
    class AddPastorToChurch {

        @Test
        @DisplayName("Should add pastor to church successfully")
        void shouldAddPastorToChurch() {
            // Given
            when(peopleReader.getPeopleById(pastorId)).thenReturn(pastor);
            when(churchPastorRepository.save(any(ChurchPastor.class))).thenAnswer(invocation -> {
                ChurchPastor saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });
            when(churchMembershipService.assignPersonToChurchAsPastor(pastorId, churchId))
                .thenReturn(pastor);

            // When
            ChurchPastor result = churchPastorService.addPastorToChurch(pastorId, church);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getPastor()).isEqualTo(pastor);
            assertThat(result.getChurch()).isEqualTo(church);
            verify(churchPastorRepository).save(any(ChurchPastor.class));
            verify(churchMembershipService).assignPersonToChurchAsPastor(pastorId, churchId);
        }

        @Test
        @DisplayName("Should throw exception when pastor not found")
        void shouldThrowWhenPastorNotFound() {
            // Given
            when(peopleReader.getPeopleById(pastorId))
                .thenThrow(new EntityNotFoundException("Person not found: " + pastorId));

            // When/Then
            assertThatThrownBy(() -> churchPastorService.addPastorToChurch(pastorId, church))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Person not found");
        }
    }

    @Nested
    @DisplayName("removePastorFromChurch")
    class RemovePastorFromChurch {

        @Test
        @DisplayName("Should remove pastor from church successfully")
        void shouldRemovePastorFromChurch() {
            // Given
            when(churchPastorRepository.findByChurch(church)).thenReturn(Optional.of(churchPastor));
            when(churchMembershipService.removeChurchAssignment(pastorId)).thenReturn(pastor);

            // When
            churchPastorService.removePastorFromChurch(church);

            // Then
            verify(churchMembershipService).removeChurchAssignment(pastorId);
            verify(churchPastorRepository).delete(churchPastor);
        }

        @Test
        @DisplayName("Should throw exception when church has no pastor")
        void shouldThrowWhenChurchHasNoPastor() {
            // Given
            when(churchPastorRepository.findByChurch(church)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> churchPastorService.removePastorFromChurch(church))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Church has no assigned pastor");
        }
    }

    @Nested
    @DisplayName("getPastorFromChurch")
    class GetPastorFromChurch {

        @Test
        @DisplayName("Should return pastor from church")
        void shouldReturnPastorFromChurch() {
            // Given
            when(churchPastorRepository.findByChurchWithPastorRelations(church)).thenReturn(Optional.of(churchPastor));

            // When
            PeopleModel result = churchPastorService.getPastorFromChurch(church);

            // Then
            assertThat(result).isEqualTo(pastor);
            assertThat(result.getFirstName()).isEqualTo("Pastor");
        }

        @Test
        @DisplayName("Should throw exception when church has no pastor")
        void shouldThrowWhenChurchHasNoPastor() {
            // Given
            when(churchPastorRepository.findByChurchWithPastorRelations(church)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> churchPastorService.getPastorFromChurch(church))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Church has no assigned pastor");
        }
    }

    @Nested
    @DisplayName("changeChurchPastor")
    class ChangeChurchPastor {

        @Test
        @DisplayName("Should change pastor successfully")
        void shouldChangePastorSuccessfully() {
            // Given
            when(peopleReader.getPeopleById(newPastorId)).thenReturn(newPastor);
            when(churchPastorRepository.findByChurch(church)).thenReturn(Optional.of(churchPastor));
            when(churchMembershipService.removeChurchAssignment(pastorId)).thenReturn(pastor);
            when(churchMembershipService.assignPersonToChurchAsPastor(newPastorId, churchId))
                .thenReturn(newPastor);
            when(churchPastorRepository.save(any(ChurchPastor.class))).thenAnswer(i -> i.getArgument(0));

            // When
            ChurchPastor result = churchPastorService.changeChurchPastor(newPastorId, church);

            // Then
            assertThat(result.getPastor()).isEqualTo(newPastor);
            verify(churchMembershipService).removeChurchAssignment(pastorId);
            verify(churchMembershipService).assignPersonToChurchAsPastor(newPastorId, churchId);
        }

        @Test
        @DisplayName("Should throw exception when new pastor not found")
        void shouldThrowWhenNewPastorNotFound() {
            // Given
            when(peopleReader.getPeopleById(newPastorId))
                .thenThrow(new EntityNotFoundException("Person not found: " + newPastorId));

            // When/Then
            assertThatThrownBy(() -> churchPastorService.changeChurchPastor(newPastorId, church))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Person not found");
        }
    }
}

