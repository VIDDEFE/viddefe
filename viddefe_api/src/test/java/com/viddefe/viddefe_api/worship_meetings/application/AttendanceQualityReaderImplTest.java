package com.viddefe.viddefe_api.worship_meetings.application;

import com.viddefe.viddefe_api.worship_meetings.configuration.AttendanceQualityEnum;
import com.viddefe.viddefe_api.worship_meetings.domain.models.AttendanceQuality;
import com.viddefe.viddefe_api.worship_meetings.domain.repository.AttendanceQualityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceQualityReaderImpl Tests")
class AttendanceQualityReaderImplTest {

    @Mock
    private AttendanceQualityRepository attendanceQualityRepository;

    @InjectMocks
    private AttendanceQualityReaderImpl attendanceQualityReader;

    private AttendanceQuality attendanceQuality;

    @BeforeEach
    void setUp() {
        attendanceQuality = new AttendanceQuality();
        attendanceQuality.setId(1L);
        attendanceQuality.setName("High");
        attendanceQuality.setAttendanceQuality(AttendanceQualityEnum.HIGH);
    }

    @Nested
    @DisplayName("getAllAttendanceQualities Tests")
    class GetAllAttendanceQualitiesTests {

        @Test
        @DisplayName("Should return all attendance qualities")
        void shouldReturnAllAttendanceQualities() {
            when(attendanceQualityRepository.findAll()).thenReturn(List.of(attendanceQuality));

            List<AttendanceQuality> result = attendanceQualityReader.getAllAttendanceQualities();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("High");
        }

        @Test
        @DisplayName("Should return empty list when no qualities exist")
        void shouldReturnEmptyListWhenNoQualitiesExist() {
            when(attendanceQualityRepository.findAll()).thenReturn(List.of());

            List<AttendanceQuality> result = attendanceQualityReader.getAllAttendanceQualities();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAttendanceQualityEnum Tests")
    class FindByAttendanceQualityEnumTests {

        @Test
        @DisplayName("Should return attendance quality by enum")
        void shouldReturnAttendanceQualityByEnum() {
            when(attendanceQualityRepository.findByAttendanceQuality(AttendanceQualityEnum.HIGH))
                    .thenReturn(attendanceQuality);

            AttendanceQuality result = attendanceQualityReader.findByAttendanceQualityEnum(
                    AttendanceQualityEnum.HIGH);

            assertThat(result).isNotNull();
            assertThat(result.getAttendanceQuality()).isEqualTo(AttendanceQualityEnum.HIGH);
        }
    }
}

