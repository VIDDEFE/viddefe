package com.viddefe.viddefe_api.churches.application;

import com.viddefe.viddefe_api.StatesCities.application.StatesCitiesService;
import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchPastorService;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.churches.domain.model.ChurchPastor;
import com.viddefe.viddefe_api.churches.domain.repository.ChurchRepository;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDTO;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchDetailedResDto;
import com.viddefe.viddefe_api.churches.infrastructure.dto.ChurchResDto;
import com.viddefe.viddefe_api.people.contracts.ChurchMembershipService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChurchServiceImpl Tests")
class ChurchServiceImplTest {

    @Mock
    private ChurchRepository churchRepository;

    @Mock
    private StatesCitiesService statesCitiesService;

    @Mock
    private ChurchPastorService churchPastorService;

    @Mock
    private ChurchMembershipService churchMembershipService;

    @InjectMocks
    private ChurchServiceImpl churchService;

    private UUID churchId;
    private UUID pastorId;
    private ChurchModel church;
    private PeopleModel pastor;
    private CitiesModel city;
    private StatesModel state;
    private ChurchDTO churchDTO;
    private ChurchPastor churchPastor;

    @BeforeEach
    void setUp() {
        churchId = UUID.randomUUID();
        pastorId = UUID.randomUUID();

        state = new StatesModel();
        state.setId(1L);
        state.setName("California");

        city = new CitiesModel();
        city.setId(1L);
        city.setName("Los Angeles");
        city.setStates(state);

        pastor = new PeopleModel();
        pastor.setId(pastorId);
        pastor.setFirstName("Pastor");
        pastor.setLastName("Test");
        pastor.setCc("123456789");
        pastor.setPhone("1234567890");
        pastor.setState(state);

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Test Church");
        church.setCity(city);
        church.setLatitude(BigDecimal.valueOf(34.0522));
        church.setLongitude(BigDecimal.valueOf(-118.2437));
        church.setFoundationDate(new Date());

        churchPastor = new ChurchPastor();
        churchPastor.setChurch(church);
        churchPastor.setPastor(pastor);

        churchDTO = new ChurchDTO();
        churchDTO.setName("Test Church");
        churchDTO.setCityId(1L);
        churchDTO.setPastorId(pastorId);
        churchDTO.setLatitude(BigDecimal.valueOf(34.0522));
        churchDTO.setLongitude(BigDecimal.valueOf(-118.2437));
    }

    @Nested
    @DisplayName("addChurch Tests")
    class AddChurchTests {

        @Test
        @DisplayName("Should create a new root church successfully")
        void shouldCreateRootChurchSuccessfully() {
            when(statesCitiesService.foundCitiesById(1L)).thenReturn(city);
            when(churchRepository.save(any(ChurchModel.class))).thenReturn(church);
            when(churchPastorService.addPastorToChurch(pastorId, church)).thenReturn(churchPastor);

            ChurchResDto result = churchService.addChurch(churchDTO);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Test Church");
            verify(churchRepository).save(any(ChurchModel.class));
            verify(churchPastorService).addPastorToChurch(pastorId, church);
        }

        @Test
        @DisplayName("Should assign city to new church")
        void shouldAssignCityToNewChurch() {
            when(statesCitiesService.foundCitiesById(1L)).thenReturn(city);
            when(churchRepository.save(any(ChurchModel.class))).thenReturn(church);
            when(churchPastorService.addPastorToChurch(eq(pastorId), any())).thenReturn(churchPastor);

            churchService.addChurch(churchDTO);

            verify(statesCitiesService).foundCitiesById(1L);
        }
    }

    @Nested
    @DisplayName("addChildChurch Tests")
    class AddChildChurchTests {

        @Test
        @DisplayName("Should create child church with parent reference")
        void shouldCreateChildChurchWithParentReference() {
            UUID parentChurchId = UUID.randomUUID();
            ChurchModel parentChurch = new ChurchModel();
            parentChurch.setId(parentChurchId);
            parentChurch.setName("Parent Church");

            when(churchRepository.getReferenceById(parentChurchId)).thenReturn(parentChurch);
            when(statesCitiesService.foundCitiesById(1L)).thenReturn(city);
            when(churchRepository.save(any(ChurchModel.class))).thenReturn(church);
            when(churchPastorService.addPastorToChurch(eq(pastorId), any())).thenReturn(churchPastor);

            ChurchResDto result = churchService.addChildChurch(parentChurchId, churchDTO, pastorId);

            assertThat(result).isNotNull();
            verify(churchRepository).getReferenceById(parentChurchId);
        }

        @Test
        @DisplayName("Should use creator pastor if no pastor specified in DTO")
        void shouldUseCreatorPastorIfNotSpecified() {
            UUID parentChurchId = UUID.randomUUID();
            UUID creatorPastorId = UUID.randomUUID();
            ChurchModel parentChurch = new ChurchModel();
            parentChurch.setId(parentChurchId);

            churchDTO.setPastorId(null);

            when(churchRepository.getReferenceById(parentChurchId)).thenReturn(parentChurch);
            when(statesCitiesService.foundCitiesById(1L)).thenReturn(city);
            when(churchRepository.save(any(ChurchModel.class))).thenReturn(church);
            when(churchPastorService.addPastorToChurch(eq(creatorPastorId), any())).thenReturn(churchPastor);

            churchService.addChildChurch(parentChurchId, churchDTO, creatorPastorId);

            verify(churchPastorService).addPastorToChurch(eq(creatorPastorId), any());
        }
    }

    @Nested
    @DisplayName("updateChurch Tests")
    class UpdateChurchTests {

        @Test
        @DisplayName("Should update existing church")
        void shouldUpdateExistingChurch() {
            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));
            when(statesCitiesService.foundCitiesById(1L)).thenReturn(city);
            when(churchRepository.save(any(ChurchModel.class))).thenReturn(church);
            when(churchPastorService.changeChurchPastor(eq(pastorId), any())).thenReturn(churchPastor);

            ChurchResDto result = churchService.updateChurch(churchId, churchDTO, pastorId);

            assertThat(result).isNotNull();
            verify(churchRepository).findById(churchId);
            verify(churchRepository).save(any(ChurchModel.class));
        }

        @Test
        @DisplayName("Should throw exception when church not found")
        void shouldThrowWhenChurchNotFound() {
            when(churchRepository.findById(churchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> churchService.updateChurch(churchId, churchDTO, pastorId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Iglesia no encontrada");
        }
    }

    @Nested
    @DisplayName("deleteChurch Tests")
    class DeleteChurchTests {

        @Test
        @DisplayName("Should delete church and transfer pastor to parent")
        void shouldDeleteChurchAndTransferPastor() {
            ChurchModel parentChurch = new ChurchModel();
            parentChurch.setId(UUID.randomUUID());
            church.setParentChurch(parentChurch);

            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(pastor);

            churchService.deleteChurch(churchId);

            verify(churchPastorService).removePastorFromChurch(church);
            verify(churchRepository).delete(church);
            verify(churchMembershipService).transferToChurch(pastor, parentChurch);
        }

        @Test
        @DisplayName("Should delete root church without transfer")
        void shouldDeleteRootChurchWithoutTransfer() {
            church.setParentChurch(null);

            when(churchRepository.findById(churchId)).thenReturn(Optional.of(church));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(pastor);

            churchService.deleteChurch(churchId);

            verify(churchRepository).delete(church);
            verify(churchMembershipService, never()).transferToChurch(any(PeopleModel.class), any(ChurchModel.class));
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent church")
        void shouldThrowWhenDeletingNonExistentChurch() {
            when(churchRepository.findById(churchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> churchService.deleteChurch(churchId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getChurches Tests")
    class GetChurchesTests {

        @Test
        @DisplayName("Should return paginated list of churches")
        void shouldReturnPaginatedChurches() {
            Pageable pageable = PageRequest.of(0, 10);
            ChurchResDto dto = mock(ChurchResDto.class);
            Page<ChurchResDto> expectedPage = new PageImpl<>(List.of(dto));

            when(churchRepository.findAllChurchesDtoByParentChurchId(null, pageable)).thenReturn(expectedPage);

            Page<ChurchResDto> result = churchService.getChurches(pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(churchRepository).findAllChurchesDtoByParentChurchId(null, pageable);
        }

        @Test
        @DisplayName("Should return children churches")
        void shouldReturnChildrenChurches() {
            UUID parentId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            Page<ChurchResDto> expectedPage = new PageImpl<>(List.of());

            when(churchRepository.findAllChurchesDtoByParentChurchId(parentId, pageable)).thenReturn(expectedPage);

            Page<ChurchResDto> result = churchService.getChildrenChurches(pageable, parentId);

            assertThat(result).isNotNull();
            verify(churchRepository).findAllChurchesDtoByParentChurchId(parentId, pageable);
        }
    }

    @Nested
    @DisplayName("getChurchById Tests")
    class GetChurchByIdTests {

        @Test
        @DisplayName("Should return detailed church info")
        void shouldReturnDetailedChurchInfo() {
            when(churchRepository.findByIdWithCityAndState(churchId)).thenReturn(Optional.of(church));
            when(churchPastorService.getPastorFromChurch(church)).thenReturn(pastor);

            ChurchDetailedResDto result = churchService.getChurchById(churchId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(churchId);
            assertThat(result.getName()).isEqualTo("Test Church");
        }

        @Test
        @DisplayName("Should throw exception when church not found")
        void shouldThrowWhenChurchNotFoundById() {
            when(churchRepository.findByIdWithCityAndState(churchId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> churchService.getChurchById(churchId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Iglesia no encontrada");
        }
    }

    @Nested
    @DisplayName("getChildrenChurchesByPositionInMap Tests")
    class GetChurchesByMapPositionTests {

        @Test
        @DisplayName("Should return churches within bounding box")
        void shouldReturnChurchesInBoundingBox() {
            BigDecimal southLat = BigDecimal.valueOf(34.0);
            BigDecimal northLat = BigDecimal.valueOf(34.5);
            BigDecimal westLng = BigDecimal.valueOf(-118.5);
            BigDecimal eastLng = BigDecimal.valueOf(-118.0);

            when(churchRepository.findChildrenInBoundingBox(churchId, southLat, northLat, westLng, eastLng))
                    .thenReturn(List.of(church));

            List<ChurchResDto> result = churchService.getChildrenChurchesByPositionInMap(
                    churchId, southLat, westLng, northLat, eastLng);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should throw exception when zoom is too large")
        void shouldThrowWhenZoomTooLarge() {
            BigDecimal southLat = BigDecimal.valueOf(34.0);
            BigDecimal northLat = BigDecimal.valueOf(35.0); // diff > 0.6
            BigDecimal westLng = BigDecimal.valueOf(-118.5);
            BigDecimal eastLng = BigDecimal.valueOf(-118.0);

            assertThatThrownBy(() -> churchService.getChildrenChurchesByPositionInMap(
                    churchId, southLat, westLng, northLat, eastLng))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Zoom too large");
        }
    }
}

