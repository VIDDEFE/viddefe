package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.configuration.TopologyEventType;
import com.viddefe.viddefe_api.worship_meetings.contracts.AttendanceQualityReader;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.models.MeetingType;
import com.viddefe.viddefe_api.worship_meetings.domain.models.TopologyMeetingModel;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.MeetingTypeRepository;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.TopologyMeetingRepository;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.AttendanceQualityDto;
import com.viddefe.viddefe_api.worship_meetings.infrastructure.dto.MeetingTypeDto;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingTypeServiceImpl Tests")
class MeetingTypeServiceImplTest {

    @Mock
    private MeetingTypeRepository meetingTypeRepository;

    @Mock
    private TopologyMeetingRepository topologyMeetingRepository;

    @Mock
    private AttendanceQualityReader attendanceQualityReader;

    @InjectMocks
    private MeetingTypeServiceImpl meetingTypeService;

    private MeetingType meetingType;
    private TopologyMeetingModel topologyMeetingModel;

    @BeforeEach
    void setUp() {
        meetingType = new MeetingType();
        meetingType.setId(1L);
        meetingType.setName("Sunday Worship");

        topologyMeetingModel = new TopologyMeetingModel();
        topologyMeetingModel.setId(1L);
        topologyMeetingModel.setType(TopologyEventType.TEMPLE_WORHSIP);
        topologyMeetingModel.setMeetingTypes(List.of(meetingType));
    }

    @Nested
    @DisplayName("getAllMeetingByTopologyEventTypes Tests")
    class GetAllMeetingByTopologyEventTypesTests {

        @Test
        @DisplayName("Should return meeting types for topology event type")
        void shouldReturnMeetingTypesForTopologyEventType() {
            when(topologyMeetingRepository.findByType(TopologyEventType.TEMPLE_WORHSIP))
                    .thenReturn(Optional.of(topologyMeetingModel));

            List<MeetingTypeDto> result = meetingTypeService.getAllMeetingByTopologyEventTypes(
                    TopologyEventType.TEMPLE_WORHSIP);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Sunday Worship");
        }

        @Test
        @DisplayName("Should throw exception when topology not found")
        void shouldThrowWhenTopologyNotFound() {
            when(topologyMeetingRepository.findByType(TopologyEventType.GROUP_MEETING))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingTypeService.getAllMeetingByTopologyEventTypes(
                    TopologyEventType.GROUP_MEETING))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Topology Meeting with type");
        }
    }

    @Nested
    @DisplayName("getMeetingTypesById Tests")
    class GetMeetingTypesByIdTests {

        @Test
        @DisplayName("Should return meeting type when found")
        void shouldReturnMeetingTypeWhenFound() {
            when(meetingTypeRepository.findById(1L)).thenReturn(Optional.of(meetingType));

            MeetingType result = meetingTypeService.getMeetingTypesById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Sunday Worship");
        }

        @Test
        @DisplayName("Should throw exception when meeting type not found")
        void shouldThrowWhenMeetingTypeNotFound() {
            when(meetingTypeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> meetingTypeService.getMeetingTypesById(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Meeting Type with id");
        }
    }

    @Nested
    @DisplayName("getAttendanceLevels Tests")
    class GetAttendanceLevelsTests {

        @Test
        @DisplayName("Should return all attendance quality levels")
        void shouldReturnAllAttendanceQualityLevels() {
            AttendanceQuality quality = new AttendanceQuality();
            quality.setId(1L);
            quality.setName("High");
            quality.setAttendanceQuality(AttendanceQualityEnum.HIGH);

            when(attendanceQualityReader.getAllAttendanceQualities()).thenReturn(List.of(quality));

            List<AttendanceQualityDto> result = meetingTypeService.getAttendanceLevels();

            assertThat(result).hasSize(1);
        }
    }
}

