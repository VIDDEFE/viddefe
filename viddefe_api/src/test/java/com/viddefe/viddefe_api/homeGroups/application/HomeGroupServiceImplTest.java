package com.viddefe.viddefe_api.homeGroups.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.churches.contracts.ChurchLookup;
import com.viddefe.viddefe_api.churches.domain.model.ChurchModel;
import com.viddefe.viddefe_api.homeGroups.contracts.RolesStrategiesService;
import com.viddefe.viddefe_api.homeGroups.contracts.StrategyReader;
import com.viddefe.viddefe_api.homeGroups.domain.model.HomeGroupsModel;
import com.viddefe.viddefe_api.homeGroups.domain.model.StrategiesModel;
import com.viddefe.viddefe_api.homeGroups.domain.repository.HomeGroupsRepository;
import com.viddefe.viddefe_api.homeGroups.infrastructure.dto.*;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HomeGroupServiceImpl Tests")
class HomeGroupServiceImplTest {

    @Mock
    private HomeGroupsRepository homeGroupsRepository;

    @Mock
    private ChurchLookup churchLookup;

    @Mock
    private PeopleReader peopleReader;

    @Mock
    private StrategyReader strategyReader;

    @Mock
    private RolesStrategiesService rolesStrategiesService;

    @InjectMocks
    private HomeGroupServiceImpl homeGroupService;

    @Captor
    private ArgumentCaptor<HomeGroupsModel> homeGroupCaptor;

    private UUID churchId;
    private UUID homeGroupId;
    private UUID leaderId;
    private UUID strategyId;
    private ChurchModel church;
    private PeopleModel leader;
    private StrategiesModel strategy;
    private CreateHomeGroupsDto createDto;

    private PeopleModel createPeopleModel(UUID id, String firstName, String lastName) {
        PeopleModel people = new PeopleModel();
        people.setId(id);
        people.setFirstName(firstName);
        people.setLastName(lastName);
        people.setBirthDate(java.time.LocalDate.of(1985, 3, 20));

        StatesModel state = new StatesModel();
        state.setId(1L);
        state.setName("Antioquia");
        people.setState(state);

        return people;
    }

    @BeforeEach
    void setUp() throws Exception {
        churchId = UUID.randomUUID();
        homeGroupId = UUID.randomUUID();
        leaderId = UUID.randomUUID();
        strategyId = UUID.randomUUID();

        church = new ChurchModel();
        church.setId(churchId);
        church.setName("Iglesia Central");

        leader = createPeopleModel(leaderId, "Pedro", "Martínez");

        strategy = new StrategiesModel();
        strategy.setId(strategyId);
        strategy.setName("Estrategia G12");
        strategy.setChurch(church);

        createDto = createHomeGroupsDto("Grupo Centro", "Descripción del grupo",
                new BigDecimal("19.432608"), new BigDecimal("-99.133209"), strategyId, leaderId);
    }

    private CreateHomeGroupsDto createHomeGroupsDto(String name, String description,
            BigDecimal latitude, BigDecimal longitude, UUID strategyId, UUID leaderId) throws Exception {
        CreateHomeGroupsDto dto = new CreateHomeGroupsDto();
        setField(dto, "name", name);
        setField(dto, "description", description);
        setField(dto, "latitude", latitude);
        setField(dto, "longitude", longitude);
        setField(dto, "strategyId", strategyId);
        setField(dto, "leaderId", leaderId);
        return dto;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private HomeGroupsModel createHomeGroupsModel() {
        HomeGroupsModel model = new HomeGroupsModel();
        model.setId(homeGroupId);
        model.setName("Grupo Centro");
        model.setDescription("Descripción del grupo");
        model.setLatitude(new BigDecimal("19.432608"));
        model.setLongitude(new BigDecimal("-99.133209"));
        model.setStrategy(strategy);
        model.setLeader(leader);
        model.setChurch(church);
        return model;
    }

    @Nested
    @DisplayName("Create Home Group Tests")
    class CreateHomeGroupTests {

        @Test
        @DisplayName("Debe crear un grupo de casa correctamente")
        void createHomeGroup_WithValidData_ShouldReturnHomeGroupsDTO() {
            // Arrange
            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleReader.getPeopleById(leaderId)).thenReturn(leader);

            HomeGroupsModel savedModel = createHomeGroupsModel();
            when(homeGroupsRepository.save(any(HomeGroupsModel.class))).thenReturn(savedModel);

            // Act
            HomeGroupsDTO result = homeGroupService.createHomeGroup(createDto, churchId);

            // Assert
            assertNotNull(result);
            assertEquals("Grupo Centro", result.getName());
            verify(homeGroupsRepository).save(homeGroupCaptor.capture());

            HomeGroupsModel captured = homeGroupCaptor.getValue();
            assertEquals(church, captured.getChurch());
            assertEquals(leader, captured.getLeader());
            assertEquals(strategy, captured.getStrategy());
        }

        @Test
        @DisplayName("Debe asignar la estrategia correctamente")
        void createHomeGroup_ShouldAssignStrategyCorrectly() {
            // Arrange
            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleReader.getPeopleById(leaderId)).thenReturn(leader);
            when(homeGroupsRepository.save(any(HomeGroupsModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            homeGroupService.createHomeGroup(createDto, churchId);

            // Assert
            verify(strategyReader).findById(strategyId);
            verify(homeGroupsRepository).save(homeGroupCaptor.capture());
            assertEquals(strategy, homeGroupCaptor.getValue().getStrategy());
        }

        @Test
        @DisplayName("Debe asignar las coordenadas correctamente")
        void createHomeGroup_ShouldAssignCoordinatesCorrectly() {
            // Arrange
            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleReader.getPeopleById(leaderId)).thenReturn(leader);
            when(homeGroupsRepository.save(any(HomeGroupsModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            homeGroupService.createHomeGroup(createDto, churchId);

            // Assert
            verify(homeGroupsRepository).save(homeGroupCaptor.capture());
            HomeGroupsModel captured = homeGroupCaptor.getValue();
            assertEquals(new BigDecimal("19.432608"), captured.getLatitude());
            assertEquals(new BigDecimal("-99.133209"), captured.getLongitude());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la estrategia no existe")
        void createHomeGroup_WhenStrategyNotFound_ShouldThrowException() {
            // Arrange
            when(strategyReader.findById(strategyId))
                    .thenThrow(new EntityNotFoundException("Estrategia no encontrada"));

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> homeGroupService.createHomeGroup(createDto, churchId));
            verify(homeGroupsRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el líder no existe")
        void createHomeGroup_WhenLeaderNotFound_ShouldThrowException() {
            // Arrange
            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(churchLookup.getChurchById(churchId)).thenReturn(church);
            when(peopleReader.getPeopleById(leaderId))
                    .thenThrow(new EntityNotFoundException("Líder no encontrado"));

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> homeGroupService.createHomeGroup(createDto, churchId));
        }
    }

    @Nested
    @DisplayName("Update Home Group Tests")
    class UpdateHomeGroupTests {

        @Test
        @DisplayName("Debe actualizar un grupo de casa existente correctamente")
        void updateHomeGroup_WithValidData_ShouldReturnUpdatedDTO() throws Exception {
            // Arrange
            HomeGroupsModel existingModel = createHomeGroupsModel();
            CreateHomeGroupsDto updateDto = createHomeGroupsDto("Nuevo Nombre", "Nueva Descripción",
                    new BigDecimal("20.0"), new BigDecimal("-100.0"), strategyId, leaderId);

            when(homeGroupsRepository.findById(homeGroupId)).thenReturn(Optional.of(existingModel));
            when(strategyReader.findById(strategyId)).thenReturn(strategy);
            when(homeGroupsRepository.save(any(HomeGroupsModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            HomeGroupsDTO result = homeGroupService.updateHomeGroup(updateDto, homeGroupId);

            // Assert
            assertNotNull(result);
            verify(homeGroupsRepository).save(any(HomeGroupsModel.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el grupo no existe")
        void updateHomeGroup_WhenGroupNotFound_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(homeGroupsRepository.findById(homeGroupId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> homeGroupService.updateHomeGroup(createDto, homeGroupId)
            );
            assertEquals("Grupo no encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Debe actualizar la estrategia cuando cambia")
        void updateHomeGroup_ShouldUpdateStrategy() throws Exception {
            // Arrange
            HomeGroupsModel existingModel = createHomeGroupsModel();
            UUID newStrategyId = UUID.randomUUID();
            StrategiesModel newStrategy = new StrategiesModel();
            newStrategy.setId(newStrategyId);
            newStrategy.setName("Nueva Estrategia");

            CreateHomeGroupsDto updateDto = createHomeGroupsDto("Grupo", "Desc",
                    new BigDecimal("19.0"), new BigDecimal("-99.0"), newStrategyId, leaderId);

            when(homeGroupsRepository.findById(homeGroupId)).thenReturn(Optional.of(existingModel));
            when(strategyReader.findById(newStrategyId)).thenReturn(newStrategy);
            when(homeGroupsRepository.save(any(HomeGroupsModel.class)))
                    .thenAnswer(inv -> inv.getArgument(0));

            // Act
            homeGroupService.updateHomeGroup(updateDto, homeGroupId);

            // Assert
            verify(homeGroupsRepository).save(homeGroupCaptor.capture());
            assertEquals(newStrategy, homeGroupCaptor.getValue().getStrategy());
        }
    }

    @Nested
    @DisplayName("Get Home Group By Id Tests")
    class GetHomeGroupByIdTests {

        @Test
        @DisplayName("Debe retornar detalles del grupo cuando existe")
        void getHomeGroupById_WhenExists_ShouldReturnHomeGroupsDetailDto() {
            // Arrange
            HomeGroupsModel model = createHomeGroupsModel();
            when(homeGroupsRepository.findWithRelationsById(homeGroupId)).thenReturn(Optional.of(model));
            when(rolesStrategiesService.getTreeRolesWithPeople(strategyId))
                    .thenReturn(List.of());

            // Act
            HomeGroupsDetailDto result = homeGroupService.getHomeGroupById(homeGroupId);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getHomeGroup());
            assertNotNull(result.getStrategy());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el grupo no existe")
        void getHomeGroupById_WhenNotExists_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(homeGroupsRepository.findWithRelationsById(homeGroupId)).thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> homeGroupService.getHomeGroupById(homeGroupId)
            );
            assertEquals("Grupo no encontrado", exception.getMessage());
        }

        @Test
        @DisplayName("Debe incluir la jerarquía de roles")
        void getHomeGroupById_ShouldIncludeRolesHierarchy() {
            // Arrange
            HomeGroupsModel model = createHomeGroupsModel();
            List<RolesStrategiesWithPeopleDto> hierarchy = List.of(new RolesStrategiesWithPeopleDto());

            when(homeGroupsRepository.findWithRelationsById(homeGroupId)).thenReturn(Optional.of(model));
            when(rolesStrategiesService.getTreeRolesWithPeople(strategyId)).thenReturn(hierarchy);

            // Act
            HomeGroupsDetailDto result = homeGroupService.getHomeGroupById(homeGroupId);

            // Assert
            assertNotNull(result.getHierarchy());
            assertEquals(1, result.getHierarchy().size());
        }
    }

    @Nested
    @DisplayName("Get Home Groups Tests")
    class GetHomeGroupsTests {

        @Test
        @DisplayName("Debe retornar página de grupos por iglesia")
        void getHomeGroups_ShouldReturnPageOfGroups() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            HomeGroupsModel model = createHomeGroupsModel();
            Page<HomeGroupsModel> page = new PageImpl<>(List.of(model));

            when(homeGroupsRepository.findAllByChurchId(churchId, pageable)).thenReturn(page);

            // Act
            Page<HomeGroupsDTO> result = homeGroupService.getHomeGroups(pageable, churchId);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Debe retornar página vacía cuando no hay grupos")
        void getHomeGroups_WhenNoGroups_ShouldReturnEmptyPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(homeGroupsRepository.findAllByChurchId(churchId, pageable)).thenReturn(Page.empty());

            // Act
            Page<HomeGroupsDTO> result = homeGroupService.getHomeGroups(pageable, churchId);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Debe filtrar por ID de iglesia")
        void getHomeGroups_ShouldFilterByChurchId() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            when(homeGroupsRepository.findAllByChurchId(eq(churchId), eq(pageable)))
                    .thenReturn(Page.empty());

            // Act
            homeGroupService.getHomeGroups(pageable, churchId);

            // Assert
            verify(homeGroupsRepository).findAllByChurchId(churchId, pageable);
        }
    }

    @Nested
    @DisplayName("Delete Home Group Tests")
    class DeleteHomeGroupTests {

        @Test
        @DisplayName("Debe eliminar un grupo existente correctamente")
        void deleteHomeGroup_WhenExists_ShouldDeleteSuccessfully() {
            // Arrange
            when(homeGroupsRepository.existsById(homeGroupId)).thenReturn(true);
            doNothing().when(homeGroupsRepository).deleteById(homeGroupId);

            // Act
            homeGroupService.deleteHomeGroup(homeGroupId);

            // Assert
            verify(homeGroupsRepository).deleteById(homeGroupId);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el grupo no existe")
        void deleteHomeGroup_WhenNotExists_ShouldThrowEntityNotFoundException() {
            // Arrange
            when(homeGroupsRepository.existsById(homeGroupId)).thenReturn(false);

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> homeGroupService.deleteHomeGroup(homeGroupId));
            verify(homeGroupsRepository, never()).deleteById(any());
        }
    }
}

