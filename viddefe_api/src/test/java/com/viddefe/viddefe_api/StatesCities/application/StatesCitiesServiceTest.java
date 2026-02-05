package com.viddefe.viddefe_api.StatesCities.application;

import com.viddefe.viddefe_api.StatesCities.domain.model.CitiesModel;
import com.viddefe.viddefe_api.StatesCities.domain.model.StatesModel;
import com.viddefe.viddefe_api.StatesCities.domain.repository.CitiesRepository;
import com.viddefe.viddefe_api.StatesCities.domain.repository.StatesRepository;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.CitiesDto;
import com.viddefe.viddefe_api.StatesCities.infrastructure.dto.StatesDto;
import com.viddefe.viddefe_api.common.exception.CustomExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StatesCitiesService Tests")
class StatesCitiesServiceTest {

    @Mock
    private CitiesRepository citiesRepository;

    @Mock
    private StatesRepository statesRepository;

    @InjectMocks
    private StatesCitiesService statesCitiesService;

    private StatesModel state;
    private CitiesModel city;

    @BeforeEach
    void setUp() {
        state = new StatesModel();
        state.setId(1L);
        state.setName("California");

        city = new CitiesModel();
        city.setId(1L);
        city.setName("Los Angeles");
        city.setStates(state);

        // Add city directly to state's cities list
        state.getCities().add(city);
    }

    @Nested
    @DisplayName("getAllStates Tests")
    class GetAllStatesTests {

        @Test
        @DisplayName("Should return all states")
        void shouldReturnAllStates() {
            StatesModel state2 = new StatesModel();
            state2.setId(2L);
            state2.setName("Texas");

            when(statesRepository.findAll()).thenReturn(List.of(state, state2));

            List<StatesDto> result = statesCitiesService.getAllStates();

            assertThat(result).hasSize(2);
            verify(statesRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no states")
        void shouldReturnEmptyListWhenNoStates() {
            when(statesRepository.findAll()).thenReturn(List.of());

            List<StatesDto> result = statesCitiesService.getAllStates();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllCitiesByState Tests")
    class GetAllCitiesByStateTests {

        @Test
        @DisplayName("Should return cities for state")
        void shouldReturnCitiesForState() {
            when(statesRepository.findById(1L)).thenReturn(Optional.of(state));

            List<CitiesDto> result = statesCitiesService.getAllCitiesByState(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Los Angeles");
        }

        @Test
        @DisplayName("Should throw exception when state not found")
        void shouldThrowWhenStateNotFound() {
            when(statesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> statesCitiesService.getAllCitiesByState(999L))
                    .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                    .hasMessageContaining("States not found");
        }
    }

    @Nested
    @DisplayName("foundCitiesById Tests")
    class FoundCitiesByIdTests {

        @Test
        @DisplayName("Should return city when found")
        void shouldReturnCityWhenFound() {
            when(citiesRepository.findById(1L)).thenReturn(Optional.of(city));

            CitiesModel result = statesCitiesService.foundCitiesById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Los Angeles");
        }

        @Test
        @DisplayName("Should throw exception when city not found")
        void shouldThrowWhenCityNotFound() {
            when(citiesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> statesCitiesService.foundCitiesById(999L))
                    .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                    .hasMessageContaining("Cities not found");
        }
    }

    @Nested
    @DisplayName("foundStatesById Tests")
    class FoundStatesByIdTests {

        @Test
        @DisplayName("Should return state when found")
        void shouldReturnStateWhenFound() {
            when(statesRepository.findById(1L)).thenReturn(Optional.of(state));

            StatesModel result = statesCitiesService.foundStatesById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("California");
        }

        @Test
        @DisplayName("Should throw exception when state not found by ID")
        void shouldThrowWhenStateNotFoundById() {
            when(statesRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> statesCitiesService.foundStatesById(999L))
                    .isInstanceOf(CustomExceptions.ResourceNotFoundException.class)
                    .hasMessageContaining("States not found");
        }
    }
}

