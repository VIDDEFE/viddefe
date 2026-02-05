package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.TopologyMeetingRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TopologyMeetingReaderImpl Tests")
class TopologyMeetingReaderImplTest {

    @Mock
    private TopologyMeetingRepository topologyMeetingRepository;

    @InjectMocks
    private TopologyMeetingReaderImpl topologyMeetingReader;

    private TopologyMeetingModel topologyMeetingModel;

    @BeforeEach
    void setUp() {
        topologyMeetingModel = new TopologyMeetingModel();
        topologyMeetingModel.setId(1L);
        topologyMeetingModel.setType(TopologyEventType.TEMPLE_WORHSIP);
    }

    @Nested
    @DisplayName("findByTopologyMeetingEnum Tests")
    class FindByTopologyMeetingEnumTests {

        @Test
        @DisplayName("Should return topology meeting model when found")
        void shouldReturnTopologyMeetingModelWhenFound() {
            when(topologyMeetingRepository.findByType(TopologyEventType.TEMPLE_WORHSIP))
                    .thenReturn(Optional.of(topologyMeetingModel));

            TopologyMeetingModel result = topologyMeetingReader.findByTopologyMeetingEnum(
                    TopologyEventType.TEMPLE_WORHSIP);

            assertThat(result).isNotNull();
            assertThat(result.getType()).isEqualTo(TopologyEventType.TEMPLE_WORHSIP);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            when(topologyMeetingRepository.findByType(TopologyEventType.GROUP_MEETING))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> topologyMeetingReader.findByTopologyMeetingEnum(
                    TopologyEventType.GROUP_MEETING))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("No se encontró una reunión topológica");
        }
    }
}

